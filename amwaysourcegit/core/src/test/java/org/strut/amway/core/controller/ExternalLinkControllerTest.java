package org.strut.amway.core.controller;

import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.util.ExternalLinkConstants;

import com.day.cq.wcm.api.Page;

public class ExternalLinkControllerTest {

    @Mock
    private Resource resource;

    @Mock
    private Page currentPage;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private SlingHttpServletRequest slingRequest;

    @Mock
    private ValueMap properties;

    String resourcePath;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        resourcePath = "http://localhost:4502/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        when(currentPage.getAbsoluteParent(4)).thenReturn(currentPage);
        when(currentPage.getPath()).thenReturn(resourcePath);
        when(slingRequest.getResourceResolver()).thenReturn(resourceResolver);
    }

    @Test
    public void testResourceIsNull() {
        when(resourceResolver.getResource(resourcePath + ExternalLinkConstants.NODE_PATH)).thenReturn(null);

        ExternalLinkController externalLinkController = new ExternalLinkController();
        String[] externalLinks = externalLinkController.getExternalLink(slingRequest, currentPage);
        Assert.assertEquals(ExternalLinkConstants.DEFAULT_LINK, externalLinks[0]);
        Assert.assertEquals(ExternalLinkConstants.DEFAULT_LINK, externalLinks[1]);
    }

    @Test
    public void testResourceIsNotNullAndPropertiesIsNull() {
        when(resourceResolver.getResource(resourcePath + ExternalLinkConstants.NODE_PATH)).thenReturn(resource);
        when(resource.adaptTo(ValueMap.class)).thenReturn(null);

        ExternalLinkController externalLinkController = new ExternalLinkController();
        String[] externalLinks = externalLinkController.getExternalLink(slingRequest, currentPage);
        Assert.assertEquals(ExternalLinkConstants.DEFAULT_LINK, externalLinks[0]);
        Assert.assertEquals(ExternalLinkConstants.DEFAULT_LINK, externalLinks[1]);
    }

    @Test
    public void testResourceAndPropertiesIsNotNull() {
        when(resourceResolver.getResource(resourcePath + ExternalLinkConstants.NODE_PATH)).thenReturn(resource);
        when(resource.adaptTo(ValueMap.class)).thenReturn(properties);

        String privacyLink = "http://www.amway2u.com/c1/amw_navindex.jsp?rfnbr=1006";
        when(properties.get(ExternalLinkConstants.PRIVACY_SECURITY_PROPERTIY, 
                ExternalLinkConstants.DEFAULT_LINK)).thenReturn(privacyLink);

        String tempOfUseLink = "http://www.amway2u.com/c1/amw_navindex.jsp?rfnbr=1005";
        when(properties.get(ExternalLinkConstants.TERM_OF_USE_PROPERTIY, 
                ExternalLinkConstants.DEFAULT_LINK)).thenReturn(tempOfUseLink);

        ExternalLinkController externalLinkController = new ExternalLinkController();
        String[] externalLinks = externalLinkController.getExternalLink(slingRequest, currentPage);
        Assert.assertEquals(privacyLink, externalLinks[0]);
        Assert.assertEquals(tempOfUseLink, externalLinks[1]);
    }
}
