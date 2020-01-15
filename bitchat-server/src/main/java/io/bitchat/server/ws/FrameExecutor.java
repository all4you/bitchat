package io.bitchat.server.ws;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.bitchat.core.executor.AbstractExecutor;
import io.bitchat.lang.BeanMapper;
import io.bitchat.packet.Payload;
import io.bitchat.packet.ctx.RequestProcessorContext;
import io.bitchat.packet.Request;
import io.bitchat.ws.Frame;
import io.bitchat.ws.FrameFactory;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyi
 */
@Slf4j
public class FrameExecutor extends AbstractExecutor<Frame> {

    private RequestProcessorContext processorContext;

    private FrameExecutor() {
        this.processorContext = RequestProcessorContext.getInstance();
    }

    public static FrameExecutor getInstance() {
        return Singleton.get(FrameExecutor.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Frame doExecute(Object... request) {
        ChannelHandlerContext ctx = (ChannelHandlerContext) request[0];
        Frame frame = (Frame) request[1];
        Request req = BeanMapper.map(frame, Request.class);
        String paramJson = frame.getParamJson();
        if (StrUtil.isNotBlank(paramJson)) {
            // 将json转换成Map
            req.setParams(JSON.parseObject(paramJson, Map.class));
        }
        Payload payload = processorContext.process(ctx, req);
        return FrameFactory.newResponseFrame(payload, frame.getId());
    }

}
