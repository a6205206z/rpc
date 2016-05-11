/*
 * {@code RPCMethod}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RPCMethod {
	String name() default "";
}
