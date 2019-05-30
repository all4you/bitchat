package io.bitchat.server;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author houyi
 */
@Getter
public enum ServerMode {

    /**
     * standalone mode
     */
    STAND_ALONE(1, "standalone"),

    /**
     * cluster mode
     */
    CLUSTER(2, "cluster");

    private int mode;
    private String text;

    ServerMode(int mode, String text) {
        this.mode = mode;
        this.text = text;
    }

    public static ServerMode getEnum(Integer mode) {
        return mode == null ? null : Arrays.stream(values())
                .filter(t -> t.getMode() == mode)
                .findFirst()
                .orElse(null);
    }

    public static String getText(Integer mode) {
        ServerMode anEnum = getEnum(mode);
        return anEnum == null ? null : anEnum.getText();
    }

}
