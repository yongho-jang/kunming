package com.chagvv.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chagvv.beans.StockData;
import com.chagvv.beans.AnalyzeData;
import com.chagvv.beans.Company;
import com.chagvv.service.AnalyzeService;
import com.chagvv.service.CompanyService;
import com.chagvv.service.DataGatheringManager;

import jakarta.annotation.PostConstruct;

@Controller
public class StockController {
	
	CompanyService companyService;
	AnalyzeService analyzeService;
	
	public StockController(CompanyService service, AnalyzeService analyzeService) {

		this.companyService = service;
		this.analyzeService = analyzeService;
		
		try {
			companyService.reloadAllCompanies();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/analyze")
	public String analyze(ModelMap map) { 

		return "analyze";
	}
	
	@GetMapping("/companyList")
	public String companyList(ModelMap map) {
		
		List<Company> list = companyService.getAllCompanyList();
		
		map.addAttribute("companyList", list);
		return "companyList";
	}
	
	@GetMapping("/analyzeCompany")
	@ResponseBody 
	public List<StockData> analyzeCompany(@RequestParam String issueCode,@RequestParam Integer EnvelopeDuration,
			@RequestParam Integer EnvelopeRate,@RequestParam Integer ObvShortTerm,@RequestParam Integer ObvLongTerm) { 
		
		DataGatheringManager manager = new DataGatheringManager(EnvelopeDuration,EnvelopeRate,ObvShortTerm,ObvLongTerm);
		List<StockData> result = null;
		try {
			result = manager.getStockData(issueCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@GetMapping("/analyzeAllCompany")
	@ResponseBody 
	public Map<String, List<AnalyzeData>> analyzeAllCompany(
			@RequestParam String marketType,
			@RequestParam Integer EnvelopeDuration,
			@RequestParam Integer EnvelopeRate,
			@RequestParam Integer ObvShortTerm,
			@RequestParam Integer ObvLongTerm,
			@RequestParam Integer minEndCost,
			@RequestParam Integer maxCount) { 

		Map<String, List<AnalyzeData>> result = analyzeService.analyzeAllCompany(marketType, EnvelopeDuration, EnvelopeRate, ObvShortTerm, ObvLongTerm, minEndCost, maxCount);
		
		return result;
	}
	
	@GetMapping("/setCompanyAddData")
	@ResponseBody 
	public Map<String,String> setCompanyAddData(@RequestParam String issueCode) {
		
        final String crawlingEnterUrl = "https://finance.naver.com/item/main.naver?code=" + issueCode;
        Connection conn = Jsoup.connect(crawlingEnterUrl);
        Map<String,String> result = new HashMap<>();
        
        try {
            Document document = conn.get();
            Elements ths = document.getElementsByTag("th");
            
            for(int i=0; i <ths.size() ; i++) {
            	Element el = ths.get(i);

            	if("매출액".equals(el.text())) {
                	Element el01 = el.nextElementSibling();
                	
                	if(el01!=null) {
                		String number1 = el01.text().replaceAll(",", "");
                		if(number1.trim().length()>0)
	                		result.put("salesCost1", number1);
                		
	                	Element el02 = el01.nextElementSibling();
	                	
	                	if(el02!=null) {
	                		
	                		String number2 = el02.text().replaceAll(",", "");
	                		if(number2.trim().length()>0)
		                		result.put("salesCost2", number2);
	                		
		                	Element el03 = el02.nextElementSibling();
		                	
		                	if(el03!=null) {

			                	String number3 = el03.text().replaceAll(",", "");
			                	if(number3.trim().length()>0)
			                		result.put("salesCost3", number3);
			                	
			                	Element el04 = el03.nextElementSibling();
			                	
			                	if(el04!=null) {
			                		String number4 = el04.text().replaceAll(",", "");
			                		if(number4.trim().length()>0)
			                			result.put("salesCost4", number4);
			                	}
		                	}
	                	}
                	}
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return result;
    }
}
