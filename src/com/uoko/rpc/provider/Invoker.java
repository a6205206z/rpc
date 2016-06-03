/*
 * {@code Invoker}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.provider;

public interface Invoker {
	Object invoke(String methodName,Class<?>[] parameterTypes,Object[] parameters)
			throws Exception;
}
