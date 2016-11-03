package au.com.auspost.startrack_corp.core.components;

import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.sling.api.resource.Resource;

import com.day.cq.tagging.TagManager;

import com.adobe.cq.sightly.WCMUsePojo;

import au.com.auspost.startrack_corp.core.Constants;

// Code behind for footer
public class Insights extends WCMUsePojo {
    public static final String LEVEL2_TAGS = "level2TagsHidden";
    public static final String LEVEL2_TAG_PREFIX = "/etc/tags/level2tags/";

    private Logger log;
    private List<Map<String, String>> filters = new ArrayList<Map<String, String>>();
    
    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(Insights.class);
       
        Resource resource = getResource();
        if (resource == null) {
            log.error("Curent sresource isn't found");
            return;
        }

        Node cn = resource.adaptTo(Node.class);
        TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
        
        if (cn.hasProperty(LEVEL2_TAGS)) {
            String value = cn.getProperty(LEVEL2_TAGS).getString();
            filters = Arrays.stream(value.split(Constants.COMMA)).map(o -> {
                return new HashMap<String, String>() {
                    private static final long serialVersionUID = 1L;
                    {
                        this.put("filter", o);
                        this.put("name", tagManager.resolve(LEVEL2_TAG_PREFIX + o).getTitle());
                    }
                };
            }).collect(Collectors.toList());
        }
    }

    public List<Map<String, String>> getFilters() {
        return filters;
    }
}
