package io.bitchat.core.server;

/**
 * @author houyi
 */
public class ServerAttrHolder {

    private static ServerAttr serverAttr;

    public static void put(ServerAttr serverAttr) {
        ServerAttrHolder.serverAttr = serverAttr;
    }

    public static ServerAttr get() {
        return ServerAttrHolder.serverAttr;
    }

}
