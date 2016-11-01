package org.strut.amway.core.servlets;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.enumeration.ContentType;

@SuppressWarnings("serial")
public abstract class AbstractJsonServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJsonServlet.class);
    private static final String DEFAULT_CHAR_ENCODING = "UTF-8";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            handle(request, response);
        } catch (final Exception e) {
            LOGGER.error("Error while processing the GET request " + ExceptionUtils.getStackTrace(e));
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            handle(request, response);
        } catch (final Exception e) {
            LOGGER.error("Error while processing the POST request " + ExceptionUtils.getStackTrace(e));
            handleError(response, e);
        }
    }

    protected abstract void handle(SlingHttpServletRequest request, SlingHttpServletResponse response) throws Exception;

    protected void writeJsonToResponse(final SlingHttpServletResponse response, final String json) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.getValue());
        response.setCharacterEncoding(DEFAULT_CHAR_ENCODING);
        response.getWriter().write(json.toString());
        response.getWriter().flush();
    }

    protected void handleError(final SlingHttpServletResponse response, final Exception e) throws IOException, ServletException {
        try {
            response.setContentType(ContentType.APPLICATION_JSON.getValue());
            response.setCharacterEncoding(DEFAULT_CHAR_ENCODING);
            response.setStatus(500);
            response.getWriter().write(getJsonError(ExceptionUtils.getMessage(e)));
            response.getWriter().flush();
        } catch (JSONException ex) {
            throw new ServletException(ex);
        }
    }

    protected String getJsonError(final String message) throws JSONException {
        final JSONObject errorJsonObj = new JSONObject();
        errorJsonObj.put("error", message);
        return errorJsonObj.toString();
    }

}
