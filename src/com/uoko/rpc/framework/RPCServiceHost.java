package com.uoko.rpc.framework;

import java.lang.reflect.Method;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import com.uoko.rpc.framework.server.RPCServer;
import com.uoko.rpc.framework.transfer.PRCMethod;

public class RPCServiceHost {
	public static void export(final Object service,int port) throws Exception{
		if(service == null){
			throw new IllegalArgumentException("service instance == null");
		}
		if(port <= 0 || port > 65535){
			throw new IllegalArgumentException("Invalid port");
		}
		
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
		System.out.println("Export service " + service.getClass().getName() + "on " + bind.getLocalAddress());
		
	}
}
