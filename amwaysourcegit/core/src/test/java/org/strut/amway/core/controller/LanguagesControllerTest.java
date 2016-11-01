package org.strut.amway.core.controller;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.model.Language;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

public class LanguagesControllerTest {

    private LanguagesController languagesController;

    @Mock
    private SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private Resource resource;
    @Mock
    private Node node;
    @Mock
    private Node languageNode;
    @Mock
    private Resource resourceLanguage;
    @Mock
    private NodeIterator nodeInterator;
    @Mock
    private Page page;

    @Before
    public void mockObject() {
        MockitoAnnotations.initMocks(this);

        languagesController = new LanguagesController();

    }

    @Test
    public void getLanguagesValidData() throws Exception {
        Mockito.doReturn(resourceResolver).when(slingHttpServletRequest).getResourceResolver();
        Mockito.doReturn(resource).when(resourceResolver).getResource("/content/asia-pac/australia-new-zealand/australia/amway-today");
        Mockito.doReturn(node).when(resource).adaptTo(Node.class);

        //en_au language node
        Mockito.doReturn(true).when(languageNode).hasNodes();
        Mockito.doReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").when(languageNode).getPath();
        Mockito.doReturn(resourceLanguage).when(resourceResolver).getResource("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        Mockito.doReturn(page).when(resourceLanguage).adaptTo(Page.class);
        Mockito.doReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").when(page).getPath();
        Mockito.doReturn(false).when(page).isHideInNav();
        Mockito.doReturn("en_au").when(languageNode).getName();
        Property jcrPrimaryTypeProp = Mockito.mock(Property.class);
        Mockito.doReturn(jcrPrimaryTypeProp).when(languageNode).getProperty(JcrConstants.JCR_PRIMARYTYPE);
        Mockito.doReturn("cq:Page").when(jcrPrimaryTypeProp).getString();

        //en language node
        Node enLanguageNode = Mockito.mock(Node.class);
        Resource enResourceLanguage = Mockito.mock(Resource.class);
        Page enPage = Mockito.mock(Page.class);
        Mockito.doReturn(true).when(enLanguageNode).hasNodes();
        Mockito.doReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en").when(enLanguageNode).getPath();
        Mockito.doReturn(enResourceLanguage).when(resourceResolver)
                .getResource("/content/asia-pac/australia-new-zealand/australia/amway-today/en");
        Mockito.doReturn(enPage).when(enResourceLanguage).adaptTo(Page.class);
        Mockito.doReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en").when(enPage).getPath();
        Mockito.doReturn(false).when(enPage).isHideInNav();
        Mockito.doReturn("en").when(enLanguageNode).getName();
        Property enJcrPrimaryTypeProp = Mockito.mock(Property.class);
        Mockito.doReturn(enJcrPrimaryTypeProp).when(enLanguageNode).getProperty(JcrConstants.JCR_PRIMARYTYPE);
        Mockito.doReturn("cq:Page").when(enJcrPrimaryTypeProp).getString();

        Mockito.doReturn(nodeInterator).when(node).getNodes();
        when(nodeInterator.hasNext()).thenReturn(true, true, false);
        when(nodeInterator.next()).thenReturn(languageNode, enLanguageNode);

        List<Language> languages = languagesController.getLaguages(slingHttpServletRequest,
                "/content/asia-pac/australia-new-zealand/australia/amway-today");

        List<Language> expectedLanguages = new ArrayList<Language>();

        expectedLanguages.add(new Language("English (Australia)", "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au"));
        expectedLanguages.add(new Language("English", "/content/asia-pac/australia-new-zealand/australia/amway-today/en"));

        Assert.assertNotNull(languages);
        Assert.assertEquals(expectedLanguages.get(0).getLanguageName(), languages.get(0).getLanguageName());
        Assert.assertEquals(expectedLanguages.get(0).getLanguageUrl(), languages.get(0).getLanguageUrl());
        Assert.assertEquals(expectedLanguages.get(1).getLanguageName(), languages.get(1).getLanguageName());
        Assert.assertEquals(expectedLanguages.get(1).getLanguageUrl(), languages.get(1).getLanguageUrl());
    }

    @Test
    public void getLanguagesIsHiddenNavigateEmpty() throws Exception {

        Mockito.doReturn(resourceResolver).when(slingHttpServletRequest).getResourceResolver();
        Mockito.doReturn(resource).when(resourceResolver).getResource("/content/asia-pac/australia-new-zealand/australia/amway-today");
        Mockito.doReturn(node).when(resource).adaptTo(Node.class);

        Mockito.doReturn(nodeInterator).when(node).getNodes();
        when(nodeInterator.hasNext()).thenReturn(true, false);
        Mockito.doReturn(languageNode).when(nodeInterator).next();

        Mockito.doReturn(true).when(languageNode).hasNodes();
        Mockito.doReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").when(languageNode).getPath();
        Mockito.doReturn(resourceLanguage).when(resourceResolver).getResource("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        Mockito.doReturn(page).when(resourceLanguage).adaptTo(Page.class);
        Mockito.doReturn(true).when(page).isHideInNav();
        Mockito.doReturn("en_au").when(languageNode).getName();
        Property jcrPrimaryTypeProp = Mockito.mock(Property.class);
        Mockito.doReturn(jcrPrimaryTypeProp).when(languageNode).getProperty(JcrConstants.JCR_PRIMARYTYPE);
        Mockito.doReturn("cq:Page").when(jcrPrimaryTypeProp).getString();

        List<Language> languages = languagesController.getLaguages(slingHttpServletRequest, "/content/asia-pac/australia-new-zealand/australia/amway-today");
        List<Language> expectedLanguages = new ArrayList<Language>();

        Assert.assertEquals(expectedLanguages, languages);
    }

    @Test
    public void getLanguagesEmptyWhenChildNodeIsNotCQPage() throws Exception {

        Mockito.doReturn(resourceResolver).when(slingHttpServletRequest).getResourceResolver();
        Mockito.doReturn(resource).when(resourceResolver).getResource("/content/asia-pac/australia-new-zealand/australia/amway-today");
        Mockito.doReturn(node).when(resource).adaptTo(Node.class);

        Mockito.doReturn(nodeInterator).when(node).getNodes();
        when(nodeInterator.hasNext()).thenReturn(true, false);
        Node invalidLanguageNode = Mockito.mock(Node.class);
        Mockito.doReturn(invalidLanguageNode).when(nodeInterator).next();

        Mockito.doReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").when(languageNode).getPath();
        Mockito.doReturn(resourceLanguage).when(resourceResolver).getResource("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        Mockito.doReturn(page).when(resourceLanguage).adaptTo(Page.class);
        Mockito.doReturn(true).when(page).isHideInNav();
        Mockito.doReturn("en_au").when(languageNode).getName();
        Property jcrPrimaryTypeProp = Mockito.mock(Property.class);
        Mockito.doReturn(jcrPrimaryTypeProp).when(invalidLanguageNode).getProperty(JcrConstants.JCR_PRIMARYTYPE);
        Mockito.doReturn("rep:ACL").when(jcrPrimaryTypeProp).getString();

        List<Language> languages = languagesController.getLaguages(slingHttpServletRequest, "/content/asia-pac/australia-new-zealand/australia/amway-today");
        List<Language> expectedLanguages = new ArrayList<Language>();

        Assert.assertEquals(expectedLanguages, languages);
    }

    @Test
    public void getLanguagesIsNullRequestEmpty() throws Exception {

        List<Language> languages = languagesController.getLaguages(null, "/content/asia-pac/australia-new-zealand/australia/amway-today");
        List<Language> expectedLanguages = new ArrayList<Language>();

        Assert.assertEquals(expectedLanguages, languages);
    }

    @Test
    public void getLanguagesIsNullNodePathEmpty() throws Exception {
        List<Language> languages = languagesController.getLaguages(slingHttpServletRequest, null);
        List<Language> expectedLanguages = new ArrayList<Language>();

        Assert.assertEquals(expectedLanguages, languages);
    }

    @Test
    public void getLanguagesIsEmptyNodePathEmpty() throws Exception {
        List<Language> languages = languagesController.getLaguages(slingHttpServletRequest, "");
        List<Language> expectedLanguages = new ArrayList<Language>();

        Assert.assertEquals(expectedLanguages, languages);
    }

    @Test
    public void getLanguagesIsInvalidNodePathEmpty() throws Exception {
        Mockito.doReturn(resourceResolver).when(slingHttpServletRequest).getResourceResolver();
        Mockito.doReturn(resource).when(resourceResolver).getResource("/invalid/invalid");
        Mockito.doReturn(null).when(resource).adaptTo(Node.class);
        List<Language> languages = languagesController.getLaguages(slingHttpServletRequest, "/invalid/invalid");
        List<Language> expectedLanguages = new ArrayList<Language>();

        Assert.assertEquals(expectedLanguages, languages);
    }

}
