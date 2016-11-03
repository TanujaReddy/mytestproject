package au.com.auspost.startrack_corp.core.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Node;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import org.apache.sling.api.resource.Resource;

import org.apache.commons.lang3.StringUtils;

import au.com.auspost.startrack_corp.core.Constants;

public class Reference {
    private final String REFERENCE_JSON_PATH_FILE = "/etc/designs/startrack_corp/share/references.json/jcr:content";

    private Map<String, String> items = new HashMap<String, String>();
    private Logger log;

    public Reference(Resource current, ResourceResolver resolver) {
        log = LoggerFactory.getLogger(Reference.class);

        // get parent ComponentName
        ValueMap vm = current.adaptTo(ValueMap.class);
        String [] cmpName = vm.get(Constants.PROPERTY_SLING_RESOURCE_TYPE, String.class).split(Constants.FORWARD_SLASH);
        String componentName = cmpName.length > 0? cmpName[cmpName.length - 1]: "default";
        //log.debug("componentName: " + componentName);
        
        // get map of references
        Session session = resolver.adaptTo(Session.class);
        try {
            Node jsonNode = session.getNode(REFERENCE_JSON_PATH_FILE);
            if(jsonNode.hasProperty(Constants.JCR_DATA)) {
                String jsonData = jsonNode.getProperty(Constants.JCR_DATA).getString();
                
                JSONObject rootJsonObject = new JSONObject(jsonData);
                JSONObject mainJsonObject = rootJsonObject.getJSONObject(componentName);
                
                Iterable<String> iterable = () -> mainJsonObject.keys();
                int count = StreamSupport.stream(iterable.spliterator(), false).map(oo -> {
                    try {
                        items.put(oo, mainJsonObject.getString(oo));
                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }
                    return 1;
                }).reduce(0, (x, y) -> x + y);
                //log.debug(StringUtils.EMPTY + count +  " references were extracted from json");
            }
        } catch (RepositoryException e) {
            log.error("RepositoryException: " + e);
        } catch (JSONException e) {
            log.error("JSONException: " + e);
        }
    }
    
    public Map<String, String> getMap (){
        return items;
    }
}
