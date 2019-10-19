package io.bitchat.protocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * <p>
 * specify the name of
 * each {@link RequestProcessor}
 * or {@link CommandProcessor}
 * </p>
 *
 *
 * @author houyi
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Processor {

    String name();

}