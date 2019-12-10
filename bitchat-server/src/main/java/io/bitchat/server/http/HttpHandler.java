package io.bitchat.server.http;

import cn.hutool.core.lang.Singleton;
import io.bitchat.core.executor.Executor;
import io.bitchat.http.util.HttpRequestUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
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
public class HttpHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private Executor<HttpResponse> executor;

    private HttpHandler() {
        this.executor = HttpExecutor.getInstance();
    }

    public static HttpHandler getInstance() {
        return Singleton.get(HttpHandler.class);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest request) {
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.error("ctx close,cause:", cause);
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
