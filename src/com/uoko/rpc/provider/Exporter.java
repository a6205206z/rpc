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

import com.uoko.rpc.common.ServiceHelper;
import com.uoko.rpc.handler.ServerProcessHandler;
import com.uoko.rpc.registry.ServiceRegistry;
import com.uoko.rpc.registry.ServiceRegistryFactory;
import com.uoko.rpc.transport.Server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public abstract class Exporter {
	private static final Logger logger = Logger.getLogger(Exporter.class); 
	
	
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
	
	public Exporter(String address,int port){
		this.address = address;
		this.port = port;
		this.serviceInvokers = new ConcurrentHashMap<String,Invoker>();
		this.serviceRegistry = ServiceRegistryFactory.getInstance().createServiceRegistry();
	}

	
	public <T> void AddService(final Class<T> interfaceClass,final Object service,String version) 
			throws InterruptedException{
		if(service == null){
			logger.error("service == null");
			throw new IllegalArgumentException("service == null");
		}
		

		if(serviceInvokers.get(ServiceHelper.generateServiceInvokersKey(interfaceClass.getName(), version)) == null){
			Invoker invoker = InvokerFactory.getInstance().create(service);
			serviceInvokers.put(ServiceHelper.generateServiceInvokersKey(interfaceClass.getName(), version), invoker);
			serviceRegistry.register(interfaceClass,version, String.format("%s:%d",address,port));
		}

	}

	protected abstract void setPipeline(SocketChannel ch);
	
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
		            	setPipeline(ch);
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
