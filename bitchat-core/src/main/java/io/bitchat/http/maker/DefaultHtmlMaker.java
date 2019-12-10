package io.bitchat.http.maker;

import cn.hutool.core.collection.CollectionUtil;
import io.bitchat.http.view.HtmlKeyHolder;

import java.util.Map;

/**
 * 默认的HtmlMaker，只处理字符串
 *
 * @author houyi
 **/
public class DefaultHtmlMaker implements HtmlMaker {

    @Override
    public String make(String htmlTemplate, Map<String, Object> contentMap) {
        String html = htmlTemplate;
        if (CollectionUtil.isNotEmpty(contentMap)) {
            for (Map.Entry<String, Object> entry : contentMap.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                if (val instanceof String) {
                    html = html.replaceAll(HtmlKeyHolder.START_ESCAPE + key + HtmlKeyHolder.END, val.toString());
                }
            }
        }
        return html;
    }

}
