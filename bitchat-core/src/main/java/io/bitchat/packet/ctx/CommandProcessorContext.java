package io.bitchat.packet.ctx;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import io.bitchat.core.init.InitAble;
import io.bitchat.core.init.InitOrder;
import io.bitchat.lang.config.BaseConfig;
import io.bitchat.lang.config.ConfigFactory;
import io.bitchat.lang.util.GenericsUtil;
import io.bitchat.packet.Command;
import io.bitchat.packet.processor.CommandProcessor;
import io.bitchat.packet.processor.Processor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * handle the command
 *
 * @author houyi
 */
@Slf4j
@InitOrder(2)
public class CommandProcessorContext implements InitAble {

    private static AtomicBoolean init = new AtomicBoolean();

    private static Map<String, CommandProcessor> processorHolder = new ConcurrentHashMap<>();

    private CommandProcessorContext() {

    }

    public static CommandProcessorContext getInstance() {
        return Singleton.get(CommandProcessorContext.class);
    }

    @Override
    public void init() {
        initCommandProcessor();
    }

    public void process(ChannelHandlerContext ctx, Command request) {
        String commandName = GenericsUtil.unifiedProcessorName(request.getCommandName());
        CommandProcessor processor = commandName == null ? null : processorHolder.get(commandName);
        if (processor == null) {
            log.warn("CommandProcessor not found with commandName={}", commandName);
            return;
        }
        processor.process(ctx, request);
    }

    private void initCommandProcessor() {
        if (!init.compareAndSet(false, true)) {
            return;
        }
        BaseConfig baseConfig = ConfigFactory.getConfig(BaseConfig.class);
        Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(baseConfig.basePackage(), CommandProcessor.class);
        if (CollectionUtil.isEmpty(classSet)) {
            log.warn("[CommandProcessorContext] No CommandProcessor found");
            return;
        }
        for (Class<?> clazz : classSet) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || !CommandProcessor.class.isAssignableFrom(clazz)) {
                continue;
            }
            try {
                // check whether the class has @Processor annotation
                // use name specified by @Processor first
                Processor processor = clazz.getAnnotation(Processor.class);
                String commandName = (processor != null && StrUtil.isNotBlank(processor.name())) ? GenericsUtil.unifiedProcessorName(processor.name()) : clazz.getName();
                cacheCommandProcessor(commandName, clazz);
            } catch (Exception e) {
                log.warn("[CommandProcessorContext] cacheCommandProcessor failed", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void cacheCommandProcessor(String commandName, Class clazz) {
        if (processorHolder.containsKey(commandName)) {
            log.warn("[CommandProcessorContext] [Warning] commandName=[{}], CommandProcessor=[{}] already exists, please check the CommandProcessor", commandName, clazz.getCanonicalName());
            return;
        }
        log.info("[CommandProcessorContext] Found commandName=[{}], CommandProcessor=[{}]", commandName, clazz.getCanonicalName());
        // Each implements Class of CommandProcessor should have a NoArgument Constructor
        CommandProcessor requestProcessor = Singleton.get((Class<? extends CommandProcessor>) clazz);
        processorHolder.putIfAbsent(commandName, requestProcessor);
    }

}
