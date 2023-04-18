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

import com.chagvv.beans.StockData;

public class DataGatheringManager {
	
	public static final String DAY_FORMAT = "yyyyMMdd";
	public static final String METHOD = "GET";
	public static final String CONTENT_TYPE_NAME = "Content-type";
	public static final String CONTENT_TYPE_VALUE = "application/json";
	public static final String CHARSET = "UTF-8";
	public static final String NAVER_SISE_URL = "https://api.finance.naver.com/siseJson.naver?symbol=%s&requestType=1&startTime=%s&endTime=%s&timeframe=day";
	public static final String LINE_START = "[\"";
	public static final String COMMA = ",";
	
	SimpleDateFormat format = new SimpleDateFormat(DAY_FORMAT);
	String startDayStr;
	String endDayStr;
	int envelopeDuration;  //env 중심선 계산 평균값을 찾기 위한 기간 
	int envelopeRate; 	//end 상,하한선을 계산 하기 위한 비율 
	
	public DataGatheringManager(int envelopeDuration, int envelopeRate) {
		this.envelopeDuration = envelopeDuration;
		this.envelopeRate = envelopeRate;
		
		Calendar endDay = Calendar.getInstance();
		Calendar startDay = Calendar.getInstance();
		startDay.add(Calendar.DATE, -(envelopeDuration+1));
		
		startDayStr = format.format(startDay.getTime());
		endDayStr = format.format(endDay.getTime());
		
		
	}
	
	/**
	 * naver 에서 기본 주식 정보를 찾는다. Search raw data
	 * @return
	 * @throws IOException
	 */
	public List<StockData> getStockData(String stockNumber) throws IOException{
		
		List<StockData> list = new ArrayList<>();
		
		String domain = String.format(NAVER_SISE_URL,stockNumber, startDayStr, endDayStr);

		URL u = new URL(domain); // (1)
		HttpURLConnection con = (HttpURLConnection) u.openConnection(); // (2)
		con.setRequestMethod(METHOD);
		con.setDoOutput(true);
		con.setRequestProperty(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE);

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
					
			}else {
				data.setObv(data.getTransactionVolume());
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
}
