/*
 * {@code Invoker}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.provider;

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
		for(int i = 0;i<parameterTypes.length;i++){
			parameters[i]=parameterTypes[i].cast(parameters[i]);
		}
		result = method.invoke(service, parameters);
		return result;
	}
}
