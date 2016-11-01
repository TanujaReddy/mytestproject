package org.strut.amway.core.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.model.Breadcrumb;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.PageUtils;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;

public class BreadcrumbController {
    private static Logger LOGGER = LoggerFactory.getLogger(BreadcrumbController.class);

    public List<Breadcrumb> createBreadcrumb(ResourceBundle resourceBundle, Page currentPage, Style currentStyle) {

        List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

        long level = currentStyle.get(CategoryConstants.ABS_PARENT, CategoryConstants.ABS_LEVEL);
        long endLevel = currentStyle.get(CategoryConstants.REL_PARENT, CategoryConstants.REL_LEVEL);
        int currentLevel = currentPage.getDepth();

        while (level < currentLevel - endLevel) {

            Page trail = currentPage.getAbsoluteParent((int) level);
            if (trail == null) {
                LOGGER.debug("Page at level " + level + " is null", trail);
                break;
            }
            
            if (PageUtils.isArticlePage(trail)) {
                level++;
                continue;
            }

            String title = trail.getNavigationTitle();

            if (StringUtils.isEmpty(title)) {
                title = trail.getTitle();
            }

            if (StringUtils.isEmpty(title)) {
                title = trail.getName();
            }

            if (level == CategoryConstants.ABS_LEVEL) {
                title = resourceBundle.getString("homePage");
            }
            
            if(!"Toolbar".equals(title)) {
                breadcrumbs.add(new Breadcrumb(title, trail.getPath() + ".html"));
            }
            
            level++;
        }

        return breadcrumbs;
    }

}
