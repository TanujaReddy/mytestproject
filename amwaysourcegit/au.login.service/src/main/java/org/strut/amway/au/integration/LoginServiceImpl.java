package org.strut.amway.au.integration;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.au.integration.config.AmwayAuthorizationConfig;
import org.strut.amway.au.integration.util.EntityTypeUtils;
import org.strut.amway.core.enumeration.EntityType;
import org.strut.amway.core.exception.UserCredentialsException;
import org.strut.amway.core.integration.HttpClientService;
import org.strut.amway.core.integration.HttpMethodService;
import org.strut.amway.core.integration.LoginService;
import org.strut.amway.core.model.Account;

@Service(value = LoginService.class)
@Component(immediate = true)
public class LoginServiceImpl extends AbstractAmwayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);

    private AmwayAuthorizationConfig config;

    @Reference
    private HttpClientService httpClientService;

    @Reference
    private HttpMethodService httpMethodService;

    public LoginServiceImpl() {

    }

    public LoginServiceImpl(final AmwayAuthorizationConfig config) {
        this.config = config;
        init();
    }

    @Activate
    private void activate(final Map<String, Object> properties) {
        final ConfigurationParameters cfg = ConfigurationParameters.of(properties);
        this.config = AmwayAuthorizationConfig.of(cfg);
        init();
    }

    @Deactivate
    private void deactivate() {
        destroy();
    }

    private void init() {

    }

    private void destroy() {

    }

    @Override
    public Account login(String userName, String passWord, String ip) throws UserCredentialsException {
        HttpMethod httpMethod = buildHttpMethod(userName, passWord, ip);
        JSONObject login = null;
        try {
            login = execute(httpMethod, httpClientService.createHttpClient());
        } catch (HttpException e) {
            LOGGER.error("login failure while executing external API with protocol occurs : " + ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            LOGGER.error("login failure while executing external API : " + ExceptionUtils.getStackTrace(e));
        }

        return buildLoginResponse(login);
    }

    protected Account buildLoginResponse(JSONObject login) throws UserCredentialsException {
        Account account = new Account();

        if (login == null) {
            LOGGER.info("login failure while calling external API");
            throw new UserCredentialsException();
        }

        try {
            JSONObject response = login.getJSONObject("Response");
            String entityId = response.getString("EntityId");
            String entityTypeInStr = response.getString("EntityType");

            final EntityType entityType = EntityTypeUtils.resolve(entityTypeInStr);
            if (entityType == null) {
                LOGGER.warn("Can not resolve with entity type {}", entityTypeInStr);
            }
            account.setUserName(entityId);
            account.setEntityType(entityType);
        } catch (JSONException e) {
            LOGGER.info("login failure while parsing response : " + ExceptionUtils.getStackTrace(e));
            throw new UserCredentialsException();
        }
        return account;
    }

    protected HttpMethod buildHttpMethod(String userName, String passWord, String ip) {
        String uri = buildURI(userName, passWord, ip);
        return httpMethodService.createGetMethod(uri);
    }

    protected String buildURI(String userName, String passWord, String ip) {
        String uri = config.getHost() + "/Service/ea.s/Kablamo/Auth" + "?" + "Login=" + userName + "&" + "passWord=" + passWord + "&" + "ip=" + ip;
        return uri;
    }

    @Override
    protected String buildAuthorizationUserName() {
        return config.getUserName();
    }

    @Override
    protected String buildAuthorizationPassword() {
        return config.getPassWord();
    }
}
