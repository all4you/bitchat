package io.bitchat.im.server.controller;


import io.bitchat.server.http.RenderType;
import io.bitchat.server.http.RequestMethod;
import io.bitchat.server.http.controller.Controller;
import io.bitchat.server.http.controller.Mapping;
import io.bitchat.server.http.maker.DefaultHtmlMaker;
import io.bitchat.server.http.maker.HtmlMaker;
import io.bitchat.server.http.maker.HtmlMakerEnum;
import io.bitchat.server.http.maker.HtmlMakerFactory;
import io.bitchat.server.http.util.HtmlContentUtil;
import io.bitchat.server.http.view.PageIndex;

/**
 * BaseController
 *
 * @author houyi
 */
@Controller(path = "/")
public class BaseController {

    @Mapping(requestMethod = RequestMethod.GET, renderType = RenderType.HTML)
    public String index() {
        HtmlMaker htmlMaker = HtmlMakerFactory.instance().build(HtmlMakerEnum.STRING, DefaultHtmlMaker.class);
        String htmlTpl = PageIndex.HTML;
        return HtmlContentUtil.getPageContent(htmlMaker, htmlTpl, null);
    }

}
