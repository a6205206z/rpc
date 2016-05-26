/*
 * {@code Client}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.transport;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;


import com.uoko.rpc.pipeline.ClientPipelineFactory;

public class Client {
	final ClientBootstrap bootstrap = new ClientBootstrap(
			new NioClientSocketChannelFactory(
					Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()
					)
			);
	
	String host;
	int port;
	
	public Client(String host,int port){
		this.host = host;
		this.port = port;
	}
	
	public void invoke(SimpleChannelHandler invokeMthodHandler){
		bootstrap.setPipelineFactory(new ClientPipelineFactory(invokeMthodHandler));
		
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,port));
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		bootstrap.releaseExternalResources();
	}
}
