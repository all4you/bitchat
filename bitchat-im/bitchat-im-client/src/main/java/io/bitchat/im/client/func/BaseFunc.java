package io.bitchat.im.client.func;

import cn.hutool.core.lang.Assert;
import io.bitchat.client.Client;
import io.bitchat.im.BaseResult;
import io.bitchat.lang.constants.ResultCode;
import io.bitchat.packet.Packet;
import io.bitchat.packet.Payload;
import io.bitchat.packet.factory.PayloadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author houyi
 */
@Slf4j
public class BaseFunc {

    private Client client;

    public BaseFunc(Client client) {
        Assert.notNull(client, "client can not be null");
        this.client = client;
    }

    public Payload request(Packet packet) {
        Payload payload;
        try {
            CompletableFuture<Packet> future = client.sendRequest(packet);
            payload = future.get(3, TimeUnit.SECONDS).getPayload();
        } catch (Exception e) {
            payload = PayloadFactory.newErrorPayload(ResultCode.BIZ_FAIL.getCode(), ResultCode.BIZ_FAIL.getMessage());
            log.error("request error,cause={}", e.getMessage(), e);
        }
        return payload;
    }

    public BaseResult transferResult(Payload payload) {
        BaseResult baseResult = new BaseResult();
        baseResult.setSuccess(payload.isSuccess());
        baseResult.setErrorCode(payload.getCode());
        baseResult.setErrorMsg(payload.getMsg());
        return baseResult;
    }
}
