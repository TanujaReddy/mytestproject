package org.strut.amway.au.servlets;

import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.strut.amway.core.servlets.AbstractJsonServlet;
import org.strut.amway.core.util.AuthenticationConstants;
import org.strut.amway.core.util.Constants;
import org.strut.amway.core.util.LinkTransformerUtils;

@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.logout", extensions = "html", methods = "POST")
public class LogoutServlet extends AbstractJsonServlet {

    private static final long serialVersionUID = 9074944401164668560L;

    @Override
    protected void handle(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws Exception {
        final HttpSession session = request.getSession();
        session.removeAttribute(AuthenticationConstants.LOGINED_USER);
        response.sendRedirect(LinkTransformerUtils.transformPath(request.getResourceResolver(), request.getResource().getPath()) + Constants.HTML_EXTENSION);
    }

}
