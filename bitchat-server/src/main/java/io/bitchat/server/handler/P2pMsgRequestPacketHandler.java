package io.bitchat.server.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import io.bitchat.core.bean.Autowired;
import io.bitchat.core.bean.Bean;
import io.bitchat.core.connection.Connection;
import io.bitchat.core.connection.ConnectionManager;
import io.bitchat.core.lang.constants.PacketSymbols;
import io.bitchat.core.lang.constants.ResultCode;
import io.bitchat.core.lang.enums.MessageCategory;
import io.bitchat.core.lang.id.IdFactory;
import io.bitchat.core.lang.id.SnowflakeIdFactory;
import io.bitchat.core.message.MessageWriter;
import io.bitchat.core.message.P2pMessage;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.core.server.SessionIdKeeper;
import io.bitchat.protocol.packet.CarrierPacket;
import io.bitchat.protocol.packet.P2pMsgPushPacket;
import io.bitchat.protocol.packet.P2pMsgRequestPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author houyi
 */
@Slf4j
@Bean
public class P2pMsgRequestPacketHandler implements PacketHandler<P2pMsgRequestPacket, CarrierPacket<String>> {

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private MessageWriter messageWriter;

    @Autowired
    private SessionIdKeeper sessionIdKeeper;

    private IdFactory idFactory = SnowflakeIdFactory.getInstance();

    @Override
    public int symbol() {
        return PacketSymbols.P2P_MSG_REQUEST_PACKET;
    }

    @Override
    public CarrierPacket<String> handle(ChannelHandlerContext ctx, P2pMsgRequestPacket packet) {
        CarrierPacket<String> response;
        Channel fromChannel = ctx.channel();
        Connection connection = connectionManager.get(fromChannel);
        Long userId = connection.getUserId();
        String userName = connection.getUserName();
        Long partnerId = packet.getPartnerId();
        Long msgId = idFactory.nextId();
        List<Connection> partnerConnections = connectionManager.getList(partnerId);
        boolean success = true;
        String msg = "success";
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
        response = CarrierPacket.<String>builder()
                .code(ResultCode.SUCCESS)
                .success(success)
                .msg(msg)
                .build();
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
        p2pMessage.setType(packet.getType());
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
        pushPacket.setType(packet.getType());
        pushPacket.setMsg(packet.getMsg());
        return pushPacket;
    }

}
