package io.bitchat.server.channel;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author houyi
 */
@Getter
@Setter
@Builder
public class ChannelWrapper {

    private Channel channel;

    private ChannelType channelType;

    @Override
    public String toString() {
        JSONObject channelInfo = new JSONObject();
        channelInfo.put("id", channel.id().asLongText());
        channelInfo.put("localAddress", channel.localAddress());
        channelInfo.put("remoteAddress", channel.remoteAddress());
        channelInfo.put("active", channel.isActive());
        channelInfo.put("open", channel.isOpen());
        channelInfo.put("registered", channel.isRegistered());
        channelInfo.put("writable", channel.isWritable());
        JSONObject object = new JSONObject();
        object.put("channelInfo", channelInfo);
        object.put("channelType", channelType);
        return object.toJSONString();
    }

}
