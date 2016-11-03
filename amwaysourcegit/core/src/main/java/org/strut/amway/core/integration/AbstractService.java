package org.strut.amway.core.integration;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.sling.commons.json.JSONObject;

public abstract class AbstractService implements LoginService {

    protected JSONObject execute(HttpMethod httpMethod, HttpClient httpClient) throws HttpException, IOException {
        buildAuthorization(httpMethod);
        httpClient.executeMethod(httpMethod);
        return handleResult(httpMethod);
    }

    protected abstract void buildAuthorization(HttpMethod httpMethod);

    protected abstract JSONObject handleResult(HttpMethod httpMethod);
}
