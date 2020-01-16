package io.bitchat.server.channel;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author houyi
 */
@Getter
public enum ChannelType {
    /**
     * Packet
     */
    Packet(1),
    /**
     * WebSocket
     */
    WebSocket(2);

    private int type;

    ChannelType(int type) {
        this.type = type;
    }

    public static ChannelType getChannelType(int type) {
        return Arrays.stream(values())
                .filter(channelType -> channelType.getType() == type)
                .findFirst()
                .orElse(null);
    }
}
