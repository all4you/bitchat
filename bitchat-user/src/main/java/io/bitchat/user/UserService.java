package io.bitchat.user;

/**
 * @author houyi
 */
public interface UserService {

    /**
     * login and return the user
     * @param userName user name
     * @param password user password
     * @return the user
     */
    User login(String userName, String password);

}
