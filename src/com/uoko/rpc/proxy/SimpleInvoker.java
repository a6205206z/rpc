package com.uoko.rpc.proxy;


import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class SimpleInvoker<T> extends Invoker<T> {
	protected SimpleInvoker(Class<T> interfaceClass, String version) {
		super(interfaceClass, version);
	}

	@Override
	protected void setPipeline(SocketChannel ch) {
    	ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this
                .getClass().getClassLoader()))); 
    	ch.pipeline().addLast(new ObjectEncoder());
	}
}
