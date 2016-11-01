package org.strut.amway.core.model;

public class AmwayTodayLoginResponse {
	
	private String message;
	private String aboId;
	private String loginToken;
	private String loginType;
	public String getLoginToken() {
		return loginToken;
	}
	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAboId() {
		return aboId;
	}
	public void setAboId(String aboId) {
		this.aboId = aboId;
	}

}
