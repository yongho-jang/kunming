package com.chagvv.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chagvv.beans.StockData;
import com.chagvv.entity.Company;
import com.chagvv.repository.CompanyRepository;
import com.chagvv.service.CompanyService;
import com.chagvv.service.DataGatheringManager;

@Controller
public class StockController {
	
	CompanyRepository companyRepository;
	CompanyService companyService;
	
	public StockController(CompanyRepository repository,CompanyService service) {
		this.companyRepository = repository;
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
		List<Company> list = companyRepository.findAll();
		
		map.addAttribute("companyList", list);
		return "analyze";
	}
	
	@GetMapping("/companyList")
	public String companyList(ModelMap map) {
		
		List<Company> list = companyRepository.findAll();
		
		map.addAttribute("companyList", list);
		return "companyList";
	}
	
	@GetMapping("/analyzeCompany")
	@ResponseBody 
	public List<StockData> analyzeCompany(@RequestParam String issueCode,@RequestParam Integer EnvelopeDuration,@RequestParam Integer EnvelopeRate) { 
		
		DataGatheringManager manager = new DataGatheringManager(EnvelopeDuration,EnvelopeRate);
		List<StockData> result = null;
		try {
			result = manager.getStockData(issueCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
