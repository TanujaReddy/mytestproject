package org.strut.amway.core.servlets.login;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jcr.Node;
import javax.servlet.http.Cookie;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.model.ABOProfile;
import org.strut.amway.core.model.AmwayTodayLoginResponse;
import org.strut.amway.core.model.Credentials;
import org.strut.amway.core.model.LoginResponse;
import org.strut.amway.core.services.ISEALoginService;
import org.strut.amway.core.servlets.AbstractJsonServlet;
import org.strut.amway.core.util.ArticleConstants;

import com.google.gson.Gson;

@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/amwaytoday/login", methods = "POST", metatype = false)
public class AmwaySEALoginServlet extends AbstractJsonServlet {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AmwaySEALoginServlet.class);
	@Reference
	private ISEALoginService loginService;

	protected ISEALoginService getLoginService() {
		return this.loginService;
	}

	@Override
	protected void handle(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws Exception {
		final ResourceResolver resourceResolver = request.getResourceResolver();
		
		AmwayTodayLoginResponse amwayResponse = new AmwayTodayLoginResponse();
		Credentials creds = new Credentials(request.getParameter("username"),
				request.getParameter("password"),null,null,request.getParameter("country"));
		
		Gson gson = new Gson();
		//Call login service to authenticate the ABO
		LoginResponse loginresponse = getLoginService().login(creds);
		if (loginresponse != null) {
			//If Login successful
			if (loginresponse.getMessage().equalsIgnoreCase("success")) {
				
//				Cookie cookie = new Cookie("dfx-auth-token", loginresponse
//						.getToken().getValue());
				
				String expiry = loginresponse.getToken().getExpiration();
				SimpleDateFormat newFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss Z"); 
				Date expiryDate = newFormat.parse(expiry);
				Date currentDate = new Date();
				long diff = expiryDate.getTime() - currentDate.getTime();
				int seconds = (int) ((diff / 1000));
				
				LOGGER.info("Seconds"+seconds);
//				cookie.setPath("/");
//				cookie.setMaxAge(seconds-10);
//				response.addCookie(cookie);				
				
				amwayResponse.setAboId(loginresponse.getAboId());
				amwayResponse.setMessage("success");
				amwayResponse.setLoginToken(loginresponse.getToken().getValue());
				
				
				//Fetch the ABO Profile to read the PIN Level
				ABOProfile profile = getLoginService().getProfile(loginresponse
						.getToken().getValue(),request.getParameter("country"));
				if(profile != null)
				{
					Node distclass = null;
					if(resourceResolver.getResource("/etc/amwaytoday/pinlevel/"+profile.getAboClass()) != null){
						distclass = (resourceResolver.getResource("/etc/amwaytoday/pinlevel/"+profile.getAboClass())).adaptTo(Node.class);						
					}
					else if(resourceResolver.getResource("/etc/amwaytoday/pinlevel/"+profile.getCurrentAwardLevel()) != null){
						distclass = (resourceResolver.getResource("/etc/amwaytoday/pinlevel/"+profile.getCurrentAwardLevel())).adaptTo(Node.class);	
					}
					
					if(distclass != null){
						
						if(distclass.hasProperty("group")){
//							Cookie cookiePin = new Cookie(
//									ArticleConstants.PERSONALIZATION_COOKIE, distclass.getProperty("group").getValue().getString());
//							cookiePin.setMaxAge(seconds-10);
//							cookiePin.setPath("/");
//							response.addCookie(cookiePin);
							
							amwayResponse.setLoginType(distclass.getProperty("group").getValue().getString());
						}
						
					}			
					
					else
					{
//						Cookie cookiePin = new Cookie(
//								ArticleConstants.PERSONALIZATION_COOKIE, "ABO");
//						cookiePin.setMaxAge(seconds-10);
//						cookiePin.setPath("/");
//						response.addCookie(cookiePin);
						amwayResponse.setLoginType("ABO");
					}
				}
				else
				{
					amwayResponse.setAboId(loginresponse.getAboId());
					amwayResponse.setMessage("failure");
				}
				
			} else {
				amwayResponse.setAboId(loginresponse.getAboId());
				amwayResponse.setMessage("failure");
			}

		} else {
			amwayResponse.setAboId(creds.getUsername());
			amwayResponse.setMessage("failure");
		}	
		
		writeJsonToResponse(response, gson.toJson(amwayResponse));
	}

}
