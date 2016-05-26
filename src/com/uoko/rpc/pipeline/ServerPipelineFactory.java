package com.uoko.rpc.pipeline;



import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class ServerPipelineFactory implements ChannelPipelineFactory {
	
	private SimpleChannelHandler invokeHandler;
	
	public ServerPipelineFactory(SimpleChannelHandler invokeHandler){
		this.invokeHandler = invokeHandler;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		return Channels.pipeline(
				//weakCachingConcurrentResolver concurrent
				new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this
		                .getClass().getClassLoader())), 
				new ObjectEncoder(),
				this.invokeHandler
				);
	}

}
