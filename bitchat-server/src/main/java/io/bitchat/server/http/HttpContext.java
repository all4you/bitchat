package io.bitchat.server.http;

import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * @author houyi
 **/
@Slf4j
public class HttpContext {

    /**
     * 使用FastThreadLocal替代JDK自带的ThreadLocal以提升并发性能
     */
    private static final FastThreadLocal<HttpContext> CONTEXT_HOLDER = new FastThreadLocal<>();

    private HttpRequest request;

    private ChannelHandlerContext context;

    private HttpResponse response;

    private Set<Cookie> cookies;

    private HttpContext() {

    }

    public HttpContext setRequest(HttpRequest request) {
        this.request = request;
        return this;
    }

    public HttpContext setContext(ChannelHandlerContext context) {
        this.context = context;
        return this;
    }

    public HttpContext setResponse(HttpResponse response) {
        this.response = response;
        return this;
    }

    public HttpContext addCookie(Cookie cookie) {
        if (cookie != null) {
            if (CollectionUtil.isEmpty(cookies)) {
                cookies = new HashSet<>();
            }
            cookies.add(cookie);
        }
        return this;
    }

    public HttpContext addCookies(Set<Cookie> cookieSet) {
        if (CollectionUtil.isNotEmpty(cookieSet)) {
            if (CollectionUtil.isEmpty(cookies)) {
                cookies = new HashSet<>();
            }
            cookies.addAll(cookieSet);
        }
        return this;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public static HttpContext currentContext() {
        HttpContext context = CONTEXT_HOLDER.get();
        if (context == null) {
            context = new HttpContext();
            CONTEXT_HOLDER.set(context);
        }
        return context;
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }


}
