package io.bitchat.protocol;

import io.bitchat.lang.constants.ResultCode;

/**
 * @author houyi
 */
public class PayloadFactory {

    public static Payload newErrorPayload(int code, String msg) {
        Payload payload = new Payload();
        payload.setErrorMsg(code, msg);
        return payload;
    }

    public static Payload newSuccessPayload() {
        Payload payload = new Payload();
        payload.setSuccessMsg(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
        return payload;
    }

}
