package org.strut.amway.core.controller;

import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import com.day.cq.wcm.api.Page;
import com.day.cq.tagging.Tag;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.strut.amway.core.util.AuthenticationConstants;

/**
 * Handle when user click on site-map link
 */
public class LoginController {

    private static Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    public LoginController() {
    }

    public Boolean getIsRedirect(final HttpSession httpSession, final Page article) {
        List<String> tags = new ArrayList<>();
        try {
            Locale locale = article.getLanguage(false);
            Tag[] a_tags = article.getTags();
            if (a_tags != null) {
                for (Tag tag : a_tags) {
                    tags.add(tag.getTitle(locale));
                }

            }
        } catch (Exception e) {
            LOGGER.error("Cannot get tags: " + e.getMessage(), e);
        }

        boolean isRequireLogIn = false;
        for (String tag : tags) {
            LOGGER.error("Current tag: " + tag);
            if (tag == "ABO" || tag == "Platinum" || tag == "Diamond") {
                isRequireLogIn = true;
            }
        }

        LOGGER.error("isRequireLogIn: " + isRequireLogIn);
        if (isRequireLogIn) {
            boolean isLoggedIn = false;
            try {
                final Object user = httpSession.getAttribute(AuthenticationConstants.LOGINED_USER);
                LOGGER.error("Current User: " + user);
                if (user != null) {
                    isLoggedIn = true;
                }
            } catch (Exception e) {
                LOGGER.error("Cannot get current user: " + e.getMessage(), e);
            }

            //return isLoggedIn;
        }
        return false;
    }
}
