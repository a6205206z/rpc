/*
 * {@code RPCClientProxy} is the Proxy of Rpc Client
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.uoko.rpc.discovery.ServiceDiscovery;
import com.uoko.rpc.discovery.ServiceDiscoveryFactory;
import com.uoko.rpc.discovery.ServiceDiscoveryHandler;

import com.uoko.rpc.transport.Client;
import com.uoko.rpc.transport.MethodInfo;

public class ServiceProxy {
	private static final Logger logger = Logger.getLogger(ServiceProxy.class); 
	private static ServiceProxy instance; 
	
	private ServiceProxy(){}
	
	public static synchronized ServiceProxy getInstance(){
		if(instance == null){
			instance = new ServiceProxy();
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T refer(final Class<T> interfaceClass,String version) throws Exception{
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler(){
			MethodInfo rpcMethod = null;
			List<String> serviceAddressList = null;
			
			/*
			 * This code needs to be optimized
			 * 
			 * */
			private String RandomServiceAddress(){
				
				//
				if(this.serviceAddressList == null){
					try{
						CountDownLatch latch = new CountDownLatch(1);
						//init serviceAddress
						//discover service
						
						ServiceDiscovery serviceDiscovery = ServiceDiscoveryFactory.getInstance().createServiceDiscovery();
						
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
				
				Random random =new Random();
				int index = random.nextInt(serviceAddressList.size());
				String address = serviceAddressList.get(index);
				return address;
			}
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
				rpcMethod = new MethodInfo();
				rpcMethod.setMethodName(method.getName());
				rpcMethod.setParameterTypes(method.getParameterTypes());
				rpcMethod.setParameters(arguments);
				

				String address = RandomServiceAddress();
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
								e.getChannel().write(rpcMethod);
							}
							
							@Override
							public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
								if(e.getMessage() instanceof MethodInfo)
								{
									rpcMethod = (MethodInfo)e.getMessage();
									super.messageReceived(ctx, e);
								}
								e.getChannel().close();
							}
				});
				
				return rpcMethod.getResult();
			}
			
		});
	}
}