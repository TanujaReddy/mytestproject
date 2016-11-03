package org.strut.amway.core.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.ArticleLabelUtils;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.Constants;
import org.strut.amway.core.util.LinkTransformerUtils;

import com.day.cq.wcm.api.Page;

public class ArticleLabelFilterControllerSeo {

    private static Logger LOGGER = LoggerFactory.getLogger(ArticleLabelFilterController.class);

    public void checkUserIsEligibleForViewArticlePage(final Page articlePage, final SlingHttpServletRequest request, final SlingHttpServletResponse response,
            boolean isPublishInstance) throws Exception {
        if (isPublishInstance) {
            final List<String> articleLabelTypesForCookies = ArticleLabelUtils.getArticleLabelByCookies(request);
            final List<String> articleLabels = Arrays.asList((String[]) articlePage.getProperties().get(ArticleConstants.LABEL_PROPERTY));
            //for(String s: articleLabelTypesForCookies) {
            //	LOGGER.error("articleLabelTypesForCookies: " + s);
            //}
            //for(String s: articleLabels) {
            //	LOGGER.error("articleLabels: " + s);
            //}
            
            if (CollectionUtils.containsAny(articleLabelTypesForCookies, articleLabels)) {
                return;
            } else {
                // redirect to home page
                LOGGER.warn("User can not enough permission to view this page {} ", articlePage.getPath());
                response.sendRedirect(LinkTransformerUtils.transformPath(request.getResourceResolver(), getPathOfHomepage(articlePage))
                        + Constants.HTML_EXTENSION);
            }
        }
    }

    private String getPathOfHomepage(final Page articlePage) {
        final Page homePage = articlePage.getAbsoluteParent(CategoryConstants.ABS_LEVEL);
        return homePage.getPath();
    }

}
