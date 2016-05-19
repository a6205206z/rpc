/*
 * {@code Exporter}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.protocol;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uoko.rpc.registry.ServiceRegistry;
import com.uoko.rpc.registry.ServiceRegistryFactory;
import com.uoko.rpc.transport.Context;
import com.uoko.rpc.transport.MethodInfo;
import com.uoko.rpc.transport.Server;
import com.uoko.rpc.transport.ServiceInfo;

public class Exporter {
	private static final Logger logger = Logger.getLogger(Exporter.class); 
	private static ApplicationContext ctx = new ClassPathXmlApplicationContext("server.xml");
	private static Exporter instance;
	
	private ConcurrentHashMap<String,Invoker> serviceInvokers; 
	private ServiceRegistry serviceRegistry;
	private Server server;
	
	private String address;
	private int port;
	
	public void setAddress(String address){
		this.address = address;
	}
	public void setPort(int port){
		this.port = port;
	}
	
	private Exporter(){
	}

	public static Exporter getInstance() {  
		if(instance == null){
			instance = (Exporter)ctx.getBean("exporter");
			instance.serviceInvokers = new ConcurrentHashMap<String,Invoker>();
			instance.serviceRegistry = ServiceRegistryFactory.getInstance().createServiceRegistry();
		}
		return instance;
	}
	
	public <T> void AddService(final Class<T> interfaceClass,final Object service,String version) 
			throws InterruptedException{
		if(service == null){
			logger.error("service == null");
			throw new IllegalArgumentException("service == null");
		}
		

		if(serviceInvokers.get(generateServiceInvokersKey(interfaceClass.getName(), version)) == null){
			Invoker invoker = new Invoker(service);
			serviceInvokers.put(generateServiceInvokersKey(interfaceClass.getName(), version), invoker);
			serviceRegistry.register(interfaceClass,version, String.format("%s:%d",address,port));
		}

	}
	
	private String generateServiceInvokersKey(String serviceName,String version){
		if(serviceName == null){
			logger.error("serviceName == null");
			throw new IllegalArgumentException("serviceName == null");
		}
		if(version == null){
			logger.error("version == null");
			throw new IllegalArgumentException("version == null");
		}
		
		return serviceName + version;
	}
	
	public void export() throws Exception{

		try{
			/*
			 * 
			 * start service 
			 * 
			 * */
			server = new Server(port,
					new SimpleChannelHandler(){
				@Override
				public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
					if(e.getMessage() instanceof Context){
						Context context = (Context) e.getMessage();
						try{
							ServiceInfo rpcService = context.getService(); 
							MethodInfo rpcMethod = context.getMethod();
							
							
							Invoker invoker = 
									serviceInvokers.get(generateServiceInvokersKey(rpcService.getServiceName(), rpcService.getVersion()));
							
							rpcMethod.setResult(invoker.invoke(
									rpcMethod.getMethodName(),
									rpcMethod.getParameterTypes(), 
									rpcMethod.getParameters()));
							
							context.setStatusCode(200);
						}catch(Exception ex){
							logger.error(ex);
							context.setStatusCode(500);
							context.setExceptionBody(ex.getMessage());
						}finally{
							e.getChannel().write(context);
						}
					}
				}
	 
			});
			
			
		
			server.start();
		}catch(Exception e){
			if(server != null){
				server.close();
			}
			if(serviceRegistry!=null){
				serviceRegistry.close();
			}
			logger.error(e);
		}
	}

}
