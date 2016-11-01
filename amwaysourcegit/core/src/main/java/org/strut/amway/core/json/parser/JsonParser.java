package org.strut.amway.core.json.parser;

import org.apache.sling.api.resource.ResourceResolver;

public interface JsonParser<T> {

    public String parse(ResourceResolver resourceResolver, T t) throws Exception;

}
