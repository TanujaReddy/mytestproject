package org.strut.amway.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LinkTransformerUtilsTest {

    @Mock
    ResourceResolver resourceResolver;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetPathProperlyWithHttpProtocol() throws Exception {
        final String pagePath = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        final String path = "http://localhost:4502/en";
        when(resourceResolver.map(eq(pagePath))).thenReturn(path);

        String result = LinkTransformerUtils.transformPath(resourceResolver, pagePath);
        assertEquals("/en", result);
    }

    @Test
    public void shouldGetPathProperlyWithHttpsProtocol() throws Exception {
        final String pagePath = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        final String path = "https://localhost:4502/en";
        when(resourceResolver.map(eq(pagePath))).thenReturn(path);

        String result = LinkTransformerUtils.transformPath(resourceResolver, pagePath);
        assertEquals("/en", result);
    }

    @Test
    public void shouldReturnPathWhenItDoesNotContainProtocolInfo() throws Exception {
        final String pagePath = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        final String path = "localhost:4502/en";
        when(resourceResolver.map(eq(pagePath))).thenReturn(path);

        String result = LinkTransformerUtils.transformPath(resourceResolver, pagePath);
        assertEquals(pagePath, result);
    }

    @Test
    public void shouldReturnPathWhenItDoesNotContainProtocolInfoAndHost() throws Exception {
        final String pagePath = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        final String path = "/en";
        when(resourceResolver.map(eq(pagePath))).thenReturn(path);

        String result = LinkTransformerUtils.transformPath(resourceResolver, pagePath);
        assertEquals(pagePath, result);
    }

    @Test
    public void shouldReturnPathWhenCanNotResolveTo() throws Exception {
        final String pagePath = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        when(resourceResolver.map(eq(pagePath))).thenReturn(null);

        String result = LinkTransformerUtils.transformPath(resourceResolver, pagePath);
        assertEquals(pagePath, result);
    }

    @Test
    public void shouldReturnNullWhenThePathIsEmpty() throws Exception {
        final String path = null;
        String result = LinkTransformerUtils.transformPath(resourceResolver, path);
        assertNull(result);
    }

}
