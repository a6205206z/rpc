/*
 * {@code ServerProcessHandler}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.handler;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.uoko.rpc.common.ServiceHelper;
import com.uoko.rpc.provider.Invoker;
import com.uoko.rpc.transport.Context;
import com.uoko.rpc.transport.MethodInfo;
import com.uoko.rpc.transport.ServiceInfo;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerProcessHandler extends ChannelHandlerAdapter {
	private ConcurrentHashMap<String,Invoker> serviceInvokers; 
	
	public ServerProcessHandler(ConcurrentHashMap<String,Invoker> serviceInvokers){
		this.serviceInvokers = serviceInvokers;
	}
	
	private static final Logger logger = Logger.getLogger(ServerProcessHandler.class); 
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		Context context = null;
		try{
			context = (Context)msg;
			ServiceInfo rpcService = context.getService(); 
			MethodInfo rpcMethod = context.getMethod();
			
			
			Invoker invoker = 
					serviceInvokers.get(ServiceHelper.generateServiceInvokersKey(rpcService.getServiceName(), rpcService.getVersion()));
			
			rpcMethod.setResult(invoker.invoke(
					rpcMethod.getMethodName(),
					rpcMethod.getParameterTypes(), 
					rpcMethod.getParameters()));
			
			context.setStatusCode(200);
		}catch(Exception ex){
			logger.error(ex);
			context = new Context(null,null);
			context.setStatusCode(500);
			context.setExceptionBody(ex.getMessage());
		}
		ctx.write(context);
	}
	
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	logger.error("Unexpected exception from downstream:"+cause.getMessage());
        ctx.close();
    }
}
