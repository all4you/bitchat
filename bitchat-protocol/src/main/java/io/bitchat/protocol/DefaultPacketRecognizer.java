package io.bitchat.protocol;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.lang.Singleton;
import io.bitchat.core.lang.config.BaseConfig;
import io.bitchat.core.lang.config.ConfigFactory;
import io.bitchat.core.init.InitAble;
import io.bitchat.core.init.InitOrder;
import io.bitchat.protocol.packet.Packet;
import io.bitchat.protocol.packet.PacketHandler;
import io.bitchat.protocol.packet.PacketSymbol;
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

    private static Map<Integer, PacketHandler> packetHandlerHolder = new ConcurrentHashMap<>();

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
    public PacketHandler packetHandler(int symbol) {
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
        Set<Class<?>> classSet = ClassScaner.scanPackageByAnnotation(baseConfig.basePackage(), PacketSymbol.class);
        if (CollectionUtil.isEmpty(classSet)) {
            log.warn("[DefaultPacketRecognizer] No PacketHandler found");
            return;
        }
        for (Class<?> clazz : classSet) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || !PacketHandler.class.isAssignableFrom(clazz)) {
                continue;
            }
            PacketSymbol packetSymbol = clazz.getAnnotation(PacketSymbol.class);
            if (packetSymbol == null) {
                continue;
            }
            try {
                cachePacketHandler(packetSymbol, clazz);
            } catch (Exception e) {
                log.warn("[DefaultPacketRecognizer] cachePacketHandler failed", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void cachePacketHandler(PacketSymbol packetSymbol, Class clazz) {
        int symbol = packetSymbol.value();
        log.info("[DefaultPacketRecognizer] Found symbol=[{}], PacketHandler=[{}]", symbol, clazz.getCanonicalName());
        if (packetHandlerHolder.containsKey(symbol)) {
            log.warn("[DefaultPacketRecognizer] [Warning] symbol=[{}], PacketHandler=[{}] already exists, please check the PacketHandler", symbol, clazz.getCanonicalName());
            return;
        }
        PacketHandler packetHandler = Singleton.get((Class<? extends PacketHandler>) clazz);
        packetHandlerHolder.putIfAbsent(symbol, packetHandler);
    }


}
