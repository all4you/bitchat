package io.bitchat.server.rest;

import io.bitchat.http.RenderType;
import io.bitchat.http.RequestMethod;
import io.bitchat.http.controller.Controller;
import io.bitchat.http.controller.Mapping;
import io.bitchat.http.controller.Param;
import io.bitchat.packet.Payload;
import io.bitchat.server.session.SessionHelper;

/**
 * @author houyi
 */
@Controller(path = "/push")
public class PushController {


    @Mapping(path = "/session", requestMethod = RequestMethod.POST, renderType = RenderType.JSON)
    public Payload pushToSession(@Param(key="sessionId", notBlank = true) String sessionId,
                                 @Param(key="commandName", notBlank = true) String commandName,
                                 @Param(key="content", notBlank = true) String content) {
        return SessionHelper.push(sessionId, commandName, content);
    }

}
