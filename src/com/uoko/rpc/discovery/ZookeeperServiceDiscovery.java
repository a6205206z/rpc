/*
 * {@code ZookeeperServiceDiscovery}
 * 
 *
 *
 * @author      Cean Cheng
 * */

package com.uoko.rpc.discovery;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperServiceDiscovery
implements ServiceDiscovery{
	
	private static final Logger logger = Logger.getLogger(ZookeeperServiceDiscovery.class);
	private CountDownLatch latch = new CountDownLatch(1);
	
	private String connectionString;
	private String rootPath;
	private int sessionTimeout;
	private ZooKeeper zookeeper;
	
	public void setZookeeper(String connectionString){
		this.connectionString = connectionString;
	}
	
	public void setZookeeperRootPath(String rootPath){
		this.rootPath = rootPath;
	}
	
	public void setSessionTimeout(int sessionTimeout){
		this.sessionTimeout = sessionTimeout;
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
	
	private void WatchNode(ZooKeeper zk,String serviceAddressZKPath,ServiceDiscoveryHandler discoveryHandler){
		try {
			List<String> addressList = zk.getChildren(serviceAddressZKPath, new Watcher(){
				@Override
				public void process(WatchedEvent event) {
					if(event.getType() == Event.EventType.NodeChildrenChanged){
						WatchNode(zk,serviceAddressZKPath,discoveryHandler);
					}
				}
			});

			if(discoveryHandler != null){
				discoveryHandler.serviceChanged(addressList);
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	@Override
	public <T> void beginDiscovery(final Class<T> interfaceClass,String version,ServiceDiscoveryHandler discoveryHandler) {
		logger.info("Service Discovery init.");
		zookeeper = zkConnect();
		String serviceAddressZKPath = rootPath + "/" + interfaceClass.getName() + "/" + version;
		if(zookeeper!=null){
			WatchNode(zookeeper,serviceAddressZKPath,discoveryHandler);
		}
	}

	@Override
	public void endDiscovery() {
		try {
			if(zookeeper !=null){
				zookeeper.close();
			}
		} catch (InterruptedException e) {
			logger.error(e);
		}
		
	}
}
