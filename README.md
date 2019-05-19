# bitchat
**bitchat** 是一个基于 Netty 的 IM 即时通讯框架

 **特性:**

- [x] **IOC容器** : 通过 @Bean 注解可以管理所有对象，通过 @Autowired 注解进行对象注入
- [x] **自定义协议**  : 一个自定义的 Packet 协议，业务的扩展非常简单
- [x] **编解码器**  : 内置 PacketCodec 编解码器，解决拆包粘包的问题
- [x] **业务处理器**  : 业务处理器 PacketHandler 与 Packet 分离，支持各种自定义业务处理器
- [x] **可选的业务处理方式**  : 服务端支持同步或异步的业务处理， 可以由客户端在 Packet 协议中自主选择，默认是在业务线程池中异步处理
- [x] **可选的序列化方式**  : 支持多种序列化方式，可以由客户端在 Packet 协议中自主选择，默认是 ProtoStuff方式
- [x] **单机模式**  : 支持单机模式
- [x] **心跳检测** ：服务端与客户端自带心跳检查机制，客户端支持断线重连


**TODO：**

- [ ] **集群模式**  : 支持服务端的集群方式部署，形成一个 Router 层，客户端通过 Router 获取可用的服务端实例
- [ ] **Connection中心** ：一个 Connection 中心，目前是在内存中保存，未来需要支持 Connection 的持久化，
- [ ] **Message中心**  : 消息的存储的查询
- [ ] **User中心** : 用户与群组的管理




## 快速开始

**bitchat-example** 模块提供了一个服务端与客户端的实现示例，可以参照该示例进行自己的业务实现。



### 启动服务端

要启动服务端，需要获取一个 Server 的实例，可以通过 ServerFactory 来获取。

目前只实现了单机模式下的 Server ，通过 SimpleServerFactory 只需要定义一个端口即可获取一个单机的 Server 实例，如下所示：

```java
public class StandaloneServerApplication {
    public static void main(String[] args) {
        Server server = SimpleServerFactory.getInstance()
            .newServer(8864);
        server.start();
    }
}
```

服务端启动成功后，将显示如下信息：

![server-startup](articles/resources/bitchat-overview/server-startup.jpg)

### 

### 启动客户端

目前只实现了直连服务器的客户端，通过 SimpleClientFactory 只需要指定一个 ServerAttr 即可获取一个客户端，然后进行客户端与服务端的连接，如下所示：

```java
public class DirectConnectServerClientApplication {

    public static void main(String[] args) {
        Client client = SimpleClientFactory.getInstance()
            .newClient(ServerAttr.getLocalServer(8864));
        client.connect();

        doClientBiz(client);
    }
}
```

客户端连接上服务端后，将显示如下信息：

![client-connect](articles/resources/bitchat-overview/client-connect.jpg)



#### 体验客户端的功能

目前客户端提供了三种 Func，分别是：登录，查看在线用户列表，发送单聊消息，每种 Func 有不同的命令格式。



##### 登录

通过在客户端中执行以下命令 `-lo houyi 123456` 即可实现登录，目前用户中心还未实现，通过 Mock 的方式实现一个假的用户服务，所以输入任何的用户名密码都会登录成功，并且会为用户创建一个用户id。

登录成功后，显示如下：

![login](articles/resources/bitchat-overview/login.jpg)



##### 查看在线用户

再启动一个客户端，并且也执行登录，登录成功后，可以执行 `-lu` 命令，获取在线用户列表，目前用户是保存在内存中，获取的结果如下所示：

![list-user](articles/resources/bitchat-overview/list-user.jpg)



##### 发送单聊信息

用 gris 这个用户向 houyi 这个用户发送单聊信息，只要执行 `-pc 1 hello,houyi` 命令即可

其中第二个参数数要发送消息给那个用户的用户id，第三个参数是消息内容

消息发送方，发送完消息：

![send-p2p-msg](articles/resources/bitchat-overview/send-p2p-msg.jpg)



消息接收方，接收到消息：

![received-p2p-msg](articles/resources/bitchat-overview/received-p2p-msg.jpg)



#### 客户端断线重连

客户端和服务端之间维持着心跳，双方都会检查连接是否可用，客户端每隔5s会向服务端发送一个 PingPacket，而服务端接收到这个 PingPacket 之后，会回复一个 PongPacket，这样表示双方都是健康的。

当因为某种原因，服务端没有收到客户端发送的消息，服务端将会把该客户端的连接断开，同样的客户端也会做这样的检查。

当客户端与服务端之间的连接断开之后，将会触发客户端 HealthyChecker 的 channelInactive 方法，从而进行客户端的断线重连。

![client-reconnect](articles/resources/bitchat-overview/client-reconnect.jpg)



## 通讯框架

**bitchat** 除了可以作为 IM 框架之外，还可以作为一个通用的通讯框架。

Packet 作为通讯的载体，通过继承 AbstractPacket 即可快速实现自己的业务，搭配 PacketHandler 作为数据处理器即可实现客户端与服务端的通讯。