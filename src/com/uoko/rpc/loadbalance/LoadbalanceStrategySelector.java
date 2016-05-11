package com.uoko.rpc.loadbalance;

public class LoadbalanceStrategySelector {
	public static LoadbalanceStrategy SelectStrategy(String strategyName){
		
		LoadbalanceStrategy loadbalanceStrategy = null;
		
		switch(strategyName){
		case "RandomSelect":
				loadbalanceStrategy = new RandomSelect();
			break;
			default:
				loadbalanceStrategy = new RandomSelect();
			break;
		}
		return loadbalanceStrategy;
	}
}
