package org.strut.amway.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.jcr.Node;
import javax.jcr.Property;

import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

public class PageUtilsTest {

    @Mock
    Page page;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(page.getProperties()).thenReturn(Mockito.mock(ValueMap.class));
    }

    @Test
    public void testIsCategoryPageIsMainBlogging() {
        when(page.getProperties().get(CategoryConstants.CATEGORY_TYPE)).thenReturn(CategoryConstants.MAIN_BLOGGING_CATEGORY);
        assertTrue(PageUtils.isCategoryPage(page));
    }

    @Test
    public void testIsCategoryPageIsCorporate() {
        when(page.getProperties().get(CategoryConstants.CATEGORY_TYPE)).thenReturn(CategoryConstants.CORPORATE_CATEGORY);
        assertTrue(PageUtils.isCategoryPage(page));
    }

    @Test
    public void testIsCategoryPageIsNull() {
        when(page.getProperties().get(CategoryConstants.CATEGORY_TYPE)).thenReturn(null);
        assertFalse(PageUtils.isCategoryPage(page));
    }

    @Test
    public void testIsArticlePageIsArticle() {
        when(page.getProperties().get(ArticleConstants.CQ_TEMPLATE)).thenReturn(ArticleConstants.ARTICLE_TEMPLATE);
        assertTrue(PageUtils.isArticlePage(page));
    }

    @Test
    public void testIsArticlePageIsNull() {
        when(page.getProperties().get(ArticleConstants.CQ_TEMPLATE)).thenReturn(null);
        assertFalse(PageUtils.isArticlePage(page));
    }

    @Test
    public void testIsSubCategoryPage() {
        when(page.getProperties().get(ArticleConstants.CQ_TEMPLATE)).thenReturn(ArticleConstants.SUB_CATEGORY_TEMPLATE);
        assertTrue(PageUtils.isSubCategoryPage(page));
    }

    @Test
    public void testIsSubCategoryPageIsNull() {
        when(page.getProperties().get(ArticleConstants.CQ_TEMPLATE)).thenReturn(null);
        assertFalse(PageUtils.isSubCategoryPage(page));
    }

    @Test
    public void shouldGetContentOfPageProperly() {
        final Page page = Mockito.mock(Page.class);
        final String path = "/content/en/blog";
        when(page.getPath()).thenReturn(path);

        final String result = PageUtils.getContentPath(page);

        assertEquals(path + "/" + JcrConstants.JCR_CONTENT, result);
    }

    @Test
    public void shouldReturnTrueWhenTwoPageHaveTheSamePath() {
        final String path = "/content/en/blog";
        final Page pageA = Mockito.mock(Page.class);
        when(pageA.getPath()).thenReturn(path);

        final Page pageB = Mockito.mock(Page.class);
        when(pageB.getPath()).thenReturn(path);

        final boolean result = PageUtils.isTheSameContentPath(pageA, pageB);

        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void shouldReturnFalseWhenTwoPageHaveNotTheSamePath() {
        final Page pageA = Mockito.mock(Page.class);
        when(pageA.getPath()).thenReturn("/content/en/blogA");

        final Page pageB = Mockito.mock(Page.class);
        when(pageB.getPath()).thenReturn("/content/en/blogB");

        final boolean result = PageUtils.isTheSameContentPath(pageA, pageB);

        assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void shouldReturnFalseWhenNodeIsNull() throws Exception {
        final Node pageNode = null;
        final boolean result = PageUtils.isCqPageType(pageNode);
        assertFalse(result);
    }

    @Test
    public void shouldReturnTrueWhenNodeIsCQPageType() throws Exception {
        final Node pageNode = Mockito.mock(Node.class);
        final Property jcrPrimaryTypeProp = Mockito.mock(Property.class);
        when(pageNode.getProperty(JcrConstants.JCR_PRIMARYTYPE)).thenReturn(jcrPrimaryTypeProp);
        when(jcrPrimaryTypeProp.getString()).thenReturn("cq:Page");
        final boolean result = PageUtils.isCqPageType(pageNode);
        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenNodeIsNotCQPageType() throws Exception {
        final Node pageNode = Mockito.mock(Node.class);
        final Property jcrPrimaryTypeProp = Mockito.mock(Property.class);
        when(pageNode.getProperty(JcrConstants.JCR_PRIMARYTYPE)).thenReturn(jcrPrimaryTypeProp);
        when(jcrPrimaryTypeProp.getString()).thenReturn("cq:PageContent");
        final boolean result = PageUtils.isCqPageType(pageNode);
        assertFalse(result);
    }

}
