package com.chagvv;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.chagvv.service.CompanyService;

@SpringBootTest
public class LoadAllCompanies {
	
	@Autowired
	CompanyService service;
	
	@Test
	public void reloadCompany() throws IOException {
		
		service.reloadAllCompanies();
	}
}
