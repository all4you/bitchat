package io.bitchat.packet;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author houyi
 */
@Data
@NoArgsConstructor
public class Payload implements Serializable {

    private boolean success;

    private int code;

    private String msg;

    private Object result;

    public void setErrorMsg(int code, String msg) {
        this.success = false;
        this.code = code;
        this.msg = msg;
    }

    public void setSuccessMsg(int code, String msg) {
        this.success = true;
        this.code = code;
        this.msg = msg;
    }

}
