package io.bitchat.message;

import lombok.*;

import java.io.Serializable;

/**
 * <p>
 * A person to person message
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
public class P2pMessage extends Message implements Serializable {

    /**
     * message send from user id
     */
    private Long userId;

    /**
     * message send to user id
     */
    private Long partnerId;

}
