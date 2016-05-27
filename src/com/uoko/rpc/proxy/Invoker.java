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

import com.uoko.rpc.transport.Client;
import com.uoko.rpc.transport.Context;
import com.uoko.rpc.transport.MethodInfo;
import com.uoko.rpc.transport.ServiceInfo;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class Invoker<T>{
	private static final Logger logger = Logger.getLogger(Invoker.class);
	
	private Class<T> interfaceClass;
	private String version;
	
	private Object reulst = null;
	protected Invoker(final Class<T> interfaceClass,String version){
		this.interfaceClass = interfaceClass;
		this.version = version;
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
					new ChannelHandlerAdapter(){

						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception{
							ServiceInfo rpcService = new ServiceInfo();
							rpcService.setServiceName(interfaceClass.getName());
							rpcService.setVersion(version);
							
							MethodInfo rpcMethod = new MethodInfo();
							rpcMethod.setMethodName(method.getName());
							rpcMethod.setParameterTypes(method.getParameterTypes());
							rpcMethod.setParameters(arguments);
							Context context = new Context(rpcService,rpcMethod);
							ctx.writeAndFlush(context);
						}
						
						
						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
							if(msg instanceof Context)
							{
								Context context = (Context)msg;
								reulst = context.getMethod().getResult();
							}
						}
						
					    @Override
					    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
					        ctx.flush();
					        ctx.close();
					    }
					    
					    @Override
					    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
					            throws Exception {
					        logger.error("Unexpected exception from downstream:"+cause.getMessage());
					        ctx.close();
					    }
					}
			);
		return reulst;
	}
}
