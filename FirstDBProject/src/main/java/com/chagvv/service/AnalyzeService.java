package com.chagvv.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.chagvv.beans.AnalyzeData;
import com.chagvv.beans.Company;
import com.chagvv.beans.StockData;

@Component
public class AnalyzeService {
	
	CompanyService companyService;
	
	static final String LOWER_RESULT = "lowerResult";
	static final String UPPER_RESULT = "upperResult";
	
	public AnalyzeService(CompanyService companyService) {
		this.companyService = companyService;
	}

	public Map<String,List<AnalyzeData>> analyzeAllCompany(String marketType, int EnvelopeDuration, int EnvelopeRate, int ObvShortTerm,int ObvLongTerm, int minEndCost, int maxCount) {
		
		List<Company> list = companyService.getAllCompanyList();
		if(marketType!=null && !"ALL".equals(marketType)) {
			list = companyService.searchMarketType(marketType);
		}
		
		DataGatheringManager manager = new DataGatheringManager(EnvelopeDuration,EnvelopeRate,ObvShortTerm,ObvLongTerm);
		
		Map<String,List<AnalyzeData>> result = new HashMap<>();
		
		List<AnalyzeData> lowerResult = new ArrayList<>();
		List<AnalyzeData> UpperResult = new ArrayList<>();
		
		for(Company company: list) {
			try {
				List<StockData> stockList = manager.getStockData(company.getIssueCode());
				
				StockData cdata = stockList.get(result.size()-1);
				
				if(cdata.getClosePrice() < minEndCost){
					continue;
				}
				
				cdata.setLowerScore(Math.round((cdata.getClosePrice() - cdata.getLowerLine())*100/(cdata.getCenterLine()-cdata.getLowerLine())));
				cdata.setUpperScore(Math.round((cdata.getUpperLine() - cdata.getClosePrice() )*100/(cdata.getUpperLine()-cdata.getCenterLine())));

				insertTr(lowerResult, cdata, cdata.getLowerScore(), company, maxCount);
				insertTr(UpperResult, cdata, cdata.getUpperScore(), company, maxCount);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		result.put(LOWER_RESULT, lowerResult);
		result.put(UPPER_RESULT, lowerResult);
		
		return result;
	}
	
	
	private void insertTr(List<AnalyzeData> ranking,StockData cdata, int tscore, Company company, int maxCount ) {
		
		int count = 0;
		for(AnalyzeData data: ranking){
			int score = data.getScore(); //score
			if(score > tscore){
				break;
			}
			count++;
		}

		if(count < maxCount) {
			AnalyzeData adata = new AnalyzeData();
			adata.setCompany(company);
			adata.setScore(tscore);
			adata.setStock(cdata);
			ranking.add(count,adata);
		}
		
		if(ranking.size() > maxCount) {
			for(int i= ranking.size()-1; i>= maxCount ; i--) {
				ranking.remove(i);
			}
		}
	}
}
