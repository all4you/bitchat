package io.bitchat.http.view;

/**
 * @author houyi
 **/
public interface HtmlKeyHolder {

    /**
     * 未转义
     */
    String START_NO_ESCAPE = "#[";

    /**
     * 对[转义
     */
    String START_ESCAPE = "#\\[";

    String END = "]";

}
