package org.strut.amway.core.json.parser.login;

import static org.junit.Assert.assertEquals;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.strut.amway.core.model.LoginResult;
import org.strut.amway.core.util.AuthenticationConstants;

public class LoginJsonParserTest {

    LoginJsonParser classUnderTest;

    @Mock
    ResourceResolver resourceResolver;

    @Before
    public void init() {
        classUnderTest = new LoginJsonParser();
    }

    @Test
    public void shouldReturnJsonProperlyForSuccessfulLogin() throws Exception {
        final String username = "hadoan";
        final LoginResult result = new LoginResult();
        result.setSuccess(Boolean.TRUE);
        result.setUsername(username);

        final String json = classUnderTest.parse(resourceResolver, result);

        assertEquals("{\"success\":1,\"username\":\"" + username + "\"}", json);
    }

    @Test
    public void shouldReturnJsonProperlyForUnSuccessfulLogin() throws Exception {
        final String error = AuthenticationConstants.USERNAME_PASSWORD_INVALID;
        final LoginResult result = new LoginResult();
        result.setSuccess(Boolean.FALSE);
        result.setErrorMsg(error);

        final String json = classUnderTest.parse(resourceResolver, result);

        assertEquals("{\"success\":0,\"error\":\"" + error + "\"}", json);
    }

    @Test
    public void shouldReturnEmptyJsonWhenLoginResultIsNull() throws Exception {
        final LoginResult result = null;

        final String json = classUnderTest.parse(resourceResolver, result);

        assertEquals("{}", json);
    }

}
