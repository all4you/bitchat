package io.bitchat.core.lang.config;

import org.aeonbits.owner.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * A Config Factory which can
 * provide singleton Config instance
 * </p>
 *
 * @author houyi
 */
public class ConfigFactory {

    private static Map<Class<? extends Config>, Object> pool = new ConcurrentHashMap<>();

    private ConfigFactory() {

    }

    @SuppressWarnings("unchecked")
    public static <T extends Config> T getConfig(Class<? extends T> clazz) {
        T config = (T) pool.get(clazz);
        if (null == config) {
            synchronized (ConfigFactory.class) {
                config = (T) pool.get(clazz);
                if (null == config) {
                    config = org.aeonbits.owner.ConfigFactory.create(clazz);
                    pool.putIfAbsent(clazz, config);
                }
            }
        }
        return config;
    }

}
