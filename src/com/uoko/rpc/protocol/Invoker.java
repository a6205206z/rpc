/*
 * {@code Invoker}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.protocol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Invoker {
	private Object service;
	protected Invoker(final Object service){
		this.service = service;
	}
	
	public Object invoke(String methodName,Class<?>[] parameterTypes,Object[] parameters) 
			throws NoSuchMethodException, SecurityException, 
			IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException{
		
		Object result = null;
		Method method = service.getClass().getMethod(methodName, parameterTypes);
		result = method.invoke(service, parameters);
		return result;
	}
}
