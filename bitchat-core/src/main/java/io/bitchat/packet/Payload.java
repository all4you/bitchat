package io.bitchat.packet;

import com.alibaba.fastjson.JSON;
import lombok.*;

import java.io.Serializable;

/**
 * @author houyi
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
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

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
