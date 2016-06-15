package com.uoko.rpc.provider;

import com.uoko.rpc.common.ProtocolOption;

public class ExporterFactory {
	
	private static ExporterFactory instance;
	
	private ExporterFactory(){
		
	}
	/*
	 * 
	 * synchronized
	 * 
	 * 
	 * */
	public static synchronized ExporterFactory getInstance() {  
		if(instance == null){
			instance = new ExporterFactory();
		}
		return instance;
	}
	
	public Exporter create(String address,int port,ProtocolOption protocol){
		Exporter exporter = null;
		switch(protocol){
			case simple:
				exporter = new SimpleExporter(address,port);
				break;
			case http:
				exporter = new HttpExporter(address,port);
				break;
		default:
			exporter = new SimpleExporter(address,port);
			break;
		}
		
		return exporter;
	}
}
