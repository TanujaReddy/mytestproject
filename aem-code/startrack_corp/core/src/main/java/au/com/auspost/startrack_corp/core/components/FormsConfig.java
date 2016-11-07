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

// Code behind for form
public class FormsConfig extends WCMUsePojo {
    private final String FORMS_JSON_PATH_FILE = "/etc/designs/startrack_corp/share/forms.json/jcr:content";

    private Map<String, Object> item;

    private Logger log;
    
    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(FormsConfig.class);
       
        ResourceResolver resolver = getResourceResolver();
        if (resolver == null) {
            log.error("ResourceResolver isn't found");
            return;
        }
        item = new HashMap<String, Object>();

        Session session = resolver.adaptTo(Session.class);
        Node jsonNode = session.getNode(FORMS_JSON_PATH_FILE);
        if (jsonNode.hasProperty(Constants.JCR_DATA)) {
            String jsonData = jsonNode.getProperty(Constants.JCR_DATA).getString();
            JSONObject jsonObject = new JSONObject(jsonData);

            jsonObject.keys().forEachRemaining(o -> {
                try {
                    item.put(o, jsonObject.get(o));
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        
        log.info("title1: " + item.get("title1"));
    }

    public Map<String, Object> getItem() {
        return item;
    }

}
