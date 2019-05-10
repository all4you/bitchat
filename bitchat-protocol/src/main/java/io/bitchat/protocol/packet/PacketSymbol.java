package io.bitchat.protocol.packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * <p>
 * specify the symbol of
 * each ${@link PacketHandler}
 * </p>
 *
 * <p>
 * use packet symbol to mapping
 * the PacketHandler to
 * the detailed Packet
 * </p>
 *
 * @author houyi
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketSymbol {

    int value();

}