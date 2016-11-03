package org.strut.amway.core.model;

public class Credentials {
	
	private String username;
	private String password;
	private String appId;
	private String appSecret;
	private String countryCode;
	
	
	public Credentials(String username,String password, String appId,String appSecret,String countryCode){
		this.username = username;
		this.password = password;
		this.appId = appId;
		this.appSecret = appSecret;
		this.countryCode = countryCode;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

}
