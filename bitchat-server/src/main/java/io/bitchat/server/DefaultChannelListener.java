package io.bitchat.server;

import cn.hutool.core.lang.Singleton;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class DefaultChannelListener extends ChannelListener {

    public static ChannelListener getInstance() {
        return Singleton.get(DefaultChannelListener.class);
    }

    @Override
    public void channelInactive(Channel channel) {
        log.info("You should extends io.bitchat.server.ChannelListener and override the channelInactive method to remove the inactive channel:{}", channel);
    }

}
