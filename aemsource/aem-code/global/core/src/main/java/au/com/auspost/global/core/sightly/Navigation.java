package au.com.auspost.global.core.sightly;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUse;
import com.day.cq.wcm.api.Page;

import au.com.auspost.global.core.components.beans.NavigationItem;
import au.com.auspost.global.utils.Constants;


public class Navigation extends WCMUse {

    protected static final Logger LOG = LoggerFactory.getLogger(Navigation.class);

    protected static final long serialVersionUID = -2177222643051798487L;

    protected static final int DEFAULT_PARENT_LEVEL = 2;

    protected static final int DEFAULT_MAXDEPTH = 2;

    private int parentLevel = DEFAULT_PARENT_LEVEL;

    private int maxDepth = DEFAULT_MAXDEPTH;

    private String parentPath = null;

    private String parentPageName = null;

    public static final String PROP_NAME_SUMMARY = "summary";

    public String image = null;
    
    private List<NavigationItem> items;

    private int firstItem = 0;

    /**
     * @param parentLevelString Setting parent level
     */
    public void setParentLevel(String parentLevelString) {
        try {
            parentLevel = Integer.valueOf(parentLevelString);
        } catch (NumberFormatException nfe) {
            parentLevel = DEFAULT_PARENT_LEVEL;
        }
    }

    public String getParentLevel() {
        return String.valueOf(parentLevel);
    }

    /**
     * @param parentPathString Setting rootPath
     */
    public void setParentPath(String parentPathString) {
        parentPath = parentPathString;
    }

    public String getParentPath() {
        return parentPath;
    }

    /**
     * @param maxDepthString Setting maxDepth
     */
    public void setMaxDepth(String maxDepthString) {
        try {
            maxDepth = Integer.valueOf(maxDepthString);
        } catch (NumberFormatException nfe) {
            maxDepth = DEFAULT_MAXDEPTH;
        }
    }

    public String getMaxDepth() {
        return String.valueOf(maxDepth);
    }

    /**
     * @param parentPageNameString Setting parentPageName
     */
    public void setParentPageName(String parentPageNameString) {
        parentPageName = parentPageNameString;
    }

    public String getParentPageName() {
        return parentPageName;
    }

    /**
     * use specific page if supplied, otherwise use level and page name
     *
     * @return
     */
    public Page getParentPage() {
        Page parentPage = null;
        ResourceResolver resolverPage = getResourceResolver();

        // if we have a specific path, use it
        if (getParentPath() != null && !"".equals(getParentPath())) {

            Resource resPage = resolverPage.getResource(getParentPath());
            parentPage = resPage.adaptTo(Page.class);

            // if we have a level and a name, find the page of the given name at the level
        } else if (getParentPageName() != null && !"".equals(getParentPageName()) && parentLevel > 0) {

            Page containerPage = getCurrentPage().getAbsoluteParent(parentLevel - 1);

            if (containerPage != null) {
                boolean pageExists = containerPage.hasChild(getParentPageName());

                if (pageExists) {
                    StringBuffer path = new StringBuffer();
                    path.append(containerPage.getPath()).append('/').append(getParentPageName());
                    Resource resPage = resolverPage.getResource(path.toString());
                    if (resPage != null) {
                        parentPage = resPage.adaptTo(Page.class);
                    }
                }
            }

            // just use the level
        } else if (getParentPageName() == null || "".equals(getParentPageName()) && parentLevel > 0) {
            parentPage = getCurrentPage().getAbsoluteParent(parentLevel);
        }

        return parentPage;
    }
    
    @Override
	public void activate() throws Exception {
    	 items = new ArrayList<NavigationItem>();

        Page parentPage = getParentPage();

        if (parentPage != null) {

            Iterator<Page> iterator = parentPage.listChildren();
            while (iterator.hasNext()) {

                Page childPage = iterator.next();
                if (!childPage.isHideInNav() && childPage.isValid()) {
                    firstItem++;
                    NavigationItem navItem = convertPageToNavItem(childPage, 1, maxDepth ,firstItem);
                    items.add(navItem);
                }
            }
        } else {
        	//do Nothing
        }
		
	}

    //Nav Items
    public List<NavigationItem> getNavItems(){
    	if(null!=items && !items.isEmpty()){
    		return items;
    	}
    	return null;
    }

    //


    protected NavigationItem convertPageToNavItem(Page page, int currentDepth, int maxDepth, int firstItem) {
        String title = getTitle(page);
        String name = getName(page);
        String pathDotHtml = getPath(page);
        String summary = getSummary(page);
        String image = getImage(page);
        String pagePath = getPagePath(page);
        boolean hideInNav = isHideInNav(page);
        boolean active = isActive(page);
        boolean follow = isFollow(page);

        NavigationItem navItem = null;

        if (!page.isHideInNav() && page.isValid()) {
            navItem = getNavitem(title, pathDotHtml, pagePath,active,firstItem, follow, hideInNav, name, summary, image);
            navItem.setItemDepth(currentDepth);
            // if we are not at maxDepth, get children and recurse..
            if (currentDepth < maxDepth) {
                Iterator<Page> children = page.listChildren();
                while (children.hasNext()) {
                    Page childPage = children.next();
                    // only add non-hidden children
                    if (!childPage.isHideInNav() && childPage.isValid()) {
                        NavigationItem child = convertPageToNavItem(childPage, currentDepth + 1, maxDepth, firstItem);
                        if (child != null) {
                            navItem.addChild(child);
                        }
                    }
                }
            }
        }
        return navItem;
    }

    protected boolean isFollow(Page page) {
        return isActive(page) || getCurrentPage().hasChild(page.getName());
    }

    protected String getTitle(Page page) {
        String navTitle = page.getNavigationTitle();
        return StringUtils.isNotBlank(navTitle) ? navTitle : page.getTitle();
    }

    protected boolean isHideInNav(Page page) {
        return page.isHideInNav();
    }


    protected String getName(Page page) {
        return page.getName();
    }

    protected String getPath(Page page) {
        return page.getPath() + Constants.DOT_HTML;
    }
    
    protected String getPagePath(Page page){
    	return page.getPath();
    }

    protected String getSummary(Page page) {
        String result = null;

        Node node = page.adaptTo(Node.class);

        if (node != null) {

            try {

                Node jcrContent = node.getNode(Constants.JCR_CONTENT);

                // locate the summary property
                if (jcrContent != null && jcrContent.hasProperty(PROP_NAME_SUMMARY)) {

                    result = jcrContent.getProperty(PROP_NAME_SUMMARY).getString();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("summary: {}", result);
                    }

                }

            } catch (PathNotFoundException e) {
                LOG.error("Errror , path not found",e);
                //No need to throw exception, doesn't matter
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }


    protected String getImage(Page page) {
        image = page.getProperties().get("image/fileReference", String.class);
        return StringUtils.isNotBlank(image) ? image : null;
    }

    protected boolean isCurrentPage(Page page) {
        return getCurrentPage().getPath().equals(page.getPath());
    }

    protected boolean containCurrentPage(Page page) {
        List<Page> parents = getAncestorsOfCurrentPage(page);

        for (Page parent : parents) {
            if (parent.getPath().equals(page.getPath())) {
                return true;
            }
        }

        return false;
    }

    protected List<Page> getAncestorsOfCurrentPage(Page page) {
        List<Page> parentPages = new ArrayList<Page>();

        int currentPageLevel = getCurrentPage().getDepth() - 1;
        int pageLevel = page.getDepth() - 1;

        for (int index = pageLevel; index < currentPageLevel; index++) {
            Page parent = getCurrentPage().getAbsoluteParent(index);
            parentPages.add(parent);
        }

        return parentPages;
    }

    protected boolean isActive(Page page) {
        return isCurrentPage(page) || containCurrentPage(page);
    }

    protected NavigationItem getNavitem(String title,String pathDotHTML,String pagePath, boolean active, int firstItem,
                                        boolean follow, boolean hideInNav,
                                        String name, String summary, String image) {

        return new NavigationItem(title,pathDotHTML, pagePath,active,firstItem,
                follow, hideInNav, name, summary, image);
    }

	

}
