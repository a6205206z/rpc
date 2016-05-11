package com.uoko.rpc.loadbalance;

import java.util.List;
import java.util.Random;

public class RandomSelect 
implements LoadbalanceStrategy{

	@Override
	public <T> T selectOne(List<T> list) {
		Random random =new Random();
		int index = random.nextInt(list.size());
		return list.get(index);
	}

}
