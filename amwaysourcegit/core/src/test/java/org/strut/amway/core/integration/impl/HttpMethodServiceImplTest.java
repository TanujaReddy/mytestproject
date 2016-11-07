package org.strut.amway.core.integration.impl;

import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class HttpMethodServiceImplTest {

    HttpMethodServiceImpl classUnderTest;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new HttpMethodServiceImpl();
    }

    @Test
    public void shouldCreateHttpClientSuccessfully() {
        String uri = "http://localhost:4502";
        GetMethod getMethod = classUnderTest.createGetMethod(uri);
        Assert.assertNotNull(getMethod);
    }
}
