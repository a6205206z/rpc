/*
 * {@code InvokerFactory}
 * 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.provider;


public class InvokerFactory {

	private static InvokerFactory instance;
	
	private InvokerFactory(){
		
	}
	/*
	 * 
	 * synchronized
	 * 
	 * 
	 * */
	public static synchronized InvokerFactory getInstance() {  
		if(instance == null){
			instance = new InvokerFactory();
		}
		return instance;
	}
	
	public Invoker create(Object service){
		return new JdkInvoker(service);
	}
}
