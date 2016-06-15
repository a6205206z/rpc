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
import com.uoko.rpc.transport.MethodInfo;
import com.uoko.rpc.transport.ServiceInfo;
import com.uoko.rpc.transport.Transporter;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ServerProcessHandler extends ChannelHandlerAdapter {
	private ConcurrentHashMap<String,Invoker> serviceInvokers; 
	
	public ServerProcessHandler(ConcurrentHashMap<String,Invoker> serviceInvokers){
		this.serviceInvokers = serviceInvokers;
	}
	
	private static final Logger logger = Logger.getLogger(ServerProcessHandler.class); 
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		Transporter transporter = null;
		try{
			transporter = (Transporter)msg;
			ServiceInfo rpcService = transporter.getService(); 
			MethodInfo rpcMethod = transporter.getMethod();
			
			
			Invoker invoker = 
					serviceInvokers.get(ServiceHelper.generateServiceInvokersKey(rpcService.getServiceName(), rpcService.getVersion()));
			
			rpcMethod.setResult(invoker.invoke(
					rpcMethod.getMethodName(),
					rpcMethod.getParameterTypes(), 
					rpcMethod.getParameters()));
			
			transporter.setStatusCode(200);
		}catch(Exception ex){
			logger.error(ex);
			transporter = new Transporter(null,null);
			transporter.setStatusCode(500);
			transporter.setExceptionBody(ex.getMessage());
		}
		ctx.writeAndFlush(transporter).addListener(ChannelFutureListener.CLOSE);
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
