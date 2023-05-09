package com.chagvv.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chagvv.beans.StockData;
import com.chagvv.beans.VoteAccount;
import com.chagvv.service.DataGatheringManager;
import com.chagvv.service.VoteService;

@Controller
@RequestMapping("vote")
public class VoteController {
	
	VoteService service;

	public VoteController(VoteService service) {

		this.service = service;

		try {
			service.reloadAllAccounts();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/accountList")
	public String accountList(ModelMap map) {
		
		List<VoteAccount> list = service.getAllAccountList();
		map.addAttribute("accountList", list);
		return "accountList";
	}
	
	@GetMapping("/manageVote")
	public String manageVote(ModelMap map) { 

		List<VoteAccount> list = service.getAllAccountList();
		map.addAttribute("accountList", list);
		return "manageVote";
	}
	
	
	@GetMapping("/voteAccount")
	@ResponseBody 
	public String voteAccount(@RequestParam String email,@RequestParam String password,
			@RequestParam String proxy,@RequestParam String keyword,@RequestParam Boolean backgroundCheck) throws InterruptedException { 
		
		
		service.voteAccount(email, password, proxy, keyword, backgroundCheck);
		String result = "{\"result\":\"ok\"}";
		
		return result;
	}
}
