/*
 * {@code ProxyFactory}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.proxy;

import java.util.concurrent.ConcurrentHashMap;

public class ProxyFactory {
	private static ProxyFactory instance;
	private ConcurrentHashMap<String,ServiceProxy<?>> existProxy;
	
	private ProxyFactory(){
		existProxy = new ConcurrentHashMap<String,ServiceProxy<?>>();
	}
	/*
	 * 
	 * synchronized
	 * 
	 * 
	 * */
	public static synchronized ProxyFactory getInstance() {  
		if(instance == null){
			instance = new ProxyFactory();
		}
		return instance;
	}
	
	/*
	 * 
	 * 
	 * single instance each class 
	 * 
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public synchronized <T> ServiceProxy<T> createProxy(final Class<T> interfaceClass,String version) {
		ServiceProxy<?> proxy = existProxy.get(interfaceClass+version);
		if(proxy == null){
			existProxy.put(interfaceClass+version, new ServiceProxy<T>(interfaceClass,version));
			proxy = existProxy.get(interfaceClass+version);
		}
		
		return (ServiceProxy<T>)proxy;
	}
}
