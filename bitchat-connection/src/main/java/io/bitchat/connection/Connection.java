package io.bitchat.connection;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * A connection is an entity which combines userId and clientId.
 * Each Client has an unique clientId.
 * A clientId stands for an endpoint.
 * One userId can mapping to multiple clientIds.
 * </p>
 *
 * @author houyi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Connection {

    /**
     * user id
     * userId <===> List<channel>
     */
    private Long userId;

    /**
     * user name
     */
    private String userName;

    /**
     * unique client id
     * clientId <===> channel
     */
    private String clientId;

    /**
     * client channel
     */
    private Channel channel;

    /**
     * the server address which the client connected to
     */
    private String serverAddress;

    /**
     * the server port which the client connected to
     */
    private Integer serverPort;

}
