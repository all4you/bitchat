package io.bitchat.connection;

import io.netty.util.AttributeKey;

/**
 * @author houyi
 */
public interface Attributes {

    /**
     * online attr
     */
    AttributeKey<Boolean> ON_LINE = AttributeKey.newInstance("online");

    /**
     * clientId attr
     */
    AttributeKey<String> CLIENT_ID = AttributeKey.newInstance("clientId");

}
