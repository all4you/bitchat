package io.bitchat.protocol;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * A command model
 * </p>
 *
 * @author houyi
 */
@Data
@NoArgsConstructor
public class Command implements Serializable {

    /**
     * the name of the command
     */
    private String commandName;

    /**
     * the command content
     */
    private Map<String, Object> content;

}
