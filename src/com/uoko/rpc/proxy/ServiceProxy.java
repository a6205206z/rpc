/*
 * {@code ServiceProxy} is the Proxy of Rpc Client
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.uoko.rpc.discovery.ServiceDiscovery;
import com.uoko.rpc.discovery.ServiceDiscoveryFactory;
import com.uoko.rpc.discovery.ServiceDiscoveryHandler;
import com.uoko.rpc.loadbalance.LoadbalanceStrategy;
import com.uoko.rpc.loadbalance.LoadbalanceStrategySelector;
import com.uoko.rpc.transport.Client;
import com.uoko.rpc.transport.MethodInfo;
import com.uoko.rpc.transport.Transporter;

public class ServiceProxy {
	private static final Logger logger = Logger.getLogger(ServiceProxy.class); 
	private static ServiceProxy instance; 
	private LoadbalanceStrategy loadbalanceStrategy ;
	private ServiceDiscovery serviceDiscovery;
	
	private ServiceProxy(){
		loadbalanceStrategy 
			= LoadbalanceStrategySelector.SelectStrategy("RandomSelect");
		serviceDiscovery 
			= ServiceDiscoveryFactory.getInstance().createServiceDiscovery();
	}
	
	public static synchronized ServiceProxy getInstance(){
		if(instance == null){
			instance = new ServiceProxy();
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T refer(final Class<T> interfaceClass,String version) throws Exception{
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler(){
			List<String> serviceAddressList = null;		
			Object reulst = null;
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
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {

				String address = getServiceAddress();
				String host = address.split(":")[0];
				int port = Integer.parseInt(address.split(":")[1]);
				
				if (host == null || host.length() == 0){
					logger.error("Host == null");
					throw new IllegalArgumentException("Host == null");
				}
				
				if (port <= 0 || port > 65535){
					logger.error("Invalid port " + port);
					throw new IllegalArgumentException("Invalid port " + port);
				}
				
				
				logger.debug("invoke on " + address);
				Client client = new Client(host,port);
				client.invoke(
						new SimpleChannelHandler(){

							@Override
							public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
								super.channelConnected(ctx, e);
								MethodInfo rpcMethod = new MethodInfo();
								rpcMethod.setMethodName(method.getName());
								rpcMethod.setParameterTypes(method.getParameterTypes());
								rpcMethod.setParameters(arguments);
								Transporter transporter = new Transporter(rpcMethod);
								e.getChannel().write(transporter);
							}
							
							@Override
							public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
								if(e.getMessage() instanceof Transporter)
								{
									Transporter transporter = (Transporter)e.getMessage();
									reulst = transporter.getMethodInfo().getResult();
									super.messageReceived(ctx, e);
								}
								e.getChannel().close();
							}
				});
				
				
				return reulst;
			}
			
		});
	}
	
	public void close(){
		serviceDiscovery.endDiscovery();
	}
}
