package org.strut.amway.core.integration.impl;

import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.strut.amway.core.integration.HttpClientService;
import org.strut.amway.core.integration.config.HttpClientConfig;

@Service(value = HttpClientService.class)
@Component(immediate = true)
public class HttpClientServiceImpl implements HttpClientService {

    private HttpClientConfig httpClientConfig;
    private HttpClient httpClient;

    public HttpClientServiceImpl() {
    }

    public HttpClientServiceImpl(final HttpClientConfig config) {
        this.httpClientConfig = config;
        init();
    }

    @Activate
    private void activate(final Map<String, Object> properties) {
        final ConfigurationParameters cfg = ConfigurationParameters.of(properties);
        this.httpClientConfig = HttpClientConfig.of(cfg);
        init();
    }

    @Deactivate
    private void deactivate() {
        destroy();
    }

    private void init() {
        final HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
        connectionManagerParams.setDefaultMaxConnectionsPerHost(Integer.valueOf(httpClientConfig.getMaxConnectionsPerHost()));
        connectionManagerParams.setMaxTotalConnections(Integer.valueOf(httpClientConfig.getMaxTotalConnections()));

        final MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
        httpConnectionManager.setParams(connectionManagerParams);

        this.httpClient = new HttpClient(httpConnectionManager);
    }

    private void destroy() {
        final HttpConnectionManager connectionManager = httpClient.getHttpConnectionManager();
        if (connectionManager != null && connectionManager instanceof MultiThreadedHttpConnectionManager) {
            ((MultiThreadedHttpConnectionManager) connectionManager).shutdown();
        }
        httpClient = null;
    }

    @Override
    public HttpClient createHttpClient() {
        return this.httpClient;
    }

}
