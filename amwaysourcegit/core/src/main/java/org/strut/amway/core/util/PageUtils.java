package org.strut.amway.core.util;

import javax.jcr.Node;
import javax.jcr.Property;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

public class PageUtils {

    private static final String CQ_PAGE_TYPE = "cq:Page";

    private PageUtils() {
    }

    public static boolean isCategoryPage(Page page) {
        String type = String.valueOf(page.getProperties().get(CategoryConstants.CATEGORY_TYPE));
        if (type != null && (type.equals(CategoryConstants.MAIN_BLOGGING_CATEGORY) || type.equals(CategoryConstants.CORPORATE_CATEGORY))) {
            return true;
        }
        return false;
    }

    public static boolean isArticlePage(Page page) {
        String template = String.valueOf(page.getProperties().get(ArticleConstants.CQ_TEMPLATE));
        if (template != null && (template.equals(ArticleConstants.ARTICLE_TEMPLATE) || template.equals(ArticleConstants.SEA_ARTICLE_TEMPLATE))) {
            return true;
        }
        return false;
    }

    public static boolean isSubCategoryPage(Page page) {
        String template = String.valueOf(page.getProperties().get(ArticleConstants.CQ_TEMPLATE));
        if (template != null && template.equals(ArticleConstants.SUB_CATEGORY_TEMPLATE)) {
            return true;
        }
        return false;
    }
    
    public static boolean isIframePage(Page page) {
        String template = String.valueOf(page.getProperties().get(ArticleConstants.CQ_TEMPLATE));
        if (template != null && template.equals(ArticleConstants.IFRAME_TEMPLATE)) {
            return true;
        }
        return false;
    }

    public static boolean isHomePage(Page page) {
        String template = String.valueOf(page.getProperties().get(ArticleConstants.RESOURCE_TYPE));
        if (template != null && template.equals(ArticleConstants.HOMEPAGE_RESOURCE_TYPE)) {
            return true;
        }
        return false;
    }  
    
    public static boolean isMostPopularPage(Page page) {
        String template = String.valueOf(page.getProperties().get(ArticleConstants.CQ_TEMPLATE));
        if (template != null && template.equals(ArticleConstants.SUB_MOST_POPULAR_TEMPLATE)) {
            return true;
        }
        return false;
    }
    
   

    public static String getContentPath(final Page page) {
        return page.getPath() + "/" + JcrConstants.JCR_CONTENT;
    }

    public static boolean isTheSameContentPath(final Page page1, final Page page2) {
        return getContentPath(page1).equals(getContentPath(page2));
    }

    public static boolean isCqPageType(final Node node) throws Exception {
        if (node != null) {
            Property jcrPrimaryTypeProp = node.getProperty(JcrConstants.JCR_PRIMARYTYPE);
            return CQ_PAGE_TYPE.equals(jcrPrimaryTypeProp.getString());
        }
        return false;
    }

}
