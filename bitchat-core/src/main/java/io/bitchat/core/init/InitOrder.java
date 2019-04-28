package io.bitchat.core.init;

import java.lang.annotation.*;

/**
 * The Order InitAble will execute
 * order by asc
 *
 * @author houyi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface InitOrder {

    /**
     * lowest precedence
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    /**
     * highest precedence
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * The order value. Lowest precedence by default.
     *
     * @return the order value
     */
    int value() default LOWEST_PRECEDENCE;
}
