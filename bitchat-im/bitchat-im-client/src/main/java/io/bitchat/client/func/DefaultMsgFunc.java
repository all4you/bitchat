package io.bitchat.client.func;

import cn.hutool.core.lang.Assert;
import io.bitchat.client.Client;
import io.bitchat.core.Carrier;
import io.bitchat.core.Listener;
import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.MemoryIdFactory;
import io.bitchat.protocol.packet.Packet;
import io.bitchat.message.Message;
import io.bitchat.message.MessageType;
import io.bitchat.protocol.packet.CarrierPacket;
import io.bitchat.transport.P2pMsgRequestPacket;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author houyi
 */
@Slf4j
public class DefaultMsgFunc implements MsgFunc {

    private IdFactory idFactory = MemoryIdFactory.getInstance();

    private Client client;

    public DefaultMsgFunc(Client client) {
        Assert.notNull(client, "client can not be null");
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sendP2pMsg(Long partnerId, MessageType type, String msg, Listener<Carrier<String>> listener) {
        P2pMsgRequestPacket request = P2pMsgRequestPacket.builder()
                .partnerId(partnerId)
                .messageType(type.getType())
                .msg(msg)
                .build();
        request.setId(idFactory.nextId());
        CompletableFuture<Packet> future = client.sendRequest(request);
        future.whenComplete(new BiConsumer<Packet, Throwable>() {
            @Override
            public void accept(Packet packet, Throwable throwable) {
                Carrier<String> carrier;
                if (throwable != null) {
                    carrier = Carrier.<String>builder().success(false).msg(throwable.getMessage()).build();
                } else {
                    CarrierPacket<String> response = (CarrierPacket) packet;
                    carrier = Carrier.<String>builder().success(response.isSuccess()).msg(response.getMsg()).build();
                }
                listener.onEvent(carrier);
            }
        });
    }

    @Override
    public void sendGroupMsg(Long groupId, MessageType type, String msg, Listener<Carrier<String>> listener) {
        // TODO
    }

    @Override
    public void fetchP2pHistoryMsg(Long partnerId, Long currentMsgId, boolean fetchForward, Integer fetchSize, Listener<Carrier<List<Message>>> listener) {
        // TODO
    }

    @Override
    public void fetchGroupHistoryMsg(Long groupId, Long currentMsgId, boolean fetchForward, Integer fetchSize, Listener<Carrier<List<Message>>> listener) {
        // TODO
    }

    @Override
    public void fetchOfflineMsg(Listener<Carrier<List<Message>>> listener) {
        // TODO
    }

}
