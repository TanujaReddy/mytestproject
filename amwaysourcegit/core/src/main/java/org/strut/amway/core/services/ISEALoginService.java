package org.strut.amway.core.services;

import org.strut.amway.core.model.ABOProfile;
import org.strut.amway.core.model.Credentials;
import org.strut.amway.core.model.LoginResponse;

public interface ISEALoginService {
	/*********Start of Changes for AmwayToday v3.0**************/
	public LoginResponse login(Credentials creds);
	public ABOProfile getProfile(String token,String country);
	public boolean logout(String accessToken);
	/*********End of Changes for AmwayToday v3.0**************/

}
