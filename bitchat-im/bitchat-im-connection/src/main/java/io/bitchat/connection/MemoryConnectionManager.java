package io.bitchat.connection;

import cn.hutool.core.collection.CollectionUtil;
import io.bitchat.core.bean.Bean;
import io.bitchat.user.User;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author houyi
 */
@Slf4j
@Bean(name = "memoryConnectionKeeper")
public class MemoryConnectionManager implements ConnectionManager {

    private List<Connection> connections = new CopyOnWriteArrayList<>();

    @Override
    public void add(Channel channel, User user, String serverAddress, Integer serverPort) {
        if (user == null || channel == null) {
            return;
        }
        Connection connection = Connection.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .clientId(getClientId(channel))
                .channel(channel)
                .serverAddress(serverAddress)
                .serverPort(serverPort)
                .build();
        connections.add(connection);
        log.info("Add a new connection={}", connection);
        ConnectionUtil.markOnline(channel);
    }

    @Override
    public void remove(Channel channel) {
        if (channel == null) {
            return;
        }
        String clientId = getClientId(channel);
        for (Connection connection : connections) {
            if (connection.getClientId().equals(clientId)) {
                connections.remove(connection);
            }
        }
        ConnectionUtil.markOffline(channel);
    }

    @Override
    public Connection get(Channel channel) {
        if (channel == null) {
            return null;
        }
        String clientId = getClientId(channel);
        for (Connection connection : connections) {
            if (connection.getClientId().equals(clientId)) {
                return connection;
            }
        }
        return null;
    }

    @Override
    public boolean contains(Channel channel) {
        return get(channel) != null;
    }

    @Override
    public List<Connection> getList(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        List<Connection> connSet = new ArrayList<>();
        for (Connection connection : connections) {
            if (connection.getUserId().equals(userId)) {
                connSet.add(connection);
            }
        }
        return connSet;
    }

    @Override
    public List<User> onlineUser() {
        Set<User> noRepeatUsers = connections.stream()
                .map(connection -> {
                    return User.builder().userId(connection.getUserId()).userName(connection.getUserName()).build();
                })
                .collect(Collectors.toSet());
        return CollectionUtil.newArrayList(noRepeatUsers);
    }

    private String getClientId(Channel channel) {
        return channel.id().asLongText();
    }

}
