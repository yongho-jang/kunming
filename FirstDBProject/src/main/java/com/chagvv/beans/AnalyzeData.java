package com.chagvv.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyzeData {
	
	private Company company;
	private StockData stock;
	private int score;
	private int salesCost1;
	private int salesCost2;
	private int salesCost3;
	private int salesCost4;
	
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public StockData getStock() {
		return stock;
	}
	public void setStock(StockData stock) {
		this.stock = stock;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getSalesCost1() {
		return salesCost1;
	}
	public void setSalesCost1(int salesCost1) {
		this.salesCost1 = salesCost1;
	}
	public int getSalesCost2() {
		return salesCost2;
	}
	public void setSalesCost2(int salesCost2) {
		this.salesCost2 = salesCost2;
	}
	public int getSalesCost3() {
		return salesCost3;
	}
	public void setSalesCost3(int salesCost3) {
		this.salesCost3 = salesCost3;
	}
	public int getSalesCost4() {
		return salesCost4;
	}
	public void setSalesCost4(int salesCost4) {
		this.salesCost4 = salesCost4;
	}
	
	
}
