/*
 * {@code ServiceRegistryFactory}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.registry;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ServiceRegistryFactory {
	private static ServiceRegistryFactory instance; 
	
	private static final Logger logger = Logger.getLogger(ServiceRegistryFactory.class);
	private static ApplicationContext ctx = new ClassPathXmlApplicationContext("server.xml");
	
	private String loadServiceRegistry;
	
	private ServiceRegistryFactory(){}
	
	public void setLoadServiceRegistry(String loadServiceRegistry){
		this.loadServiceRegistry = loadServiceRegistry;
	}
	
	/*
	 * 
	 * synchronized
	 * 
	 * 
	 * */
	public static synchronized ServiceRegistryFactory getInstance() {  
		if(instance == null){
			instance = (ServiceRegistryFactory)ctx.getBean("serviceRegistryFactory");
		}
		return instance;
	}
	
	
	/*
	 * 
	 * createServiceRegistry
	 * 
	 * */
	public ServiceRegistry createServiceRegistry(){
		ServiceRegistry serviceRegistry = null;
		try{
			serviceRegistry = (ServiceRegistry)ctx.getBean(this.loadServiceRegistry);
		}
		catch(Exception e){
			logger.error(e);
		}
		return serviceRegistry;
	}
}
