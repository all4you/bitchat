package io.bitchat.im;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author houyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ListResult<T> extends BaseResult {

    private List<T> content;

    public ListResult() {

    }

    public ListResult(List<T> content) {
        this.content = content;
    }

}
