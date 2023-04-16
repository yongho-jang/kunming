package com.chagvv;

import java.io.IOException;
import java.util.List;

import com.chagvv.beans.StockData;
import com.chagvv.service.DataGatheringManager;

public class DataGatheringTest {
	public static void main(String[] args) throws IOException {
		
		DataGatheringManager manager = new DataGatheringManager(20,20);
		List<StockData> list = manager.getStockData("085370");
		
		for(StockData data: list) {
			System.out.println(data);
		}
	}
}
