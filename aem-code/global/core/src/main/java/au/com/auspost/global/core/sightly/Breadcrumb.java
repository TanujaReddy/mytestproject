package au.com.auspost.global.core.sightly;

import com.adobe.cq.sightly.WCMUse;
import com.day.cq.wcm.api.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * This generate breadcrumb for the page
 */
public class Breadcrumb extends WCMUse{

    private List<Page> breadcrumbList = new ArrayList<Page>();

    @Override
    public void activate() throws Exception {
        breadcrumbList = generateBreadcrumb();
    }

    public List<Page> getBreadcrumbList() {
        return breadcrumbList;
    }

    private List<Page> generateBreadcrumb() {
        List<Page> breadcrumbLinks = new ArrayList<Page>();

        long level = getCurrentStyle().get("absParent", 2L);
        long endLevel = getCurrentStyle().get("relParent", 1L);
        int currentLevel = getCurrentPage().getDepth();
        while (level < currentLevel - endLevel) {
            Page trail = getCurrentPage().getAbsoluteParent((int) level);
            breadcrumbLinks.add(trail);
            level++;
        }
        return breadcrumbLinks;
    }
}
