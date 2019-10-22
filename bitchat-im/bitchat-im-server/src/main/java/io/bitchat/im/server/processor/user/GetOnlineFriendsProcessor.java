package io.bitchat.im.server.processor.user;

import cn.hutool.core.collection.CollectionUtil;
import io.bitchat.im.ImServiceName;
import io.bitchat.im.server.connection.ConnectionManager;
import io.bitchat.im.server.connection.DefaultConnectionManager;
import io.bitchat.im.user.User;
import io.bitchat.protocol.AbstractRequestProcessor;
import io.bitchat.protocol.Payload;
import io.bitchat.protocol.PayloadFactory;
import io.bitchat.protocol.Processor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author houyi
 */
@Slf4j
@Processor(name = ImServiceName.GET_ONLINE_USER)
public class GetOnlineFriendsProcessor extends AbstractRequestProcessor {

    private ConnectionManager connectionManager;

    public GetOnlineFriendsProcessor() {
        this.connectionManager = DefaultConnectionManager.getInstance();
    }

    @Override
    public Payload doProcess(ChannelHandlerContext ctx, Map<String, Object> params) {
        // transfer map to bean
        GetOnlineFriendsRequest getOnlineFriendsRequest = mapToBean(params, GetOnlineFriendsRequest.class);
        List<User> userList = connectionManager.onlineUser();
        Payload payload = PayloadFactory.newSuccessPayload();
        if (CollectionUtil.isNotEmpty(userList)) {
            payload.setResult(userList);
        }
        return payload;
    }


}
