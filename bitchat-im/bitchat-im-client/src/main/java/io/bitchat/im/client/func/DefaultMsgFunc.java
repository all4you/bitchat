package io.bitchat.im.client.func;

import cn.hutool.core.lang.Assert;
import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.SnowflakeIdFactory;
import io.bitchat.im.BaseResult;
import io.bitchat.im.ImServiceName;
import io.bitchat.im.ListResult;
import io.bitchat.im.message.Message;
import io.bitchat.im.message.MessageType;
import io.bitchat.protocol.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author houyi
 */
@Slf4j
public class DefaultMsgFunc implements MsgFunc {

    private IdFactory idFactory;
    private BaseFunc baseFunc;

    public DefaultMsgFunc(BaseFunc baseFunc) {
        Assert.notNull(baseFunc, "baseFunc can not be null");
        this.baseFunc = baseFunc;
        this.idFactory = SnowflakeIdFactory.getInstance(3L);
    }

    @Override
    public BaseResult sendP2pMsg(Long partnerId, MessageType type, String msg) {
        Map<String, Object> params = buildParams(partnerId, type, msg);
        Request request = RequestFactory.newRequest(ImServiceName.SEND_P2P_MSG, null, params);
        Packet packet = PacketFactory.newRequestPacket(request, idFactory.nextId());
        Payload payload = baseFunc.request(packet);
        return baseFunc.transferResult(payload);
    }

    @Override
    public BaseResult sendGroupMsg(Long groupId, MessageType type, String msg) {
        return new BaseResult();
    }

    @Override
    public ListResult<Message> fetchP2pHistoryMsg(Long partnerId, Long currentMsgId, boolean fetchForward, Integer fetchSize) {
        return null;
    }

    @Override
    public ListResult<Message> fetchGroupHistoryMsg(Long groupId, Long currentMsgId, boolean fetchForward, Integer fetchSize) {
        return null;
    }

    @Override
    public ListResult<Message> fetchOfflineMsg() {
        return null;
    }


    private Map<String, Object> buildParams(Long partnerId, MessageType messageType, String msg) {
        Map<String, Object> params = new HashMap<>();
        params.put("partnerId", partnerId);
        params.put("messageType", messageType);
        params.put("msg", msg);
        return params;
    }

}
