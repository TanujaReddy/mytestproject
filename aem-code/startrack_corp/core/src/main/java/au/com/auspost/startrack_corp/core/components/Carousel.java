package au.com.auspost.startrack_corp.core.components;

import javax.jcr.RepositoryException;
import javax.jcr.Node;

import java.util.stream.StreamSupport;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import au.com.auspost.startrack_corp.core.Constants;
import au.com.auspost.startrack_corp.core.helpers.Reference;
import au.com.auspost.startrack_corp.core.helpers.Search;

// Code behind for footer
public class Carousel extends WCMUsePojo {
    private static final String IMAGE_PATH_REF = "imagePathRef";
    private static final String CARD_HEADING_TEXT = "cardHeadingText";
    private static final String CARD_BODY_TEXT = "cardBodyText";
    private static final String CTA_TYPE_CONFIG = "ctaTypeConfig";

    private static final String MAX_PAGES_NAME = "maxpages";
    private static final Integer DEFAULT_MAX_PAGES = 3;
    
    private static final String TAG_PROPERTY_NAME = "tagproperty";
    private static final String PAGE_TYPE_NAME = "pagetype";
    private static final String HTML_PATH = ".html";
    
    private static final String STARTRACK_LEVEL2 = "level2TagsHidden";

    private Logger log;

    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(Carousel.class);

        final String maxPagesStr = get(MAX_PAGES_NAME, String.class);
        final Integer maxPages = maxPagesStr != null? Integer.parseInt(maxPagesStr): DEFAULT_MAX_PAGES;
        final String tagproperty = get(TAG_PROPERTY_NAME, String.class);
        final String pagetype = get(PAGE_TYPE_NAME, String.class);

        final PageManager pm = getPageManager();
        if (pm == null) {
            log.error("PageManager isn't found");
            return;
        }
        final Resource cr = getResource();
        if (cr == null) {
            log.error("Current resource isn't found");
            return;
        }
        final ResourceResolver resolver = getResourceResolver();
        if (resolver == null) {
            log.error("ResourceResolver isn't found");
            return;
        }
        final TagManager tagManager = resolver.adaptTo(TagManager.class);
        if (tagManager == null) {
            log.error("TagManager isn't found");
            return;
        }
       
        final Search search = new Search();
        final Reference reference = new Reference(cr, resolver);
        final Map<String, String> references = reference.getMap();

        Supplier<Tag[]> getTags = () -> {
            if (tagproperty == null) {
                return getCurrentPage().getTags(); 
            }
            return search.getCustomTags(tagManager, cr, tagproperty);
        };
        final Tag[] tags = getTags.get();
        
        
        List<Page> pages = STARTRACK_LEVEL2.equals(tagproperty)?
            search.searchPagesByInsights(pm, tagManager, tags, pagetype, maxPages):
            search.searchPagesByTags(pm, tagManager, tags, pagetype, maxPages);

        pages.iterator().forEachRemaining(page -> {
            try {
                @SuppressWarnings("unchecked")
                final Iterator<Node> npi = (Iterator<Node>)page.getContentResource().adaptTo(Node.class).getNode(Constants.CONTENT_PARSYS).getNodes();
                npi.forEachRemaining(op -> {
                    try {
                        if (op.hasProperty(Constants.SLING_RESOURCE_TYPE) && op.hasProperty("layoutContainerParsys/card/containerTypeCard")) {
                            final Boolean isLayout = op.getProperty(Constants.SLING_RESOURCE_TYPE).getString().equals("startrack_corp/components/layout");
                            final Boolean isHero = op.getProperty("layoutContainerParsys/card/containerTypeCard").getString().equals("hero");
                            if (isLayout && isHero) {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("finalPath", page.getPath() + HTML_PATH);
                                final String imageStr = op.hasProperty(references.get(IMAGE_PATH_REF))?
                                    op.getProperty(references.get(IMAGE_PATH_REF)).getString(): StringUtils.EMPTY;
                                map.put("image", imageStr);
                                final String titleStr = op.hasProperty(references.get(CARD_HEADING_TEXT))?
                                    op.getProperty(references.get(CARD_HEADING_TEXT)).getString(): StringUtils.EMPTY;
                                map.put("title", titleStr);
                                final String bodyStr = op.hasProperty(references.get(CARD_BODY_TEXT))?
                                    op.getProperty(references.get(CARD_BODY_TEXT)).getString(): StringUtils.EMPTY;
                                map.put("body", bodyStr);
                                final String ctaStr = op.hasProperty(references.get(CTA_TYPE_CONFIG))?
                                    op.getProperty(references.get(CTA_TYPE_CONFIG)).getString(): null;
                                if (ctaStr != null) {
                                    try {
                                        JSONObject json = new JSONObject(ctaStr);
                                        Iterable<String> iterable = () -> json.keys();
                                        StreamSupport.stream(iterable.spliterator(), false).map(oo -> {
                                            try {
                                                if (oo.equals("ctaURL")) {
                                                    String ctaUrl = json.getString(oo);
                                                    map.put("ctaUrl", ctaUrl.startsWith("/content")? ctaUrl + HTML_PATH: ctaUrl);
                                                }
                                                if (oo.equals("ctaTypeText")) {
                                                    map.put("ctaText", json.getString(oo));
                                                }
                                            } catch (JSONException ex) {
                                                log.error("json exception: " + ex.getMessage());
                                            }
                                            return 1;
                                        }).reduce(0, (xr, yr) -> xr + yr);
                                    }
                                    catch (JSONException ex) {
                                        log.error("json exception: " + ex.getMessage());
                                    }
                                }
                                items.add(map);
                            }
                        }
                    } catch (RepositoryException e) {
                        log.error("RepositoryException: " + e);
                    }
                });
            } catch (RepositoryException e) {
                log.error("RepositoryException: " + e);
            }
        });
    }

    private List<Map<String, String>> items = new ArrayList<Map<String, String>>();
    public List<Map<String, String>> getItems() {
        return items;
    }
}
