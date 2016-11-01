package au.com.auspost.global.core.sightly;

import java.util.Collection;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUse;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import au.com.auspost.global.core.util.PropertyUtil;
import au.com.auspost.global.utils.Constants;

/**
 * For implementing Adaptive Image in any of the components we need to do few things
 * 
 *  1) We need to first create adaptive image node within the component, to enable this we add
 *  <div data-sly-use.inlineEdit="${'au.com.auspost.global.core.sightly.InlineEditNodeGenerator'@ paramNodeType='global/components/adpativeimage',nodeToBeCreated='adaptiveimage'}"
     data-sly-unwrap></div> 

     2) Then we include the global/components/adaptiveImage.

     3) Once the adaptive image component is added as data-sly-resource within any component, then we need to 
        add the component the component definition within the layoutContainer.json file or adaptiveImage.json file which resides under the
        
        /etc/designs/global/adapativeImage directory.

     4) Our implementation of adaptive image uses the same logic as OOTB, the files are now referenced by /content/dam. 

     5) The other parameters are altText which again is looked from Parent node.

     6) We also have additonal prop, again looked off the parent node. This parameter is useful for cases where
      	different adaptive images need to be produced for different types of the same component. The json file with
      	card and hero-banner-microsites component should be useful for implementation.
 * 
 * @author Sam
 *
 */

public class AdaptiveImage extends WCMUse{

	private static final String IMAGE_PROP_NAME = "imagePropName";

	private static final String ADDITIONAL_PROPERTY = "additionalProp";

	private static final String ADAPTIVE_IMAGE = "adaptiveimage";

	private static final String LAYOUT_VARIATION = "layoutvariation";

	private String imagePropName;

	public String imagePath= StringUtils.EMPTY;

	public String altText = StringUtils.EMPTY;

	public String path = StringUtils.EMPTY;

	private final String ADAPTIVE_IMAGE_JSON_PATH_FILE= "/etc/designs/global/adaptiveImage/adaptiveImage.json/jcr:content";

	private final String JSON_DEFAULT_PATH_FILE= "/etc/designs/global/adaptiveImage/default.json/jcr:content";

	private final String JSON_LAYOUT_CONTAINER_PATH_FILE= "/etc/designs/global/adaptiveImage/layoutContainer.json/jcr:content";

	private final String CONTENT_DAM_PATH= "/content/dam";

	public Multimap<String, String> breakPointDimensionMap = LinkedListMultimap.create();

	private String JSON_DATA =StringUtils.EMPTY;

	@Override
	public void activate() throws Exception {
		/**
		 * The order of fetching the relevant JSON is in order below 
		 *  1) layoutContainer.json
		 *  2) adaptiveImage.json
		 *  3) default.json
		 */

		JsonParser jsonParser = new JsonParser();
		JsonObject mainJsonObject = null;
		JsonObject subObject = null;
		Node componentNode = getResource().adaptTo(Node.class);
		String componentName = StringUtils.EMPTY;
		String resourceType = PropertyUtil.returnSinglePropertyValue(componentNode.getParent(), 
				Constants.PROPERTY_SLING_RESOURCE_TYPE);
		if(StringUtils.isNotBlank(resourceType)){
			String [] cmpName = resourceType.split(Constants.FORWARD_SLASH);
			componentName = cmpName[cmpName.length-1];
		}

		Session session = getResourceResolver().adaptTo(Session.class);
		String layoutContainerPath = getParentLayoutContainer(componentNode);
		boolean defaultComponentMapping = false;
 		if(StringUtils.isNotBlank(layoutContainerPath)){
			Node layoutContainerNode = getResourceResolver().getResource(layoutContainerPath).adaptTo(Node.class);
			if(null!=layoutContainerNode){
				String containerJustification = PropertyUtil.returnSinglePropertyValue(layoutContainerNode, LAYOUT_VARIATION);
				if(StringUtils.isNotBlank(containerJustification)){
					JSON_DATA = PropertyUtil.returnSingleBinaryPropertyValue(session.getNode(JSON_LAYOUT_CONTAINER_PATH_FILE), Constants.JCR_DATA);
					mainJsonObject  = new JsonObject();
					mainJsonObject =jsonParser.parse(JSON_DATA).getAsJsonObject();
					//First get the container justification and then look for the component name within it.
					mainJsonObject = mainJsonObject.getAsJsonObject(containerJustification);
					if(null!=mainJsonObject){
						mainJsonObject = mainJsonObject.getAsJsonObject(componentName);
					}
				}
			}
		}

		if(null==mainJsonObject && StringUtils.isNotEmpty(componentName)) {
			Node node = session.getNode(ADAPTIVE_IMAGE_JSON_PATH_FILE);
			if(node.hasProperty(Constants.JCR_DATA)) {
				JSON_DATA = PropertyUtil.returnSingleBinaryPropertyValue(node, Constants.JCR_DATA);
			}
			mainJsonObject  = new JsonObject();
			mainJsonObject =jsonParser.parse(JSON_DATA).getAsJsonObject();
			mainJsonObject = mainJsonObject.getAsJsonObject(componentName);
		}

		if(null==mainJsonObject){
			//Since no mappings are found for image name and path, we need to get the mappings from default.json file.
			Node parentNode = componentNode.getParent();
			PropertyIterator propertyItr = parentNode.getProperties();
			while (propertyItr.hasNext()) {
				Property currentProperty = propertyItr.nextProperty();
				String value = PropertyUtil.returnSinglePropertyValue(parentNode, currentProperty.getName());
				if(StringUtils.isNotBlank(value) && value.startsWith(CONTENT_DAM_PATH)){
					imagePropName = currentProperty.getName();
				}

			}
			JSON_DATA = PropertyUtil.returnSingleBinaryPropertyValue(session.getNode(JSON_DEFAULT_PATH_FILE), Constants.JCR_DATA);
			mainJsonObject =jsonParser.parse(JSON_DATA).getAsJsonObject();
		}
		
		if(null!=mainJsonObject){
			imagePropName = StringUtils.isEmpty(imagePropName)?
					null!=mainJsonObject.get(IMAGE_PROP_NAME)?
							mainJsonObject.get(IMAGE_PROP_NAME).getAsString():StringUtils.EMPTY:imagePropName;
		}

		String subPropertyValue = StringUtils.EMPTY;

		if(null!=mainJsonObject && !mainJsonObject.entrySet().isEmpty()){
			//If not layout container then try to find the json mappings within the default.json file
			defaultComponentMapping = true;
			String subProperty =null!=mainJsonObject.get(ADDITIONAL_PROPERTY)?
			mainJsonObject.get(ADDITIONAL_PROPERTY).getAsString():StringUtils.EMPTY; 
			subPropertyValue = PropertyUtil.returnSinglePropertyValue(componentNode.getParent(), subProperty);

			if(StringUtils.isNotEmpty(subPropertyValue)){
				subObject = mainJsonObject.getAsJsonObject(subPropertyValue);
			}
			altText = PropertyUtil.returnSinglePropertyValue(componentNode.getParent(), 
					null!=mainJsonObject.get(Constants.ALT_TXT_PROP)?mainJsonObject.get(Constants.ALT_TXT_PROP).getAsString():StringUtils.EMPTY);
		}

		imagePath = PropertyUtil.returnSinglePropertyValue(componentNode.getParent(), imagePropName);

		if( !configureAdaptiveImage(imagePropName,imagePath)){
			throw new RuntimeException("Adaptive image isn't configured, throwing exception");
		}


		for (Map.Entry<String,JsonElement> entry : null!=subObject?subObject.entrySet():mainJsonObject.entrySet()) {
			if(entry.getValue().getAsString().contains(Constants.COMMA)){
				for(String str: entry.getValue().getAsString().split(Constants.COMMA)){
					if(shouldAddToMap(entry.getKey())){
						breakPointDimensionMap.put(entry.getKey(), processDensity(str));
					}
				}
			}else if(shouldAddToMap(entry.getKey())){
				breakPointDimensionMap.put(entry.getKey(), processDensity(entry.getValue().getAsString()));
			}
		}

	}

	private boolean shouldAddToMap(String str){
		boolean shouldAdd=false;
		if(StringUtils.isNotBlank(str) && !str.equals(Constants.IMAGE_PROP_NAME) 
				&& !str.equals(Constants.ADDITIONAL_PROP) && !str.equals(Constants.ALT_TEXT_PROP)){
			shouldAdd = true;
		}
		return shouldAdd;
	}

	public Map<String, Collection<String>> getFinalConfigMap (){
		return breakPointDimensionMap.asMap();
	}


	private String processDensity(String str){
		if(str.contains("[")){
			String arr [] = str.split("\\[");
			return arr[0].concat("-").concat(arr[1].replace("]", ""));
		}else{
			return StringUtils.EMPTY;
		}
	}

	public String getImagePath(){
		return imagePath;
	}

	public String getAltTextImg(){
		return altText;
	}

	/**
	 * This method puts the imageProperty and the path on the adapative image node.
	 * Since the physical dam file isn't needed in the content node as the files are now mapped to /content/dam, we aren't
	 * physically copying the files anymore, we need to do the file clean up of the content structure.
	 * @param imagePath
	 * @return
	 * @throws RepositoryException
	 */
	private boolean configureAdaptiveImage(String imagePropName,String imagePath) throws RepositoryException{
		boolean status = false;

		if(StringUtils.isNotBlank(imagePath)){
			Node currentNode = getResource().adaptTo(Node.class);
			Node adaptiveImageNode = currentNode.getName().equals(ADAPTIVE_IMAGE)?currentNode:null;
			if(null!=adaptiveImageNode && adaptiveImageNode.hasNode(Constants.FILE)){
				//File needs to be removed as we are referencing the file from DAM now.
				adaptiveImageNode.getNode(Constants.FILE).remove();
			}else if(null!=adaptiveImageNode && !adaptiveImageNode.hasProperty(imagePropName)){
				adaptiveImageNode.setProperty(imagePropName, imagePath);
			}else if(null!=adaptiveImageNode && adaptiveImageNode.hasProperty(imagePropName)
					&& !adaptiveImageNode.getProperty(imagePropName).getString().equals(imagePath)){
				adaptiveImageNode.setProperty(imagePropName, imagePath);
			}
			getResourceResolver().adaptTo(Session.class).save();
			status = true;
		}
		//If there is no image path, adaptive image can't be generated, hence remove the adaptiveimage node.
		if(StringUtils.isBlank(imagePath)){
			Node currentNode = getResource().adaptTo(Node.class);
			Node adaptiveImageNode = currentNode.getName().equals(ADAPTIVE_IMAGE)?currentNode:null;
			if(null!=adaptiveImageNode){
				adaptiveImageNode.remove();
				getResourceResolver().adaptTo(Session.class).save();
				status = true;
			}
		}
		return status;
	}

	/**
	 * Recursively check for the layout container mapping.
	 * @param currentNode
	 * @return
	 * @throws RepositoryException
	 */
	private String getParentLayoutContainer(Node currentNode) throws RepositoryException{

		if(null!=currentNode && !currentNode.getPath().equals(Constants.FORWARD_SLASH) && null!=currentNode.getParent() && 
				currentNode.hasProperty(Constants.PROPERTY_SLING_RESOURCE_TYPE) 
				&& currentNode.getProperty(Constants.PROPERTY_SLING_RESOURCE_TYPE).getString().equals(Constants.LAYOUT_CONTAINER_RESOURCETYPE)){
			path = currentNode.getPath();
			return path;
		}else if(null!=currentNode && !currentNode.getPath().equals(Constants.FORWARD_SLASH) && null!=currentNode.getParent()){
			getParentLayoutContainer(currentNode.getParent());
		}

		return path;
	}
}
