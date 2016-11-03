
/**
 * Searches tags
 *
 * @author Andrei
 *
 */
package au.com.auspost.startrack_corp.core.services;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import java.rmi.ServerException;
import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.day.cq.tagging.Tag;

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
@SlingServlet(paths="/bin/StartrackSearchTagService", methods = "GET", metatype = true)
@Property(name = "sling.auth.requirements", value = "-/bin/StartrackSearchTagService")
public class SearchTagService extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(SearchTagService.class);

    private static final String FILTER_NAME = "filter";
    private static final String DEFAULT_FILTER = "default";

    @Reference
    private SlingRepository repository;
    
    @Reference
    ResourceResolverFactory resourceResolverFactory;
    
    @Reference
    private XSSAPI xssapi;
    
    public void bindRepository(SlingRepository repository) {
           this.repository = repository; 
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServerException, IOException {
        final String filter = request.getParameter(FILTER_NAME) != null? xssapi.getValidJSToken(request.getParameter(FILTER_NAME).toLowerCase(), DEFAULT_FILTER): DEFAULT_FILTER;
        
        String res = "{ 'result': 'error' }";
        try {
            Tag t = resourceResolverFactory.getAdministrativeResourceResolver(null).getResource(Constants.TAGS_BASE_PATH + filter + Constants.FORWARD_SLASH).adaptTo(Tag.class);
            Iterable<Tag> ti = () -> t.listChildren();

            JSONObject obj = new JSONObject();
            obj.put("result", "ok");
            obj.put("filter", filter);
            obj.put("data", StreamSupport.stream(ti.spliterator(), false).map(o -> {
                JSONObject so = new JSONObject();
                try {
                    so.put("value", o.getName());
                    so.put("name", o.getTitle());
                } catch (JSONException e) {
                    log.error("JSONException:" + e.toString());
                }
                return so;
            }).collect(Collectors.toList()));
            res = obj.toString();
        } catch(JSONException e) {
            log.error("JSONException:" + e.toString());
        } catch(org.apache.sling.api.resource.LoginException e) {
            log.error("LoginException:" + e.toString());
        }
        response.getWriter().write(res);
    }
}
