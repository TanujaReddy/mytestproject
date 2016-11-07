package org.strut.amway.au.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.exception.UserCredentialsException;
import org.strut.amway.core.integration.LoginService;
import org.strut.amway.core.model.Account;
import org.strut.amway.core.util.AuthenticationConstants;

public class LoginServletTest {

    LoginServlet classUnderTest;

    @Mock
    LoginService loginService;

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
        createLoginServlet();

        outputStream = new ByteArrayOutputStream(1024);
        PrintWriter writer = new PrintWriter(outputStream);

        when(slingHttpServletRequest.getMethod()).thenReturn("POST");
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(slingHttpServletResponse.getWriter()).thenReturn(writer);
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(slingHttpServletRequest.getSession()).thenReturn(httpSession);
    }

    @Test
    public void shouldLoginSuccessfully() throws Exception {
        final String username = "hadoan";
        final String password = "123456";
        final String ip = "10.25.36.10";

        final Account user = new Account();
        user.setUserName(username);

        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.USERNAME_PARAM))).thenReturn(username);
        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.PASSWORD_PARAM))).thenReturn(password);
        when(slingHttpServletRequest.getRemoteAddr()).thenReturn(ip);
        when(loginService.login(eq(username), eq(password), eq(ip))).thenReturn(user);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"success\":1,\"username\":\"" + username + "\"}", outputStream.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldLoginFailedWhenUsernameAndPasswordIsInvalid() throws Exception {
        final String username = "invalid";
        final String password = "123456";
        final String ip = "10.25.36.10";

        final Account user = new Account();
        user.setUserName(username);

        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.USERNAME_PARAM))).thenReturn(username);
        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.PASSWORD_PARAM))).thenReturn(password);
        when(slingHttpServletRequest.getRemoteAddr()).thenReturn(ip);
        when(loginService.login(eq(username), eq(password), eq(ip))).thenThrow(UserCredentialsException.class);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"success\":0,\"error\":\"" + AuthenticationConstants.USERNAME_PASSWORD_INVALID + "\"}", outputStream.toString());
    }

    @Test
    public void shouldReturnErrorWhenUsernameParamInvalid() throws Exception {
        final String username = null;
        final String password = "123456";
        final String ip = "10.25.36.10";

        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.USERNAME_PARAM))).thenReturn(username);
        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.PASSWORD_PARAM))).thenReturn(password);
        when(slingHttpServletRequest.getRemoteAddr()).thenReturn(ip);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalArgumentException: username is missing\"}", outputStream.toString());
    }

    @Test
    public void shouldReturnErrorWhenUsernameParamIsBlank() throws Exception {
        final String username = "";
        final String password = "123456";
        final String ip = "10.25.36.10";

        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.USERNAME_PARAM))).thenReturn(username);
        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.PASSWORD_PARAM))).thenReturn(password);
        when(slingHttpServletRequest.getRemoteAddr()).thenReturn(ip);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalArgumentException: username is missing\"}", outputStream.toString());
    }

    @Test
    public void shouldReturnErrorWhenPasswordParamInvalid() throws Exception {
        final String username = "hadoan";
        final String password = null;
        final String ip = "10.25.36.10";

        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.USERNAME_PARAM))).thenReturn(username);
        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.PASSWORD_PARAM))).thenReturn(password);
        when(slingHttpServletRequest.getRemoteAddr()).thenReturn(ip);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalArgumentException: password is missing\"}", outputStream.toString());
    }

    @Test
    public void shouldReturnErrorWhenPasswordParamIsBlank() throws Exception {
        final String username = "hadoan";
        final String password = "";
        final String ip = "10.25.36.10";

        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.USERNAME_PARAM))).thenReturn(username);
        when(slingHttpServletRequest.getParameter(eq(AuthenticationConstants.PASSWORD_PARAM))).thenReturn(password);
        when(slingHttpServletRequest.getRemoteAddr()).thenReturn(ip);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalArgumentException: password is missing\"}", outputStream.toString());
    }

    @Test
    public void shouldDestroySuccessfully() throws Exception {
        createLoginServlet();
        classUnderTest.destroy();
    }

    private void createLoginServlet() throws Exception {
        classUnderTest = new LoginServlet();
        setLoginService();
        classUnderTest.init();
    }

    private void setLoginService() throws Exception {
        final Field loginField = FieldUtils.getField(LoginServlet.class, "loginService", true);
        loginField.setAccessible(true);
        loginField.set(classUnderTest, loginService);
    }

}
