package org.strut.amway.core.services;

import org.apache.sling.api.resource.ResourceResolver;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;

public interface ArticleQueryService {

    public ArticleSearchResult search(ResourceResolver resourceResolver, ArticlesCriteria articlesCriteria);

}
