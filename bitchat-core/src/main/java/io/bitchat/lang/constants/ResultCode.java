package io.bitchat.lang.constants;

import lombok.Getter;

/**
 * @author houyi
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "SUCCESS"),
    INTERNAL_ERROR(301, "INTERNAL_ERROR"),
    RESOURCE_NOT_FOUND(404, "RESOURCE_NOT_FOUND"),
    BIZ_FAIL(3001, "BIZ_FAIL");

    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
