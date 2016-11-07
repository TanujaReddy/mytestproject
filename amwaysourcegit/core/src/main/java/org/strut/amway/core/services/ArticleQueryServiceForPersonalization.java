package org.strut.amway.core.services;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;

public interface ArticleQueryServiceForPersonalization {
	/*********Start of Changes for AmwayToday v3.0**************/
    public ArticleSearchResult search(ResourceResolver resourceResolver, ArticlesCriteria articlesCriteria,SlingHttpServletRequest request);
    /*********End of Changes for AmwayToday v3.0**************/

}
