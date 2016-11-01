
/**
 * Searches and filters articles
 *
 * @author Andrei
 *
 */
package au.com.auspost.startrack_corp.core.services;

import java.util.stream.Collectors;
import java.util.function.*;
import java.util.*;

import java.rmi.ServerException;
import java.lang.reflect.Field;
import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.Node;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import org.apache.commons.lang3.StringUtils;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Property;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.xss.XSSAPI;

import au.com.auspost.startrack_corp.core.Constants;

/**
 * Service to search articles.
 */
@SlingServlet(paths="/bin/StartrackSearchService", methods = "GET", metatype = true)
@Property(name = "sling.auth.requirements", value = "-/bin/StartrackSearchService")
public class SearchService extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private static final String TAG_NAME = "tag";
    private static final String FILTER_NAME = "filter";
    private static final String ITEMS_PER_PAGE = "itemsperpage";
    private static final String PAGE_NAME = "page";
    private static final String DEFAULT_FILTER = "default";
    private static final Integer DEFAULT_ITEMS_PER_PAGE = 10;
    private static final Integer DEFAULT_PAGE = 0;
    
    
    @Reference
    private SlingRepository repository;
    
    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    private XSSAPI xssapi;

    public void bindRepository(SlingRepository repository) {
           this.repository = repository; 
    }

    private class SearchResult {
        public Integer id;
        public String title;
        public String text;
        public String image;
        public String url;
    }

    private class StepSupport {
        Integer i = 0;
    }

    private class Recursive<T> {
        public T func;
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServerException, IOException {
        final String tag = request.getParameter(TAG_NAME) != null? xssapi.getValidJSToken(request.getParameter(TAG_NAME).toLowerCase(), null): null;
        final String filter = request.getParameter(FILTER_NAME) != null? xssapi.getValidJSToken(request.getParameter(FILTER_NAME).toLowerCase(), DEFAULT_FILTER): DEFAULT_FILTER;
        final Integer itemsPerPage = request.getParameter(ITEMS_PER_PAGE) != null? xssapi.getValidInteger(request.getParameter(ITEMS_PER_PAGE), DEFAULT_ITEMS_PER_PAGE): DEFAULT_ITEMS_PER_PAGE;
        final Integer page = request.getParameter(PAGE_NAME) != null? xssapi.getValidInteger(request.getParameter(PAGE_NAME), DEFAULT_PAGE): DEFAULT_PAGE;

        final Integer startIndex = page * itemsPerPage;
        final Integer endIndex = startIndex + itemsPerPage;

        List<SearchResult> data = new ArrayList<SearchResult>();
        
        Recursive<BiConsumer<Node, StepSupport>> recursive = new Recursive<>();
        recursive.func = (x, y) -> {
            try {
                List<String> tags = null;  
                if (x.hasNode(Constants.JCR_CONTENT)) {
                    Node cnt = x.getNode(Constants.JCR_CONTENT);
                    if (cnt.hasProperty(Constants.CQ_TAGS)) {
                        Value[] values = cnt.getProperty(Constants.CQ_TAGS).getValues();
                        tags = Arrays.stream(values).map(o -> {
                            String str = o.toString().toLowerCase();
                            //String fltr = StringUtils.contains(str, ':')? StringUtils.substring(str, 0, StringUtils.indexOf(str, ':')): "default";
                            String key = StringUtils.contains(str, ':')? StringUtils.substring(str, StringUtils.indexOf(str, ':') + 1): str;
                            String[] keys = StringUtils.split(key, '/');
                            return StringUtils.trim(keys[keys.length - 1]);
                        }).collect(Collectors.toList());
                    }
                }
                
                if (tags != null && tags.stream().anyMatch(o -> {
                    return o.equals(tag);
                })) {
                    // collect data for page
                    if (y.i >= startIndex && y.i < endIndex) {
                        SearchResult sr = new SearchResult() {{
                            id = y.i + 1;
                            url = x.getPath() + ".html";
                            title = "What's in store for retail in 2016";
                            text = "From mobile commerce to fully visible inventory to in-store beacons, we take a look at what's in store for retail in 2016";
                            image = "/etc/designs/startrack_corp/clientlib/img/article-list-01.png";
                        }};
                        data.add(sr);                
                    }

                    // increase number of results
                    y.i++;
                }
                
                @SuppressWarnings("unchecked")
                final Iterator<Node> ni = (Iterator<Node>)x.getNodes();
                ni.forEachRemaining(o -> {
                    try {
                        if (o.hasProperty(Constants.JCR_PRIMARY_TYPE)) {
                            final String pt = o.getProperty(Constants.JCR_PRIMARY_TYPE).getString();
                            if (pt.equals(Constants.CQ_PAGE)) {
                                recursive.func.accept(o, y);
                            }
                        }
                    } catch (RepositoryException e) {
                        log.error("SearchService::doGet RepositoryException: " + e);
                        return;
                    }
                });
            } catch (RepositoryException e) {
                log.error("SearchService::doGet RepositoryException: " + e);
                return;
            }
        };
        
        String res = "{ 'result': 'error' }";
        try {
            StepSupport sp = new StepSupport();
            if (tag != null) {
                //TODO ; deperecated getAdministrativeResourceResolver method must be replaced with ResourceResolver getResourceResolver(Map<String,Object> authenticationInfo) with the support of config changes for the default system user
                Node base = resourceResolverFactory.getAdministrativeResourceResolver(null).getResource(Constants.STARTRACK_BASE_PATH).adaptTo(Node.class);
                //Node base = request.getResource().getResourceResolver().getResource(Constants.STARTRACK_BASE_PATH).adaptTo(Node.class);
                recursive.func.accept(base, sp);
            }
            
            JSONObject obj = new JSONObject();
            obj.put("result", "ok");
            obj.put("tag", tag);
            obj.put("filter", filter);
            obj.put("page", page);
            obj.put("pages", sp.i / itemsPerPage + 1);
            obj.put("itemsperpage", itemsPerPage);
            obj.put("data", data.stream().map(o -> {
                Class<? extends SearchResult> c = o.getClass();
                JSONObject so = new JSONObject();
                Field[] fs = c.getFields();
                Arrays.stream(fs).map(oo -> {
                    try {
                        so.put(oo.getName(), oo.get(o));
                    } catch(IllegalAccessException  e) {
                        log.error("SearchService::doGet IllegalAccessException:" + e.toString());
                    } catch(JSONException e) {
                        log.error("SearchService::doGet JSONException:" + e.toString());
                    }
                    return 1;
                }).reduce(0, (x, y) -> x + y);
                return so;
            }).collect(Collectors.toList()));
            res = obj.toString();
        } catch(JSONException e) {
            log.error("SearchService::doGet JSONException:" + e.toString());
        } catch(org.apache.sling.api.resource.LoginException e) {
            log.error("SearchService::doGet LoginException:" + e.toString());
        }
        response.getWriter().write(res);
    }
}
