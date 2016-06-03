/*
 * {@code JdkInvoker}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.provider;

import java.lang.reflect.Method;

public class JdkInvoker implements Invoker {
	private Object service;
	protected JdkInvoker(final Object service){
		this.service = service;
	}

	@Override
	public Object invoke(String methodName,Class<?>[] parameterTypes,Object[] parameters) 
			throws Exception{
		
		Object result = null;
		Method method = service.getClass().getMethod(methodName, parameterTypes);
		for(int i = 0;i<parameterTypes.length;i++){
			parameters[i]=parameterTypes[i].cast(parameters[i]);
		}
		result = method.invoke(service, parameters);
		return result;
	}
}
