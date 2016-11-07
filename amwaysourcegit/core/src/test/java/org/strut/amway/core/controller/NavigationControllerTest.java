package org.strut.amway.core.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.strut.amway.core.controller.NavigationController;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.core.impl.PageImpl;
import com.day.cq.wcm.core.impl.designer.StyleImpl;
import com.day.cq.wcm.foundation.Navigation;
import com.day.cq.wcm.foundation.Navigation.Element;

public class NavigationControllerTest {
    private Page currentPage;
    private Style currentStyle;
    private SlingHttpServletRequest request;
    private Navigation navigation;
    
    @Before
    public void mockObject(){
        currentPage = Mockito.mock(PageImpl.class);
        Mockito.when(currentPage.getPath()).thenReturn("http://localhost:4502/content/asia-pac/australia-new-zealand/australia/amway-today/en_au.html");
        
        currentStyle = Mockito.mock(StyleImpl.class);
        Mockito.when(currentStyle.get("absParent", 2)).thenReturn(2);
        
        request = Mockito.mock(SlingHttpServletRequest.class);
        
        navigation = Mockito.mock(Navigation.class);
        Iterator<Element> dummyListElement = dummyListElement();
        Mockito.when(navigation.iterator()).thenReturn(dummyListElement);
        
    }

    private Iterator<Element> dummyListElement() {
        List<Element> dummy = new ArrayList<Element>();
        
        dummy.add(dummyElement("main-blogging"));
        dummy.add(dummyElement("corporate"));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement("main-blogging"));
        dummy.add(dummyElement("corporate"));
        dummy.add(dummyElement("corporate"));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement("main-blogging"));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement("corporate"));
        dummy.add(dummyElement("main-blogging"));
        dummy.add(dummyElement("main-blogging"));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement(""));
        dummy.add(dummyElement(""));
        
        return dummy.iterator();
    }
    
    private List<Element> dummyExpectedListElement() {
        List<Element> expected = new ArrayList<Element>();
        
        expected.add(dummyElement("main-blogging"));
        expected.add(dummyElement("main-blogging"));
        expected.add(dummyElement("main-blogging"));
        expected.add(dummyElement(""));
        expected.add(dummyElement("main-blogging"));
        expected.add(dummyElement("main-blogging"));
        expected.add(dummyElement(""));
        expected.add(dummyElement(""));
        expected.add(dummyElement(""));
        expected.add(dummyElement(""));
        expected.add(dummyElement("corporate"));
        expected.add(dummyElement(""));
        expected.add(dummyElement(""));
        expected.add(dummyElement(""));
        expected.add(dummyElement("corporate"));
        expected.add(dummyElement("corporate"));
        expected.add(dummyElement(""));
        expected.add(dummyElement(""));
        expected.add(dummyElement(""));
        expected.add(dummyElement(""));
        expected.add(dummyElement("corporate"));
                
        return expected;
    }

    private Element dummyElement(String categoryType) {
        Element element =Mockito.mock(Element.class);
        Mockito.when(element.getPage()).thenReturn(Mockito.mock(Page.class));        
        Mockito.when(element.getPage().getProperties()).thenReturn(Mockito.mock(ValueMap.class));        
        Mockito.when(element.getPage().getProperties().get("categoryType")).thenReturn(categoryType);
        return element;
    }

    @Test
    public void testSortNavigation() {
        NavigationController navigationPage = new NavigationController(currentPage, currentStyle, request);
        List<Element> actual = navigationPage.sort(navigation);
        List<Element> expected = dummyExpectedListElement();

        // Assert actual and expected
        Assert.assertEquals(actual.size(), expected.size());

        for (int i = 0; i < actual.size(); i++) {
            Assert.assertEquals(actual.get(i).getPage().getProperties().get("categoryType"), expected.get(i).getPage().getProperties().get("categoryType"));
        }

    }

}