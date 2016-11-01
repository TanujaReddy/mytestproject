package org.strut.amway.core.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.model.Breadcrumb;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.CategoryConstants;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;

public class BreadcrumbControllerTest {
    @Mock
    private Page currentPage;

    @Mock
    private Style currentStyle;

    @Mock
    private SlingHttpServletRequest slingRequest;

    @Mock
    private Page page;

    private BreadcrumbController breadcrumbService;
    private ResourceBundleTest resourceBundleTest;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        // These are not accurate but since it is relative the tests still work
        int level = 5;
        int endLevel = 1;
        int currentLevel = 9;

        resourceBundleTest = new ResourceBundleTest();

        when(currentStyle.get(CategoryConstants.ABS_PARENT, CategoryConstants.ABS_LEVEL)).thenReturn(level);
        when(currentStyle.get(CategoryConstants.REL_PARENT, CategoryConstants.REL_LEVEL)).thenReturn(endLevel);

        when(currentPage.getDepth()).thenReturn(currentLevel);

        when(page.getProperties()).thenReturn(Mockito.mock(ValueMap.class));

    }

    @Test
    public void testCreateBreadcrumbHasMainBlogging() {
        when(currentPage.getAbsoluteParent(anyInt())).thenReturn(page);
        when(page.getProperties().get(CategoryConstants.CATEGORY_TYPE)).thenReturn(CategoryConstants.MAIN_BLOGGING_CATEGORY);

        breadcrumbService = new BreadcrumbController();
        List<Breadcrumb> breadcrumbs = breadcrumbService.createBreadcrumb(resourceBundleTest, currentPage, currentStyle);

        assertNotNull(breadcrumbs);
        assertEquals(breadcrumbs.size(), 3);

    }

    @Test
    public void testCreateBreadcrumbHasEmpty() {
        when(currentPage.getAbsoluteParent(anyInt())).thenReturn(page);
        when(page.getProperties().get(CategoryConstants.CATEGORY_TYPE)).thenReturn(null);

        breadcrumbService = new BreadcrumbController();
        List<Breadcrumb> breadcrumbs = breadcrumbService.createBreadcrumb(resourceBundleTest, currentPage, currentStyle);

        assertNotNull(breadcrumbs);
        assertEquals(breadcrumbs.size(), 3);

    }

    @Test
    public void testCreateBreadcrumbHasSubCategory() {
        when(currentPage.getAbsoluteParent(anyInt())).thenReturn(page);
        when(page.getProperties().get(CategoryConstants.CATEGORY_TYPE)).thenReturn(CategoryConstants.MAIN_BLOGGING_CATEGORY);
        when(page.getProperties().get(ArticleConstants.CQ_TEMPLATE)).thenReturn(ArticleConstants.SUB_CATEGORY_TEMPLATE);
        breadcrumbService = new BreadcrumbController();
        List<Breadcrumb> breadcrumbs = breadcrumbService.createBreadcrumb(resourceBundleTest, currentPage, currentStyle);

        assertNotNull(breadcrumbs);
        assertEquals(breadcrumbs.size(), 3);

    }

    @Test 
    public void testCreateBreadcrumbHasArticlePage() {
        when(currentPage.getAbsoluteParent(anyInt())).thenReturn(page);
        when(page.getProperties().get(CategoryConstants.CATEGORY_TYPE)).thenReturn(CategoryConstants.MAIN_BLOGGING_CATEGORY);
        when(page.getProperties().get(ArticleConstants.CQ_TEMPLATE)).thenReturn(ArticleConstants.ARTICLE_TEMPLATE);

        breadcrumbService = new BreadcrumbController();
        List<Breadcrumb> breadcrumbs = breadcrumbService.createBreadcrumb(resourceBundleTest, currentPage, currentStyle);

        assertNotNull(breadcrumbs);
        assertEquals(breadcrumbs.size(), 0);

    }

    @Test
    public void testCreateBreadcrumbIsEmpty() {
        when(currentPage.getAbsoluteParent(anyInt())).thenReturn(null);

        breadcrumbService = new BreadcrumbController();
        List<Breadcrumb> breadcrumbs = breadcrumbService.createBreadcrumb(resourceBundleTest, currentPage, currentStyle);

        assertNotNull(breadcrumbs);
        assertEquals(breadcrumbs.size(), 0);

    }
    
    @Test
    public void testBreadcrumbContainToolbarText() {
        when(currentPage.getAbsoluteParent(anyInt())).thenReturn(page);
        when(page.getNavigationTitle()).thenReturn("Toolbar");

        breadcrumbService = new BreadcrumbController();
        List<Breadcrumb> breadcrumbs = breadcrumbService.createBreadcrumb(resourceBundleTest, currentPage, currentStyle);

        assertEquals(breadcrumbs.size(), 1);

    }

}
