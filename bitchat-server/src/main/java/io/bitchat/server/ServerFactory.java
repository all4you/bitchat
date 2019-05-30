package io.bitchat.server;

import io.bitchat.protocol.PacketRecognizer;
import io.bitchat.protocol.SerializerChooser;
import io.bitchat.router.RouterServerAttr;

/**
 * <p>
 * A server factory
 * </p>
 *
 * @author houyi
 */
public interface ServerFactory {

    /**
     * create a standalone server
     *
     * @param serverPort the server port
     * @return the server
     */
    Server newServer(Integer serverPort);

    /**
     * create a standalone server
     *
     * @param serverPort      the server port
     * @param chooser         the chooser
     * @param recognizer      the recognizer
     * @param channelListener the channelListener
     * @return the server
     */
    Server newServer(Integer serverPort, SerializerChooser chooser, PacketRecognizer recognizer, ChannelListener channelListener);

    /**
     * create a cluster server
     *
     * @param serverPort       the server port
     * @param routerServerAttr the router attr
     * @return the server
     */
    Server newClusterServer(Integer serverPort, RouterServerAttr routerServerAttr);

    /**
     * create a cluster server
     *
     * @param serverPort       the server port
     * @param chooser          the chooser
     * @param recognizer       the recognizer
     * @param channelListener  the channelListener
     * @param routerServerAttr the router attr
     * @return the server
     */
    Server newClusterServer(Integer serverPort, SerializerChooser chooser, PacketRecognizer recognizer, ChannelListener channelListener, RouterServerAttr routerServerAttr);

    /**
     * get current server
     *
     * @return current server
     */
    Server currentServer();

}
