package org.strut.amway.core.integration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.integration.config.HttpClientConfig;

public class HttpClientServiceImplTest {

    HttpClientServiceImpl classUnderTest;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new HttpClientServiceImpl();
    }

    @Test
    public void shouldCreateHttpClientSuccessfully() {
        final String maxConnectionPerHost = "10";
        final String maxTotalConnections = "5";
        final HttpClientConfig config = new HttpClientConfig();
        config.setMaxConnectionsPerHost(maxConnectionPerHost);
        config.setMaxTotalConnections(maxTotalConnections);

        classUnderTest = new HttpClientServiceImpl(config);
        HttpClient httpClient = classUnderTest.createHttpClient();

        final HttpConnectionManagerParams connectionParams = httpClient.getHttpConnectionManager().getParams();
        assertTrue(httpClient.getHttpConnectionManager() instanceof MultiThreadedHttpConnectionManager);
        assertEquals(Integer.valueOf(maxConnectionPerHost).intValue(), connectionParams.getDefaultMaxConnectionsPerHost());
        assertEquals(Integer.valueOf(maxTotalConnections).intValue(), connectionParams.getMaxTotalConnections());
    }

    @Test
    public void shouldCreateHttpClientSuccessfullyAfterActivate() throws Exception {
        final String maxConnectionPerHost = "10";
        final String maxTotalConnections = "5";
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(HttpClientConfig.PARAM_MAX_CONNECTIONS_PER_HOST, maxConnectionPerHost);
        properties.put(HttpClientConfig.PARAM_MAX_TOTAL_CONNECTIONS, maxTotalConnections);

        classUnderTest = new HttpClientServiceImpl();

        final Method activateMethod = HttpClientServiceImpl.class.getDeclaredMethod("activate", Map.class);
        activateMethod.setAccessible(true);
        activateMethod.invoke(classUnderTest, properties);

        HttpClient httpClient = classUnderTest.createHttpClient();

        final HttpConnectionManagerParams connectionParams = httpClient.getHttpConnectionManager().getParams();
        assertTrue(httpClient.getHttpConnectionManager() instanceof MultiThreadedHttpConnectionManager);
        assertEquals(Integer.valueOf(maxConnectionPerHost).intValue(), connectionParams.getDefaultMaxConnectionsPerHost());
        assertEquals(Integer.valueOf(maxTotalConnections).intValue(), connectionParams.getMaxTotalConnections());
    }

    @Test
    public void shouldCreateHttpClientSuccessfullyAfterDeactivate() throws Exception {
        final String maxConnectionPerHost = "10";
        final String maxTotalConnections = "5";
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(HttpClientConfig.PARAM_MAX_CONNECTIONS_PER_HOST, maxConnectionPerHost);
        properties.put(HttpClientConfig.PARAM_MAX_TOTAL_CONNECTIONS, maxTotalConnections);

        classUnderTest = new HttpClientServiceImpl();

        final Method activateMethod = HttpClientServiceImpl.class.getDeclaredMethod("activate", Map.class);
        activateMethod.setAccessible(true);
        activateMethod.invoke(classUnderTest, properties);

        final Method deactivateMethod = HttpClientServiceImpl.class.getDeclaredMethod("deactivate");
        deactivateMethod.setAccessible(true);
        deactivateMethod.invoke(classUnderTest);

        HttpClient httpClient = classUnderTest.createHttpClient();

        assertNull(httpClient);
    }

}
