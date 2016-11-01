package org.strut.amway.core.servlets.login;

import javax.servlet.http.Cookie;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.strut.amway.core.model.AmwayTodayLoginResponse;
import org.strut.amway.core.services.ISEALoginService;
import org.strut.amway.core.servlets.AbstractJsonServlet;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.ArticleLabelUtils;

import com.google.gson.Gson;

@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/amwaytoday/logout", methods = "POST", metatype = false)
public class AmwaySEALogoutServlet extends AbstractJsonServlet {

	@Reference
	private ISEALoginService loginService;

	protected ISEALoginService getLoginService() {
		return this.loginService;
	}

	@Override
	protected void handle(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws Exception {
		AmwayTodayLoginResponse amwayResponse = new AmwayTodayLoginResponse();

		Gson gson = new Gson();
		boolean logOut = getLoginService()
				.logout(request.getParameter("token"));

		if (logOut) {
			Cookie cookie = new Cookie("dfx-auth-token", "");
			cookie.setPath("/");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
			Cookie pinCookie = new Cookie(ArticleConstants.PERSONALIZATION_COOKIE,"");
			pinCookie.setMaxAge(0);
			pinCookie.setPath("/");
			response.addCookie(pinCookie);
			amwayResponse.setMessage("success");
		} else {
			amwayResponse.setMessage("failure");
		}

		writeJsonToResponse(response, gson.toJson(amwayResponse));
	}

}
