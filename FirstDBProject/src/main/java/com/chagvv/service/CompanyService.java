package com.chagvv.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.chagvv.beans.AnalyzeData;
import com.chagvv.beans.Company;

@Component
public class CompanyService {
	
	private List<Company> allCompanyList = new ArrayList<>(); 
	
	public void reloadAllCompanies() throws IOException{
		
		allCompanyList.clear();
		
		File file = new File("list.csv");
		
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
	
	public List<Company> searchMarketType(String marketType){
		
		List<Company> result = new ArrayList<>();
		
		for(Company com: allCompanyList) {
			if(marketType.equals(com.getMarketType())) {
				result.add(com);
			}
		}
		
		return result;
	}
	
	
	public void setCompanyAddData(AnalyzeData adata) {
		
		String issueCode = adata.getCompany().getIssueCode();
		
        final String crawlingEnterUrl = "https://finance.naver.com/item/main.naver?code=" + issueCode;
        Connection conn = Jsoup.connect(crawlingEnterUrl);
        
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
	                		adata.setSalesCost1(Integer.parseInt(number1));
                		
	                	Element el02 = el01.nextElementSibling();
	                	
	                	if(el02!=null) {
	                		
	                		String number2 = el02.text().replaceAll(",", "");
	                		if(number2.trim().length()>0)
	                			adata.setSalesCost2(Integer.parseInt(number2));
		                	Element el03 = el02.nextElementSibling();
		                	
		                	if(el03!=null) {

			                	String number3 = el03.text().replaceAll(",", "");
			                	if(number3.trim().length()>0)
			                		adata.setSalesCost3(Integer.parseInt(number3));
			                	
			                	Element el04 = el03.nextElementSibling();
			                	
			                	if(el04!=null) {
			                		String number4 = el04.text().replaceAll(",", "");
			                		if(number4.trim().length()>0)
			                			adata.setSalesCost4(Integer.parseInt(number4));
			                	}
		                	}
	                	}
                	}
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
