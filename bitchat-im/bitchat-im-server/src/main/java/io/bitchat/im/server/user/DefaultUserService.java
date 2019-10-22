package io.bitchat.im.server.user;

import cn.hutool.core.lang.Singleton;
import io.bitchat.im.user.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author houyi
 */
@Slf4j
public class DefaultUserService implements UserService {

    private static Map<String, User> users = new ConcurrentHashMap<>();
    private static AtomicLong mockUserId = new AtomicLong(1);

    private DefaultUserService() {

    }

    public static UserService getInstance() {
        return Singleton.get(DefaultUserService.class);
    }

    @Override
    public User login(String userName, String password) {
        User user = users.get(userName);
        if (user != null) {
            return user;
        }
        user = User.builder()
                .userId(mockUserId.getAndIncrement())
                .userName(userName)
                .password(password)
                .build();
        users.putIfAbsent(userName, user);
        return user;
    }

}
