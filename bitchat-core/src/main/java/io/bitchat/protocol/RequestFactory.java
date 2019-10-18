package io.bitchat.protocol;

import java.util.Map;

/**
 * @author houyi
 */
public class RequestFactory {

    public static Request newRequest(String serviceName) {
        return RequestFactory.newRequest(serviceName, null);
    }

    public static Request newRequest(String serviceName, String methodName) {
        return RequestFactory.newRequest(serviceName, methodName, null);
    }

    public static Request newRequest(String serviceName, String methodName, Map<String, Object> params) {
        Request request = new Request();
        request.setServiceName(serviceName);
        request.setMethodName(methodName);
        request.setParams(params);
        return request;
    }

}
