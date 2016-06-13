package com.uoko.rpc.provider;


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
	
	public Exporter create(){
		Exporter exporter = new HttpExporter("127.0.0.1",8080);
		return exporter;
	}
}
