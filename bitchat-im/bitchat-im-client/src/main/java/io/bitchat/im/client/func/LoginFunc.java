package io.bitchat.im.client.func;

import cn.hutool.core.lang.Assert;
import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.SnowflakeIdFactory;
import io.bitchat.im.BaseResult;
import io.bitchat.im.ImServiceName;
import io.bitchat.packet.*;
import io.bitchat.packet.factory.PacketFactory;
import io.bitchat.packet.factory.RequestFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author houyi
 */
@Slf4j
public class LoginFunc {

    private IdFactory idFactory;
    private BaseFunc baseFunc;

    public LoginFunc(BaseFunc baseFunc) {
        Assert.notNull(baseFunc, "baseFunc can not be null");
        this.baseFunc = baseFunc;
        this.idFactory = SnowflakeIdFactory.getInstance(1L);
    }

    public BaseResult login(String userName, String password) {
        Map<String, Object> params = buildParams(userName, password);
        Request request = RequestFactory.newRequest(ImServiceName.LOGIN, null, params);
        Packet packet = PacketFactory.newRequestPacket(request, idFactory.nextId());
        Payload payload = baseFunc.request(packet);
        return baseFunc.transferResult(payload);
    }

    private Map<String, Object> buildParams(String userName, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("password", password);
        return params;
    }

}
