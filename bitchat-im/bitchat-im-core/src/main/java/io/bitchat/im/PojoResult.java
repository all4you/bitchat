package io.bitchat.im;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author houyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PojoResult<T> extends BaseResult {

    private T content;

    public PojoResult() {

    }

    public PojoResult(T content) {
        this.content = content;
    }

}
