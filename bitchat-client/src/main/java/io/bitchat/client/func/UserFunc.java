package io.bitchat.client.func;

import io.bitchat.core.Carrier;
import io.bitchat.core.Listener;
import io.bitchat.user.User;

import java.util.List;

/**
 * @author houyi
 */
public interface UserFunc {

    /**
     * get online user list
     *
     * @param listener listener
     */
    void onlineUser(Listener<Carrier<List<User>>> listener);

}
