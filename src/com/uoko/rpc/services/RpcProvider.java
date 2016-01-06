package com.uoko.rpc.services;
import com.uoko.rpc.framework.RPCFramework;

public class RpcProvider {
	public static void main(String[] args) throws Exception{
		HelloService service = new HelloServiceImpl();
		RPCFramework.export(service, 8080);
	}
}
