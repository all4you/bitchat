package io.bitchat.core;

import io.bitchat.core.lang.util.GenericsUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * A config which holds the address and port of a server
 * </p>
 *
 * @author houyi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerAttr {
    private String address;
    private int port;

    public static ServerAttr getLocalServer(int serverPort) {
        return ServerAttr.builder()
                .address(GenericsUtil.getLocalIpV4())
                .port(serverPort)
                .build();
    }

}
