package com.uoko.rpc.cluster;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.uoko.rpc.discovery.ServiceDiscovery;
import com.uoko.rpc.discovery.ServiceDiscoveryFactory;
import com.uoko.rpc.discovery.ServiceDiscoveryHandler;

public class Router<T> {
	private static final Logger logger = Logger.getLogger(Router.class);
	
	private Class<T> interfaceClass;
	private String version;
	private LoadbalanceStrategy loadbalanceStrategy;
	private ServiceDiscovery serviceDiscovery;
	
	private List<String> serviceAddressList;
	
	public Router(final Class<T> interfaceClass,String version){
		this.interfaceClass = interfaceClass;
		this.version = version;
		
		this.loadbalanceStrategy = LoadbalanceStrategySelector.SelectStrategy("RandomSelect");
		this.serviceDiscovery = ServiceDiscoveryFactory.getInstance().createServiceDiscovery();
		this.serviceAddressList = null;
	}
	
	/*
	 * This code needs to be optimized
	 * 
	 * */
	public String getServiceAddress(){
		
		//
		if(this.serviceAddressList == null){
			try{
				CountDownLatch latch = new CountDownLatch(1);
				//init serviceAddress
				//discover service

				serviceDiscovery.beginDiscovery(interfaceClass, version, new ServiceDiscoveryHandler(){
					@Override
					public void serviceChanged(List<String> addressList) {
						serviceAddressList = addressList;
						latch.countDown();
					}
				});
				
				
				latch.await();
			}
			catch(Exception e){
				logger.error("Can''t find sevice");
				throw new IllegalArgumentException("serviceAddressList == null");
			}
		}
		
		if(this.serviceAddressList.size() == 0){
			logger.error("Can''t find sevice");
			throw new IllegalArgumentException("serviceAddressList == null");
		}

		String address = loadbalanceStrategy.selectOne(serviceAddressList);
		return address;
	}
	
	
	public void close(){
		if(this.serviceDiscovery != null){
			this.serviceDiscovery.endDiscovery();
		}
	}
}
