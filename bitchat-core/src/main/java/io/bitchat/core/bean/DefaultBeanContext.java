package io.bitchat.core.bean;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import io.bitchat.core.lang.config.BaseConfig;
import io.bitchat.core.lang.config.ConfigFactory;
import io.bitchat.core.init.InitAble;
import io.bitchat.core.init.InitOrder;
import lombok.extern.slf4j.Slf4j;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DefaultBeanContext
 *
 * @author houyi
 */
@Slf4j
@InitOrder(1)
public class DefaultBeanContext implements BeanContext, InitAble {

    private static BaseConfig baseConfig = ConfigFactory.getConfig(BaseConfig.class);

    /**
     * hold all the beans
     */
    private static Map<String, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * whether the bean has been inited
     */
    private static AtomicBoolean inited = new AtomicBoolean(false);

    private DefaultBeanContext() {

    }

    public static BeanContext getInstance() {
        return Singleton.get(DefaultBeanContext.class);
    }

    @Override
    public void init() {
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        initBean();
        injectAnnotation();
        processBeanContextAware();
    }

    @Override
    public Object getBean(String name) {
        return beanMap.get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> clazz) {
        for(Object bean : beanMap.values()){
            if(bean.getClass()==clazz) {
                return (T) bean;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        Object bean = getBean(name);
        return bean == null ? null : (T) bean;
    }

    private void initBean() {
        try {
            Set<Class<?>> classSet = ClassScaner.scanPackageByAnnotation(baseConfig.basePackage(), Bean.class);
            if (CollectionUtil.isEmpty(classSet)) {
                log.warn("[DefaultBeanContext] No Bean classes scanned!");
                return;
            }
            for (Class<?> clazz : classSet) {
                Bean bean = clazz.getAnnotation(Bean.class);
                if (bean == null) {
                    continue;
                }
                String beanName = StrUtil.isNotBlank(bean.name()) ? bean.name() : clazz.getName();
                if (beanMap.containsKey(beanName)) {
                    log.warn("[DefaultBeanContext] duplicate Bean with name={}", beanName);
                    continue;
                }
                beanMap.putIfAbsent(beanName, Singleton.get(clazz));
                log.info("[DefaultBeanContext] Found Bean=[{}]", clazz.getCanonicalName());
            }
        } catch (Exception e) {
            log.error("[DefaultBeanContext] initBean error,cause:{}", e.getMessage(), e);
        }
    }

    private void injectAnnotation() {
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Object bean = entry.getValue();
            if (bean == null) {
                continue;
            }
            propertyInject(bean);
            fieldAnnotation(bean);
        }
    }

    private void processBeanContextAware() {
        try {
            Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(baseConfig.basePackage(), BeanContextAware.class);
            if (CollectionUtil.isEmpty(classSet)) {
                log.warn("[DefaultBeanContext] No BeanContextAware classes scanned!");
                return;
            }
            for (Class<?> clazz : classSet) {
                if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || !BeanContextAware.class.isAssignableFrom(clazz)) {
                    continue;
                }
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                BeanContextAware aware = (BeanContextAware) constructor.newInstance();
                aware.setBeanContext(getInstance());
            }
        } catch (Exception e) {
            log.error("[DefaultBeanContext] processBeanContextAware error,cause:{}", e.getMessage(), e);
        }
    }

    /**
     * inject those beans defined by properties
     *
     * @param bean bean need to be injected
     */
    private void propertyInject(Object bean) {
        try {
            // get property descriptors
            PropertyDescriptor[] descriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                // get all setter method
                Method setter = descriptor.getWriteMethod();
                // if setter method does not use @Autowired annotation
                if (setter == null || !setter.isAnnotationPresent(Autowired.class)) {
                    continue;
                }
                Autowired autowired = setter.getAnnotation(Autowired.class);
                String name;
                Object object = null;
                // get bean by beanName first
                if (StrUtil.isNotBlank(autowired.name())) {
                    name = autowired.name();
                    object = beanMap.get(name);
                } else {
                    for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
                        if (descriptor.getPropertyType().isAssignableFrom(entry.getValue().getClass())) {
                            object = entry.getValue();
                            break;
                        }
                    }
                }
                // set private access
                setter.setAccessible(true);
                // inject the actual object into the bean
                setter.invoke(bean, object);
            }
        } catch (Exception e) {
            log.info("[DefaultBeanContext] propertyInject error,cause:{}", e.getMessage(), e);
        }
    }

    /**
     * inject those beans defined by field
     *
     * @param bean bean need to be injected
     */
    private void fieldAnnotation(Object bean) {
        try {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field == null || !field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);
                Object object = null;
                String name = autowired.name();
                if (StrUtil.isNotBlank(name)) {
                    object = beanMap.get(name);
                } else {
                    for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
                        if (field.getType().isAssignableFrom(entry.getValue().getClass())) {
                            object = entry.getValue();
                            break;
                        }
                    }
                }
                field.setAccessible(true);
                field.set(bean, object);
            }
        } catch (Exception e) {
            log.info("[DefaultBeanContext] fieldAnnotation error,cause:{}", e.getMessage(), e);
        }
    }


}