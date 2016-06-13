package com.uoko.rpc.protocol;



import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ProtocolProcessHandler extends ChannelHandlerAdapter {
	public ProtocolProcessHandler(){
		
	}
	
	//protected abstract void readBody();
	//protected abstract void writeBody();
	
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        ctx.write(msg, promise);
        ctx.flush();
    }
    
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
        ctx.fireChannelRead(msg);
    }
}
