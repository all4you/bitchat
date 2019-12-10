package io.bitchat.im.server.controller;


import io.bitchat.http.RenderType;
import io.bitchat.http.RequestMethod;
import io.bitchat.http.controller.Controller;
import io.bitchat.http.controller.Mapping;
import io.bitchat.http.maker.DefaultHtmlMaker;
import io.bitchat.http.maker.HtmlMaker;
import io.bitchat.http.maker.HtmlMakerEnum;
import io.bitchat.http.maker.HtmlMakerFactory;
import io.bitchat.http.util.HtmlContentUtil;
import io.bitchat.http.view.PageIndex;

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
