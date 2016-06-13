package com.uoko.rpc.provider;


import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class SimpleExporter extends Exporter {

	public SimpleExporter(String address, int port) {
		super(address, port);
	}

	@Override
	protected void setPipeline(SocketChannel ch) {
    	ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this
                .getClass().getClassLoader()))); 
    	ch.pipeline().addLast(new ObjectEncoder());
	}
}
