package io.bitchat.server.http;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.bitchat.core.executor.Executor;
import io.bitchat.http.util.HttpRequestUtil;
import io.bitchat.server.channel.ChannelListener;
import io.bitchat.server.ws.FrameHandler;
import io.bitchat.ws.codec.FrameCodec;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * HttpRequest ChannelHandler
 *
 * @author houyi
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpHandler extends SimpleChannelInboundHandler<Object> {

    private Executor<HttpResponse> executor;

    private ChannelListener channelListener;

    private HttpHandler() {

    }

    private HttpHandler(ChannelListener channelListener) {
        Assert.notNull(channelListener, "channelListener can not be null");
        this.executor = HttpExecutor.getInstance();
        this.channelListener = channelListener;
    }

    public static HttpHandler getInstance(ChannelListener channelListener) {
        return Singleton.get(HttpHandler.class, channelListener);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            if (upgradeToWebSocket(ctx, request)) {
                ctx.fireChannelRead(((FullHttpRequest) msg).retain());
            } else {
                handleHttpRequest(ctx, request);
            }
        } else if (msg instanceof WebSocketFrame) {
            log.info("frame={}", msg);
            ctx.fireChannelRead(((WebSocketFrame) msg).retain());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.error("ctx close,cause:", cause);
    }

    private boolean upgradeToWebSocket(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpHeaders headers = request.headers();
        if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION)) &&
                "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {
            ChannelPipeline pipeline = ctx.pipeline();
            // 将http升级为WebSocket
            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
            pipeline.addLast(FrameCodec.getInstance());
            pipeline.addLast(FrameHandler.getInstance(channelListener));
            pipeline.remove(this);
            // 将channelActive事件传递到FrameHandler
            ctx.fireChannelActive();
            return true;
        }
        return false;
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (handleAsync(request)) {
            // 当前通道所持有的线程池
            EventExecutor channelExecutor = ctx.executor();
            // 创建一个异步结果，并指定该promise
            Promise<HttpResponse> promise = new DefaultPromise<>(channelExecutor);
            // 在自定义线程池中执行业务逻辑，并返回一个异步结果
            Future<HttpResponse> future = executor.asyncExecute(promise, request);
            future.addListener(new GenericFutureListener<Future<HttpResponse>>() {
                @Override
                public void operationComplete(Future<HttpResponse> f) throws Exception {
                    // 异步结果执行成功后，取出结果
                    HttpResponse response = f.get();
                    // 通过IO线程写响应结果
                    writeResponse(ctx, response);
                }
            });
        } else {
            // 同步执行
            HttpResponse response = executor.execute(request);
            writeResponse(ctx, response);
        }
    }

    private boolean handleAsync(HttpRequest request) {
        Map<String, List<String>> paramMap = HttpRequestUtil.getParameterMap(request);
        return paramMap.containsKey("handleAsync");
    }

    private void writeResponse(ChannelHandlerContext ctx, HttpResponse response) {
        if (response != null) {
            ctx.channel().writeAndFlush(response);
        }
    }

}
