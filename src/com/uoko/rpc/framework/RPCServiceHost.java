/*
 * {@code RPCServiceHost}
 * 
 * the host of service 
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.framework;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.uoko.rpc.example.services.HelloService;
import com.uoko.rpc.framework.server.RPCServer;
import com.uoko.rpc.framework.transfer.PRCMethod;

public class RPCServiceHost {
	private static final Logger logger = Logger.getLogger(RPCServiceHost.class); 
	
	public static void export(final Object service,String version,String ip,int port) throws Exception{
		if(service == null){
			logger.error("service instance == null");
			throw new IllegalArgumentException("service instance == null");
		}
		if(port <= 0 || port > 65535){
			logger.error("Invalid port");
			throw new IllegalArgumentException("Invalid port");
		}
		
		/*
		 * 
		 * start service 
		 * 
		 * */
		RPCServer server = new RPCServer(port,
				new SimpleChannelHandler(){
			@Override
			public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
				if(e.getMessage() instanceof PRCMethod){
					PRCMethod  rpcMethod = (PRCMethod) e.getMessage();
					Method method = service.getClass().getMethod(rpcMethod.getMethodName(), rpcMethod.getParameterTypes());
					rpcMethod.setResult(method.invoke(service, rpcMethod.getParameters()));
					
					e.getChannel().write(rpcMethod);
				}
				super.messageReceived(ctx, e);
			}
 
		});
		
		Channel bind = server.Start();
		logger.info("Export service " + service.getClass().getName() + "on " + bind.getLocalAddress());
		
		
		/*
		 * 
		 * register service
		 * 
		 * */
		RPCServiceRegistry serviceRegistry = RPCServiceRegistry.Create();
		serviceRegistry.Register(HelloService.class,version, String.format("%s:%d",ip,port));
		logger.info("Register service " + service.getClass().getName() + "on zookeeper");
	}
}
