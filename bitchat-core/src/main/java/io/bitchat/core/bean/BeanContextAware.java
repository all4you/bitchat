package io.bitchat.core.bean;

/**
 * @author houyi
 */
public interface BeanContextAware extends Aware {

    /**
     * set BeanContext
     *
     * @param beanContext BeanContext
     */
    void setBeanContext(BeanContext beanContext);
}