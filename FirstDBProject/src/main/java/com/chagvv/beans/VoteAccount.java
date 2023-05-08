package com.chagvv.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteAccount {
	
	private String email;
	private String password;
	private String proxy;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getProxy() {
		return proxy;
	}
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	@Override
	public String toString() {
		ObjectMapper objectMapper = new ObjectMapper();
        try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return email;
		}
	}
	
}
