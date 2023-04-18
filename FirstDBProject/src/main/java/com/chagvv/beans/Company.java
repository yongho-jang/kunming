package com.chagvv.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Company {
	
	private String isin;
	private String issueCode;
	private String issueName;
	private String ListingDate;
	private String marketType;
	private String securitiesType;
	private String companyCategory;
	private String shareType;
	private int parValue;
	private long noOfListedShares;
	
	public String getIsin() {
		return isin;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}
	public String getIssueCode() {
		return issueCode;
	}
	public void setIssueCode(String issueCode) {
		this.issueCode = issueCode;
	}
	public String getIssueName() {
		return issueName;
	}
	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}
	public String getListingDate() {
		return ListingDate;
	}
	public void setListingDate(String listingDate) {
		ListingDate = listingDate;
	}
	public String getMarketType() {
		return marketType;
	}
	public void setMarketType(String marketType) {
		this.marketType = marketType;
	}
	public String getSecuritiesType() {
		return securitiesType;
	}
	public void setSecuritiesType(String securitiesType) {
		this.securitiesType = securitiesType;
	}
	public String getCompanyCategory() {
		return companyCategory;
	}
	public void setCompanyCategory(String companyCategory) {
		this.companyCategory = companyCategory;
	}
	public String getShareType() {
		return shareType;
	}
	public void setShareType(String shareType) {
		this.shareType = shareType;
	}
	public int getParValue() {
		return parValue;
	}
	public void setParValue(int parValue) {
		this.parValue = parValue;
	}
	public long getNoOfListedShares() {
		return noOfListedShares;
	}
	public void setNoOfListedShares(long noOfListedShares) {
		this.noOfListedShares = noOfListedShares;
	}
	
	@Override
	public String toString() {
		ObjectMapper objectMapper = new ObjectMapper();
        try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return issueCode;
		}
	}
}
