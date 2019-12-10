package io.bitchat.http.controller;


import io.bitchat.http.RenderType;
import io.bitchat.http.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    /**
     * 请求方法类型
     *
     * @return 方法类型
     */
    RequestMethod requestMethod() default RequestMethod.GET;

    /**
     * 请求的uri
     *
     * @return url
     */
    String path() default "";

    /**
     * 返回类型
     *
     * @return 返回类型
     */
    RenderType renderType() default RenderType.JSON;

}