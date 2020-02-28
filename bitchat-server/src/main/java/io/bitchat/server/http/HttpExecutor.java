package io.bitchat.server.http;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Singleton;
import io.bitchat.core.executor.AbstractExecutor;
import io.bitchat.http.HttpContext;
import io.bitchat.http.controller.ControllerProxy;
import io.bitchat.http.controller.ProxyInvocation;
import io.bitchat.http.exception.InvocationException;
import io.bitchat.http.util.HttpRenderUtil;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author houyi
 */
@Slf4j
public class HttpExecutor extends AbstractExecutor<HttpResponse> {

    private ControllerContext controllerContext;

    private HttpExecutor() {
        this.controllerContext = ControllerContext.getInstance();
    }

    public static HttpExecutor getInstance() {
        return Singleton.get(HttpExecutor.class);
    }


    @Override
    public HttpResponse doExecute(Object... request) {
        HttpRequest httpRequest = (HttpRequest) request[0];
        // 暂存请求对象
        // 将request存储到ThreadLocal中去，便于后期在其他地方获取并使用
        HttpContext.currentContext().setRequest(httpRequest);
        HttpResponse response = null;
        try {
            // 处理业务逻辑
            response = invoke(httpRequest);
        } catch (Exception e) {
            log.error("Server Internal Error,cause:", e);
            response = getErrorResponse(e);
        } finally {
            // 构造响应头
            buildHeaders(response, HttpContext.currentContext());
            // 释放ThreadLocal对象
            HttpContext.clear();
        }
        return response;
    }

    private HttpResponse invoke(HttpRequest request) throws Exception {
        // 根据路由获得具体的ControllerProxy
        ControllerProxy controllerProxy = controllerContext.getProxy(request.method(), request.uri());
        if (controllerProxy == null) {
            return HttpRenderUtil.getNotFoundResponse();
        }
        // 调用用户自定义的Controller，获得结果
        Object result = ProxyInvocation.invoke(controllerProxy);
        return HttpRenderUtil.render(result, controllerProxy.getRenderType());
    }

    private HttpResponse getErrorResponse(Exception e) {
        HttpResponse response;
        if (e instanceof IllegalArgumentException || e instanceof InvocationException) {
            response = HttpRenderUtil.getErrorResponse(e.getMessage());
        } else {
            response = HttpRenderUtil.getServerErrorResponse();
        }
        return response;
    }

    private void buildHeaders(HttpResponse response, HttpContext httpContext) {
        if (response == null) {
            return;
        }
        FullHttpResponse fullHttpResponse = (FullHttpResponse) response;
        // 写cookie
        Set<Cookie> cookies = httpContext.getCookies();
        if (CollectionUtil.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                fullHttpResponse.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
            }
        }
    }

}
