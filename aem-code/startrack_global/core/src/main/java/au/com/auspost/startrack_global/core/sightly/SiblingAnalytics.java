package au.com.auspost.startrack_global.core.sightly;

import au.com.auspost.startrack_global.core.util.AusPostStringUtils;
import au.com.auspost.startrack_global.core.util.PropertyUtil;
import au.com.auspost.startrack_global.utils.Constants;
import com.adobe.cq.sightly.WCMUse;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.jcr.Node;

/**
 * This class is used to get values from sibling or child components for analytics purposes.
 *  <div data-sly-use.cta="${'au.com.auspost.startrack_global.core.sightly.SiblingAnalytics'@ childNode='hero-heading-text-1',childNodeProperty='text'}"
 * data-sly-unwrap></div>
 */
public class SiblingAnalytics extends WCMUse {

    // create title for analytics purpose
    public String titleForDataAnalytics = StringUtils.EMPTY;

    @Override
    public void activate() throws Exception {
        titleForDataAnalytics = prepareTitleForAnalytics(getResource());
    }

    public String prepareTitleForAnalytics(Resource resource) throws Exception {

        Node currentNode = resource.adaptTo(Node.class);
        String childNode = get(Constants.CHILD_NODE, String.class);
        String childNodeProperty = get(Constants.CHILD_NODE_PROPERTY, String.class);
        String title = StringUtils.EMPTY;

        if (StringUtils.isNotBlank(childNode) && StringUtils.isNotBlank(childNodeProperty)) {
            if (currentNode.hasNode(childNode)) {
                title = PropertyUtil.returnSinglePropertyValue(currentNode.getNode(childNode), childNodeProperty);
                if (StringUtils.isNotBlank(title)) {
                    //lower case
                    title = title.toLowerCase();
                    //replace spaces for hyphens
                    title = title.replaceAll(Constants.SPACE,Constants.HYPHEN);
                    //remove all extra html elements
                    title = AusPostStringUtils.removeHtmlAttributes(title);
                }
            }
        }
        return title;
    }

    public String getTitleForDataAnalytics() {
        return titleForDataAnalytics;
    }
}
