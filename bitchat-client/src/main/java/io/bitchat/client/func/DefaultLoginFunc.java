package io.bitchat.client.func;

import cn.hutool.core.lang.Assert;
import io.bitchat.core.Carrier;
import io.bitchat.core.Listener;
import io.bitchat.core.client.Client;
import io.bitchat.core.lang.id.IdFactory;
import io.bitchat.core.lang.id.StandaloneMemoryIdFactory;
import io.bitchat.core.protocol.packet.Packet;
import io.bitchat.protocol.packet.LoginRequestPacket;
import io.bitchat.protocol.packet.MsgCarrierPacket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author houyi
 */
@Slf4j
public class DefaultLoginFunc implements LoginFunc {

    private volatile boolean login = false;

    private IdFactory idFactory = StandaloneMemoryIdFactory.getInstance();

    private Client client;

    public DefaultLoginFunc(Client client) {
        Assert.notNull(client, "client can not be null");
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void login(String userName, String password, Listener<Carrier<String>> listener) {
        if (login) {
            log.debug("Already logged in");
            Carrier<String> carrier = Carrier.<String>builder().success(false).errorMsg("Already logged in").build();
            listener.onEvent(carrier);
            return;
        }
        LoginRequestPacket request = LoginRequestPacket.builder()
                .userName(userName)
                .password(password)
                .build();
        request.setId(idFactory.nextId());
        CompletableFuture<Packet> future = client.sendRequest(request);
        future.whenComplete(new BiConsumer<Packet, Throwable>() {
            @Override
            public void accept(Packet packet, Throwable throwable) {
                Carrier<String> carrier;
                if (throwable != null) {
                    carrier = Carrier.<String>builder().success(false).errorMsg(throwable.getMessage()).build();
                } else {
                    MsgCarrierPacket<String> response = (MsgCarrierPacket) packet;
                    carrier = Carrier.<String>builder().success(response.isSuccess()).errorMsg(response.getMsg()).build();
                    login = response.isSuccess();
                }
                listener.onEvent(carrier);
            }
        });
    }

}
