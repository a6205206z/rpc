package com.uoko.rpc.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.uoko.rpc.framework.client.RPCClient;
import com.uoko.rpc.framework.server.RPCServer;

public class RPCFramework {
	/*
	 * 暴露服务
	 * @param service 服务实现 
	 * @param port 服务端口
	 * @throws Exception
	 * 
	 */
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
				if(e.getMessage() instanceof RPCTransferBean){
					RPCTransferBean  rpctd = (RPCTransferBean) e.getMessage();
					Method method = service.getClass().getMethod(rpctd.getMethodName(), rpctd.getParameterTypes());
					Object result = method.invoke(service, rpctd.getParameters());
					
					e.getChannel().write(result);
				}
				super.messageReceived(ctx, e);
			}
 
		},
				new SimpleChannelHandler(){
			@Override
			public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
				super.messageReceived(ctx, e);
				e.getChannel().close();
			}
 
		});
		
		Channel bind = server.Start();
		System.out.println("Export service " + service.getClass().getName() + "on " + bind.getLocalAddress());
		
	}

	
	/*
	 * 服务引用
	 * @param <T> 接口泛型 
	 * @param interfaceClass 接口类型 
	 * @param port 服务器端口 
	 * @return 远程服务 
	 * @throws Exception 
	 * 
	 */
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
			Object result = null;
			@Override
			public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
				
				RPCTransferBean rpctd = new RPCTransferBean();
				rpctd.setMethodName(method.getName());
				rpctd.setParameterTypes(method.getParameterTypes());
				rpctd.setParameters(arguments);
				
				RPCClient client = new RPCClient(host,port);
				client.Invoke(
						new SimpleChannelHandler(){
							@Override
							public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{

								result = e.getMessage();
								super.messageReceived(ctx, e);
							}
							
							@Override
							public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
								super.channelConnected(ctx, e);
								e.getChannel().write(rpctd);
							}
				});
				
				return result;
			}
			
		});
	}
}
