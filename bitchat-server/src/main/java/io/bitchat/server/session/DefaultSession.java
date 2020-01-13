package io.bitchat.server.session;

import com.alibaba.fastjson.JSONObject;
import io.bitchat.server.channel.ChannelType;
import io.netty.channel.ChannelId;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author houyi
 */
public class DefaultSession implements Session {

    private String sessionId;
    private ChannelId channelId;
    private ChannelType channelType;

    private AtomicBoolean bounded;

    public DefaultSession(String sessionId) {
        this.sessionId = sessionId;
        this.bounded = new AtomicBoolean(false);
    }

    @Override
    public void bound(ChannelId channelId, ChannelType channelType) {
        if (bounded.compareAndSet(false, true)) {
            this.channelId = channelId;
            this.channelType = channelType;
        }
    }

    @Override
    public String sessionId() {
        return sessionId;
    }

    @Override
    public ChannelId channelId() {
        if (!bounded.get()) {
            throw new IllegalStateException("Not bounded yet, Please bound the ChannelId with the Session first");
        }
        return channelId;
    }

    @Override
    public ChannelType channelType() {
        if (!bounded.get()) {
            throw new IllegalStateException("Not bounded yet, Please bound the ChannelType with the Session first");
        }
        return channelType;
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        object.put("sessionId", sessionId);
        object.put("shortId", channelId.asShortText());
        object.put("longId", channelId.asLongText());
        object.put("channelType", channelType);
        return object.toJSONString();
    }
}
