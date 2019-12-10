package io.bitchat.protocol;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import io.bitchat.core.init.InitAble;
import io.bitchat.core.init.InitOrder;
import io.bitchat.lang.config.BaseConfig;
import io.bitchat.lang.config.ConfigFactory;
import io.bitchat.lang.constants.ResultCode;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * handle the request
 *
 * @author houyi
 */
@Slf4j
@InitOrder(1)
public class RequestHandler implements InitAble {

    private static AtomicBoolean init = new AtomicBoolean();

    private static Map<String, RequestProcessor> processorHolder = new ConcurrentHashMap<>();

    private RequestHandler() {

    }

    public static RequestHandler getInstance() {
        return Singleton.get(RequestHandler.class);
    }

    @Override
    public void init() {
        initRequestProcessor();
    }

    public Payload handle(ChannelHandlerContext ctx, Request request) {
        String serviceName = request.getServiceName();
        RequestProcessor processor = processorHolder.get(serviceName);
        if (processor == null) {
            return PayloadFactory.newErrorPayload(ResultCode.RESOURCE_NOT_FOUND.getCode(), StrFormatter.format("RequestProcessor not found with serviceName={}", serviceName));
        }
        return processor.process(ctx, request);
    }

    private void initRequestProcessor() {
        if (!init.compareAndSet(false, true)) {
            return;
        }
        BaseConfig baseConfig = ConfigFactory.getConfig(BaseConfig.class);
        Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(baseConfig.basePackage(), RequestProcessor.class);
        if (CollectionUtil.isEmpty(classSet)) {
            log.warn("[RequestHandler] No RequestProcessor found");
            return;
        }
        for (Class<?> clazz : classSet) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || !RequestProcessor.class.isAssignableFrom(clazz)) {
                continue;
            }
            try {
                // check whether the class has @Processor annotation
                // use name specified by @Processor first
                Processor processor = clazz.getAnnotation(Processor.class);
                String serviceName = (processor != null && StrUtil.isNotBlank(processor.name())) ? processor.name() : clazz.getName();
                cacheRequestProcessor(serviceName, clazz);
            } catch (Exception e) {
                log.warn("[RequestHandler] cacheRequestProcessor failed", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void cacheRequestProcessor(String serviceName, Class clazz) {
        if (processorHolder.containsKey(serviceName)) {
            log.warn("[RequestHandler] [Warning] serviceName=[{}], RequestProcessor=[{}] already exists, please check the RequestProcessor", serviceName, clazz.getCanonicalName());
            return;
        }
        log.info("[RequestHandler] Found serviceName=[{}], RequestProcessor=[{}]", serviceName, clazz.getCanonicalName());
        // Each implements Class of RequestProcess should have a NoArgument Constructor
        RequestProcessor requestProcessor = Singleton.get((Class<? extends RequestProcessor>) clazz);
        processorHolder.putIfAbsent(serviceName, requestProcessor);
    }

}
