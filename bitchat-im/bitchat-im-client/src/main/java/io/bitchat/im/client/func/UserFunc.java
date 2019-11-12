package io.bitchat.im.client.func;

import cn.hutool.core.lang.Assert;
import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.SnowflakeIdFactory;
import io.bitchat.im.ImServiceName;
import io.bitchat.im.ListResult;
import io.bitchat.im.user.User;
import io.bitchat.protocol.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author houyi
 */
public class UserFunc {

    private IdFactory idFactory;
    private BaseFunc baseFunc;

    public UserFunc(BaseFunc baseFunc) {
        Assert.notNull(baseFunc, "baseFunc can not be null");
        this.baseFunc = baseFunc;
        this.idFactory = SnowflakeIdFactory.getInstance(2L);
    }


    @SuppressWarnings("unchecked")
    public ListResult<User> getOnlineFriends(long userId) {
        Map<String, Object> params = buildParams(userId);
        Request request = RequestFactory.newRequest(ImServiceName.GET_ONLINE_USER, null, params);
        Packet packet = PacketFactory.newRequestPacket(request, idFactory.nextId());
        Payload payload = baseFunc.request(packet);
        ListResult<User> listResult = new ListResult<>();
        if (payload.isSuccess()) {
            listResult.setContent((List<User>) payload.getResult());
        } else {
            listResult.setErrorMessage(payload.getCode(), payload.getMsg());
        }
        return listResult;
    }

    private Map<String, Object> buildParams(long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return params;
    }

}
