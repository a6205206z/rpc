/*
 * {@code Invoker}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.uoko.rpc.common.InvokeCallback;
import com.uoko.rpc.handler.ClientInvokeHandler;
import com.uoko.rpc.transport.Client;
import com.uoko.rpc.transport.MethodInfo;
import com.uoko.rpc.transport.ServiceInfo;
import com.uoko.rpc.transport.Transporter;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;

public abstract class Invoker<T>{
	private static final Logger logger = Logger.getLogger(Invoker.class);
	
	private Class<T> interfaceClass;
	private String version;
	
	private Client client;
	private Object result = null;
	private ClientInvokeHandler invokeHandler;
	private CountDownLatch doneSignal;
	
	protected Invoker(final Class<T> interfaceClass,String version){
		this.doneSignal = new CountDownLatch(1);
		this.interfaceClass = interfaceClass;
		this.version = version;
		this.invokeHandler = new ClientInvokeHandler(new InvokeCallback(){

			@Override
			public void callback(Object o) {
				doneSignal.countDown();
				result = o;
			}
			
		});
		
		this.client = new Client(new ChannelInitializer<SocketChannel>(){

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				setPipeline(ch);
		        ch.pipeline().addLast(invokeHandler);
			}
		});
	}
	
	protected abstract void setPipeline(SocketChannel ch);
	
	protected void dispose(){
		if(this.client != null){
			this.client.close();
		}
	}
	
	public Object invoke(Method method, Object[] arguments,String address,int timeout) 
			throws InterruptedException
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
		
		ServiceInfo rpcService = new ServiceInfo();
		rpcService.setServiceName(interfaceClass.getName());
		rpcService.setVersion(version);
		
		MethodInfo rpcMethod = new MethodInfo();
		rpcMethod.setMethodName(method.getName());
		rpcMethod.setParameterTypes(method.getParameterTypes());
		rpcMethod.setParameters(arguments);
		Transporter transporter = new Transporter(rpcService,rpcMethod);
		
		
		client.invoke(host,port,transporter);
		
		doneSignal.await(timeout, TimeUnit.MILLISECONDS);
		
		return result;
	}
}
