package org.strut.amway.core.integration.impl;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.strut.amway.core.integration.HttpMethodService;

@Service(value = HttpMethodService.class)
@Component(immediate = true)
public class HttpMethodServiceImpl implements HttpMethodService {

    @Override
    public GetMethod createGetMethod(String uri) {
        GetMethod getMethod = new GetMethod(uri);
        return getMethod;
    }

}
