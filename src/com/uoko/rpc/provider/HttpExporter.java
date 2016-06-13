package com.uoko.rpc.provider;

import com.uoko.rpc.protocol.ProtocolProcessHandler;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class HttpExporter extends Exporter {

	public HttpExporter(String address, int port) {
		super(address, port);
	}

	@Override
	protected void setPipeline(SocketChannel ch) {
    	ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this
                .getClass().getClassLoader()))); 
    	ch.pipeline().addLast(new ObjectEncoder());
    	ch.pipeline().addLast(new ProtocolProcessHandler());
	}
}
