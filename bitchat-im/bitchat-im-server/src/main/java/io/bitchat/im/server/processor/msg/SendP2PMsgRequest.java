package io.bitchat.im.server.processor.msg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author houyi
 */
@Data
@NoArgsConstructor
public class SendP2PMsgRequest {

    private Long partnerId;

    private Integer channelType;

    private Byte messageType;

    private String msg;

}
