package io.bitchat.server;

import io.bitchat.http.RenderType;
import io.bitchat.http.RequestMethod;
import io.bitchat.http.controller.Controller;
import io.bitchat.http.controller.Mapping;
import io.bitchat.server.channel.ChannelManager;
import io.bitchat.server.channel.ChannelWrapper;
import io.bitchat.server.channel.DefaultChannelManager;
import io.bitchat.server.session.DefaultSessionManager;
import io.bitchat.server.session.Session;
import io.bitchat.server.session.SessionManager;

import java.util.Collection;
import java.util.List;

/**
 * @author houyi
 */
@Controller(path = "/status")
public class StatusController {

    private ChannelManager channelManager = DefaultChannelManager.getInstance();

    private SessionManager sessionManager = DefaultSessionManager.getInstance();


    @Mapping(path = "/channels", requestMethod = RequestMethod.GET, renderType = RenderType.JSON)
    public List<ChannelWrapper> channelWrappers() {
        return channelManager.getAllChannelWrappers();
    }

    @Mapping(path = "/sessions", requestMethod = RequestMethod.GET, renderType = RenderType.JSON)
    public Collection<Session> sessions() {
        return sessionManager.getAllSessions();
    }

}
