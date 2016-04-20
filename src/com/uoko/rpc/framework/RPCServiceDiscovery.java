/*
 * {@code RPCServiceDiscovery} is a discovery service use on ZooKeeper
 *
 *
 * @author      Cean Cheng
 * */
package com.uoko.rpc.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class RPCServiceDiscovery {
	private static final Logger logger = Logger.getLogger(RPCServiceDiscovery.class);
	private CountDownLatch latch = new CountDownLatch(1);
	
	private String connectionString;
	private String rootPath;
	private int sessionTimeout;
	private RPCServiceDiscoveryHandler serviceDiscoveryHandler;
	
	
	public RPCServiceDiscovery(String zkConnectionString,String registerRootPath,
			int zkSessionTimeout,RPCServiceDiscoveryHandler discoveryHandler){
		this.connectionString = zkConnectionString;
		this.rootPath = registerRootPath;
		this.sessionTimeout = zkSessionTimeout;
		this.serviceDiscoveryHandler = discoveryHandler;
	}
	
	public void Init(){
		logger.info("Service Discovery init.");
		ZooKeeper zk = zkConnect();
		if(zk!=null){
			WatchNode(zk);
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
	
	private void WatchNode(ZooKeeper zk){
		try {
			List<String> serviceInfos = new ArrayList<String>();
			List<String> nodeList = zk.getChildren(rootPath, new Watcher(){
				@Override
				public void process(WatchedEvent event) {
					if(event.getType() == Event.EventType.NodeChildrenChanged){
						WatchNode(zk);
					}
				}
			});
			
			for(String node:nodeList){
				byte[] buffer = zk.getData(rootPath + "/" +node, false, null);
				String data = new String(buffer);
				serviceInfos.add(data);
			}
			
			if(this.serviceDiscoveryHandler != null){
				this.serviceDiscoveryHandler.serviceChanged(serviceInfos);
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
