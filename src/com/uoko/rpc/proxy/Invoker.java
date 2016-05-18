/*
 * {@code Invoker}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.proxy;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.uoko.rpc.transport.Client;
import com.uoko.rpc.transport.Context;
import com.uoko.rpc.transport.MethodInfo;

public class Invoker{
	private static final Logger logger = Logger.getLogger(Invoker.class); 
	
	
	
	private Object reulst = null;
	
	
	protected Invoker(){
		
	}

	public Object invoke(Method method, Object[] arguments,String address)
	{
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
						MethodInfo rpcMethod = new MethodInfo();
						rpcMethod.setMethodName(method.getName());
						rpcMethod.setParameterTypes(method.getParameterTypes());
						rpcMethod.setParameters(arguments);
						Context context = new Context(rpcMethod);
						e.getChannel().write(context);
					}
					
					@Override
					public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
						if(e.getMessage() instanceof Context)
						{
							Context context = (Context)e.getMessage();
							reulst = context.getMethodInfo().getResult();
						}
						e.getChannel().close();
					}
		});
		
		
		return reulst;
	}
}
