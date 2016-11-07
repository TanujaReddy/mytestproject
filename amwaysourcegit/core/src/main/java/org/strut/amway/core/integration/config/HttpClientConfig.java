package org.strut.amway.core.integration.config;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;

/**
 * Configuration of Core Http Service.
 */
@Component(label = "Amway Http Client Service", description = "Amway Http Client Service Configuration", name = "org.strut.amway.core.integration.impl.HttpClientServiceImpl", configurationFactory = true, metatype = true, ds = false)
public class HttpClientConfig {

    public static final String DEFAULT_MAX_CONNECTIONS_PER_HOST = "100";

    @Property(label = "Max Connection Per Host", description = "The Maximum Connection Per Host", value = DEFAULT_MAX_CONNECTIONS_PER_HOST)
    public static final String PARAM_MAX_CONNECTIONS_PER_HOST = "httpclient.maxConnectionPerHost";

    public static final String DEFAULT_MAX_TOTAL_CONNECTIONS = "100";

    @Property(label = "Max Total Connections", description = "The Maximum Total Connections", value = DEFAULT_MAX_TOTAL_CONNECTIONS)
    public static final String PARAM_MAX_TOTAL_CONNECTIONS = "httpclient.maxTotalConnections";

    private String maxConnectionsPerHost = DEFAULT_MAX_CONNECTIONS_PER_HOST;
    private String maxTotalConnections = DEFAULT_MAX_TOTAL_CONNECTIONS;

    public String getMaxConnectionsPerHost() {
        return maxConnectionsPerHost;
    }

    public HttpClientConfig setMaxConnectionsPerHost(final String maxConnectionsPerHost) {
        this.maxConnectionsPerHost = maxConnectionsPerHost;
        return this;
    }

    public String getMaxTotalConnections() {
        return maxTotalConnections;
    }

    public HttpClientConfig setMaxTotalConnections(final String maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
        return this;
    }

    public static HttpClientConfig of(final ConfigurationParameters params) {
        final HttpClientConfig cfg =
                new HttpClientConfig()
                        .setMaxConnectionsPerHost(params.getConfigValue(PARAM_MAX_CONNECTIONS_PER_HOST, DEFAULT_MAX_CONNECTIONS_PER_HOST))
                        .setMaxTotalConnections(params.getConfigValue(PARAM_MAX_TOTAL_CONNECTIONS, DEFAULT_MAX_TOTAL_CONNECTIONS));
        return cfg;
    }

}
