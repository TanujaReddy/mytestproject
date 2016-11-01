package org.strut.amway.au.integration;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.xml.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.integration.AbstractService;
import org.strut.amway.core.util.IntegrationConstants;

public abstract class AbstractAmwayService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAmwayService.class);

    @Override
    protected void buildAuthorization(HttpMethod httpMethod) {
        String s = buildAuthorizationUserName() + ":" + buildAuthorizationPassword();
        String basic = IntegrationConstants.HEADER_AUTHORIZATION_KEY + " " + new String(Base64.encodeBase64(s.getBytes()));
        if (httpMethod != null) {
            httpMethod.addRequestHeader(IntegrationConstants.HEADER_AUTHORIZATION, basic);
        }
    }

    @SuppressWarnings("static-access")
    @Override
    protected JSONObject handleResult(HttpMethod httpMethod) {
        if (httpMethod != null) {
            LOGGER.debug("Status Code : " + httpMethod.getStatusCode());
            try {
                if (httpMethod.getStatusCode() == IntegrationConstants.HTTP_STATUS_CODE_200) {
                    String result = httpMethod.getResponseBodyAsString();
                    if (result != null && !result.equals("")) {
                        XML xml = new XML();
                        return xml.toJSONObject(result);
                    }
                }
            } catch (JSONException e) {
                LOGGER.error("login failure while parsing response from external API : " + ExceptionUtils.getStackTrace(e));
            } catch (IOException e) {
                LOGGER.error("login failure while transporting response from external API : " + ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    protected abstract String buildAuthorizationUserName();

    protected abstract String buildAuthorizationPassword();
}
