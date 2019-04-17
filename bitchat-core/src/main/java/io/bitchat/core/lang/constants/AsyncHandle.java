package io.bitchat.core.lang.constants;

/**
 * @author houyi
 */
public interface AsyncHandle {

    /**
     * handle the packet in async
     */
    byte ASYNC = (byte) 1;

    /**
     * handle the packet in sync
     */
    byte SYNC = (byte) 0;

}
