package com.uoko.rpc.framework;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class RPCServiceRegistry {
	private static final Logger logger = Logger.getLogger(RPCServiceRegistry.class); 
	
	private CountDownLatch latch = new CountDownLatch(1);
	private String connectionString;
	private String registerPath;
	private int sessionTimeout;
	
	public RPCServiceRegistry(String zkConnectionString,String serviceRegisterPath,int zkSessionTimeout){
		this.connectionString = zkConnectionString;
		this.sessionTimeout = zkSessionTimeout;
		this.registerPath = serviceRegisterPath;
	}
	
	public void Register(final Object service,String address){
		if(service == null){
			throw new IllegalArgumentException("service instance == null");
		}
		
		
		String zkData = String.format("service name:%s\n",service.getClass().getName());
		zkData = String.format("address:%s\n",address);
		
		Method[] methods = service.getClass().getMethods();
		if(methods != null){
			for(int i=0; i<methods.length;++i){
				String.format("method: %s\n",methods[i].getName());
			}
		}
		
		ZooKeeper zk = zkConnect();
		if(zk != null){
			createNode(zk,zkData);
		}
	}
	
	
	private ZooKeeper zkConnect(){
		ZooKeeper zk = null;
		
		try {
			zk = new ZooKeeper(connectionString,sessionTimeout, new Watcher(){
				@Override
				public void process(WatchedEvent event) {
					if(event.getState() == Event.KeeperState.SyncConnected){
						latch.countDown();
					}
				}
			});
			latch.await();
		} catch (Exception e) {
			logger.error(e);
		}
		
		return zk;
	}
	
	private void createNode(ZooKeeper zk,String data){
		try {
			byte[] buffer = data.getBytes();
			String result = zk.create(registerPath, buffer, 
					ZooDefs.Ids.OPEN_ACL_UNSAFE, 
					CreateMode.EPHEMERAL_SEQUENTIAL);
			
			logger.info("create zookeeper node : " + result);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}