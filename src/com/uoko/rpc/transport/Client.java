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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client  {
	private static final Logger logger = Logger.getLogger(Client.class);
	private Bootstrap bootstrap;
	private EventLoopGroup workerGroup;
	
	public Client(ChannelInitializer<SocketChannel> channelInitializer){
		workerGroup = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class); // (3)  
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // (4)
		bootstrap.handler(channelInitializer);
	}
	
	public void invokeWaitforResult(String host,int port,Transporter msg){
		try {
			ChannelFuture future = bootstrap.connect(host,port).sync();
			
			future.channel().write(msg);
			future.channel().flush();
		} catch (InterruptedException e) {
			logger.equals(e);
		} 
	}
	
	public void close(){
		if(workerGroup!=null){
			workerGroup.shutdownGracefully();
		}
	}
}
