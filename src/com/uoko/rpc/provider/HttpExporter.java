package com.uoko.rpc.provider;

import com.uoko.rpc.protocol.HttpServerHandler;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpExporter extends Exporter {

	public HttpExporter(String address, int port) {
		super(address, port);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void setPipeline(SocketChannel ch) {
        ch.pipeline().addLast(new HttpResponseEncoder());
        ch.pipeline().addLast(new HttpRequestDecoder());
    	ch.pipeline().addLast(new HttpServerHandler());
	}
}
