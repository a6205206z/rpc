/*
 * {@code Server}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.transport;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class Server {
	final ServerBootstrap bootstrap = new ServerBootstrap(
			new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool())
			);
	String host;
	int port;
	
	public Server(int port,SimpleChannelHandler invokeHandler){
		
		this.port = port;
		bootstrap.setPipelineFactory(new ChannelPipelineFactory(){
			
			@Override
			public ChannelPipeline getPipeline() throws Exception {

				return Channels.pipeline(
						new ObjectDecoder(ClassResolvers.cacheDisabled(this
				                .getClass().getClassLoader())), 
						new ObjectEncoder(),
						invokeHandler
						);
			}

		});
		
	}
	
	public Channel start(){
		Channel bind = bootstrap.bind(new InetSocketAddress(port));
		return bind;
	}
}
