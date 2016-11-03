package au.com.auspost.global.core.sightly;

import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUse;

import au.com.auspost.global.utils.Constants;

/**
 * Tries to get the value from Node or subNode.
 * @author Sam
 *
 */
public class StripTagsRTE extends WCMUse{
	
	private String value;

	@Override
	public void activate() throws Exception {
		
		Resource resource = getResource();
		String subNodeName = get("subNode", String.class);
		
		String [] subNodes = subNodeName.split(Constants.COMMA);
		for(String str:subNodes){
			Node currentNode = resource.adaptTo(Node.class);
			Node subNode = null;
			if(StringUtils.isNotBlank(str)){
				subNode = currentNode.getNode(str);
			}
			
			if(null!=subNode){
				value =subNode.hasProperty(Constants.TEXT)?subNode.getProperty(Constants.TEXT).getString():StringUtils.EMPTY;
			}
			
			if(StringUtils.isBlank(value)){
				value =currentNode.hasProperty(Constants.TEXT)?currentNode.getProperty(Constants.TEXT).getString():StringUtils.EMPTY;
			}
			//Break if the value is found anywhere.
			if(StringUtils.isNotEmpty(value)){
				break;
			}
		}
		
		
	}
	
	public String getRteValue(){
		return value;
	}

}
