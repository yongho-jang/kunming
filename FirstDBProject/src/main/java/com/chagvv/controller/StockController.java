package com.chagvv.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chagvv.beans.AnalyzeData;
import com.chagvv.beans.Company;
import com.chagvv.beans.StockData;
import com.chagvv.service.AnalyzeService;
import com.chagvv.service.CompanyService;
import com.chagvv.service.DataGatheringManager;

@Controller
@RequestMapping("stock")
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
}
