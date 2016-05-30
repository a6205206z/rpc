/*
 * {@code Exporter}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.provider;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.uoko.rpc.common.ServiceHelper;
import com.uoko.rpc.handler.ServerProcessHandler;
import com.uoko.rpc.registry.ServiceRegistry;
import com.uoko.rpc.registry.ServiceRegistryFactory;
import com.uoko.rpc.transport.Server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

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
	
	private Exporter(){ }

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
		

		if(serviceInvokers.get(ServiceHelper.generateServiceInvokersKey(interfaceClass.getName(), version)) == null){
			Invoker invoker = new Invoker(service);
			serviceInvokers.put(ServiceHelper.generateServiceInvokersKey(interfaceClass.getName(), version), invoker);
			serviceRegistry.register(interfaceClass,version, String.format("%s:%d",address,port));
		}

	}

	
	public void export() throws Exception{

		try{
			/*
			 * 
			 * start service 
			 * 
			 * */
			server = new Server(port,
					new ChannelInitializer<SocketChannel>() {  
		            @Override  
		            public void initChannel(SocketChannel ch) throws Exception {
		            	ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this
				                .getClass().getClassLoader()))); 
		            	ch.pipeline().addLast(new ObjectEncoder());
		                ch.pipeline().addLast(new ServerProcessHandler(serviceInvokers));  
		            }
	            }
			);
			
			
		
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
