package com.wiltech.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify an object to be mapped to an exchange and a queue within the AMQP broker messaging framework.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectExchange {

    AMQPExchangeKeyRoutingType value();
}
