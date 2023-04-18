package com.chagvv.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.chagvv.beans.Company;

@Component
public class CompanyService {
	
	private List<Company> allCompanyList = new ArrayList<>(); 
	
	public void reloadAllCompanies() throws IOException{
		
		allCompanyList.clear();
		
		String dataLocation = "src\\main\\resources\\static\\data\\";
		File file = new File(dataLocation + "data_5155_20230418.csv");
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"euc-kr"));
			
			//header
			String line = reader.readLine();
			
			if(line!=null) {
				//line
				line = reader.readLine();
				while(line !=null) {

					line = line.substring(1,line.length()-1);
					line= line.replaceAll(",,",",\"\",");
					String[] data = line.split("\",\"");
					
					Company company = new Company();
					company.setIsin(data[0]);
					company.setIssueCode(data[1]);
					company.setIssueName(data[2]);
					company.setListingDate(data[5]);
					company.setMarketType(data[6]);
					company.setSecuritiesType(data[7]);
					company.setCompanyCategory(data[8]);
					company.setShareType(data[9]);
					try {
						company.setParValue(Integer.parseInt(data[10]));
					}catch(NumberFormatException ex) {}
					company.setNoOfListedShares(Long.parseLong(data[11]));
					
					allCompanyList.add(company);

					line = reader.readLine();
				}
			}
		}finally{
			if(reader!=null)reader.close();
		}
	}

	public List<Company> getAllCompanyList() {
		return allCompanyList;
	}

	public void setAllCompanyList(List<Company> allCompanyList) {
		this.allCompanyList = allCompanyList;
	}
	
	
}
