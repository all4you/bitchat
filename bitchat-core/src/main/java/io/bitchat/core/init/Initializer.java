package io.bitchat.core.init;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import io.bitchat.core.lang.config.BaseConfig;
import io.bitchat.core.lang.config.ConfigFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * A InitAble executor
 * <p>
 * The init method of this class
 * should be called before Server
 * startup to init all the InitAble
 * classes
 * </p>
 *
 * @author houyi
 */
@Slf4j
public final class Initializer {

    private static AtomicBoolean initialized = new AtomicBoolean(false);

    private Initializer() {

    }

    /**
     * Scan all InitAbles
     * and do the init method
     */
    public static void init() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }
        try {
            BaseConfig baseConfig = ConfigFactory.getConfig(BaseConfig.class);
            Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(baseConfig.basePackage(), InitAble.class);
            if (CollectionUtil.isEmpty(classSet)) {
                log.info("[Initializer] No InitAble found");
                return;
            }
            List<OrderWrapper> initList = new ArrayList<>();
            for (Class<?> clazz : classSet) {
                if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || !InitAble.class.isAssignableFrom(clazz)) {
                    continue;
                }
                try {
                    Constructor<?> constructor = clazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    InitAble initAble = (InitAble) constructor.newInstance();
                    log.info("[Initializer] Found InitAble=[{}]", initAble.getClass().getCanonicalName());
                    insertSorted(initList, initAble);
                } catch (Exception e) {
                    log.warn("[Initializer] Prepare InitAble failed", e);
                }
            }
            for (OrderWrapper wrapper : initList) {
                // execute the init method of initAble
                wrapper.initAble.init();
                log.info("[Initializer] Initialized [{}] with order={}", wrapper.initAble.getClass().getCanonicalName(), wrapper.order);
            }
        } catch (Exception e) {
            log.warn("[Initializer] Init failed", e);
        } catch (Error error) {
            log.warn("[Initializer] Init failed with fatal error", error);
            throw error;
        }
    }

    private static void insertSorted(List<OrderWrapper> list, InitAble initAble) {
        int order = resolveOrder(initAble);
        int idx = 0;
        for (; idx < list.size(); idx++) {
            // put initAble before the first initAble who's order is larger than it
            if (list.get(idx).getOrder() > order) {
                break;
            }
        }
        list.add(idx, new OrderWrapper(order, initAble));
    }

    private static int resolveOrder(InitAble initAble) {
        if (!initAble.getClass().isAnnotationPresent(InitOrder.class)) {
            return InitOrder.LOWEST_PRECEDENCE;
        } else {
            return initAble.getClass().getAnnotation(InitOrder.class).value();
        }
    }


    @Getter
    private static class OrderWrapper {
        private final int order;
        private final InitAble initAble;

        OrderWrapper(int order, InitAble initAble) {
            this.order = order;
            this.initAble = initAble;
        }
    }

}
