package io.bitchat.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import io.bitchat.core.router.RouterServerAttr;
import io.bitchat.core.server.Server;
import io.bitchat.core.server.ServerFactory;
import io.bitchat.core.server.ServerMode;

/**
 * @author houyi
 */
public class ServerBootstrap {

    public static void main(String[] args) {
        Server server = getServer(args);
        server.start();
    }

    private static Server getServer(String[] args) {
        ServerStartupParameter param = new ServerStartupParameter();
        JCommander.newBuilder()
                .addObject(param)
                .build()
                .parse(args);
        Integer mode = param.mode;
        ServerFactory factory = SimpleServerFactory.getInstance();
        return mode == null || mode == ServerMode.STAND_ALONE
                ? factory.newServer(param.serverPort)
                : factory.newServer(RouterServerAttr.builder().address(param.routerAddress).port(param.routerPort).build(), param.serverPort);
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