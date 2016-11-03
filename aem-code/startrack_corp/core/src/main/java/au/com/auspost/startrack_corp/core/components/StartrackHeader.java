package au.com.auspost.startrack_corp.core.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import au.com.auspost.startrack_corp.core.models.HeaderModel;

// Code behind for footer
public class StartrackHeader extends WCMUsePojo {
    private final int MAX_TOP_MENU = 4;
    private final String STARTRACK_BASE_PATH = "/content/startrack_corp/startrack";
    private final String[] css = {"flex-container--single-row", "flex-container--multi-50", "flex-container--multi-33", "flex-container--multi-25"};

    private Logger log;
    
    private class Recursive<T> {
        public T func;
    }

    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(StartrackHeader.class);
       
        Recursive<BiFunction<Page, Integer, Map<String, Object>>> recursive = new Recursive<>();
        recursive.func = (parentPage, depth) -> {
            HeaderModel model = parentPage.getContentResource().adaptTo(HeaderModel.class);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("title", StringUtils.isNotEmpty(model.menutitle)? model.menutitle: model.title);
            map.put("imagethumbnail", model.thumbnailImagePath);
            map.put("items", list);
            
            if (depth <= 2) {
                parentPage.listChildren().forEachRemaining(o -> {
                    ValueMap vm = o.getProperties();
                    if(!vm.getOrDefault("hideInNav", "false").equals("true")) {
                        Map<String, Object> mp = recursive.func.apply(o, depth + 1);
                        list.add(mp);                        
                    }
                });
            }
            if (list.size() > 0) {
                map.put("childPresent", true);
                map.put("secondChildPresent", list.stream().limit(1).map(o -> (Integer)o.get("count")).reduce(0, (x, y) -> x + y) > 0);
                map.put("cssValue", list.size() <= css.length? css[list.size() - 1]: css[css.length - 1]);
            } else {
                map.put("secondChildPresent", false);
                map.put("childPresent", false);
            }
            map.put("path", StringUtils.isNotEmpty(model.externalLink)? model.externalLink: (parentPage.getPath() + ".html"));
            map.put("count", list.size());
            map.put("midValue", list.size() / 2);
            return map;
        };

        PageManager pm = getPageManager();
        if (pm == null) {
            log.error("PageManager isn't found");
            return;
        }
        Page p = pm.getPage(STARTRACK_BASE_PATH);
        if (p == null) {
            log.error("Startrack base page isn't found");
            return;
        }
        Iterator<Page> pi = p.listChildren();
        Iterable<Page> pit = () -> pi;
        StreamSupport.stream(pit.spliterator(), false).map(o -> {
            if (items.size() <= MAX_TOP_MENU) {
                ValueMap vm = o.getProperties();
                if(!vm.getOrDefault("hideInNav", "false").equals("true")) {
                    Map<String, Object> map = recursive.func.apply(o, 1);
                    map.put("id", "item" + items.size());
                    items.add(map);                        
                }
            }
            return 1;
        }).reduce((x, y) -> x + y);
    }

    private List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
    public List<Map<String, Object>> getItems() {
        return items;
    }
}
