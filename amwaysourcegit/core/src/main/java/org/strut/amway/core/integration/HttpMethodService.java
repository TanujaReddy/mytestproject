package org.strut.amway.core.integration;

import org.apache.commons.httpclient.methods.GetMethod;

public interface HttpMethodService {

    public GetMethod createGetMethod(String uri);
}
