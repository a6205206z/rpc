/*
 * {@code Server}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.transport;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.uoko.rpc.pipeline.ServerPipelineFactory;

public class Server {
	private Channel bind;
	final ServerBootstrap bootstrap = new ServerBootstrap(
			new NioServerSocketChannelFactory(
					Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()
					)
			);
	int port;
	
	public Server(int port,SimpleChannelHandler invokeHandler){
		
		this.port = port;
		
		bootstrap.setPipelineFactory(new ServerPipelineFactory(invokeHandler));
		
	}
	
	/*
	 * 
	 * start server
	 * 
	 * 
	 * */
	public void start(){
		bind = bootstrap.bind(new InetSocketAddress(port));
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
		bind.close();
	}

	/*
	 * 
	 * 
	 * get server address
	 * 
	 * 
	 * */
	public SocketAddress getServerAddress() {
		SocketAddress address = null;
		if(bind != null){
			address = bind.getLocalAddress();
		}
		return address;
	}
}
