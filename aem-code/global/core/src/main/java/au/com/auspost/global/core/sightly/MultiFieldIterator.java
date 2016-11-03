package au.com.auspost.global.core.sightly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUse;

import au.com.auspost.global.utils.Constants;

/**
 * This class iterates over json objects. You can pass the property on which iteration will happen. You can pass the nodepath on which the property will be looked.
 * If node path isn't supplied, it will look for the property on the current node. you can also pass the addJcrContent property. This is to sometimes iterate over 
 * page properties.
 *
 * Usage as below:
 * <div data-sly-use.linkListUtils="${'au.com.auspost.global.core.sightly.MultiFieldIterator' @ property='robots',addJcrContent='true', nodePath=currentPage.path}" data-sly-unwrap>
 * <ul data-sly-list="${linkListUtils.linkListItems}">
 * <li> ${item.propertyName} afterwards</li>
 * </ul>
 * </div>
 *
 * Don't change the parameter @property, you can only change the name of the property.
 *
 */

public class MultiFieldIterator extends WCMUse {
    private List<HashMap<String, Object>> linkListItems = new ArrayList<HashMap<String, Object>>();
    private Logger log ;
    
    @Override
    public void activate() throws Exception {
    	log = LoggerFactory.getLogger(MultiFieldIterator.class);
    	String propertyToExtract = get("property",String.class);
    	String nodeToExtractValueFrom = get("nodePath",String.class);
    	String addJcrContent = get("addJcrContent",String.class);
    	if(StringUtils.isNotEmpty(addJcrContent)){
    		nodeToExtractValueFrom = nodeToExtractValueFrom+Constants.STRING_JCR_CONTENT;
    	}
        String[] links = StringUtils.isBlank(nodeToExtractValueFrom)?getProperties().get(propertyToExtract, String[].class):
        	getResourceResolver().getResource(nodeToExtractValueFrom).adaptTo(ValueMap.class).get(propertyToExtract, String[].class);
        if (links != null && links.length > 0) {
            setLinkListItems(links);
        }
    }

    public List<HashMap<String, Object>> getLinkListItems() {
        return linkListItems;
    }

    protected void setLinkListItems(String[] links) throws IOException {
        linkListItems = processLinkListHashMap(links);
    }

    public List<HashMap<String, Object>> processLinkListHashMap(String[] links)
            throws IOException {
    	log.debug("processLinkListHashMap method called");
        List<HashMap<String, Object>> linkList = new ArrayList<HashMap<String, Object>>();
        if (links != null) {
            ObjectMapper mapper = new ObjectMapper();
            for (String linkJson : links) {
                if (StringUtils.isNotBlank(linkJson)) {
                    HashMap<String, Object> item = mapper.readValue(linkJson, HashMap.class);
                    if (item != null) {
                        linkList.add(item);
                    }
                }
            }
        }
        return linkList;
    }
    
    public String getCombinedListValue(){
    	StringBuilder strBuilder = new StringBuilder();
    	for(HashMap<String, Object> value: linkListItems){
    		Iterator<Map.Entry<String,Object>> itr = value.entrySet().iterator();
    		while(itr.hasNext()){
    			Map.Entry<String, Object> mapVal= itr.next();
    			strBuilder.append(mapVal.getValue().toString());
    			strBuilder.append(Constants.COMMA);
    		}
    	}
    	return StringUtils.removeEnd(strBuilder.toString(), Constants.COMMA);
    }

}