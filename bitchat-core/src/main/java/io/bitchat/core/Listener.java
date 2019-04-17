package io.bitchat.core;

/**
 * @author houyi
 */
public interface Listener<T> {

    /**
     * on event has received
     * @param event the event
     */
    void onEvent(T event);

}
