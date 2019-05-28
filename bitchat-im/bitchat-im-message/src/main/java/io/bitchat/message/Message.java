package io.bitchat.message;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * A base message
 * </p>
 *
 * @author houyi
 */
@Data
public abstract class Message implements Serializable {

    /**
     * message id
     */
    private Long msgId;

    /**
     * the session id of the message
     */
    private Long sessionId;

    /**
     * message category
     * e.g. p2p,group
     */
    private Byte category;

    /**
     * message type
     * e.g. text,image,emoji,video,link
     */
    private MessageType type;

    /**
     * create time
     */
    private Long createTime;

    /**
     * message detail
     */
    private String msg;

}
