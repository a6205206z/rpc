/*
 * {@code Server}
 * 
 *
 *
 * @author      Cean Cheng
 * 
 * 
 * */

package com.uoko.rpc.transport;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
	private ChannelFuture channelFuture;
	private ChannelFuture closeChannelFuture;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private ServerBootstrap bootstrap;
	
	
	int port;
	
	
	public Server(int port,ChannelInitializer<SocketChannel> handler){
		bossGroup = new NioEventLoopGroup(); // (1) 
		workerGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		
		bootstrap.group(bossGroup, workerGroup)
		.channel(NioServerSocketChannel.class) // (3)  
        .option(ChannelOption.SO_BACKLOG, 1024)          // (5)  
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childHandler(handler);// (6)  
		
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
