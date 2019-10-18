package io.bitchat.im.client.func;

import io.bitchat.im.BaseResult;
import io.bitchat.protocol.Packet;
import io.bitchat.protocol.Payload;

/**
 * @author houyi
 */
public interface BaseFunc {

    Payload request(Packet packet);

    BaseResult transferResult(Payload payload);

}
