package org.strut.amway.core.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.strut.amway.core.model.Language;
import org.strut.amway.core.util.PageUtils;

import com.day.cq.wcm.api.Page;

public class LanguagesController {

    /**
     * Get Languages node title for Languages menu.
     * 
     * @param request
     *            {@link SlingHttpServletRequest}
     * @param nodePath
     *            {@link String}
     * @return List<String>
     * @throws RepositoryException
     */
    public List<Language> getLaguages(SlingHttpServletRequest request, String nodePath) throws Exception {

        List<Language> languages = new ArrayList<Language>();
        if (null != request && null != nodePath && !nodePath.isEmpty()) {
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource resource = resourceResolver.getResource(nodePath);
            Node node = resource.adaptTo(Node.class);
            if (null != node) {
                NodeIterator nodeIter = node.getNodes();
                while (nodeIter.hasNext()) {
                    Node childNode = ((Node) nodeIter.next());
                    if (PageUtils.isCqPageType(childNode)) {
                        Resource pageResource = resourceResolver.getResource(childNode.getPath());
                        Page page = pageResource.adaptTo(Page.class);
                        boolean isHideInNav = page.isHideInNav();
                        if (!isHideInNav) {
                            // Support for localized languages like en_us and en_au
                            String tweakedNodeName = childNode.getName().replace("_", "-");
                            Locale locale = Locale.forLanguageTag(tweakedNodeName);
                            languages.add(new Language(locale.getDisplayName(), page.getPath()));
                        }
                    }
                }
            }
        }
        return languages;
    }

}
