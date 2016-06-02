package com.uoko.rpc.handler;



import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;


@Sharable
public class ProtocolProcessHandler extends ChannelHandlerAdapter {
	public ProtocolProcessHandler(){
	}
	
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	 //GsonBuilder gsonBuilder = new GsonBuilder();  
    	 //gsonBuilder.registerTypeAdapter(Class.class,  
         //        new ClassSerializer());  
         //Gson gson = gsonBuilder.create();
         //String jsonMsg = gson.toJson(msg, Transporter.class);
        
    	/*
    	 * 
    	 * 
    	 * Serialization from entity to json,soap........
    	 * 
    	 * 
    	 * 
    	 * 
    	 * 
    	 * */
    	
         ctx.write(msg, promise);
         ctx.flush();
    }
    
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
   	 //GsonBuilder gsonBuilder = new GsonBuilder();  
   	 //gsonBuilder.registerTypeAdapter(Class.class,  
     //           new ClassSerializer());  
     //   Gson gson = gsonBuilder.create();
     //   Transporter data = gson.fromJson((String) msg, Transporter.class);
        
    	/*
    	 * 
    	 * 
    	 * Deserializer from json,soap........ to entity
    	 * 
    	 * 
    	 * 
    	 * 
    	 * */
    	
    	
        ctx.fireChannelRead(msg);
    }
}
