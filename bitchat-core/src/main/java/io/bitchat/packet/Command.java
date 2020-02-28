package io.bitchat.packet;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
     * json string format
     */
    private String contentJson;

}
