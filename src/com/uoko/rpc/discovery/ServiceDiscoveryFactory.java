package com.uoko.rpc.discovery;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceDiscoveryFactory {
	private static ServiceDiscoveryFactory instance; 
	
	private static final Logger logger = Logger.getLogger(ServiceDiscoveryFactory.class);
	private static ApplicationContext ctx = new ClassPathXmlApplicationContext("proxy.xml");
	
	private String loadServiceDiscovery;
	
	private ServiceDiscoveryFactory(){}
	
	public void setLoadServiceDiscovery(String loadServiceDiscovery){
		this.loadServiceDiscovery = loadServiceDiscovery;
	}
	
	/*
	 * 
	 * synchronized
	 * 
	 * 
	 * */
	public static synchronized ServiceDiscoveryFactory getInstance() {  
		if(instance == null){
			instance = (ServiceDiscoveryFactory)ctx.getBean("serviceDiscoveryFactory");
		}
		return instance;
	}
	
	/*
	 * 
	 * createServiceDiscovery
	 * 
	 * */
	public ServiceDiscovery createServiceDiscovery(){
		ServiceDiscovery serviceDiscovery = null;
		try{
			serviceDiscovery = (ServiceDiscovery)ctx.getBean(this.loadServiceDiscovery);
		}
		catch(Exception e){
			logger.error(e);
		}
		return serviceDiscovery;
	}
}
