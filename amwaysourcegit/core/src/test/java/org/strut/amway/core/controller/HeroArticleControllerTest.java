package org.strut.amway.core.controller;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.core.stats.PageViewStatistics;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.model.HeroArticle;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.DateTimeUtils;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class HeroArticleControllerTest {

    @Mock
    private PageManager pageManager;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Page currentPage;

    @Mock
    private Page heroArticlePage;

    @Mock
    private Page heroArticle1Page;
    @Mock
    private Page heroArticle2Page;

    @Mock
    private PageViewStatistics pageViewStatistics;

    @Mock
    Resource resource;

    @Mock
    Resource nonNullImageResource;

    @Mock
    Node node;

    @Mock
    Property property;

    @Mock
    Property articlesProperty;

    @Mock
    ValueMap valueMap;

    @Mock
    Value value1;
    @Mock
    Value value2;

    private static final String ARTICLE_PATH_1 = "ARTICLE_PATH_1";
    private static final String ARTICLE_PATH_2 = "ARTICLE_PATH_2";
    private static final String ARTICLE_CUSTOM_IMAGE_1 = "ARTICLE_CUSTOM_IMAGE_1";

    private HeroArticleController heroArticleController;
    private HeroArticle heroArticle;
    private List<HeroArticle> heroArticles;

    private String noImageUrl = "noImageUrl.img.png";
    private String heroArticleUrl = "content/asia-pac/australia-new-zealand/australia/amway-today/en_au/beauty/skincare/nutriway-quick-facts";
    private String title = "HeroArticleTitle";
    private String category = "HeroArticleCategory";
    private GregorianCalendar publishedDate = (GregorianCalendar) GregorianCalendar.getInstance();

    @Before
    public void mockObject() throws RepositoryException {
        MockitoAnnotations.initMocks(this);
        heroArticleController = new HeroArticleController();
        dummyDataForTest();
    }

    @Test
    public void testGetHeroArticle() {
        heroArticle = heroArticleController.getHeroArticle(noImageUrl, pageManager, currentPage, pageViewStatistics);

        assertNotNull(heroArticle);
        assertEquals(heroArticle.getImageUrl(), noImageUrl);
        assertEquals(heroArticle.getUrl(), heroArticleUrl);
        assertEquals(heroArticle.getTitle(), title);
        assertEquals(heroArticle.getCategory(), category);
        assertEquals(heroArticle.getPublishedDate(), DateTimeUtils.parseToUTC(publishedDate)
                .toString(DateTimeFormat.forPattern("dd MMM YYYY")));
    }

    @Test
    public void testGetHeroArticleHasLinkUrlIsNull() throws RepositoryException {
        when(node.hasProperty(ArticleConstants.LINK_URL)).thenReturn(false);

        heroArticle = heroArticleController.getHeroArticle(noImageUrl, pageManager, currentPage, pageViewStatistics);

        assertNotNull(heroArticle);
        assertEquals(heroArticle.getImageUrl(), noImageUrl);
        assertEquals(heroArticle.getUrl(), StringUtils.EMPTY);
        assertEquals(heroArticle.getTitle(), StringUtils.EMPTY);
        assertEquals(heroArticle.getCategory(), StringUtils.EMPTY);
        assertEquals(heroArticle.getPublishedDate(), StringUtils.EMPTY);
    }

    @Test
    public void testGetHeroArticleWithNodeNotChild() throws RepositoryException {
        when(node.hasNode(ArticleConstants.HERO_ARTICLE_NODE_CHILD)).thenReturn(false);

        heroArticle = heroArticleController.getHeroArticle(noImageUrl, pageManager, currentPage, pageViewStatistics);

        assertNotNull(heroArticle);
        assertEquals(heroArticle.getImageUrl(), noImageUrl);
        assertEquals(heroArticle.getUrl(), StringUtils.EMPTY);
        assertEquals(heroArticle.getTitle(), StringUtils.EMPTY);
        assertEquals(heroArticle.getCategory(), StringUtils.EMPTY);
        assertEquals(heroArticle.getPublishedDate(), StringUtils.EMPTY);
    }

    @Test
    public void testGetHeroArticleWithResourceIsNull() {
        when(currentPage.getContentResource(ArticleConstants.HERO_ARTICLE_NODE_ABS)).thenReturn(null);
        heroArticle = heroArticleController.getHeroArticle(noImageUrl, pageManager, currentPage, pageViewStatistics);

        assertNotNull(heroArticle);
        assertEquals(heroArticle.getImageUrl(), noImageUrl);
        assertEquals(heroArticle.getUrl(), StringUtils.EMPTY);
        assertEquals(heroArticle.getTitle(), StringUtils.EMPTY);
        assertEquals(heroArticle.getCategory(), StringUtils.EMPTY);
        assertEquals(heroArticle.getPublishedDate(), StringUtils.EMPTY);
    }

    @Test
    public void shouldReturnNullWhenThereIsNoHeroArticle() {
        final Page heroArticlePage = null;
        when(pageManager.getPage(heroArticleUrl)).thenReturn(heroArticlePage);

        heroArticle = heroArticleController.getHeroArticle(noImageUrl, pageManager, currentPage, pageViewStatistics);

        assertNull(heroArticle);
    }

    private void dummyDataForTest() throws RepositoryException {
        when(currentPage.getContentResource(ArticleConstants.HERO_ARTICLE_NODE_ABS)).thenReturn(resource);
        when(resource.adaptTo(Node.class)).thenReturn(node);

        when(node.hasNode(ArticleConstants.HERO_ARTICLE_NODE_CHILD)).thenReturn(true);
        when(node.getNode(ArticleConstants.HERO_ARTICLE_NODE_CHILD)).thenReturn(node);

        when(node.hasProperty(ArticleConstants.LINK_URL)).thenReturn(true);
        when(node.getProperty(ArticleConstants.LINK_URL)).thenReturn(property);
        when(property.getString()).thenReturn(heroArticleUrl);

        when(pageManager.getPage(heroArticleUrl)).thenReturn(heroArticlePage);

        when(currentPage.getContentResource(ArticleConstants.HERO_ARTICLE_NODE_ABS + "/" + ArticleConstants.HERO_ARTICLE_NODE_CHILD)).thenReturn(resource);
        when(heroArticlePage.getContentResource(ArticleConstants.IMAGE_STRING)).thenReturn(resource);

        when(heroArticlePage.getTitle()).thenReturn(title);
        when(heroArticlePage.getParent(CategoryConstants.ABS_LEVEL)).thenReturn(currentPage);
        when(currentPage.getTitle()).thenReturn(category);
        when(currentPage.getProperties()).thenReturn(valueMap);
        when(heroArticlePage.getProperties()).thenReturn(valueMap);
        when(valueMap.get(CategoryConstants.CATEGORY_TYPE)).thenReturn(CategoryConstants.MAIN_BLOGGING_CATEGORY);
        when(valueMap.get(ArticleConstants.PUBLISH_DATE)).thenReturn(publishedDate);

        // carousel
        when(node.hasNode(ArticleConstants.HERO_ARTICLE_CAROUSEL_NODE_CHILD)).thenReturn(true);
        when(node.getNode(ArticleConstants.HERO_ARTICLE_CAROUSEL_NODE_CHILD)).thenReturn(node);

        when(node.hasProperty(ArticleConstants.ARTICLES_PROP)).thenReturn(true);
        when(node.getProperty(ArticleConstants.ARTICLES_PROP)).thenReturn(articlesProperty);

        when(value1.getString()).thenReturn(ARTICLE_PATH_1 + HeroArticleController.FIELD_SEPARATOR + ARTICLE_CUSTOM_IMAGE_1);
        when(value2.getString()).thenReturn(ARTICLE_PATH_2 + HeroArticleController.FIELD_SEPARATOR);

        //article 1
        when(pageManager.getPage(ARTICLE_PATH_1)).thenReturn(heroArticle1Page);
        when(resourceResolver.getResource(ARTICLE_CUSTOM_IMAGE_1)).thenReturn(nonNullImageResource);

        when(heroArticle1Page.getProperties()).thenReturn(valueMap);

        //article 2
        when(pageManager.getPage(ARTICLE_PATH_2)).thenReturn(heroArticle2Page);
        when(heroArticle2Page.getProperties()).thenReturn(valueMap);
    }

    ///////////////////////////////////////////////////////////////////
    // Articles section
    ///////////////////////////////////////////////////////////////////

    @Test
    public void testGetHeroArticlesReturnsEmpty() {
        heroArticles = heroArticleController.getHeroArticles(noImageUrl, pageManager, resourceResolver, currentPage,
                pageViewStatistics);
        assertTrue(heroArticles.isEmpty());
    }

    @Test
    public void testSingleHeroArticleMultiProperty() throws RepositoryException {
        when(articlesProperty.isMultiple()).thenReturn(true);
        when(articlesProperty.getValues()).thenReturn(new Value[]{value1});

        heroArticles = heroArticleController.getHeroArticles(noImageUrl, pageManager, resourceResolver, currentPage,
                pageViewStatistics);
        assertEquals(1, heroArticles.size());

        HeroArticle heroArticle = heroArticles.get(0);
        assertEquals(ARTICLE_PATH_1, heroArticle.getUrl());
        assertEquals(ARTICLE_CUSTOM_IMAGE_1, heroArticle.getImageUrl());
    }

    @Test
    public void testSingleHeroArticleSingleProperty() throws RepositoryException {
        when(articlesProperty.isMultiple()).thenReturn(false);
        String value = value1.getString();
        when(articlesProperty.getString()).thenReturn(value);

        heroArticles = heroArticleController.getHeroArticles(noImageUrl, pageManager, resourceResolver, currentPage,
                pageViewStatistics);
        assertEquals(1, heroArticles.size());

        HeroArticle heroArticle = heroArticles.get(0);
        assertEquals(ARTICLE_PATH_1, heroArticle.getUrl());
        assertEquals(ARTICLE_CUSTOM_IMAGE_1, heroArticle.getImageUrl());
    }

    @Test
    public void testMultipleHeroArticles() throws RepositoryException {
        when(articlesProperty.isMultiple()).thenReturn(true);
        when(articlesProperty.getValues()).thenReturn(new Value[]{value1, value2});

        heroArticles = heroArticleController.getHeroArticles(noImageUrl, pageManager, resourceResolver, currentPage,
                pageViewStatistics);
        assertEquals(2, heroArticles.size());

        HeroArticle heroArticle = heroArticles.get(0);
        assertEquals(ARTICLE_PATH_1, heroArticle.getUrl());
        assertEquals(ARTICLE_CUSTOM_IMAGE_1, heroArticle.getImageUrl());

        heroArticle = heroArticles.get(1);
        assertEquals(ARTICLE_PATH_2, heroArticle.getUrl());
        assertEquals(noImageUrl, heroArticle.getImageUrl());
    }
}
