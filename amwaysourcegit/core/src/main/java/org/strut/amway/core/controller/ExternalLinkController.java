package org.strut.amway.core.controller;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.ExternalLinkConstants;

import com.day.cq.wcm.api.Page;

public class ExternalLinkController {

    protected final Logger LOGGER = LoggerFactory.getLogger(ExternalLinkController.class);

    public String[] getExternalLink(SlingHttpServletRequest request, Page currentPage) {

        String[] externalLinks = new String[2];
        externalLinks[0] = ExternalLinkConstants.DEFAULT_LINK;
        externalLinks[1] = ExternalLinkConstants.DEFAULT_LINK;

        String resourcePath = currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL - 1).getPath()
                + ExternalLinkConstants.NODE_PATH;

        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource(resourcePath);
        if (resource != null) {
            ValueMap properties = resource.adaptTo(ValueMap.class);
            if (properties != null) {
                externalLinks[0] = properties.get(ExternalLinkConstants.PRIVACY_SECURITY_PROPERTIY, 
                        ExternalLinkConstants.DEFAULT_LINK);
                externalLinks[1] = properties.get(ExternalLinkConstants.TERM_OF_USE_PROPERTIY, 
                        ExternalLinkConstants.DEFAULT_LINK);
            }
        }

        return externalLinks;
    }
}
