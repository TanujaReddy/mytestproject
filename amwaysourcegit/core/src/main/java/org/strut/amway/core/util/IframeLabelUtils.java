package org.strut.amway.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.sling.api.SlingHttpServletRequest;
import org.strut.amway.core.enumeration.EntityType;
import org.strut.amway.core.enumeration.IframeLabelType;
import org.strut.amway.core.model.Account;

public final class IframeLabelUtils {
	@SuppressWarnings("serial")
	private static Map<IframeLabelType, List<IframeLabelType>> iframeLabelMap = new HashMap<IframeLabelType, List<IframeLabelType>>() {
		{
			put(IframeLabelType.PUBLIC, Arrays.asList(IframeLabelType.PUBLIC));
			put(IframeLabelType.PRIVATE, Arrays.asList(
					IframeLabelType.PUBLIC, IframeLabelType.PRIVATE));
			put(IframeLabelType.IBO, Arrays.asList(IframeLabelType.PUBLIC,
					IframeLabelType.PRIVATE, IframeLabelType.IBO));
			put(IframeLabelType.CLIENT, Arrays.asList(IframeLabelType.PUBLIC,
					IframeLabelType.PRIVATE, IframeLabelType.CLIENT));
		}
	};
	/*********Start of Changes for AmwayToday v3.0**************/
	//Personalization ATO
	public static List<String> getIframeLabelByCookies(
			SlingHttpServletRequest request) {

		String value = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(ArticleConstants.PERSONALIZATION_COOKIE)) {
					value = cookie.getValue();
				}
			}
		}

		switch (value) {

		case "ABO": {
			String[] values = { "Public", "ABO" };
			return new ArrayList<String>(Arrays.asList(values));
		}

		case "Platinum": {
			String[] values = { "Public","ABO", "Platinum" };
			return new ArrayList<String>(Arrays.asList(values));
		}

		case "Diamond": {
			String[] values = { "Public", "ABO", "Platinum",
					"Diamond" };
			return new ArrayList<String>(Arrays.asList(values));
		}

		default:
			String[] values = { "Public" };
			return new ArrayList<String>(Arrays.asList(values));
		}

	}
	
	
	
	public static boolean isPersonalizationCookiePresent(SlingHttpServletRequest request)
	
	{
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(ArticleConstants.PERSONALIZATION_COOKIE)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*********End of Changes for AmwayToday v3.0**************/

	public static List<IframeLabelType> getIframeLabelBySession(
			final HttpSession session) {

		if (session != null
				&& session.getAttribute(AuthenticationConstants.LOGINED_USER) != null) {
			final Account loginedAccount = (Account) session
					.getAttribute(AuthenticationConstants.LOGINED_USER);
			final EntityType entityType = loginedAccount.getEntityType();
			if (EntityType.IBO.equals(entityType)) {
				return iframeLabelMap.get(IframeLabelType.IBO);
			} else if (EntityType.CLIENT.equals(entityType)) {
				return iframeLabelMap.get(IframeLabelType.CLIENT);
			}
			return iframeLabelMap.get(IframeLabelType.PRIVATE);
		}
		return iframeLabelMap.get(IframeLabelType.PUBLIC);
	}

}
