package org.strut.amway.core.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.builder.ArticleQuerySearchBuilderForPersonalization;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.services.ArticleQueryServiceForPersonalization;

import com.day.cq.wcm.api.Page;

/**
 * One implementation of the {@link SearchService}. Note that the settings
 * service is injected, not retrieved.
 */
@Service(value = ArticleQueryServiceForPersonalization.class)
@Component(immediate = true)
public class ArticleQueryServiceForPersonalizationImpl implements ArticleQueryServiceForPersonalization {

    private static Logger LOGGER = LoggerFactory.getLogger(ArticleQueryServiceForPersonalizationImpl.class);

    @Override
    public ArticleSearchResult search(ResourceResolver resourceResolver, ArticlesCriteria articlesCriteria,SlingHttpServletRequest request) {
        try {
            ArticleSearchResult articleSearchResult = new ArticleSearchResult();
            LOGGER.info("resourceResolver != null && articlesCriteria != null"+String.valueOf(resourceResolver != null && articlesCriteria != null));
            if (resourceResolver != null && articlesCriteria != null) {

                Session session = resourceResolver.adaptTo(Session.class);
                QueryManager queryManager = session.getWorkspace().getQueryManager();

                String expression = ArticleQuerySearchBuilderForPersonalization.buildSearchArticleQuery(articlesCriteria,request);
                LOGGER.info("Expression_new"+expression);

                if (StringUtils.isNotEmpty(expression)) {
                    List<Page> pages = new ArrayList<Page>();
                    final Long offset = articlesCriteria.getOffset();
                    final Long Limit = articlesCriteria.getLimit();

                    Query query = queryManager.createQuery(expression, Query.JCR_SQL2);

                    if (offset != null)
                        query.setOffset(offset);

                    if (Limit != null)
                        query.setLimit(Limit);

                    QueryResult queryResult = query.execute();
                    NodeIterator nodeIterator = queryResult.getNodes();
                    if (nodeIterator != null) {
                        while (nodeIterator.hasNext()) {
                            String path = nodeIterator.nextNode().getParent().getPath();
                            Page page = resourceResolver.getResource(path).adaptTo(Page.class);
                            pages.add(page);
                        }
                    }

                    articleSearchResult.setPages(pages);
                    articleSearchResult.setLimit(Limit);
                    articleSearchResult.setOffset(offset);
                }
            }
            return articleSearchResult;
        } catch (IllegalArgumentException | RepositoryException e) {
            LOGGER.error("Error while get  data from SearchResult" + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
