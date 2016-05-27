/*
 * {@code Server}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.transport;

import java.util.concurrent.ConcurrentHashMap;

import com.uoko.rpc.handler.ServerProcessHandler;
import com.uoko.rpc.provider.Invoker;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Server {
	private ChannelFuture channelFuture;
	private ChannelFuture closeChannelFuture;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private ServerBootstrap bootstrap;
	
	
	int port;
	
	
	public Server(int port,ConcurrentHashMap<String,Invoker> serviceInvokers){
		bossGroup = new NioEventLoopGroup(); // (1) 
		workerGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		
		bootstrap.group(bossGroup, workerGroup)
		.channel(NioServerSocketChannel.class) // (3)  
        .option(ChannelOption.SO_BACKLOG, 1024)          // (5)  
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childHandler(new ChannelInitializer<SocketChannel>() {  
            @Override  
            public void initChannel(SocketChannel ch) throws Exception {
            	ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this
		                .getClass().getClassLoader()))); 
            	ch.pipeline().addLast(new ObjectEncoder());
                ch.pipeline().addLast(new ServerProcessHandler(serviceInvokers));  
            }
            });// (6)  
		
		this.port = port;
		
	}
	
	/*
	 * 
	 * start server
	 * 
	 * 
	 * */
	public void start() throws InterruptedException{
		try {
			channelFuture = bootstrap.bind(this.port).sync();
			closeChannelFuture = channelFuture.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();  
			workerGroup.shutdownGracefully();
		}
	}
	
	/*
	 * 
	 * 
	 * close server
	 * 
	 * 
	 * 
	 * */
	public void close(){
		closeChannelFuture.channel().close();
	}
}
