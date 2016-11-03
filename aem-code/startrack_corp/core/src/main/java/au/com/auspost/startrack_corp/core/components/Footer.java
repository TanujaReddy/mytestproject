package au.com.auspost.startrack_corp.core.components;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;

import au.com.auspost.startrack_corp.core.helpers.Reference;
import au.com.auspost.startrack_corp.core.helpers.Json;

// Code behind for footer
public class Footer extends WCMUsePojo {
    private static final String COPYRIGHT_PATH = "copyrightPath";
    private static final String FOOTER_PATH = "footerPath";
    private static final String CORPORATE_URLS = "corporateUrls";
    private static final String CORPORATE_LINKS = "corporateLinks";
    private static final String SOCIAL_NETWORKS = "socialNetworks";
    
    private Logger log;
    
    private String copyrightText;
    private List<Map<String, Object>> corporateUrls;
    private List<Map<String, Object>> corporateLinks;
    private List<Map<String, Object>> socialNetworks;

    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(Footer.class);
        
        ResourceResolver resolver = getResourceResolver();
        if (resolver == null) {
            log.error("ResourceResolver isn't found");
            return;
        }
        Resource resource = getResource();
        if (resource == null) {
            log.error("Current resource isn't found");
            return;
        }
        Reference reference = new Reference(resource, resolver);
        Map<String, String> references = reference.getMap();

        ValueMap copyright_vm = resolver.getResource(references.get(COPYRIGHT_PATH)).adaptTo(ValueMap.class);
        copyrightText = copyright_vm.get("text", String.class);
        
        ValueMap footer_vm = resolver.getResource(references.get(FOOTER_PATH)).adaptTo(ValueMap.class);
        corporateUrls = Json.extractJsonArray(footer_vm.get(references.get(CORPORATE_URLS), String[].class));
        corporateLinks = Json.extractJsonArray(footer_vm.get(references.get(CORPORATE_LINKS), String[].class));
        socialNetworks = Json.extractJsonArray(footer_vm.get(references.get(SOCIAL_NETWORKS), String[].class));
    }

    public String getCopyrightText() {
        return copyrightText;
    }

    public List<Map<String, Object>> getCorporateUrls() {
        return corporateUrls;
    }

    public List<Map<String, Object>> getCorporateLinks() {
        return corporateLinks;
    }

    public List<Map<String, Object>> getSocialNetworks() {
        return socialNetworks;
    }
}
