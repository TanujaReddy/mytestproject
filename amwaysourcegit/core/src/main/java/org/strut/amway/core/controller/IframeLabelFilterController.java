package org.strut.amway.core.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.enumeration.IframeLabelType;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.Constants;
import org.strut.amway.core.util.IframeLabelUtils;
import org.strut.amway.core.util.LinkTransformerUtils;

import com.day.cq.wcm.api.Page;

public class IframeLabelFilterController {

    private static Logger LOGGER = LoggerFactory.getLogger(IframeLabelFilterController.class);

    public void checkUserIsEligibleForViewIframePage(final Page iframePage, final SlingHttpServletRequest request, final SlingHttpServletResponse response,
            boolean isPublishInstance) throws Exception {
        if (isPublishInstance) {
            final List<IframeLabelType> iframeLabelTypesForSession = IframeLabelUtils.getIframeLabelBySession(request.getSession());
            final String[] iframeLabels = (String[]) iframePage.getProperties().get(ArticleConstants.LABEL_PROPERTY);
            final List<IframeLabelType> iframeLabelTypes = IframeLabelType.resolve(iframeLabels);
            if (CollectionUtils.containsAny(iframeLabelTypesForSession, iframeLabelTypes)) {
                return;
            } else {
                // redirect to home page
                LOGGER.warn("User can not enough permission to view this page {} ", iframePage.getPath());
                response.sendRedirect(LinkTransformerUtils.transformPath(request.getResourceResolver(), getPathOfHomepage(iframePage))
                        + Constants.HTML_EXTENSION);
            }
        }
    }

    private String getPathOfHomepage(final Page iframePage) {
        final Page homePage = iframePage.getAbsoluteParent(CategoryConstants.ABS_LEVEL);
        return homePage.getPath();
    }

}
