package io.bitchat.protocol;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.lang.Singleton;
import io.bitchat.core.lang.config.BaseConfig;
import io.bitchat.core.lang.config.ConfigFactory;
import io.bitchat.core.lang.init.InitAble;
import io.bitchat.core.lang.init.InitOrder;
import io.bitchat.core.protocol.packet.Packet;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.core.protocol.packet.PacketRecognizer;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * A default PacketRecognizer implement
 * </p>
 *
 * @author houyi
 */
@Slf4j
@InitOrder(2)
public class DefaultPacketRecognizer implements PacketRecognizer, InitAble {

    private static Map<Integer, Class<? extends Packet>> packetHolder = new ConcurrentHashMap<>();

    private static Map<Integer, Class<? extends PacketHandler>> packetHandlerHolder = new ConcurrentHashMap<>();

    private DefaultPacketRecognizer() {

    }

    public static PacketRecognizer getInstance() {
        return Singleton.get(DefaultPacketRecognizer.class);
    }

    @Override
    public void init() {
        try {
            initPacket();
            initPacketHandler();
        } catch (Exception e) {
            log.warn("[DefaultPacketRecognizer] init failed", e);
        }
    }

    @Override
    public Class<? extends Packet> packet(int symbol) {
        return packetHolder.get(symbol);
    }

    @Override
    public Class<? extends PacketHandler> packetHandler(int symbol) {
        return packetHandlerHolder.get(symbol);
    }

    private void initPacket() {
        BaseConfig baseConfig = ConfigFactory.getConfig(BaseConfig.class);
        Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(baseConfig.basePackage(), Packet.class);
        if (CollectionUtil.isEmpty(classSet)) {
            log.warn("[DefaultPacketRecognizer] No Packet found");
            return;
        }
        for (Class<?> clazz : classSet) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || !Packet.class.isAssignableFrom(clazz)) {
                continue;
            }
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                Packet packet = (Packet) constructor.newInstance();
                cachePacket(packet);
            } catch (Exception e) {
                log.warn("[DefaultPacketRecognizer] cachePacket failed", e);
            }
        }
    }

    private void cachePacket(Packet packet) {
        int symbol = packet.symbol();
        log.info("[DefaultPacketRecognizer] Found symbol=[{}], Packet=[{}]", symbol, packet.getClass().getCanonicalName());
        if (packetHolder.containsKey(symbol)) {
            log.warn("[DefaultPacketRecognizer] [Warning] symbol=[{}],Packet=[{}] already exists, please check the Packet", symbol, packet.getClass().getCanonicalName());
            return;
        }
        packetHolder.putIfAbsent(symbol, packet.getClass());
    }

    private void initPacketHandler() {
        BaseConfig baseConfig = ConfigFactory.getConfig(BaseConfig.class);
        Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(baseConfig.basePackage(), PacketHandler.class);
        if (CollectionUtil.isEmpty(classSet)) {
            log.warn("[DefaultPacketRecognizer] No PacketHandler found");
            return;
        }
        for (Class<?> clazz : classSet) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || !PacketHandler.class.isAssignableFrom(clazz)) {
                continue;
            }
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                PacketHandler packetHandler = (PacketHandler) constructor.newInstance();
                cachePacketHandler(packetHandler);
            } catch (Exception e) {
                log.warn("[DefaultPacketRecognizer] cachePacketHandler failed", e);
            }
        }
    }

    private void cachePacketHandler(PacketHandler packetHandler) {
        int symbol = packetHandler.symbol();
        log.info("[DefaultPacketRecognizer] Found symbol=[{}], PacketHandler=[{}]", symbol, packetHandler.getClass().getCanonicalName());
        if (packetHandlerHolder.containsKey(symbol)) {
            log.warn("[DefaultPacketRecognizer] [Warning] symbol=[{}], PacketHandler=[{}] already exists, please check the PacketHandler", symbol, packetHandler.getClass().getCanonicalName());
            return;
        }
        packetHandlerHolder.putIfAbsent(symbol, packetHandler.getClass());
    }


}
