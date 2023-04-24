package com.chagvv.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chagvv.beans.StockData;
import com.chagvv.beans.Company;
import com.chagvv.service.CompanyService;
import com.chagvv.service.DataGatheringManager;

import jakarta.annotation.PostConstruct;

@Controller
public class StockController {
	
	CompanyService companyService;
	
	public StockController(CompanyService service) {

		this.companyService = service;
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
		List<Company> list = companyService.getAllCompanyList();
		
		map.addAttribute("companyList", list);
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
}
