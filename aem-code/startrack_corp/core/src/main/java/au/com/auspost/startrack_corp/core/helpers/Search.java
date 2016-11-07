
/**
 * Search pages by tags
 *
 * @author Andrei
 *
 */
package au.com.auspost.startrack_corp.core.helpers;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import au.com.auspost.startrack_corp.core.Constants;

/**
 * Search pages by tags
 */
public class Search {
    private static final String STARTRACK_LAYOUT = "startrack_corp/components/layout";
    private static final String STARTRACK_LEVEL1 = "level1TagsHidden";
    private static final String STARTRACK_LEVEL2 = "level2TagsHidden";
    private static final String STARTRACK_LEVEL3 = "level3tags";
    private static final String STARTRACK_HERO = "hero";
    
    private Logger log = LoggerFactory.getLogger(Search.class);
    private List<Page> pages = new ArrayList<Page>();

    private class Recursive<T> {
        public T func;
    }
    
    public Tag[] getCustomTags(TagManager tagManager, Resource r, String tagproperty) {
        String[] tagsStr = r.getValueMap().get(tagproperty, String[].class);
        if (tagsStr != null) {
            return Arrays.stream(tagsStr).map(o -> tagManager.resolve(o)).toArray(o -> new Tag[o]);
        }
        return new Tag[0];
    }
    
    public List<Page> searchPagesByTags(PageManager pm, TagManager tm, Tag[] tags, String pageType, int limit) {
        Page page = pm.getPage(Constants.STARTRACK_BASE_PATH);
        if (page == null) {
            log.error("Startrack base page isn't found");
            return null;
        }
        
        Recursive<BiConsumer<Page, Boolean>> recursive = new Recursive<>();
        recursive.func = (p, isTag) -> {
            for (Iterator<Page> iter = p.listChildren(); iter.hasNext() && pages.size() < limit; ) {
                Page o = iter.next();
                
                Boolean isSlide = true;

                String level1 = o.getContentResource(STARTRACK_LEVEL1) != null ? o.getContentResource(STARTRACK_LEVEL1).adaptTo(String.class): StringUtils.EMPTY;
                if (isSlide && pageType != null) {
                    isSlide = pageType.equals(level1);
                }

                if (isSlide && isTag) {
                    Tag[] ts = this.getCustomTags(tm, o.getContentResource(), STARTRACK_LEVEL3);
                    isSlide = tags.length > 0? Arrays.stream(tags).filter(t -> {
                        return Arrays.stream(ts).filter(t1 -> t1.equals(t)).limit(1).mapToInt(ti1 -> 1).sum() > 0;
                    }).limit(1).mapToInt(ti -> 1).sum() > 0: true;
                }
                
                if (isSlide) {
                    isSlide = false;
                    Iterable<Resource> children = o.getContentResource().getChild(Constants.CONTENT_PARSYS).getChildren();
                    for (Resource child : children) {
                        final Boolean isLayout = STARTRACK_LAYOUT.equals(child.getChild(Constants.SLING_RESOURCE_TYPE) != null ? child.getChild(Constants.SLING_RESOURCE_TYPE).adaptTo(String.class): StringUtils.EMPTY);
                        final Boolean isHero = STARTRACK_HERO.equals(child.getChild("layoutContainerParsys/card/containerTypeCard") != null ? child.getChild("layoutContainerParsys/card/containerTypeCard").adaptTo(String.class): StringUtils.EMPTY);
                        if (isLayout && isHero) {
                            isSlide = true;
                            break;
                        }
                    }
                }

                if (isSlide && pages.size() < limit) {
                    pages.add(o);
                }

                if (pages.size() < limit) {
                    recursive.func.accept(o, isTag);                    
                }
            }
        };
        recursive.func.accept(page, true);

        if (pages.size() <= 0) {
            recursive.func.accept(page, false);
        }

        pages = pages.stream().sorted((xc, yc) -> { 
            return yc.getLastModified().compareTo(xc.getLastModified());
        }).collect(Collectors.toList());

        return pages;
    }
    
    private class ConsummerSupport {
        public Set<String> pagesSet;
        public List<Page> ps;
        public Integer lim;
        public Boolean isTag = true;
        public Boolean isUnique = false;
    }

    public List<Page> searchPagesByInsights(PageManager pm, TagManager tm, Tag[] tags, String pageType, int limit) {
        Page page = pm.getPage(Constants.STARTRACK_BASE_PATH);
        if (page == null) {
            log.error("Startrack base page isn't found");
            return null;
        }
        
        Recursive<BiConsumer<ConsummerSupport, Page>> recursive = new Recursive<>();
        recursive.func = (data, p) -> {
            for (Iterator<Page> iter = p.listChildren(); iter.hasNext() && data.ps.size() < data.lim; ) {
                Page o = iter.next();
                
                Boolean isSlide = true;
                // remove existings
                if (data.isUnique && data.pagesSet.contains(o.getPath())) {
                    isSlide = false;
                }

                String level1 = o.getContentResource(STARTRACK_LEVEL1) != null ? o.getContentResource(STARTRACK_LEVEL1).adaptTo(String.class): StringUtils.EMPTY;
                if (isSlide && pageType != null) {
                    isSlide = pageType.equals(level1);
                }

                if (isSlide && data.isTag) {
                    Tag[] ts = this.getCustomTags(tm, o.getContentResource(), STARTRACK_LEVEL2);
                    isSlide = tags.length > 0? Arrays.stream(tags).filter(t -> {
                        return Arrays.stream(ts).filter(t1 -> t1.equals(t)).limit(1).mapToInt(ti1 -> 1).sum() > 0;
                    }).limit(1).mapToInt(ti -> 1).sum() > 0: true;
                }
                
                if (isSlide) {
                    isSlide = false;
                    Iterable<Resource> children = o.getContentResource().getChild(Constants.CONTENT_PARSYS).getChildren();
                    for (Resource child : children) {
                        final Boolean isLayout = STARTRACK_LAYOUT.equals(child.getChild(Constants.SLING_RESOURCE_TYPE) != null ? child.getChild(Constants.SLING_RESOURCE_TYPE).adaptTo(String.class): StringUtils.EMPTY);
                        final Boolean isHero = STARTRACK_HERO.equals(child.getChild("layoutContainerParsys/card/containerTypeCard") != null ? child.getChild("layoutContainerParsys/card/containerTypeCard").adaptTo(String.class): StringUtils.EMPTY);
                        if (isLayout && isHero) {
                            isSlide = true;
                            break;
                        }
                    }
                }

                if (isSlide && data.ps.size() < data.lim) {
                    data.ps.add(o);
                }

                if (data.ps.size() < data.lim) {
                    recursive.func.accept(data, o);                    
                }
            }
        };
        recursive.func.accept(new ConsummerSupport() {{ this.ps = pages; this.lim = limit; }}, page);
        pages = pages.stream().sorted((xc, yc) -> { 
            return yc.getLastModified().compareTo(xc.getLastModified());
        }).collect(Collectors.toList());
        
        // fill the rest
        if (pages.size() < limit) {
            ConsummerSupport cs = new ConsummerSupport() {{
                this.ps = new ArrayList<Page>();
                this.lim = pages.size() - limit;
                this.isTag = false;
                this.pagesSet = pages.stream().map(o -> o.getPath()).collect(Collectors.toSet());
                this.isUnique = true;
            }};
            recursive.func.accept(cs, page);
            cs.ps = cs.ps.stream().sorted((xc, yc) -> { 
                return yc.getLastModified().compareTo(xc.getLastModified());
            }).collect(Collectors.toList());
            cs.ps.forEach(o -> pages.add(o));
        }

        return pages;
    }
}
