package au.com.auspost.startrack_corp.core.components;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Node;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.api.resource.Resource;
import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;

import au.com.auspost.startrack_corp.core.Constants;

public class StartrackImage extends WCMUsePojo {
    private final String ADAPTIVE_IMAGE_JSON_PATH_FILE = "/etc/designs/startrack_corp/adaptiveImage/adaptiveImage.json/jcr:content";

    private String imagePropName = StringUtils.EMPTY;
    public String imagePath = StringUtils.EMPTY;
    public String altText = StringUtils.EMPTY;
    
    private Logger log;

    private Map<String, Collection<String>> items = new HashMap<String, Collection<String>>();

    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(StartrackImage.class);
        log.debug("AdaptiveImage::activate is called");

        Resource resource = getResource();
        if (resource == null) {
            log.error("Current resource isn't found");
            return;
        }
        ResourceResolver resolver = getResourceResolver();
        if (resolver == null) {
            log.error("Current resolver isn't found");
            return;
        }
        
        Node node = resource.adaptTo(Node.class);
        Session session = resolver.adaptTo(Session.class);

        String parentName = StringUtils.EMPTY;
        Node parent = node.getParent();
        try {
            if (parent.hasProperty(Constants.PROPERTY_SLING_RESOURCE_TYPE)) {
                String [] cmpName = parent.getProperty(Constants.PROPERTY_SLING_RESOURCE_TYPE).getString().split(Constants.FORWARD_SLASH);
                parentName = cmpName[cmpName.length-1];
            }
        } catch (RepositoryException e) {
            log.error("RepositoryException: " + e);
            return;
        }

        JSONObject mainJsonObject = null;
        if (StringUtils.isNotEmpty(parentName)) {
            Node jsonNode = session.getNode(ADAPTIVE_IMAGE_JSON_PATH_FILE);
            String jsonData = StringUtils.EMPTY;
            if(jsonNode.hasProperty(Constants.JCR_DATA)) {
                jsonData = jsonNode.getProperty(Constants.JCR_DATA).getString();
            }
          
            mainJsonObject = new JSONObject(jsonData);
            mainJsonObject = mainJsonObject.getJSONObject(parentName);
        }

        if (null != mainJsonObject) {
            imagePropName = mainJsonObject.has(Constants.IMAGE_PROP_NAME)?
                mainJsonObject.getString(Constants.IMAGE_PROP_NAME): StringUtils.EMPTY;

            log.debug("imagePropName: " + imagePropName);
        }

        String subPropertyValue = StringUtils.EMPTY;
        if (null != mainJsonObject) {
            String subPropertyName = mainJsonObject.has(Constants.ADDITIONAL_PROPERTY)?
                mainJsonObject.getString(Constants.ADDITIONAL_PROPERTY): null;
                
            if (parent.hasProperty(Constants.ALT_TEXT_PROP)) {
                altText = parent.getProperty(Constants.ALT_TEXT_PROP).getString();
            }
            log.debug("altText: " + altText);
            
            if (parent.hasProperty(imagePropName)) {
                imagePath = parent.getProperty(imagePropName).getString();
            }
            log.debug("imagePath: " + imagePath);

            if (subPropertyName != null && parent.hasProperty(subPropertyName)) {
                subPropertyValue = parent.getProperty(subPropertyName).getString();
            } else {
                subPropertyValue = "default";
            }
            log.debug("subPropertyValue: " + subPropertyValue);

            final JSONObject subJsonObject = mainJsonObject.getJSONObject(subPropertyValue);
            final Iterable<String> iterable = () -> subJsonObject.keys();
            StreamSupport.stream(iterable.spliterator(), false).map(o -> {
                try {
                    final String value = subJsonObject.getString(o);
                    if (value.contains(Constants.COMMA)) {
                        for (String str: value.split(Constants.COMMA)) {
                            if (shouldAddToMap(o)) {
                                items.put(o, new ArrayList<String>() {
                                    private static final long serialVersionUID = 1L;
                                    { add(processDensity(str)); }
                                });
                            }
                        }
                    } else if(shouldAddToMap(o)) {
                        items.put(o, new ArrayList<String>() {
                            private static final long serialVersionUID = 1L;
                            { add(processDensity(value)); }
                        });
                    }
                } catch (JSONException e) {
                    log.error("JSONException: " + e);
                }
                return 1;
            }).reduce(0, (x, y) -> x + y);
        }
       
    }

    private boolean shouldAddToMap(String str){
        boolean shouldAdd=false;
        if(StringUtils.isNotBlank(str) && !str.equals(Constants.IMAGE_PROP_NAME) 
                && !str.equals(Constants.ADDITIONAL_PROPERTY) && !str.equals(Constants.ALT_TEXT_PROP)){
            shouldAdd = true;
        }
        return shouldAdd;
    }

    public Map<String, Collection<String>> getFinalConfigMap (){
        return items;
    }


    private String processDensity(String str) {
        if (str.contains("[")) {
            String arr [] = str.split("\\[");
            return arr[0].concat("-").concat(arr[1].replace("]", ""));
        } else {
            return StringUtils.EMPTY;
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getAltTextImg() {
        return altText;
    }
}
