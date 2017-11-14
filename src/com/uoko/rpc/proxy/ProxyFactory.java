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
	private ServiceProxyPool serviceProxyPool;
	
	
	private ProxyFactory(){
		serviceProxyPool = new ServiceProxyPool();
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
	public <T> ServiceProxy<T> createProxy(final Class<T> interfaceClass,String version) {
		return serviceProxyPool.getOrCreateServiceProxy(interfaceClass, version);
	}

	public void closeProxy(final Class interfaceClass,String version){
		serviceProxyPool.closeServiceProxy(interfaceClass,version);
	}
}
