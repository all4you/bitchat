package io.bitchat.protocol;

import cn.hutool.core.bean.BeanUtil;
import io.bitchat.lang.constants.ResultCode;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * <p>
 * An abstract RequestProcessor
 * Every Class extends AbstractRequestProcessor should have a NoArgument Constructor
 * see {@link RequestHandler#initRequestProcessor()}
 * </p>
 * @author houyi
 */
@Slf4j
public abstract class AbstractRequestProcessor implements RequestProcessor {

    @Override
    public Payload process(ChannelHandlerContext ctx, Request request) {
        Payload payload = new Payload();
        try {
            return doProcess(ctx, request.getParams());
        } catch (Exception e) {
            payload.setErrorMsg(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
            log.error("process occurred error, cause={}", e.getMessage(), e);
        }
        return payload;
    }

    /**
     * do process
     *
     * @param params the request params
     * @return the response
     */
    public abstract Payload doProcess(ChannelHandlerContext ctx, Map<String, Object> params);

    /**
     * transfer the map to bean
     */
    protected <T> T  mapToBean(Map<String, Object> params, Class<T> beanType) {
        T bean = null;
        if (params != null) {
            // transfer the map to bean
            bean = BeanUtil.mapToBean(params, beanType, false);
        }
        return bean;
    }

}
