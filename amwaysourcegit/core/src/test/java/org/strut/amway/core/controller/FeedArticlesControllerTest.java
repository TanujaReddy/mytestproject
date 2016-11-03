package org.strut.amway.core.controller;

import static org.mockito.Matchers.any;

import java.util.Arrays;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.services.ArticleQueryService;

import com.day.cq.wcm.api.Page;

public class FeedArticlesControllerTest {

    @Mock
    Page currentPage;

    @Mock
    SlingHttpServletRequest request;

    @Mock
    SlingScriptHelper sling;

    @Mock
    ArticleQueryService articleQueryService;

    @Mock
    ResourceResolver resourceResolver;

    FeedArticlesController classUnderTest;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        String path = "http://localhost:4502/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        Mockito.when(currentPage.getPath()).thenReturn(path);
        Mockito.when(sling.getService(ArticleQueryService.class)).thenReturn(articleQueryService);
        Mockito.when(request.getResourceResolver()).thenReturn(resourceResolver);        
    }

    @Test
    public void testGetListChildrenSuccessfully() {
        ArticleSearchResult articleSearchResult = createArticleSearchResult();
        Mockito.when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(articleSearchResult);
        classUnderTest = new FeedArticlesController(currentPage, sling, request);
        List<Page> pages = classUnderTest.getListChildren();
        Assert.assertNotNull(pages);
        Assert.assertEquals(pages.size(), 2);
    }

    public ArticleSearchResult createArticleSearchResult() {
        ArticleSearchResult articleSearchResult = new ArticleSearchResult();
        Page page1 = Mockito.mock(Page.class);
        Page page2 = Mockito.mock(Page.class);
        articleSearchResult.setPages(Arrays.asList(page1, page2));

        return articleSearchResult;
    }
}
