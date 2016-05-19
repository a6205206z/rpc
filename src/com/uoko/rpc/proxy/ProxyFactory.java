/*
 * {@code ProxyFactory}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.proxy;

public class ProxyFactory {
	
	
	private static ProxyFactory instance;
	
	
	private ProxyFactory(){
		
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
	public synchronized <T> ServiceProxy<T> createProxy(final Class<T> interfaceClass,String version) {
		return ServiceProxy.getInstance(interfaceClass, version);
	}

}
