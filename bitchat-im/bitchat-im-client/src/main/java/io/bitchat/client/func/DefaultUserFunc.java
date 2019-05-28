package io.bitchat.client.func;

import cn.hutool.core.lang.Assert;
import io.bitchat.core.Carrier;
import io.bitchat.core.Listener;
import io.bitchat.client.Client;
import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.MemoryIdFactory;
import io.bitchat.protocol.packet.Packet;
import io.bitchat.user.User;
import io.bitchat.transport.ListOnlineUserRequestPacket;
import io.bitchat.protocol.packet.CarrierPacket;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author houyi
 */
public class DefaultUserFunc implements UserFunc {

    private IdFactory idFactory = MemoryIdFactory.getInstance();

    private Client client;

    public DefaultUserFunc(Client client) {
        Assert.notNull(client, "client can not be null");
        this.client = client;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onlineUser(Listener<Carrier<List<User>>> listener) {
        ListOnlineUserRequestPacket request = ListOnlineUserRequestPacket.builder()
                .build();
        request.setId(idFactory.nextId());
        CompletableFuture<Packet> future = client.sendRequest(request);
        future.whenComplete(new BiConsumer<Packet, Throwable>() {
            @Override
            public void accept(Packet packet, Throwable throwable) {
                Carrier<List<User>> carrier;
                if (throwable != null) {
                    carrier = Carrier.<List<User>>builder().success(false).msg(throwable.getMessage()).build();
                } else {
                    CarrierPacket<List<User>> response = (CarrierPacket) packet;
                    carrier = Carrier.<List<User>>builder().success(response.isSuccess()).msg(response.getMsg()).data(response.getData()).build();
                }
                listener.onEvent(carrier);
            }
        });
    }

}
