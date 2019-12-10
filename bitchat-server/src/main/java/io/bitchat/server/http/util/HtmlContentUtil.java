package io.bitchat.server.http.util;

import io.bitchat.server.http.maker.HtmlMaker;
import io.bitchat.lang.config.BaseConfig;
import io.bitchat.lang.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyi
 **/
@Slf4j
public class HtmlContentUtil {

    private HtmlContentUtil() {

    }

    /**
     * 获取页面内容
     *
     * @param htmlMaker    htmlMaker
     * @param htmlTemplate html模板
     * @param contentMap   参数
     * @return 页面内容
     */
    public static String getPageContent(HtmlMaker htmlMaker, String htmlTemplate, Map<String, Object> contentMap) {
        try {
            return htmlMaker.make(htmlTemplate, contentMap);
        } catch (Exception e) {
            log.error("getPageContent Error,cause:", e);
        }
        return ConfigFactory.getConfig(BaseConfig.class).serverInternalError();
    }


}
