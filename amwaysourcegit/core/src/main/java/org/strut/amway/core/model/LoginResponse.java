package org.strut.amway.core.model;

/**
 * @author MY020219
 *
 */
public class LoginResponse {

	private String aboId;	
	private String message;
	private APIToken token;
	public APIToken getToken() {
		return token;
	}

	public void setToken(APIToken token) {
		this.token = token;
		
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
