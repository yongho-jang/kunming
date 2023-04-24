package com.chagvv.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockData {

	//raw data
	String date;
	int openPrice;
	int highPrice;
	int lowPrice;
	int closePrice;
	long transactionVolume;
	double foreignerExhaustionRate;

	//계산
	int centerLine;
	int upperLine;
	int lowerLine;
	long obv;
	long ndi;
	double mdm;
	double tr;
	int obvSignal;
	
	//점수
	int score;

	@Override
	public String toString() {
		return date + "," + openPrice + "," + highPrice + "," + lowPrice + "," + closePrice + "," + transactionVolume + "," 
				+ foreignerExhaustionRate+ "," + centerLine+ "," + upperLine+ "," + lowerLine + "," +obv + "," + ndi+ "," + mdm+ "," + tr;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(int openPrice) {
		this.openPrice = openPrice;
	}

	public int getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(int highPrice) {
		this.highPrice = highPrice;
	}

	public int getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(int lowPrice) {
		this.lowPrice = lowPrice;
	}

	public int getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(int closePrice) {
		this.closePrice = closePrice;
	}

	public long getTransactionVolume() {
		return transactionVolume;
	}

	public void setTransactionVolume(long transactionVolume) {
		this.transactionVolume = transactionVolume;
	}

	public double getForeignerExhaustionRate() {
		return foreignerExhaustionRate;
	}

	public void setForeignerExhaustionRate(double foreignerExhaustionRate) {
		this.foreignerExhaustionRate = foreignerExhaustionRate;
	}

	public int getCenterLine() {
		return centerLine;
	}

	public void setCenterLine(int centerLine) {
		this.centerLine = centerLine;
	}

	public int getUpperLine() {
		return upperLine;
	}

	public void setUpperLine(int upperLine) {
		this.upperLine = upperLine;
	}

	public int getLowerLine() {
		return lowerLine;
	}

	public void setLowerLine(int lowerLine) {
		this.lowerLine = lowerLine;
	}

	public long getObv() {
		return obv;
	}

	public void setObv(long obv) {
		this.obv = obv;
	}

	public long getNdi() {
		return ndi;
	}

	public void setNdi(long ndi) {
		this.ndi = ndi;
	}

	public double getMdm() {
		return mdm;
	}

	public void setMdm(double mdm) {
		this.mdm = mdm;
	}

	public double getTr() {
		return tr;
	}

	public void setTr(double tr) {
		this.tr = tr;
	}

	public int getObvSignal() {
		return obvSignal;
	}

	public void setObvSignal(int obvSignal) {
		this.obvSignal = obvSignal;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	
}
