package com.uoko.rpc.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.uoko.rpc.framework.client.RPCClient;
import com.uoko.rpc.framework.transfer.PRCMethod;

public class RPCClientProxy {
	@SuppressWarnings("unchecked")
	public static <T> T refer(final Class<T> interfaceClass, final String host, final int port) throws Exception{
		if (interfaceClass == null){
			throw new IllegalArgumentException("interface class == null");
		}
		if (! interfaceClass.isInterface()){
			throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");  
		}
		
		if (host == null || host.length() == 0){
			throw new IllegalArgumentException("Host == null");
		}
		
		if (port <= 0 || port > 65535){
			throw new IllegalArgumentException("Invalid port " + port);
		}
		
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler(){
			PRCMethod rpcMethod = null;

			@Override
			public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
				rpcMethod = new PRCMethod();
				rpcMethod.setMethodName(method.getName());
				rpcMethod.setParameterTypes(method.getParameterTypes());
				rpcMethod.setParameters(arguments);
				
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
