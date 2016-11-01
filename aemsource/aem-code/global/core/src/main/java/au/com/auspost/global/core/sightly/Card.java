package au.com.auspost.global.core.sightly;

import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUse;

import au.com.auspost.global.utils.Constants;

public class Card extends WCMUse{
	
	public String imageSrc = StringUtils.EMPTY;

	@Override
	public void activate() throws Exception {
		Resource resource = getResource();
		Node currentNode = resource.adaptTo(Node.class);
		String layoutVariation = StringUtils.EMPTY;
		String cardSize = currentNode.hasProperty(Constants.CONTAINER_SIZE)?
				currentNode.getProperty(Constants.CONTAINER_SIZE).getString():StringUtils.EMPTY;
		if(StringUtils.isEmpty(cardSize)){
			cardSize = "large";
		}
		
		imageSrc = currentNode.hasProperty(Constants.IMAGE_PATH_REF)?
				currentNode.getProperty(Constants.IMAGE_PATH_REF).getString():StringUtils.EMPTY;
		
		Resource grandParent = getResource().getParent().getParent();

		//Check if the grand parent is layout container.
		if(null!=grandParent && 
				getResourceResolver().isResourceType(grandParent,Constants.LAYOUT_CONTAINER_RESOURCETYPE )){
			Node layoutContainerNode = grandParent.adaptTo(Node.class);
			layoutVariation = layoutContainerNode.hasProperty(Constants.LAYOUT_VARIATION)?
					layoutContainerNode.getProperty(Constants.LAYOUT_VARIATION).getString():"flex-container--single-row";
			
			if(layoutVariation.equals("flex-container--single-row") && cardSize.equals("large")){
				imageSrc = imageSrc.concat(Constants.COMMON_RENDITION_PATH).concat("1892.1024.png");
			} else if(layoutVariation.equals("flex-container--single-row") && cardSize.equals("medium") || 
					layoutVariation.equals("flex-container--multi-50") && cardSize.equals("large")){
				imageSrc = imageSrc.concat(Constants.COMMON_RENDITION_PATH).concat("1120.1120.png");
			} else {
				imageSrc = imageSrc.concat(Constants.COMMON_RENDITION_PATH).concat("840.840.png");
			} 
		}
		
	}
	
	public String getImageScrForCard(){
		return imageSrc;
	}

}
