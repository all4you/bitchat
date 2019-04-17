package io.bitchat.core.lang.util;

import cn.hutool.core.util.NetUtil;

import java.util.LinkedHashSet;

/**
 * @author houyi
 */
public class GenericsUtil {

    /**
     * get local ip V4
     *
     * @return ipV4
     */
    public static String getLocalIpV4() {
        LinkedHashSet<String> ipV4Set = NetUtil.localIpv4s();
        return ipV4Set.isEmpty() ? "" : ipV4Set.toArray()[0].toString();
    }

}
