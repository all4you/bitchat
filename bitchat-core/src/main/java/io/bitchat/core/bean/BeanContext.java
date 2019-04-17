package io.bitchat.core.bean;

/**
 * @author houyi
 */
public interface BeanContext {

    /**
     * get Bean
     *
     * @param name bean name
     * @return Bean
     */
    Object getBean(String name);

    /**
     * get Bean
     *
     * @param clazz bean class
     * @param <T>   T
     * @return Bean
     */
    <T> T getBean(Class<T> clazz);

    /**
     * get Bean
     *
     * @param name  bean name
     * @param clazz bean class
     * @param <T>   T
     * @return Bean
     */
    <T> T getBean(String name, Class<T> clazz);

}
