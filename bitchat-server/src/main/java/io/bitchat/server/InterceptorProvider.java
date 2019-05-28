package io.bitchat.server;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import io.bitchat.core.init.InitOrder;
import io.bitchat.core.lang.config.BaseConfig;
import io.bitchat.core.lang.config.ConfigFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author houyi
 **/
public class InterceptorProvider {

    private static volatile boolean loaded = false;

    private static volatile InterceptorBuilder builder = null;

    public static List<Interceptor> getInterceptors() {
        // get user defined interceptor list by InterceptorBuilder first
        if (!loaded) {
            synchronized (InterceptorProvider.class) {
                if (!loaded) {
                    BaseConfig baseConfig = ConfigFactory.getConfig(BaseConfig.class);
                    Set<Class<?>> builders = ClassScaner.scanPackageBySuper(baseConfig.basePackage(), InterceptorBuilder.class);
                    if (CollectionUtil.isNotEmpty(builders)) {
                        try {
                            for (Class<?> cls : builders) {
                                builder = (InterceptorBuilder) cls.newInstance();
                                break;
                            }
                        } catch (IllegalAccessException | InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                    loaded = true;
                }
            }
        }
        if (builder != null) {
            return builder.build();
        }
        // scan the interceptors when can not get any interceptors by InterceptorBuilder
        return InterceptorsHolder.interceptors;
    }

    static class InterceptorsHolder {

        static List<Interceptor> interceptors;

        static {
            interceptors = scanInterceptors();
        }

        private static List<Interceptor> scanInterceptors() {
            BaseConfig baseConfig = ConfigFactory.getConfig(BaseConfig.class);
            Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(baseConfig.basePackage(), Interceptor.class);
            if (CollectionUtil.isEmpty(classSet)) {
                return Collections.emptyList();
            }
            List<InterceptorWrapper> wrappers = new ArrayList<>(classSet.size());
            try {
                for (Class<?> cls : classSet) {
                    Interceptor interceptor = (Interceptor) cls.newInstance();
                    insertSorted(wrappers, interceptor);
                }
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            return wrappers.stream()
                    .map(InterceptorWrapper::getInterceptor)
                    .collect(Collectors.toList());
        }

        private static void insertSorted(List<InterceptorWrapper> list, Interceptor interceptor) {
            int order = resolveOrder(interceptor);
            int idx = 0;
            for (; idx < list.size(); idx++) {
                if (list.get(idx).getOrder() > order) {
                    break;
                }
            }
            list.add(idx, new InterceptorWrapper(order, interceptor));
        }

        private static int resolveOrder(Interceptor interceptor) {
            if (!interceptor.getClass().isAnnotationPresent(InitOrder.class)) {
                return InitOrder.LOWEST_PRECEDENCE;
            } else {
                return interceptor.getClass().getAnnotation(InitOrder.class).value();
            }
        }

        private static class InterceptorWrapper {
            private final int order;
            private final Interceptor interceptor;

            InterceptorWrapper(int order, Interceptor interceptor) {
                this.order = order;
                this.interceptor = interceptor;
            }

            int getOrder() {
                return order;
            }

            Interceptor getInterceptor() {
                return interceptor;
            }
        }
    }

}
