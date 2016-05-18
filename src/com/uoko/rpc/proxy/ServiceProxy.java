package com.uoko.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.uoko.rpc.cluster.Router;

public class ServiceProxy<T> {
	 
	private Class<T> interfaceClass;
	private String version;
	private Router<T> router;
	
	

	
	public ServiceProxy(final Class<T> interfaceClass,String version){
		this.interfaceClass = interfaceClass;
		this.version = version;
		this.router = new Router<T>(interfaceClass,version);
	}
	
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public T refer() throws Exception {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler(){
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String address = router.getServiceAddress();
				Invoker<T> invoker = new Invoker<T>(interfaceClass, version);
				return invoker.invoke(method, args, address);
			}
		});
	}


	public void close() {
		if(router != null){
			router.close();
		}
	}
}
