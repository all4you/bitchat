package io.bitchat.message;

import lombok.*;

import java.io.Serializable;

/**
 * <p>
 * A group message
 * </p>
 *
 * @author houyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessage extends Message implements Serializable {

    /**
     * message send from user id
     */
    private Long userId;

    /**
     * message send to group id
     */
    private Long groupId;

}
