package com.chagvv.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyzeData {
	
	private Company company;
	private StockData stock;
	private int score;
	
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
	
	
}
