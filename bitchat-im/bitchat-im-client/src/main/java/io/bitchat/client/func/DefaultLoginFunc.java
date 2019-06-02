package io.bitchat.client.func;

import cn.hutool.core.lang.Assert;
import io.bitchat.client.Client;
import io.bitchat.core.Carrier;
import io.bitchat.core.Listener;
import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.MemoryIdFactory;
import io.bitchat.protocol.packet.CarrierPacket;
import io.bitchat.protocol.packet.Packet;
import io.bitchat.transport.LoginRequestPacket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author houyi
 */
@Slf4j
public class DefaultLoginFunc implements LoginFunc {

    private IdFactory idFactory = MemoryIdFactory.getInstance();

    private Client client;

    public DefaultLoginFunc(Client client) {
        Assert.notNull(client, "client can not be null");
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void login(String userName, String password, Listener<Carrier<String>> listener) {
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
                    carrier = Carrier.<String>builder().success(false).msg(throwable.getMessage()).build();
                } else {
                    CarrierPacket<String> response = (CarrierPacket) packet;
                    carrier = Carrier.<String>builder().success(response.isSuccess()).msg(response.getMsg()).build();
                }
                listener.onEvent(carrier);
            }
        });
    }

}
