package io.bitchat.im.server.session;

import com.alibaba.fastjson.JSONObject;
import io.bitchat.server.session.DefaultSession;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author houyi
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class ImSession extends DefaultSession {

    /**
     * user name
     */
    private String userName;

    /**
     * the server address which the client connected to
     */
    private String serverAddress;

    /**
     * the server port which the client connected to
     */
    private Integer serverPort;

    public ImSession(String sessionId) {
        super(sessionId);
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        object.put("sessionId", sessionId());
        object.put("userId", userId());
        object.put("shortId", channelId().asShortText());
        object.put("longId", channelId().asLongText());
        object.put("channelType", channelType());
        object.put("userName", userName);
        object.put("serverAddress", serverAddress);
        object.put("serverPort", serverPort);
        return object.toJSONString();
    }
}
