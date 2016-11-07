package au.com.auspost.startrack_corp.core.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import com.adobe.cq.sightly.WCMUsePojo;

import au.com.auspost.startrack_corp.core.Constants;

// Code behind for footer
public class SearchConfig extends WCMUsePojo {
    private final String SEARCH_JSON_PATH_FILE = "/etc/designs/startrack_corp/share/variables.json/jcr:content";

    private List<Map<String, Object>> items;

    private Logger log;
    
    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(SearchConfig.class);
       
        ResourceResolver resolver = getResourceResolver();
        if (resolver == null) {
            log.error("ResourceResolver isn't found");
            return;
        }
        items = new ArrayList<>();

        Session session = resolver.adaptTo(Session.class);
        Node jsonNode = session.getNode(SEARCH_JSON_PATH_FILE);
        if(jsonNode.hasProperty(Constants.JCR_DATA)) {
            String jsonData = jsonNode.getProperty(Constants.JCR_DATA).getString();
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonObject.keys().forEachRemaining(o -> {
                Map<String, Object> item = new HashMap<String, Object>();
                try {
                    item.put("name", o);
                    item.put("value", jsonObject.get(o));
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
                items.add(item);
            });
        }
    }
    public List<Map<String, Object>> getItems() {
        return items;
    }
}
