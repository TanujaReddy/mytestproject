package org.strut.amway.au.servlets;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.util.AuthenticationConstants;

public class LogoutServletTest {

    LogoutServlet classUnderTest;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    @Mock
    HttpSession httpSession;

    @Mock
    Resource resource;

    @Mock
    ResourceResolver resourceResolver;

    ByteArrayOutputStream outputStream;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        createLogoutServlet();

        outputStream = new ByteArrayOutputStream(1024);
        PrintWriter writer = new PrintWriter(outputStream);

        when(slingHttpServletRequest.getMethod()).thenReturn("POST");
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(slingHttpServletResponse.getWriter()).thenReturn(writer);
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(slingHttpServletRequest.getSession()).thenReturn(httpSession);
    }

    @Test
    public void shouldLogoutSuccessfully() throws Exception {
        final String path = "/content/amway-today/en";

        when(resource.getPath()).thenReturn(path);
        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        verify(httpSession).removeAttribute(AuthenticationConstants.LOGINED_USER);
        verify(slingHttpServletResponse).sendRedirect(path + ".html");
    }

    @Test
    public void shouldDestroySuccessfully() throws Exception {
        createLogoutServlet();
        classUnderTest.destroy();
    }

    private void createLogoutServlet() throws Exception {
        classUnderTest = new LogoutServlet();
        classUnderTest.init();
    }

}
