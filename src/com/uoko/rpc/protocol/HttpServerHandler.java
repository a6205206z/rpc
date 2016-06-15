/*
 * {@code HttpServerHandler}
 * 
 *
 *
 * @author      Cean Cheng
 * 
 * 
 * */
package com.uoko.rpc.protocol;


import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.uoko.rpc.common.ServerInfo;
import com.uoko.rpc.transport.MethodInfo;
import com.uoko.rpc.transport.ServiceInfo;
import com.uoko.rpc.transport.Transporter;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;


public class HttpServerHandler extends ProtocolHandler {
	private static final Logger logger = Logger.getLogger(HttpServerHandler.class);
	@Override
	protected Object packageProtocol(Transporter in) {

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        
		if(in instanceof Transporter){
			Transporter transporter = (Transporter)in;
			if(transporter.getStatusCode() == 200){
				response.setStatus(HttpResponseStatus.OK);
				 /*
				  * 
				  * 
				  * Serialize : Transporter to  String(json xml and so on)
				  * 
				  * 
				  * */
				
				
			}else{
				response.setStatus(HttpResponseStatus.BAD_REQUEST);
				response.content().writableBytes();
				try {
					response.content().writeBytes("BAD REQUEST".getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(e);
				}
			}
		}

		
        response.headers().set("Content-Type", "text/plain; charset=utf-8");
        response.headers().set("Server",ServerInfo.SERVER_NAME);
        response.headers().setInt("Content_Length",
                response.content().readableBytes());
		
		
		return response;
	}

	@Override
	protected Transporter unpackageProtocol(Object in) {
		Transporter transporter = null;
		
		if(in instanceof HttpRequest){
			//break it
			return null;
		}
		
		if (in instanceof HttpContent) {
			 HttpContent content = (HttpContent) in;
			 byte[] req = new byte[content.content().readableBytes()];
			 content.content().readBytes(req);
			 
			 try {
				String body = new String(req,"UTF-8");
				 /*
				  * 
				  * 
				  * Deserialize: String(json xml and so on) to Transporter
				  * 
				  * 
				  * */
				
				
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
			}
		}
		
		if(transporter == null){
			transporter = new Transporter(new ServiceInfo(), new MethodInfo());
		}
		
		return transporter;
	}

}
