package org.strut.amway.au.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.strut.amway.core.exception.UserCredentialsException;
import org.strut.amway.core.integration.LoginService;
import org.strut.amway.core.json.parser.JsonParser;
import org.strut.amway.core.json.parser.login.LoginJsonParser;
import org.strut.amway.core.model.Account;
import org.strut.amway.core.model.LoginResult;
import org.strut.amway.core.servlets.AbstractJsonServlet;
import org.strut.amway.core.util.AuthenticationConstants;

@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.login", extensions = "html", methods = "POST")
public class LoginServlet extends AbstractJsonServlet {

    private static final long serialVersionUID = -7260982932964166762L;

    @Reference
    private LoginService loginService;

    private JsonParser<LoginResult> jsonParser;

    @Override
    public void init() throws ServletException {
        super.init();
        jsonParser = new LoginJsonParser();
    }

    @Override
    public void destroy() {
        super.destroy();
        jsonParser = null;
    }

    @Override
    protected void handle(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws Exception {
        final String username = request.getParameter(AuthenticationConstants.USERNAME_PARAM);
        final String password = request.getParameter(AuthenticationConstants.PASSWORD_PARAM);
        validateParams(username, password);

        final LoginResult loginResult = new LoginResult();
        try {
            final Account loginedAccount = login(username, password, request.getRemoteAddr());
            saveCredentialsToHttpSession(request.getSession(), loginedAccount);
            loginResult.setSuccess(Boolean.TRUE);
            loginResult.setUsername(loginedAccount.getUserName());
        } catch (UserCredentialsException e) {
            loginResult.setSuccess(Boolean.FALSE);
            loginResult.setErrorMsg(AuthenticationConstants.USERNAME_PASSWORD_INVALID);
        }
        writeJsonToResponse(response, jsonParser.parse(request.getResourceResolver(), loginResult));
    }

    private Account login(final String username, final String password, final String ip) throws Exception {
        final Account loginedAccount = loginService.login(username, password, ip);
        return loginedAccount;
    }

    private void validateParams(final String username, final String password) {
        if (StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException("username is missing");
        }
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("password is missing");
        }
    }

    private void saveCredentialsToHttpSession(final HttpSession httpSession, final Account loginedAccount) {
        httpSession.setAttribute(AuthenticationConstants.LOGINED_USER, loginedAccount);
    }

}
