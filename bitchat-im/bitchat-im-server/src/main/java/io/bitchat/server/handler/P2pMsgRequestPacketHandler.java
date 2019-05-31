package io.bitchat.server.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import io.bitchat.core.bean.Autowired;
import io.bitchat.core.bean.Bean;
import io.bitchat.connection.Connection;
import io.bitchat.connection.ConnectionManager;
import io.bitchat.message.MessageCategory;
import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.SnowflakeIdFactory;
import io.bitchat.message.MessageType;
import io.bitchat.message.MessageWriter;
import io.bitchat.message.P2pMessage;
import io.bitchat.protocol.packet.PacketHandler;
import io.bitchat.protocol.packet.PacketSymbol;
import io.bitchat.server.SessionIdKeeper;
import io.bitchat.protocol.packet.CarrierPacket;
import io.bitchat.transport.P2pMsgPushPacket;
import io.bitchat.transport.P2pMsgRequestPacket;
import io.bitchat.transport.PacketSymbols;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author houyi
 */
@Slf4j
@Bean
@PacketSymbol(PacketSymbols.P2P_MSG_REQUEST_PACKET)
public class P2pMsgRequestPacketHandler implements PacketHandler<P2pMsgRequestPacket, CarrierPacket<String>> {

    private ConnectionManager connectionManager;

    private MessageWriter messageWriter;

    private SessionIdKeeper sessionIdKeeper;

    @Autowired
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Autowired
    public void setMessageWriter(MessageWriter messageWriter) {
        this.messageWriter = messageWriter;
    }

    @Autowired
    public void setSessionIdKeeper(SessionIdKeeper sessionIdKeeper) {
        this.sessionIdKeeper = sessionIdKeeper;
    }

    private IdFactory idFactory = SnowflakeIdFactory.getInstance();

    @Override
    public CarrierPacket<String> handle(ChannelHandlerContext ctx, P2pMsgRequestPacket packet) {
        Channel fromChannel = ctx.channel();
        Connection connection = connectionManager.get(fromChannel);
        Long userId = connection.getUserId();
        String userName = connection.getUserName();
        Long partnerId = packet.getPartnerId();
        Long msgId = idFactory.nextId();
        List<Connection> partnerConnections = connectionManager.getList(partnerId);
        boolean success = true;
        String msg = "SUCCESS";
        // partner is not online
        if (CollectionUtil.isEmpty(partnerConnections)) {
            success = false;
            msg = "partner is not online";
            // save offline msg for partner
            saveOfflineMsg(userId, packet, msgId);
        } else {
            P2pMsgPushPacket pushPacket = getPushPacket(userId, userName, packet);
            // push msg to all partner endpoints
            for (Connection conn : partnerConnections) {
                Channel toChannel = conn.getChannel();
                toChannel.writeAndFlush(pushPacket);
            }
        }
        CarrierPacket<String> response = CarrierPacket.getStringCarrierPacket(success, msg, null);
        response.setId(packet.getId());
        // save history msg
        saveHistoryMsg(userId, packet, msgId);
        return response;
    }

    private void saveOfflineMsg(Long userId, P2pMsgRequestPacket packet, Long msgId) {
        P2pMessage p2pMessage = getP2pMessage(userId, packet, msgId);
        messageWriter.saveOfflineMsg(p2pMessage, packet.getPartnerId());
    }

    private void saveHistoryMsg(Long userId, P2pMsgRequestPacket packet, Long msgId) {
        P2pMessage p2pMessage = getP2pMessage(userId, packet, msgId);
        messageWriter.saveHistoryMsg(p2pMessage);
    }

    private P2pMessage getP2pMessage(Long userId, P2pMsgRequestPacket packet, Long msgId) {
        Long partnerId = packet.getPartnerId();
        P2pMessage p2pMessage = new P2pMessage();
        p2pMessage.setMsgId(msgId);
        p2pMessage.setSessionId(sessionIdKeeper.p2pSessionId(userId, partnerId));
        p2pMessage.setCategory(MessageCategory.P2P.getType());
        p2pMessage.setType(MessageType.getEnum(packet.getMessageType()));
        p2pMessage.setCreateTime(DateUtil.current(false));
        p2pMessage.setMsg(packet.getMsg());
        p2pMessage.setUserId(userId);
        p2pMessage.setPartnerId(partnerId);
        p2pMessage.setMsg(packet.getMsg());
        return p2pMessage;
    }

    private P2pMsgPushPacket getPushPacket(Long userId, String userName, P2pMsgRequestPacket packet) {
        P2pMsgPushPacket pushPacket = new P2pMsgPushPacket();
        pushPacket.setPartnerId(userId);
        pushPacket.setPartnerName(userName);
        pushPacket.setMessageType(packet.getMessageType());
        pushPacket.setMsg(packet.getMsg());
        return pushPacket;
    }

}
