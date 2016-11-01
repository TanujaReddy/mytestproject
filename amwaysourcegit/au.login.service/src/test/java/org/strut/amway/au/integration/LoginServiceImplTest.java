package org.strut.amway.au.integration;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.strut.amway.au.integration.config.AmwayAuthorizationConfig;
import org.strut.amway.core.enumeration.EntityType;
import org.strut.amway.core.exception.UserCredentialsException;
import org.strut.amway.core.integration.HttpClientService;
import org.strut.amway.core.integration.HttpMethodService;
import org.strut.amway.core.model.Account;

public class LoginServiceImplTest {

    LoginServiceImpl classUnderTest;
    AmwayAuthorizationConfig config;
    String validUserName;
    String validPassWord;
    String invalidUserName;
    String invalidPassWord;
    String ip;

    @Mock
    HttpClientService httpClientService;

    @Mock
    private HttpMethodService httpMethodService;

    @Mock
    HttpClient httpClient;

    @Mock
    GetMethod httpMethod;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        validUserName = "9116821";
        validPassWord = "testClient";
        invalidUserName = "invalidUserName";
        invalidPassWord = "invalidPassWord";
        ip = "127.0.0.1";
        config = new AmwayAuthorizationConfig();
        config.setHost("http://localhost:4502");
        config.setUserName("userTest");
        config.setPassWord("123456");
        classUnderTest = new LoginServiceImpl(config);
        setHttpClientService();
        setHttpMethodService();
    }

    @Test
    public void shouldLoginSucessfullyWithIBOEntityType() throws UserCredentialsException, HttpException, IOException {
        final String entityType = "EMPLOYEE";
        String response =
                "<Response><EntityId>9116821</EntityId><EntityName>Nirmit Shah</EntityName><EntityType>"
                        + entityType
                        + "</EntityType><MarketCode>AU</MarketCode><Person><EntityId>9116821_APPLICANT1</EntityId><FirstName>Nirmit</FirstName><LastName>Shah</LastName><Email>nirmitshah1@yahoo.co.in</Email><Gender>MALE</Gender></Person></Response>";
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenReturn(1);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(200);
        Mockito.when(httpMethod.getResponseBodyAsString()).thenReturn(response);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        Account account = classUnderTest.login(validUserName, validPassWord, ip);
        Assert.assertNotNull(account);
        Assert.assertEquals(validUserName, account.getUserName());
        Assert.assertEquals(EntityType.IBO, account.getEntityType());
    }

    @Test
    public void shouldLoginSucessfullyWithClientEntityType() throws UserCredentialsException, HttpException, IOException {
        final String entityType = "CLIENT";
        String response =
                "<Response><EntityId>9116821</EntityId><EntityName>Nirmit Shah</EntityName><EntityType>"
                        + entityType
                        + "</EntityType><MarketCode>AU</MarketCode><Person><EntityId>9116821_APPLICANT1</EntityId><FirstName>Nirmit</FirstName><LastName>Shah</LastName><Email>nirmitshah1@yahoo.co.in</Email><Gender>MALE</Gender></Person></Response>";
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenReturn(1);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(200);
        Mockito.when(httpMethod.getResponseBodyAsString()).thenReturn(response);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        Account account = classUnderTest.login(validUserName, validPassWord, ip);
        Assert.assertNotNull(account);
        Assert.assertEquals(validUserName, account.getUserName());
        Assert.assertEquals(EntityType.CLIENT, account.getEntityType());
    }

    @Test
    public void shouldSetNullValueWhenEntityTypeIsInvalid() throws UserCredentialsException, HttpException, IOException {
        final String entityType = "Invalid";
        String response =
                "<Response><EntityId>9116821</EntityId><EntityName>Nirmit Shah</EntityName><EntityType>"
                        + entityType
                        + "</EntityType><MarketCode>AU</MarketCode><Person><EntityId>9116821_APPLICANT1</EntityId><FirstName>Nirmit</FirstName><LastName>Shah</LastName><Email>nirmitshah1@yahoo.co.in</Email><Gender>MALE</Gender></Person></Response>";
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenReturn(1);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(200);
        Mockito.when(httpMethod.getResponseBodyAsString()).thenReturn(response);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        Account account = classUnderTest.login(validUserName, validPassWord, ip);
        Assert.assertNotNull(account);
        Assert.assertEquals(validUserName, account.getUserName());
        Assert.assertNull(account.getEntityType());
    }

    @Test(expected = UserCredentialsException.class)
    public void shouldThrowUserCredentialsExceptionWhenInvalidAccount() throws UserCredentialsException, HttpException, IOException {
        String response = "<Response><Error>Entity Not Authorised</Error></Response>";
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenReturn(1);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(200);
        Mockito.when(httpMethod.getResponseBodyAsString()).thenReturn(response);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + invalidUserName + "&passWord=" + invalidPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        classUnderTest.login(invalidUserName, invalidPassWord, ip);
    }

    @Test(expected = UserCredentialsException.class)
    public void shouldThrowUserCredentialsExceptionWhenHandleResultThrowJSONException() throws UserCredentialsException, HttpException, IOException {
        String response = "test";
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenReturn(1);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(200);
        Mockito.when(httpMethod.getResponseBodyAsString()).thenReturn(response);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        classUnderTest.login(validUserName, validPassWord, ip);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = UserCredentialsException.class)
    public void shouldIOExceptionptionWhenHandleResultThrowJSONException() throws UserCredentialsException, HttpException, IOException {
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenReturn(1);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(200);
        Mockito.when(httpMethod.getResponseBodyAsString()).thenThrow(IOException.class);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        classUnderTest.login(validUserName, validPassWord, ip);
    }

    @Test(expected = UserCredentialsException.class)
    public void shouldIOExceptionptionWhenHandleResultWithHttpMethodIsNull() throws UserCredentialsException, HttpException, IOException {
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(null)).thenReturn(1);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(null);
        classUnderTest.login(validUserName, validPassWord, ip);
    }

    @Test(expected = UserCredentialsException.class)
    public void shouldIOExceptionptionWhenHandleResultWithHttpStatusIsNot200() throws UserCredentialsException, HttpException, IOException {
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenReturn(1);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(503);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        classUnderTest.login(validUserName, validPassWord, ip);
    }

    @Test(expected = UserCredentialsException.class)
    public void shouldIOExceptionptionWhenHandleResultIsNull() throws UserCredentialsException, HttpException, IOException {
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenReturn(1);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(200);
        Mockito.when(httpMethod.getResponseBodyAsString()).thenReturn(null);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        classUnderTest.login(validUserName, validPassWord, ip);
    }

    @Test(expected = UserCredentialsException.class)
    public void shouldIOExceptionptionWhenHandleResultIsEmpty() throws UserCredentialsException, HttpException, IOException {
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenReturn(1);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(200);
        Mockito.when(httpMethod.getResponseBodyAsString()).thenReturn("");
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        classUnderTest.login(validUserName, validPassWord, ip);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = UserCredentialsException.class)
    public void shouldThrowUserCredentialsExceptionWhenExecuteMethodThrowHttpException() throws UserCredentialsException, HttpException, IOException {
        String response = "test";
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenThrow(HttpException.class);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(200);
        Mockito.when(httpMethod.getResponseBodyAsString()).thenReturn(response);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        classUnderTest.login(validUserName, validPassWord, ip);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = UserCredentialsException.class)
    public void shouldThrowUserCredentialsExceptionWhenExecuteMethodThrowIOException() throws UserCredentialsException, HttpException, IOException {
        String response = "test";
        Mockito.when(httpClientService.createHttpClient()).thenReturn(httpClient);
        Mockito.when(httpClient.executeMethod(httpMethod)).thenThrow(IOException.class);
        Mockito.when(httpMethod.getStatusCode()).thenReturn(200);
        Mockito.when(httpMethod.getResponseBodyAsString()).thenReturn(response);
        Mockito.when(
                httpMethodService.createGetMethod("http://localhost:4502/Service/ea.s/Kablamo/Auth?Login=" + validUserName + "&passWord=" + validPassWord
                        + "&ip=" + ip)).thenReturn(httpMethod);
        classUnderTest.login(validUserName, validPassWord, ip);
    }

    private void setHttpClientService() throws Exception {
        final Field articleQueryServiceField = FieldUtils.getField(LoginServiceImpl.class, "httpClientService", true);
        articleQueryServiceField.setAccessible(true);
        articleQueryServiceField.set(classUnderTest, httpClientService);
    }

    private void setHttpMethodService() throws Exception {
        final Field articleQueryServiceField = FieldUtils.getField(LoginServiceImpl.class, "httpMethodService", true);
        articleQueryServiceField.setAccessible(true);
        articleQueryServiceField.set(classUnderTest, httpMethodService);
    }
}
