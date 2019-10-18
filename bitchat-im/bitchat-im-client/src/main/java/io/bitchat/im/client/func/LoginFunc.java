package io.bitchat.im.client.func;

import io.bitchat.im.BaseResult;

/**
 * @author houyi
 */
public interface LoginFunc {

    /**
     * user login
     *
     * @param userName userName
     * @param password password
     */
    BaseResult login(String userName, String password);

}
