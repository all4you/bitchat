package io.bitchat.core.lang.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author houyi
 */
@Slf4j
public class ConfigTest {

    @Test
    public void testConfig() {
        BaseConfig baseConfig1 = ConfigFactory.getConfig(BaseConfig.class);
        BaseConfig baseConfig2 = ConfigFactory.getConfig(BaseConfig.class);
        log.info("baseConfig1={}, basePackage={}", baseConfig1.getClass(), baseConfig1.basePackage());
        log.info("baseConfig2={}, basePackage={}", baseConfig2.getClass(), baseConfig2.basePackage());
    }

}
