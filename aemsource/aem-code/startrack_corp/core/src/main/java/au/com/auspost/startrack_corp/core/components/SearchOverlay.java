package au.com.auspost.startrack_corp.core.components;

import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;

import au.com.auspost.startrack_corp.core.Constants;
import au.com.auspost.startrack_corp.core.helpers.Json;

// Code behind for footer
public class SearchOverlay extends WCMUsePojo {
    private String relatedServicesTitle = StringUtils.EMPTY;
    private List<Map<String, Object>> relatedServices;
    private String popularSearchesTitle = StringUtils.EMPTY;
    private List<Map<String, Object>> popularSearchUrls;
    private String relatedInsightsTitle = StringUtils.EMPTY;
    private List<Map<String, Object>> realtedInsights;
    private Logger log;
    
    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(SearchOverlay.class);
       
        ResourceResolver resolver = getResourceResolver();
        if (resolver == null) {
            log.error("ResourceResolver isn't found");
            return;
        }
        if (null == resolver.getResource(Constants.STARTRACK_BASE_JCR_PATH)) {
            log.error("Startrack base resource isn't found");
            return;
        }
        Resource r = resolver.getResource(Constants.STARTRACK_SEARCH_JCR_PATH + "/contentParsys/search_overlay");
        if (null == r) {
            log.error("Search resource isn't found");
            return;
        }

        ValueMap valueMap = r.adaptTo(ValueMap.class);
        relatedServicesTitle = valueMap.get("relatedServicesTitle", String.class);
        relatedServices = Json.extractJsonArray(valueMap.get("relatedServices", String[].class));

        popularSearchesTitle = valueMap.get("popularSearchesTitle", String.class);
        popularSearchUrls = Json.extractJsonArray(valueMap.get("popularSearchUrls", String[].class));

        relatedInsightsTitle = valueMap.get("relatedInsightsTitle", String.class);
        realtedInsights = Json.extractJsonArray(valueMap.get("realtedInsights", String[].class));
    }

    public String getRelatedServicesTitle() {
        return relatedServicesTitle;
    }
    public List<Map<String, Object>> getRelatedServices() {
        return relatedServices;
    }

    public String getPopularSearchesTitle() {
        return popularSearchesTitle;
    }
    public List<Map<String, Object>> getPopularSearchUrls() {
        return popularSearchUrls;
    }
    
    public String getRelatedInsightsTitle() {
        return relatedInsightsTitle;
    }
    public List<Map<String, Object>> realtedInsights() {
        return realtedInsights;
    }
}
