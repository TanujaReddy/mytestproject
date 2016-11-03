package org.strut.amway.core.services.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.model.ABOProfile;
import org.strut.amway.core.model.APIToken;
import org.strut.amway.core.model.Credentials;
import org.strut.amway.core.model.LoginResponse;
import org.strut.amway.core.services.ISEALoginService;

import com.google.gson.Gson;

@Service(value = ISEALoginService.class)
@Component(immediate = true, metatype = true,
label = "AmwayToday SEA Login Service")
public class SEALoginServiceImpl implements ISEALoginService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SEALoginServiceImpl.class);

	 @Property(label = "DFX Host", description = "DFX Host", value = "dfx-uat.preprod.apac.amway.net")
	    public static final String DFX_HOST = "dfx.host";
	 
	 @Property(label = "Protocol", description = "DFX Protocol", value = "https")
	    public static final String DFX_PROTOCOL = "dfx.protocol";
	 
	 @Property(label = "DFX APP ID", description = "DFX APP ID", value = "fps_new")
	    public static final String DFX_APP_ID = "dfx.appid";
	 
	 @Property(label = "DFX APP Secret", description = "DFX APP Secret", value = "$2a$11$gxpnezmYfNJRYnw")
	    public static final String DFX_APP_SECRET = "dfx.appsecret";
	 
	 @Property(label = "DFX Request Time Out in ms", description = "DFX Request Time Out in ms", value = "20000")
	    public static final String DFX_CONNECTION_TIMEOUT = "dfx.timeout";
	 
	 private String dfxHost;
	 private String dfxProtocol;
	 private String dfxAppId;
	 private String dfxAppSecret;
	 private int timeout;
	 
	 @Activate
	 protected void activate(ComponentContext componentContext){
			configure(componentContext.getProperties());
	}
	 
		protected void configure(Dictionary<?, ?> properties) {
			//this.cleanupPath = OsgiUtil.toString(properties.get(CLEANUP_PATH), null);
			this.dfxHost = OsgiUtil.toString(properties.get(DFX_HOST),null);
			this.dfxProtocol = OsgiUtil.toString(properties.get(DFX_PROTOCOL),null);
			this.dfxAppId = OsgiUtil.toString(properties.get(DFX_APP_ID),null);
			this.dfxAppSecret = OsgiUtil.toString(properties.get(DFX_APP_SECRET),null);
			this.timeout = Integer.parseInt((OsgiUtil.toString(properties.get(DFX_CONNECTION_TIMEOUT),null)));
		}
	
	@Override
	public LoginResponse login(Credentials creds) {
		try {
			Gson gson = new Gson();
			creds.setAppId(this.dfxAppId);
			creds.setAppSecret(this.dfxAppSecret);
			StringEntity entity = new StringEntity(gson.toJson(creds));
			

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
			httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
			HttpPost post = new HttpPost(this.dfxProtocol+"://"+this.dfxHost+"/auth/login/"+creds.getCountryCode());
			LOGGER.info("URL"+post.getURI());
			post.setHeader("Content-Type", "application/json");
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			LoginResponse loginResponse = gson
					.fromJson(br, LoginResponse.class);

			httpClient.getConnectionManager().shutdown();
			return loginResponse;
		} catch (Exception e) {
			LOGGER.error("Error in SEA login::" + e);
			return null;
		}
	}

	@Override
	public ABOProfile getProfile(String token,String country) {
		try {
			Gson gson = new Gson();
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
			httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
			HttpGet getProfile = new HttpGet(
					this.dfxProtocol+"://"+this.dfxHost+"/auth/profile/"+country);
			getProfile.setHeader("Authorization", "Bearer " + token);

			HttpResponse response = httpClient.execute(getProfile);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			ABOProfile profile = gson.fromJson(br, ABOProfile.class);

			httpClient.getConnectionManager().shutdown();
			return profile;

		} catch (Exception e) {
			LOGGER.error("Error in reading SEA Profile::" + e);
			return null;
		}
	}

	@Override
	public boolean logout(String accessToken) {
		try{
		APIToken token = new APIToken();
		token.setValue(accessToken);
		Gson gson = new Gson();
		StringEntity entity = new StringEntity(gson.toJson(token));

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams httpParams = httpClient.getParams();
		httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
		httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
		HttpPost post = new HttpPost(this.dfxProtocol+"://"+this.dfxHost+"/auth/logout");
		LOGGER.info("Log Out URL"+post.getURI());
		post.setHeader("Content-Type", "application/json");
		post.setEntity(entity);
		HttpResponse response = httpClient.execute(post);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatusLine().getStatusCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));

		LoginResponse loginResponse = gson
				.fromJson(br, LoginResponse.class);

		httpClient.getConnectionManager().shutdown();
		if(loginResponse.getMessage().equalsIgnoreCase("success")){
			return true;
		}
		else
		{
			return true;
		}
		
		}
		catch(Exception e){
			
			LOGGER.error("Error during Log Out",e);
			return false;
		}
	}

}
