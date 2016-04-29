/*
 * {@code RPCClientProxy} is the Proxy of Rpc Client
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.framework;

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

import com.uoko.rpc.example.services.HelloService;
import com.uoko.rpc.framework.client.RPCClient;
import com.uoko.rpc.framework.transfer.PRCMethod;

public class RPCClientProxy<T> {
	private static final Logger logger = Logger.getLogger(RPCClientProxy.class); 
	
	private Class<T> serviceInterfaceClass;
	
	public RPCClientProxy(final Class<T> interfaceClass,String version,String zkConnectionString,String registerRootPath,
			int zkSessionTimeout){
		this.serviceInterfaceClass = interfaceClass;
	}
	
	@SuppressWarnings("unchecked")
	public T refer() throws Exception{
		return (T) Proxy.newProxyInstance(serviceInterfaceClass.getClassLoader(), new Class<?>[]{serviceInterfaceClass}, new InvocationHandler(){
			PRCMethod rpcMethod = null;
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
						RPCServiceDiscovery<HelloService> serviceDiscovery = new RPCServiceDiscovery<HelloService>(HelloService.class,"1.0","127.0.0.1:2181", "/services", 10000, 
								new RPCServiceDiscoveryHandler(){
									@Override
									public void serviceChanged(List<String> addressList) {
										serviceAddressList = addressList;
										latch.countDown();
									}
						});
						serviceDiscovery.Init();
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
				rpcMethod = new PRCMethod();
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
				RPCClient client = new RPCClient(host,port);
				client.Invoke(
						new SimpleChannelHandler(){

							@Override
							public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
								super.channelConnected(ctx, e);
								e.getChannel().write(rpcMethod);
							}
							
							@Override
							public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
								if(e.getMessage() instanceof PRCMethod)
								{
									rpcMethod = (PRCMethod)e.getMessage();
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
