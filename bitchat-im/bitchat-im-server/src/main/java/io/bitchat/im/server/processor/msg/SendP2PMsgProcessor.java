package io.bitchat.im.server.processor.msg;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.SnowflakeIdFactory;
import io.bitchat.im.ImServiceName;
import io.bitchat.im.connection.Connection;
import io.bitchat.im.server.connection.ConnectionManager;
import io.bitchat.im.server.connection.DefaultConnectionManager;
import io.bitchat.im.message.*;
import io.bitchat.im.server.message.DefaultMessageWriter;
import io.bitchat.im.server.message.MessageWriter;
import io.bitchat.im.server.session.DefaultSessionIdKeeper;
import io.bitchat.lang.constants.ResultCode;
import io.bitchat.protocol.*;
import io.bitchat.server.SessionIdKeeper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author houyi
 */
@Slf4j
@Processor(name = ImServiceName.SEND_P2P_MSG)
public class SendP2PMsgProcessor extends AbstractRequestProcessor {

    private IdFactory idFactory;
    private ConnectionManager connectionManager;
    private MessageWriter messageWriter;
    private SessionIdKeeper sessionIdKeeper;

    public SendP2PMsgProcessor() {
        this.idFactory = SnowflakeIdFactory.getInstance();
        this.connectionManager = DefaultConnectionManager.getInstance();
        this.messageWriter = DefaultMessageWriter.getInstance();
        this.sessionIdKeeper = DefaultSessionIdKeeper.getInstance();
    }

    @Override
    public Payload doProcess(ChannelHandlerContext ctx, Map<String, Object> params) {
        // transfer map to bean
        SendP2PMsgRequest request = mapToBean(params, SendP2PMsgRequest.class);

        Channel fromChannel = ctx.channel();
        Connection connection = connectionManager.get(fromChannel);
        Long userId = connection.getUserId();
        String userName = connection.getUserName();
        Long partnerId = request.getPartnerId();
        Long msgId = idFactory.nextId();
        List<Connection> partnerConnections = connectionManager.getList(partnerId);
        boolean success = true;
        // partner is not online
        if (CollectionUtil.isEmpty(partnerConnections)) {
            success = false;
            // save offline msg for partner
            saveOfflineMsg(userId, request, msgId);
        } else {
            Packet pushPacket = buildPushPacket(userId, userName, request);
            // push msg to all partner endpoints
            for (Connection conn : partnerConnections) {
                Channel toChannel = conn.getChannel();
                toChannel.writeAndFlush(pushPacket);
            }
        }
        Payload payload = success ?
                PayloadFactory.newSuccessPayload() :
                PayloadFactory.newErrorPayload(ResultCode.BIZ_FAIL.getCode(), "partner is not online");
        // save history msg
        saveHistoryMsg(userId, request, msgId);
        return payload;
    }


    private void saveOfflineMsg(Long userId, SendP2PMsgRequest request, Long msgId) {
        P2pMessage p2pMessage = getP2pMessage(userId, request, msgId);
        messageWriter.saveOfflineMsg(p2pMessage, request.getPartnerId());
    }

    private void saveHistoryMsg(Long userId, SendP2PMsgRequest request, Long msgId) {
        P2pMessage p2pMessage = getP2pMessage(userId, request, msgId);
        messageWriter.saveHistoryMsg(p2pMessage);
    }

    private P2pMessage getP2pMessage(Long userId, SendP2PMsgRequest request, Long msgId) {
        Long partnerId = request.getPartnerId();
        P2pMessage p2pMessage = new P2pMessage();
        p2pMessage.setMsgId(msgId);
        p2pMessage.setSessionId(sessionIdKeeper.p2pSessionId(userId, partnerId));
        p2pMessage.setCategory(MessageCategory.P2P.getType());
        p2pMessage.setType(MessageType.getEnum(request.getMessageType()));
        p2pMessage.setCreateTime(DateUtil.current(false));
        p2pMessage.setMsg(request.getMsg());
        p2pMessage.setUserId(userId);
        p2pMessage.setPartnerId(partnerId);
        p2pMessage.setMsg(request.getMsg());
        return p2pMessage;
    }

    /**
     * build push packet
     */
    private Packet buildPushPacket(Long userId, String userName, SendP2PMsgRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("partnerId", userId);
        params.put("partnerName", userName);
        params.put("messageType", request.getMessageType());
        params.put("msg", request.getMsg());
        Command pushMsgCommand = CommandFactory.newCommand(ImServiceName.PUSH_MSG, params);
        // create a new command packet
        return PacketFactory.newCmdPacket(pushMsgCommand);
    }

}
