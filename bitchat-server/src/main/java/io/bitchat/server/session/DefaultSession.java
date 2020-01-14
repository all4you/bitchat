package io.bitchat.server.session;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import io.bitchat.server.channel.ChannelManager;
import io.bitchat.server.channel.ChannelType;
import io.bitchat.server.channel.ChannelWrapper;
import io.bitchat.server.channel.DefaultChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author houyi
 */
public class DefaultSession implements Session {

    private String sessionId;
    private long userId;
    private ChannelId channelId;
    private Channel channel;
    private ChannelType channelType;

    private ChannelManager channelManager;
    private AtomicBoolean bounded;

    public DefaultSession(String sessionId) {
        this.sessionId = sessionId;
        this.channelManager = DefaultChannelManager.getInstance();
        this.bounded = new AtomicBoolean(false);
    }

    @Override
    public String sessionId() {
        return sessionId;
    }

    @Override
    public void bound(ChannelId channelId, long userId) {
        if (bounded.compareAndSet(false, true)) {
            ChannelWrapper channelWrapper = channelManager.getChannelWrapper(channelId);
            Assert.notNull(channelWrapper, "channelId does not exists");
            this.channelId = channelId;
            this.userId = userId;
            this.channel = channelWrapper.getChannel();
            this.channelType = channelWrapper.getChannelType();
        }
    }

    @Override
    public long userId() {
        if (!bounded.get()) {
            throw new IllegalStateException("Not bounded yet, Please call bound first");
        }
        return userId;
    }

    @Override
    public ChannelId channelId() {
        if (!bounded.get()) {
            throw new IllegalStateException("Not bounded yet, Please call bound first");
        }
        return channelId;
    }

    @Override
    public ChannelType channelType() {
        if (!bounded.get()) {
            throw new IllegalStateException("Not bounded yet, Please call bound first");
        }
        return channelType;
    }

    @Override
    public void writeAndFlush(Object msg) {
        if (!bounded.get()) {
            throw new IllegalStateException("Not bounded yet, Please call bound first");
        }
        channel.writeAndFlush(msg);
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        object.put("sessionId", sessionId);
        object.put("userId", userId);
        object.put("shortId", channelId.asShortText());
        object.put("longId", channelId.asLongText());
        object.put("channelType", channelType);
        return object.toJSONString();
    }
}
