package org.strut.amway.core.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.PageUtils;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.foundation.Navigation;
import com.day.cq.wcm.foundation.Navigation.Element;

public class NavigationController {
	
	  private static Logger LOGGER = LoggerFactory.getLogger(NavigationController.class);

    private Page currentPage;
    private Style currentStyle;
    private SlingHttpServletRequest request;

    public NavigationController(Page currentPage, Style currentStyle, SlingHttpServletRequest request) {
        this.currentPage = currentPage;
        this.currentStyle = currentStyle;
        this.request = request;
    }

    public List<Element> create() {
        int absParent = currentStyle.get(CategoryConstants.ABS_PARENT, CategoryConstants.ABS_LEVEL);
        PageFilter filter = new PageFilter(request);

        final int CATEGORY_LEVELS = 3; // cat & sub-cat
        Navigation navigation = new Navigation(currentPage, absParent, filter, CATEGORY_LEVELS);

        return sort(navigation);
    }

    public List<Navigation.Element> sort(Navigation navigation) {
        List<Navigation.Element> blogging = new ArrayList<Navigation.Element>();
        List<Navigation.Element> corporate = new ArrayList<Navigation.Element>();

        boolean bloggingPages = true;
        boolean hasMostPopularPage = false;
        for (Navigation.Element e : navigation) {
            if (PageUtils.isMostPopularPage(e.getPage())) {
                if (!hasMostPopularPage) {
                    blogging.add(0, e);
                    hasMostPopularPage = true;
                }
            } else if (isMainBlogging(e)) {
                blogging.add(e);
                bloggingPages = true;
            } else if (isCorporate(e)) {
                corporate.add(e);
                bloggingPages = false;
            } else {
            	// blogging.add(e);
				if (bloggingPages) {
					
					String label ="";
					
					String template = e.getPage().getProperties().get("cq:template","");
                	String[] bits = template.split("/");
                	String templatePage = bits[bits.length-1];
                //	LOGGER.info(template + "  iframe " + templatePage);
                	if(templatePage!=null && template!="")  {
					if (templatePage.equals("sub-category") && e.hasChildren()) {
						
					//	blogging.add(e);
						Iterator<Page> page = e.getPage().listChildren();
						while (page.hasNext()) {
							Page childPage = page.next();
							ValueMap labelMap = childPage.getProperties();
							
							String articletemplate = childPage.getProperties().get("cq:template","");
		                	String[] articlebits = articletemplate.split("/");
		                	String articleTemplatePage = articlebits[articlebits.length-1];
							
							if (articleTemplatePage.equalsIgnoreCase("sea-article-page")
									|| articleTemplatePage.equalsIgnoreCase("article-page")) {

								String[] labelAddresses = labelMap.get("label", new String[0]);

								if (labelAddresses.length > 0 && labelAddresses != null) {

									for (int i = 0; i < labelAddresses.length; i++) {

										if (labelAddresses[i].equalsIgnoreCase("Public")) {

											label = labelAddresses[i];
											//LOGGER.info(label + " label **");

										}
									}

								}

							}						
							
						}
						if (label != null && label != "" && label.equalsIgnoreCase("Public")) {

							blogging.add(e);
						}
					}
				}
					if (templatePage.equals("iframe-page") || templatePage.equals("redirect")) {
						blogging.add(e);
					}

                } else {
                    corporate.add(e);
                }
            }
        }

        blogging.addAll(corporate);
        return blogging;
    }

    private boolean isMainBlogging(Element e) {
        return CategoryConstants.MAIN_BLOGGING_CATEGORY.equalsIgnoreCase(String.valueOf(e.getPage().getProperties().get(CategoryConstants.CATEGORY_TYPE)));
    }

    private boolean isCorporate(Element e) {
        return CategoryConstants.CORPORATE_CATEGORY.equalsIgnoreCase(String.valueOf(e.getPage().getProperties().get(CategoryConstants.CATEGORY_TYPE)));
    }

}
