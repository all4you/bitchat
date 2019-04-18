package io.bitchat.client.func;

import io.bitchat.core.Carrier;
import io.bitchat.core.Listener;

/**
 * @author houyi
 */
public interface LoginFunc {

    /**
     * user login
     *
     * @param userName userName
     * @param password password
     * @param listener listener
     */
    void login(String userName, String password, Listener<Carrier<String>> listener);

}
