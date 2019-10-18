package io.bitchat.im.client.func;

import io.bitchat.im.ListResult;
import io.bitchat.im.user.User;

/**
 * @author houyi
 */
public interface UserFunc {

    /**
     * get online friends list
     *
     * @param userId userId
     */
    ListResult<User> getOnlineFriends(long userId);

}
