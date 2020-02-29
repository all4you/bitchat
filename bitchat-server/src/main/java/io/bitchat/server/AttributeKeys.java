package io.bitchat.server;

import io.netty.util.AttributeKey;

/**
 * @author houyi
 */
public interface AttributeKeys {

    AttributeKey<String> SESSION_ID = AttributeKey.newInstance("sessionId");

}
