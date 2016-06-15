package com.uoko.rpc.protocol;



import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import com.uoko.rpc.transport.Transporter;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public abstract class ProtocolHandler extends ChannelHandlerAdapter {
	public ProtocolHandler(){
		
	}
	
	protected abstract Object packageProtocol(Transporter in);
	protected abstract Transporter unpackageProtocol(Object in);
	
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	
    	if(msg instanceof Transporter){
    		ctx.write(packageProtocol((Transporter)msg), promise);
    	}
        //ctx.flush();
        
    }
    
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	Transporter transporter = unpackageProtocol(msg);
    	if(transporter == null){
    		ctx.fireChannelReadComplete();
    	}else{
    		ctx.fireChannelRead(transporter);
    	}
    	
    }
}
