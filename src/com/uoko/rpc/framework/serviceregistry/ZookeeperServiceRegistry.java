package com.uoko.rpc.framework.serviceregistry;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import com.uoko.rpc.framework.annotations.RPCMethod;

public class ZookeeperServiceRegistry 
implements ServiceRegistry{
	private static final Logger logger = Logger.getLogger(ZookeeperServiceRegistry.class);
	private CountDownLatch latch = new CountDownLatch(1);
	
	private String connectionString;
	private String rootPath;
	private int sessionTimeout;
	
	public void setZookeeper(String connectionString){
		this.connectionString = connectionString;
	}
	
	public void setZookeeperRootPath(String rootPath){
		this.rootPath = rootPath;
	}
	
	public void setSessionTimeout(int sessionTimeout){
		this.sessionTimeout = sessionTimeout;
	}
	
	@Override
	public <T> void Register(Class<T> interfaceClass, String version, String serviceAddress) {
		String methodsInfo = "";
		
		Method[] methods = interfaceClass.getMethods();
		if(methods != null){
			for(Method method:methods){
				if(method.getAnnotation(RPCMethod.class) != null){
					methodsInfo += String.format("method: %s\n",method.getName());
				}
			}
		}
		
		ZooKeeper zk = zkConnect();
		if(zk != null){
			createNode(zk,interfaceClass.getSimpleName(),version,serviceAddress,methodsInfo);
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
	
	/**
    create node in ZooKeeper
    
	 **/
	private void createNode(ZooKeeper zk, String serviceName, String version, String serviceAddress,String serviceInfo){
		try {
			if(zk.exists(rootPath, false) == null){
				zk.create(rootPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			
			/*
			 * This code needs to be optimized
			 * 
			 * */
			String servicePath = rootPath + "/" + serviceName;
			if(zk.exists(servicePath, false) == null){
				zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			
			servicePath += "/" + version;
			if(zk.exists(servicePath, false) == null){
				zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			
			servicePath += "/" + serviceAddress;
			byte[] buffer = serviceInfo.getBytes();
			String result = zk.create(servicePath, buffer, 
					ZooDefs.Ids.OPEN_ACL_UNSAFE, 
					CreateMode.EPHEMERAL);
			
			logger.info("create zookeeper node : " + result);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
