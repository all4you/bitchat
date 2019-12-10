package io.bitchat.server.http.controller;


import io.bitchat.server.http.RenderType;
import io.bitchat.server.http.RequestMethod;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 路由请求代理，用以根据路由调用具体的controller类
 *
 * @author houyi
 */
@Data
public class ControllerProxy {

    private RenderType renderType;

    private RequestMethod requestMethod;

    private Object controller;

    private Method method;

    private String methodName;

}
