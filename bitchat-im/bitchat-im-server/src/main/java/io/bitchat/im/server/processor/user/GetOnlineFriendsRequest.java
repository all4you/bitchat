package io.bitchat.im.server.processor.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author houyi
 */
@Data
@NoArgsConstructor
public class GetOnlineFriendsRequest {

    private long userId;

}
