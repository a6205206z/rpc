/*
 * {@code Exporter}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.protocol;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.uoko.rpc.example.services.HelloService;
import com.uoko.rpc.registry.ServiceRegistry;
import com.uoko.rpc.registry.ServiceRegistryFactory;
import com.uoko.rpc.transport.Context;
import com.uoko.rpc.transport.MethodInfo;
import com.uoko.rpc.transport.Server;

public class Exporter {
	private static final Logger logger = Logger.getLogger(Exporter.class); 
	private static Exporter instance;
	
	private Exporter(){}

	public static Exporter getInstance() {  
		if(instance == null){
			instance = new Exporter();
		}
		return instance;
	}
	
	public void export(final Object service,String version,String ip,int port) throws Exception{
		if(port <= 0 || port > 65535){
			logger.error("Invalid port");
			throw new IllegalArgumentException("Invalid port");
		}
		
		Server server = null;
		ServiceRegistry serviceRegistry = null;
		Invoker invoker = new Invoker(service);
		
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
							MethodInfo rpcMethod = context.getMethodInfo();
							
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
			logger.info("Export service " + service.getClass().getName() + "on " + server.getServerAddress());
			
			
			/*
			 * 
			 * register service
			 * 
			 * */
			serviceRegistry = ServiceRegistryFactory.getInstance().createServiceRegistry();
			serviceRegistry.register(HelloService.class,version, String.format("%s:%d",ip,port));
			logger.info("Register service " + service.getClass().getName() + "on zookeeper");
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
