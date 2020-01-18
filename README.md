# bitchat

**bitchat** 是一个基于 Netty 的网络框架

 **特性:**

- [x] **自定义协议**  : 一个自定义的 Packet 协议，业务的扩展非常简单
- [x] **支持WebSocket协议**  : 在同一端口上支持自定义的Packet协议以及Http、WebSocket协议
- [x] **编解码器**  : 内置 PacketCodec 和 FrameCodec 编解码器，解决拆包粘包的问题
- [x] **统一的业务处理器**  : 通过抽象的 Processor 统一了 Packet 协议和 WebSocket 协议的处理流程
- [x] **可选的业务处理方式**  : 服务端支持同步或异步的业务处理， 可以由客户端在 Packet 协议中自主选择，默认是在业务线程池中异步处理
- [x] **可选的序列化方式**  : 支持多种序列化方式，可以由客户端在 Packet 协议中自主选择，默认是 ProtoStuff方式
- [x] **单机模式**  : 支持单机模式
- [x] **心跳检测** : 服务端与客户端自带心跳检查机制，客户端支持断线重连
- [x] **Channel管理** : 管理所有连接上的 Channel，并支持通过 Rest 接口查询
- [x] **Session管理** : 管理所有登录并绑定到 Channel 上的 Session，并支持通过 Rest 接口查询

**TODO：**

- [ ] **集群模式**  : 支持服务端的集群方式部署，形成一个 Router 层，客户端通过 Router 获取可用的服务端实例



## 服务端入口

服务端启动的入口为：`io.bitchat.server.ServerShell` 

目前只实现了单机模式下的 Server ，通过 ServerBootstrap 只需要定义一个端口即可获取一个单机的 Server 实例，如下所示：

```java
public class ServerShell {
    public static void main(String[] args) {
        ServerStartupParameter param = new ServerStartupParameter();
        JCommander.newBuilder()
                .addObject(param)
                .build()
                .parse(args);
        ServerMode serverMode = ServerMode.getEnum(param.mode);
        RouterServerAttr routerServerAttr = RouterServerAttr.builder()
                .address(param.routerAddress)
                .port(param.routerPort)
                .build();
        Integer serverPort = param.serverPort;
    
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.serverMode(serverMode)
                .routerServerAttr(routerServerAttr)
                .start(serverPort);
    }
}
```





## 自定义协议

通过一个自定义协议来实现服务端与客户端之间的通讯，协议中有如下几个字段：

```java
*
* <p>
* The structure of a Packet is like blow:
* +----------+----------+----------------------------+
* |  size    |  value   |  intro                     |
* +----------+----------+----------------------------+
* | 1 bytes  | 0xBC     |  magic number              |
* | 1 bytes  |          |  serialize algorithm       |
* | 1 bytes  |          |  the type 1:req 2:res 3:cmd|
* | 4 bytes  |          |  content length            |
* | ? bytes  |          |  the content               |
* +----------+----------+----------------------------+
* </p>
*
```

每个字段的含义

| 所占字节 | 用途              |
| -------- | ----------------- |
| 1        | 魔数，默认为 0xBC |
| 1        | 序列化的算法      |
| 1        | Packet 的类型     |
| 4        | Packet 的内容长度 |
| ?        | Packet 的内容     |

序列化算法将会决定该 Packet 在编解码时，使用何种序列化方式。

Packet 的类型将会决定到达服务端的字节流将被反序列化为何种 Packet，也决定了该 Packet 将会被哪个 PacketHandler 进行处理。

内容长度将会解决 Packet 的拆包与粘包问题，服务端在解析字节流时，将会等到字节的长度达到内容的长度时，才进行字节的读取。

除此之外，Packet 中还会存储一个 handleAsync 字段，该字段将指定服务端在处理该 Packet 的数据时是否需要使用异步的业务线程池来处理。



## 健康检查

服务端与客户端各自维护了一个健康检查的服务，即 Netty 为我们提供的 IdleStateHandler，通过继承该类，并且实现 channelIdle 方法即可实现连接 “空闲” 时的逻辑处理，当出现空闲时，目前我们只关心读空闲，我们既可以认为这条链接出现问题了。

那么只需要在链接出现问题时，将这条链接关闭即可，如下所示：

```java
public class IdleStateChecker extends IdleStateHandler {

    private static final int DEFAULT_READER_IDLE_TIME = 15;

    private int readerTime;

    public IdleStateChecker(int readerIdleTime) {
        super(readerIdleTime == 0 ? DEFAULT_READER_IDLE_TIME : readerIdleTime, 0, 0, TimeUnit.SECONDS);
        readerTime = readerIdleTime == 0 ? DEFAULT_READER_IDLE_TIME : readerIdleTime;
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        log.warn("[{}] Hasn't read data after {} seconds, will close the channel:{}", 
        IdleStateChecker.class.getSimpleName(), readerTime, ctx.channel());
        ctx.channel().close();
    }

}
```

另外，客户端需要额外再维护一个健康检查器，正常情况下他负责定时向服务端发送心跳，当链接的状态变成 inActive 时，该检查器将负责进行重连，如下所示：

```java
public class HealthyChecker extends ChannelInboundHandlerAdapter {

    private static final int DEFAULT_PING_INTERVAL = 5;

    private Client client;

    private int pingInterval;

    public HealthyChecker(Client client, int pingInterval) {
        Assert.notNull(client, "client can not be null");
        this.client = client;
        this.pingInterval = pingInterval <= 0 ? DEFAULT_PING_INTERVAL : pingInterval;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        schedulePing(ctx);
    }

    private void schedulePing(ChannelHandlerContext ctx) {
        ctx.executor().schedule(() -> {
            Channel channel = ctx.channel();
            if (channel.isActive()) {
                Packet pingPacket = PacketFactory.newPingPacket();
                log.debug("[{}] Send a Ping={}", HealthyChecker.class.getSimpleName(), pingPacket);
                channel.writeAndFlush(pingPacket);
                schedulePing(ctx);
            }
        }, pingInterval, TimeUnit.SECONDS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.executor().schedule(() -> {
            log.info("[{}] Try to reconnecting...", HealthyChecker.class.getSimpleName());
            client.connect();
        }, 5, TimeUnit.SECONDS);
        ctx.fireChannelInactive();
    }

}
```



## 业务线程池

我们知道，Netty 中维护着两个 IO 线程池，一个 boss 主要负责链接的建立，另外一个 worker 主要负责链接上的数据读写，我们不应该使用 IO 线程来处理我们的业务，因为这样很可能会对 IO 线程造成阻塞，导致新链接无法及时建立或者数据无法及时读写。

为了解决这个问题，我们需要在业务线程池中来处理我们的业务逻辑，但是这并不是绝对的，如果我们要执行的逻辑很简单，不会造成太大的阻塞，则可以直接在 IO 线程中处理，比如客户端发送一个 Ping 服务端回复一个 Pong，这种情况是没有必要在业务线程池中进行处理的，因为处理完了最终还是要交给 IO 线程去写数据。但是如果一个业务逻辑需要查询数据库或者读取文件，这种操作往往比较耗时间，所以就需要将这些操作封装起来交给业务线程池去处理。

服务端允许客户端在传输的 Packet 中指定采用何种方式进行业务的处理，服务端在将字节流解码成 Packet 之后，会根据 Packet 中的 handleAsync 字段的值，确定怎样对该 Packet 进行处理，如下所示：

```java
public class PacketHandler extends 
    SimpleChannelInboundHandler<Packet> {
    
    private void onRequest(ChannelHandlerContext ctx, Packet packet) {
        // if the packet should be handled async
        if (packet.isHandleAsync()) {
            EventExecutor channelExecutor = ctx.executor();
            // create a promise
            Promise<Packet> promise = new DefaultPromise<>(channelExecutor);
            // async execute and get a future
            Future<Packet> future = executor.asyncExecute(promise, ctx, packet);
            future.addListener(new GenericFutureListener<Future<Packet>>() {
                @Override
                public void operationComplete(Future<Packet> f) throws Exception {
                    if (f.isSuccess()) {
                        Packet response = f.get();
                        writeResponse(ctx, response);
                    }
                }
            });
        } else {
            // sync execute and get the response packet
            Packet response = executor.execute(ctx, packet);
            writeResponse(ctx, response);
        }
    }
}
```




