package org.strut.amway.core.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.strut.amway.core.model.CategoryModel;
import org.strut.amway.core.model.SubCategory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.core.impl.PageImpl;
import com.day.cq.wcm.core.impl.designer.StyleImpl;
import com.day.cq.wcm.foundation.Navigation;
import com.day.cq.wcm.foundation.Navigation.Element;

public class SiteMapControllerTest {

    private Page currentPage;
    private Style currentStyle;
    private SlingHttpServletRequest request;
    private Navigation navigation;

    /**
     * Prepare environment before testing
     */
    @Before
    public void mockObject() {
        currentPage = Mockito.mock(PageImpl.class);
        Mockito.when(currentPage.getPath()).thenReturn("http://localhost:4502/content/asia-pac/australia-new-zealand/australia/amway-today/en_au.html");
        currentStyle = Mockito.mock(StyleImpl.class);
        Mockito.when(currentStyle.get("absParent", 2)).thenReturn(2);
        request = Mockito.mock(SlingHttpServletRequest.class);
        navigation = Mockito.mock(Navigation.class);
        Iterator<Element> dummyListElement = dummyListElement();
        Mockito.when(navigation.iterator()).thenReturn(dummyListElement);
    }

    /**
     * Create dummy categories
     * 
     * @return list categories
     */
    private Iterator<Element> dummyListElement() {
        List<Element> dummy = new ArrayList<Element>();
        dummy.add(dummyElement("News"));
        dummy.add(dummyElement("Events"));
        dummy.add(dummyElement("Events"));
        Element family = dummyElement("Family");
        // Hide family category
        Mockito.when(family.getPage().isHideInNav()).thenReturn(true);
        dummy.add(family);

        Element bbc = dummyElement("BBC");
        Mockito.when(bbc.getPage()).thenReturn(null);
        dummy.add(bbc);
        return dummy.iterator();
    }

    /**
     * Create dummy sub-category
     * 
     * @param title
     *            title of category
     * @return Element contain category and sub-categories
     */
    private Element dummyElement(String title) {
        Element element = Mockito.mock(Element.class);
        // Create list sub-categories
        List<Page> childrenPages = new ArrayList<>();
        Page childrenPage = Mockito.mock(PageImpl.class);
        Mockito.when(childrenPage.getTitle()).thenReturn("Sub-1");
        childrenPages.add(childrenPage);
        childrenPage = Mockito.mock(PageImpl.class);
        Mockito.when(childrenPage.getTitle()).thenReturn("Sub-2");
        // Hide Sub-2
        Mockito.when(childrenPage.isHideInNav()).thenReturn(true);
        childrenPages.add(childrenPage);
        Iterator<Page> iteratorChildrens = childrenPages.listIterator();
        // Create content for categories
        Mockito.when(element.getTitle()).thenReturn(title);
        Mockito.when(!element.hasChildren()).thenReturn(true);
        Mockito.when(element.getPage()).thenReturn(Mockito.mock(Page.class));
        Mockito.when(element.getPage().listChildren()).thenReturn(iteratorChildrens);
        return element;
    }

    /**
     * Create expectation list
     * 
     * @return list CategoryModel
     */
    private List<CategoryModel> dummyExpectedList() {
        List<CategoryModel> categories = new ArrayList<>();
        List<SubCategory> subCategories = new ArrayList<SubCategory>();
        SubCategory subCategory = new SubCategory("Sub-1", "#");
        subCategories.add(subCategory);
        // Create News category
        CategoryModel category = new CategoryModel();
        category.setSubCategories(subCategories);
        category.setTitle("News");
        categories.add(category);
        // Create Events category
        category = new CategoryModel();
        category.setTitle("Events");
        category.setSubCategories(subCategories);
        categories.add(category);
        return categories;

    }

    /**
     * Test get category;
     */
    @Test
    public void testGetCategory() {
        new SiteMapController(currentPage, currentStyle, request);
    }

    /**
     * Test method getCategoriesInNav
     */
    @Test
    public void testGetAllCategories() {
        SiteMapController controller = new SiteMapController(currentPage, currentStyle, request);
        List<CategoryModel> actuals = controller.getCategoriesInNav(navigation);
        List<CategoryModel> expectations = dummyExpectedList();
        // Assert actual and expected
        Assert.assertEquals(actuals.size(), expectations.size());
        for (int i = 0; i < actuals.size(); i++) {
            CategoryModel actual = actuals.get(i);
            CategoryModel expectation = expectations.get(i);
            Assert.assertEquals(actual.getTitle(), expectation.getTitle());
            List<SubCategory> subCategoriesActutal = actual.getSubCategories();
            List<SubCategory> subCategoriesExpection = expectation.getSubCategories();
            for (int j = 0; j < subCategoriesActutal.size(); j++) {
                Assert.assertEquals(subCategoriesActutal.get(j).getTitle(), subCategoriesExpection.get(j).getTitle());
                Assert.assertEquals(subCategoriesActutal.get(j).getUrl(), subCategoriesExpection.get(j).getUrl());
            }

        }
    }

    /**
     * Test method getCategoriesInNav
     */
    @Test
    public void testNavigationNull() {
        SiteMapController controller = new SiteMapController(currentPage, currentStyle, request);
        List<CategoryModel> actuals = controller.getCategoriesInNav(null);
        Assert.assertEquals(0, actuals.size());
    }
}
