package com.chagvv.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.chagvv.beans.VoteAccount;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.netty.util.internal.StringUtil;

@Component
public class VoteService {
	
	private List<VoteAccount> allAccountList = new ArrayList<>(); 
	
	int randomMin = 1;
	int randomMax = 100;
	
	public void reloadAllAccounts() throws IOException{
		
		allAccountList.clear();
		
		File file = new File("AccountList.csv");
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"euc-kr"));
			
			//header
			String line = reader.readLine();
			
			if(line!=null) {
				//line
				line = reader.readLine();
				while(line !=null) {

					String[] data = line.split(",");
					
					VoteAccount account = new VoteAccount();
					account.setEmail(data[0]);
					account.setPassword(data[1]);
					if(data.length>2) {
						account.setProxy(data[2]);
					}
					
					allAccountList.add(account);

					line = reader.readLine();
				}
			}
		}finally{
			if(reader!=null)reader.close();
		}
	}

	public List<VoteAccount> getAllAccountList() {
		return allAccountList;
	}

	public void setAllAccountList(List<VoteAccount> allAccountList) {
		this.allAccountList = allAccountList;
	}
	

	public void voteAccount(String email,String password, String proxy,String keyword,Boolean backgroundCheck) throws InterruptedException {
		
		System.out.println("voteAccount !!");
		
		WebDriverManager.chromedriver().setup();
    	//System.setProperty("webdriver.chrome.driver","D:\\geckodriver.exe");
    	
		ChromeOptions options = new ChromeOptions();
		
		if(backgroundCheck) {
			options.addArguments("--headless");
			options.addArguments("--start-maximized");
		}
		options.addArguments("--window-size=800,600");
		options.addArguments("disable-gpu");
		options.addArguments("--remote-allow-origins=*");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.setCapability("ignoreProtectedModeSettings", true);
			
//		options.addArguments("--disable-web-security");
//		options.addArguments("--allow-running-insecure-conten t");
//		options.addArguments("--disable-cookie-encryption");
//		options.addArguments("--blink-settings=imagesEnabled=false");

		
		if(!StringUtil.isNullOrEmpty(proxy)) {
			//options.addArguments("--proxy-server="+ proxy);
			
			//Proxy proxyObj = new Proxy();
			//proxyObj.setHttpProxy(proxy);
			//options.setProxy(proxyObj);
		}

		//options.setExperimentalOption("prefs", "{'download.default_directory': '/path/to/download/directory'}");
		WebDriver driver = new ChromeDriver(options);
	
		driver.get("https://vote.pinemuse.com/ko/auth/login");

        // 이메일 입력
        WebElement emailEle = driver.findElement(By.name("email"));
        emailEle.sendKeys(email);
        WebElement passwordEle = driver.findElement(By.name("password"));
        passwordEle.sendKeys(password);
        
        WebElement loginButton = driver.findElement(By.id("formik-semantic-ui-react-submit-button"));
        loginButton.click();
        
        System.out.println("submit");
        
        Duration timeout = Duration.ofSeconds(60); 
        
        // 로그인 완료 후 페이지 확인
        new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("card")));
        System.out.println("1. Logged in to pinemuse!");
        
        if(voteCard(driver, keyword)) {
	        for(int i=0; i< 4; i++) {
	        	voteCard(driver, getRandomSearch());
	        }
        }
        
		driver.quit();
	}
	
	private String getRandomSearch() {
	    int randomInt = (int) ((Math.random() * (randomMax - randomMin)) + randomMin);
	    return Integer.toString(randomInt);
	}
	
	private boolean voteCard(WebDriver driver , String keyword) throws InterruptedException {
		
		boolean result = false;
		WebElement searchInput = driver.findElement(By.tagName("input"));
		searchInput.clear();
        searchInput.sendKeys(keyword);
        WebElement searchdiv = searchInput.findElement(By.xpath(".."));
        WebElement searchButton = searchdiv.findElement(By.xpath("following-sibling::*"));
        searchButton.click();
        
        System.out.println("2. search OK :" + keyword);
        
        Thread.sleep(3000);
        
        List<WebElement> spans = driver.findElements(By.tagName("div"));
        
        for(WebElement ele : spans) {
        	
        	String cc = ele.getAttribute("class");

        	if("ui card".equals(cc)) {
        		WebElement cardDiv = ele;
        		
        		System.out.println("3. find target card");
        		
        		List<WebElement> buttons = cardDiv.findElements(By.tagName("button"));
	       		for(WebElement bb : buttons) {
	       			WebElement buttonDiv = bb.findElement(By.xpath(".."));
	       			String bttonId = buttonDiv.getAttribute("id");
	       			if(bttonId.startsWith("tooltip_vote")) {
	       				System.out.println("4. find vote button");
	       				
	       				WebElement isNotVote = buttonDiv.findElement(By.tagName("i"));
	       				String cc2 = isNotVote.getAttribute("class");
	       				
	       				if("red heart outline large icon".equals(cc2)) {
	       					System.out.println("5. 투표 여부 체크");
	       					buttonDiv.click();
	       					result = true;
	       				}
	       				break;
	       			 }
	       		}
	       		break;
        	}
        }
        return result;
	}
}
