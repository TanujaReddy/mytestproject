package org.strut.amway.core.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.services.ArticleQueryService;

import com.day.cq.wcm.api.Page;

public class FeedArticlesController {

    private static final Long FEED_LIMIT = 50L;
    private static final Long FEED_OFFSET = 0L;

    private Page currentPage;
    private SlingScriptHelper sling;
    private SlingHttpServletRequest request;

    public FeedArticlesController(Page currentPage, SlingScriptHelper sling, SlingHttpServletRequest request) {
        this.currentPage = currentPage;
        this.sling = sling;
        this.request = request;
    }

    public List<Page> getListChildren() {
        final List<ArticleLabelType> articleLabelTypes = new ArrayList<>();
        articleLabelTypes.add(ArticleLabelType.PUBLIC);
        final ArticlesCriteria criteria =
                new ArticlesCriteria.Builder()
                        .setPath(currentPage.getPath()).setArticleLabelTypes(articleLabelTypes).setOffset(FEED_OFFSET).setLimit(FEED_LIMIT)
                        .setSearchType(SearchType.SEARCH_BY_SUB_CATEGORY).build();
        ArticleQueryService service = sling.getService(ArticleQueryService.class);
        ArticleSearchResult articleSearchResult = service.search(request.getResourceResolver(), criteria);
        List<Page> pages = articleSearchResult.getPages();

        return pages;
    }
}
