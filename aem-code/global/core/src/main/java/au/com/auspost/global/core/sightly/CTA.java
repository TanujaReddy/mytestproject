package au.com.auspost.global.core.sightly;

import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUse;

import au.com.auspost.global.core.util.PropertyUtil;
import au.com.auspost.global.utils.Constants;


public class CTA extends WCMUse {

    public String title = StringUtils.EMPTY;

    @Override
    public void activate() throws Exception {
        Resource resource = getResource();
        title = getTitle(resource);
    }

    public String getTitle(Resource resource) throws Exception{

        Node currentNode = resource.adaptTo(Node.class);
        String childNode = get(Constants.CHILD_NODE, String.class);
        String childNodeProperty = get(Constants.CHILD_NODE_PROPERTY, String.class);

        if(StringUtils.isNotBlank(childNode) && StringUtils.isNotBlank(childNodeProperty)) {
            if(currentNode.hasNode(childNode)) {
                title = PropertyUtil.returnSinglePropertyValue(currentNode.getNode(childNode), childNodeProperty);
                if(StringUtils.isNotBlank(title)){
                    title = title.toLowerCase();
                }
            }

        }
        return title;
    }

    public String getTitle() {
        return title;
    }
}