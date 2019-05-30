package io.bitchat.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import io.bitchat.router.RouterServerAttr;

/**
 * <p>
 * A Main class that can be startup by jar or shell
 * </p>
 *
 * @author houyi
 */
public class ServerShell {

    public static void main(String[] args) {
        ServerStartupParameter param = new ServerStartupParameter();
        JCommander.newBuilder()
                .addObject(param)
                .build()
                .parse(args);
        ServerMode serverMode = ServerMode.getEnum(param.mode);
        RouterServerAttr routerServerAttr = RouterServerAttr.builder()
                .address(param.routerAddress)
                .port(param.routerPort)
                .build();
        Integer serverPort = param.serverPort;

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.serverMode(serverMode)
                .routerServerAttr(routerServerAttr)
                .start(serverPort);
    }


    private static class ServerStartupParameter {

        @Parameter(names = "-mode", description = "Server mode. 1 : standalone mode 2 : cluster mode. If null will use default mode: standalone")
        private Integer mode;

        @Parameter(names = "-serverPort", description = "Server port. If null will use default port: 8864")
        private Integer serverPort;

        @Parameter(names = "-routerAddress", description = "Router address.")
        private String routerAddress;

        @Parameter(names = "-routerPort", description = "Router port.")
        private Integer routerPort;

    }

}
