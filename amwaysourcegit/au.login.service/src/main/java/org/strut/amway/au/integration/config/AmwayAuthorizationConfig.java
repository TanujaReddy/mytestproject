package org.strut.amway.au.integration.config;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;

/**
 * Configuration of Amway AU Authorization.
 */
@Component(label = "Amway AU Authorization Identity Provider", description = "Amway AU Authorization Configuration", name = "org.strut.amway.au.integration.LoginService", configurationFactory = true, metatype = true, ds = false)
public class AmwayAuthorizationConfig {
    public static final String PARAM_HOST_DEFAULT = "http://www.amway.com.au";

    @Property(label = "Amway AU Host", description = "The host of Amway AU to login", value = PARAM_HOST_DEFAULT)
    public static final String PARAM_HOST = "provider.host";

    public static final String PARAM_USERNAME_DEFAULT = "Kablamo";

    @Property(label = "Amway AU Authorization Username", description = "The username of Amway AU Authorization API", value = PARAM_USERNAME_DEFAULT)
    public static final String PARAM_USERNAME = "provider.username";

    public static final String PARAM_PASSWORD_DEFAULT = "#7h915W7AuQL0s5";

    @Property(label = "Amway AU Authorization Password", description = "The password of Amway AU Authorization API", value = PARAM_USERNAME_DEFAULT)
    public static final String PARAM_PASSWORD = "provider.password";

    private String host = PARAM_HOST_DEFAULT;
    private String userName = PARAM_USERNAME_DEFAULT;
    private String passWord = PARAM_PASSWORD_DEFAULT;

    public String getHost() {
        return host;
    }

    public AmwayAuthorizationConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AmwayAuthorizationConfig setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassWord() {
        return passWord;
    }

    public AmwayAuthorizationConfig setPassWord(String passWord) {
        this.passWord = passWord;
        return this;
    }

    public static AmwayAuthorizationConfig of(ConfigurationParameters params) {
        final AmwayAuthorizationConfig cfg =
                new AmwayAuthorizationConfig()
                        .setHost(params.getConfigValue(PARAM_HOST, PARAM_HOST_DEFAULT))
                        .setUserName(params.getConfigValue(PARAM_USERNAME, PARAM_USERNAME_DEFAULT))
                        .setPassWord(params.getConfigValue(PARAM_PASSWORD, PARAM_PASSWORD_DEFAULT));
        return cfg;
    }
}
