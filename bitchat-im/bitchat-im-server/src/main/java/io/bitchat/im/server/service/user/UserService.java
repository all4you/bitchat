package io.bitchat.im.server.service.user;

import io.bitchat.im.BaseResult;
import io.bitchat.im.PojoResult;
import io.bitchat.im.server.processor.login.LoginRequest;
import io.bitchat.im.server.processor.register.RegisterRequest;
import io.bitchat.im.user.User;

/**
 * @author houyi
 */
public interface UserService {

    /**
     * register new user
     * @param registerRequest registerRequest
     * @return true: register success false: register fail
     */
    BaseResult register(RegisterRequest registerRequest);

    /**
     * login and return the user
     * @param loginRequest loginRequest
     * @return the user
     */
    PojoResult<User> login(LoginRequest loginRequest);

}
