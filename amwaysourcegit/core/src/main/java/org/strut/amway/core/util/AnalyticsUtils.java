package org.strut.amway.core.util;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import org.apache.jackrabbit.commons.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 *
 */
public class AnalyticsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsUtils.class);

    public static final String SECTION = "ana_section";
    public static final String CATEGORY = "ana_category";
    public static final String SUBCATEGORY = "ana_subcategory";
    public static final String DETAIL = "ana_detail";

    public static final String SECTION_ARTICLES = "articles";
    public static final String SECTION_HOME = "home";
    public static final String SECTION_SITEMAP = "sitemap";
    public static final String SECTION_CONTACT = "contact";
    public static final String SECTION_MOST_POPULAR = "most_popular";

    public static final String PROP_REGION = "anaRegion";
    public static final String PROP_SUBGROUP = "anaSubgroup";
    public static final String PROP_SUBREGION = "anaSubregion";
    public static final String PROP_COUNTRY = "anaCountry";


    public static String getSiteCountry(Page page) {
        return getAnalyticsProperty(page, PROP_COUNTRY);
    }

    public static String getSiteRegion(Page page) {
        return getAnalyticsProperty(page, PROP_REGION);
    }

    public static String getSiteSubGroup(Page page) {
        return getAnalyticsProperty(page, PROP_SUBGROUP);
    }

    public static String getSiteSubRegion(Page page) {
        return getAnalyticsProperty(page, PROP_SUBREGION);
    }

    /**
     * Tries to return the site property for analytics reporting. Straight value from the property if available. Null otherwise.
     * Assumes the content structure specified in {@link CategoryConstants}.
     *
     * @param page any page
     */
    private static String getAnalyticsProperty(Page page, String property) {
        try {
            Node siteNode = (Node) page.adaptTo(Node.class).getAncestor(CategoryConstants.SITE_LEVEL + 2); // api level mismatch
            return JcrUtils.getStringProperty(siteNode, JcrConstants.JCR_CONTENT + "/" + property, "");
        } catch (RepositoryException e) {
            LOGGER.error("Error getting analytics property", e);
            return "";
        }
    }
}
