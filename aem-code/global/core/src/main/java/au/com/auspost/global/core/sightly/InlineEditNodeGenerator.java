package au.com.auspost.global.core.sightly;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUse;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;

import au.com.auspost.global.utils.Constants;

/**
 * This class is used to dynamically create physical node prior to a cq:include of a component.
 * Issue :  <div data-sly-resource="${'bodyTextSmall' @ resourceType='global/components/rte-components/text-body'}" data-sly-unwrap/>
 * cq:include of the above global/components/rte-components/text-body does not automatically create the node bodyTextSmall
 * Usage as below:
 * <div data-sly-use.inlineEdit="${'au.com.auspost.global.core.sightly.InlineEditNodeGenerator'@ paramNodeType='global/components/rte-components/text-heading',nodeToBeCreated='hero-heading-text-2'}"
 * data-sly-unwrap/>
 * paramNodeType- sling:resourceType
 * nodeToBeCreated - name of the node which needs to be created
 */

public class InlineEditNodeGenerator extends WCMUse {

    @Override
    public void activate() throws Exception {
        Resource resource = getResource();
        boolean nodeCreated = createJcrNode(resource);
        if(!nodeCreated){
        	throw new RuntimeException("couldn't create the node, check your privileges");
        }
    }

    public boolean createJcrNode(Resource resource) throws Exception {
    	boolean nodeCreated = false;
        Session session = resource.getResourceResolver().adaptTo(Session.class);
        Node currentNode = resource.adaptTo(Node.class);
        String nodeToBeCreated = get(Constants.NODE_TO_BE_CREATED, String.class);
        String paramNodeType = get(Constants.PARAM_NODE_TYPE, String.class);

        if (currentNode != null) {
            if (!currentNode.hasNode(nodeToBeCreated)) {
                Node node = JcrUtil.createPath(currentNode.getPath() + "/" + nodeToBeCreated, JcrConstants.NT_UNSTRUCTURED, session);
                node.setProperty(Constants.PROPERTY_NAME, nodeToBeCreated);
                node.setProperty(Constants.PROPERTY_DESCRTIPTION, Constants.PROPERTY_DESCRTIPTION_SAMPLE_TEXT);
                node.setProperty(Constants.PROPERTY_SLING_RESOURCE_TYPE, paramNodeType);
            }
            session.save();
            nodeCreated = true;
        }
        return nodeCreated;
    }


}