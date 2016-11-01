package org.strut.amway.core.model;

/**
 * @author MY020219
 *
 */
public class APIToken {

	private String value;
	private String expiration;
	private String tokenType;
	private APIToken refreshToken;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public APIToken getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(APIToken refreshToken) {
		this.refreshToken = refreshToken;
	}
}
