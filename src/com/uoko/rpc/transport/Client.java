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
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class Client {
	final ClientBootstrap bootstrap = new ClientBootstrap(
			new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool())
			);
	
	String host;
	int port;
	
	public Client(String host,int port){
		this.host = host;
		this.port = port;
	}
	
	public void invoke(SimpleChannelHandler invokeMthodHandler){
		bootstrap.setPipelineFactory(new ChannelPipelineFactory(){

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(
						new ObjectDecoder(ClassResolvers.cacheDisabled(this
				                .getClass().getClassLoader())), 
						new ObjectEncoder(), 
						invokeMthodHandler
						);
			}
			
		});
		
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,port));
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		bootstrap.releaseExternalResources();
	}
}
