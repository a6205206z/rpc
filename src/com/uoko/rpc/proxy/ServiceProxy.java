package com.uoko.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.uoko.rpc.discovery.ServiceDiscovery;
import com.uoko.rpc.discovery.ServiceDiscoveryFactory;
import com.uoko.rpc.discovery.ServiceDiscoveryHandler;
import com.uoko.rpc.loadbalance.LoadbalanceStrategy;
import com.uoko.rpc.loadbalance.LoadbalanceStrategySelector;

public class ServiceProxy<T> {
	private static final Logger logger = Logger.getLogger(ServiceProxy.class); 
	private LoadbalanceStrategy loadbalanceStrategy;
	private ServiceDiscovery serviceDiscovery;
	private Class<T> interfaceClass;
	private String version;
	
	private List<String> serviceAddressList = null;
	
	public ServiceProxy(final Class<T> interfaceClass,String version){
		this.interfaceClass = interfaceClass;
		this.version = version;
		this.loadbalanceStrategy = LoadbalanceStrategySelector.SelectStrategy("RandomSelect");
		this.serviceDiscovery = ServiceDiscoveryFactory.getInstance().createServiceDiscovery();
	}
	
	/*
	 * This code needs to be optimized
	 * 
	 * */
	private String getServiceAddress(){
		
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
				String address = getServiceAddress();
				Invoker invoker = new Invoker();
				return invoker.invoke(method, args, address);
			}
		});
	}
	
	public void close(){
		if(this.serviceDiscovery != null){
			this.serviceDiscovery.endDiscovery();
		}
	}
}
