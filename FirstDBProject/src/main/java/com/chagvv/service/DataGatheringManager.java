package com.chagvv.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.text.Element;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.chagvv.beans.StockData;

public class DataGatheringManager {
	
	public static final String DAY_FORMAT = "yyyyMMdd";
	public static final String METHOD = "GET";
	public static final String CONTENT_TYPE_NAME = "Content-type";
	public static final String CONTENT_TYPE_VALUE = "application/json";
	public static final String CHARSET = "UTF-8";
	//public static final String NAVER_SISE_URL = "https://api.finance.naver.com/siseJson.naver?symbol=%s&requestType=1&startTime=%s&endTime=%s&timeframe=day";
	public static final String NAVER_SISE_URL = "https://finance.naver.com/item/sise_day.naver?code=%s&page=1";
	
	public static final String LINE_START = "[\"";
	public static final String COMMA = ",";
	
	SimpleDateFormat format = new SimpleDateFormat(DAY_FORMAT);
	String startDayStr;
	String endDayStr;
	int envelopeDuration;  //env 중심선 계산 평균값을 찾기 위한 기간 
	int envelopeRate; 	//end 상,하한선을 계산 하기 위한 비율 
	int obvShortTerm;
	int obvLongTerm;
	
	public DataGatheringManager(int envelopeDuration, int envelopeRate, int obvShortTerm, int obvLongTerm) {
		this.envelopeDuration = envelopeDuration;
		this.envelopeRate = envelopeRate;
		this.obvShortTerm = obvShortTerm;
		this.obvLongTerm = obvLongTerm;
		
		int duration = envelopeDuration;
		if(envelopeDuration < obvLongTerm) {
			duration = obvLongTerm;
		}
		
		Calendar endDay = Calendar.getInstance();
		Calendar startDay = Calendar.getInstance();
		startDay.add(Calendar.DATE, -(duration+1));
		
		startDayStr = format.format(startDay.getTime());
		endDayStr = format.format(endDay.getTime());
	}
	
	/**
	 * naver 에서 기본 주식 정보를 찾는다. Search raw data
	 * @return
	 * @throws IOException
	 */
	public List<StockData> getStockData(String stockNumber) throws IOException{
		
		System.out.println("getStockData");
		
		List<StockData> list = new ArrayList<>();
		
		try {
		
		String domain = String.format(NAVER_SISE_URL,stockNumber);

		URL u = new URL(domain); // (1)
		HttpURLConnection con = (HttpURLConnection) u.openConnection(); // (2)
		con.setRequestMethod(METHOD);
		con.setDoOutput(true);
		con.setRequestProperty(CONTENT_TYPE_NAME, "text/html;charset=EUC-KR");

		int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 데이터 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            Document doc = Jsoup.parse(response.toString());
            Elements tables = doc.getElementsByTag("table");
            Element table01 = (Element) tables.get(0);

        } else {
            System.out.println("HTTP request failed. Response Code: " + responseCode);
        }
		

		}catch (IOException e) {
            e.printStackTrace();
        }
		return list;
	}
	
	public List<StockData> getStockData_20230627(String stockNumber) throws IOException{
		
		List<StockData> list = new ArrayList<>();
		
		String domain = String.format(NAVER_SISE_URL,stockNumber, startDayStr, endDayStr);

		URL u = new URL(domain); // (1)
		HttpURLConnection con = (HttpURLConnection) u.openConnection(); // (2)
		con.setRequestMethod(METHOD);
		con.setDoOutput(true);
		con.setRequestProperty(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE);
		
		Object content = con.getContent();
		
		

		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), CHARSET));
		while(br.ready()) {
			String line = br.readLine();

			if(line.startsWith(LINE_START)) {
				line = line.substring(1, line.length()-2);
				String[] dayData = line.split(COMMA);
				StockData data = new StockData();
				data.setDate(dayData[0].trim().substring(1,9));
				data.setOpenPrice(Integer.parseInt(dayData[1].trim()));
				data.setHighPrice(Integer.parseInt(dayData[2].trim()));
				data.setLowPrice(Integer.parseInt(dayData[3].trim()));
				data.setClosePrice(Integer.parseInt(dayData[4].trim()));
				data.setTransactionVolume(Long.parseLong(dayData[5].trim()));
				data.setForeignerExhaustionRate(Double.parseDouble(dayData[6].trim()));
				list.add(data);
			}
		}
		
		searchEnv(list, envelopeDuration, envelopeRate);
		
		searchNdi(list);
		
		calculateOBVSignal(list,obvShortTerm,obvLongTerm);
		
		return list;
	}
	
	/**
	 * 중심선, 상한선, 하한선 계
	 * @param list
	 */
	private void searchEnv(List<StockData> list,int envelopeDuration,int envelopeRate) {


		List<StockData> edatas = new ArrayList<>();
			
		for(StockData data: list) {
				
			if(edatas.size()==envelopeDuration) {
				edatas.remove(0);
			}
			edatas.add(data);
				
			int sum = 0;
			for(StockData cal: edatas) {
				sum += cal.getClosePrice();
			}
			double avg = sum/(double)edatas.size();
				
			data.setCenterLine((int)Math.round(avg));
			double ss = avg * envelopeRate * 0.01;
			data.setUpperLine((int)Math.round(avg + ss));
			data.setLowerLine((int)Math.round(avg - ss));
		}
	}
	
	/**
	 * OBV, MDM, TR 계산 
	 * @param list
	 */
	private void searchNdi(List<StockData> list) {
			
		StockData preData=null;
		for(StockData data: list) {
				
			if(preData!=null) {
				double a = data.getHighPrice();
				double b = data.getLowPrice();
				double c = preData.getHighPrice();
				double d = preData.getLowPrice();
				double e = preData.getClosePrice();
				double f = data.getClosePrice();
				
				//OBV 찾기 
				if(f == e) {
					data.setObv(preData.getObv());
				}else if(f > e) {	
					data.setObv(preData.getObv() + data.getTransactionVolume());
				}else {
					data.setObv(preData.getObv() - data.getTransactionVolume());
				}
					
				//MDM
				if(d-b>0 && a-c<d-b) {
					data.setMdm(d-b);
				}
					
				//TR
				data.setTr(Math.max(a-b, Math.max(Math.abs(e-a), Math.abs(e-b))));
					
			}
			
			preData = data;
		}
			
		//NDI 찾기
		int ndiLimit = 14;
			
		List<StockData> ndidatas = new ArrayList<>();
			
		for(StockData data: list) {
				
			if(ndidatas.size()==ndiLimit) {
				ndidatas.remove(0);
			}
			ndidatas.add(data);
				
			int mdmsum = 0;
			int trsum = 0;
			for(StockData cal: ndidatas) {
				mdmsum += cal.getMdm();
				trsum += cal.getTr();
			}
			double mdmavg = mdmsum/(double)ndidatas.size();
			double travg = trsum/(double)ndidatas.size();
				
			data.setNdi( (long)Math.round(mdmavg / travg) );
		}
	}
	
	// OBV Signal을 계산하는 메소드 by AI 
    public void calculateOBVSignal(List<StockData> stockDataList, int shortTerm, int longTerm) {
        int obvSignal = 0;
        int shortTermSum = 0;
        int longTermSum = 0;
        for (int i = 0; i < stockDataList.size(); i++) {
            StockData stockData = stockDataList.get(i);
            if (i >= shortTerm) {
                shortTermSum -= stockDataList.get(i - shortTerm).getObv();
            }
            if (i >= longTerm) {
                longTermSum -= stockDataList.get(i - longTerm).getObv();
            }
            shortTermSum += stockData.getObv();
            longTermSum += stockData.getObv();
            double shortTermMovingAverage = shortTermSum / (double)shortTerm;
            double longTermMovingAverage = longTermSum / (double)longTerm;
            obvSignal = (int)(shortTermMovingAverage - longTermMovingAverage);
            stockData.setObvSignal(obvSignal);
        }
    }

}
