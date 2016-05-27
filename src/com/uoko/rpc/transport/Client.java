/*
 * {@code Client}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.transport;


import org.apache.log4j.Logger;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Client {
	private static final Logger logger = Logger.getLogger(Client.class);
	private Bootstrap bootstrap;
	private EventLoopGroup workerGroup;
	private String host;
	private int port;
	
	public Client(String host,int port){
		workerGroup = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class); // (3)  
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // (4)  
		
 
		this.host = host;
		this.port = port;
	}
	
	public void invoke(ChannelHandlerAdapter invokeMthodHandler){
		try {
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {  
	            @Override  
	            public void initChannel(SocketChannel ch) throws Exception {
	            	ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this
			                .getClass().getClassLoader()))); 
	            	ch.pipeline().addLast(new ObjectEncoder());
	                ch.pipeline().addLast(invokeMthodHandler);  
	                
	            }  
	        });
			
			ChannelFuture future = bootstrap.connect(host,port).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			logger.equals(e);
		} finally {
			workerGroup.shutdownGracefully();
        }
	}
}
