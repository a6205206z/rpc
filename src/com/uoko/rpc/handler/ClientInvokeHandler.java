/*
 * {@code ClientInvokeHandler}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.handler;

import org.apache.log4j.Logger;

import com.uoko.rpc.common.InvokeCallback;
import com.uoko.rpc.transport.Transporter;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ClientInvokeHandler extends ChannelHandlerAdapter {
	
	private static final Logger logger = Logger.getLogger(ClientInvokeHandler.class);
	

	private InvokeCallback ic;
	
	public ClientInvokeHandler(InvokeCallback ic){
		this.ic = ic;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		if(msg instanceof Transporter)
		{
			Transporter transporter = (Transporter)msg;
			if(transporter.getStatusCode() == 200){
				ic.callback(transporter.getMethod().getResult());
			}else{
				throw new Exception(transporter.getExceptionBody());
			}
		}
	}
	
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(ChannelFutureListener.CLOSE);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.error("Unexpected exception from downstream:"+cause.getMessage());
        ctx.close();
    }
}
