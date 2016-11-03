
/**
 * SalesForceAuthService
 *
 * @author Andrei
 *
 */
package au.com.auspost.startrack_corp.core.services;

import java.util.stream.Collectors;
import java.rmi.ServerException;

import javax.jcr.RepositoryException;
import javax.jcr.Node;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Property;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.HttpClient;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONTokener;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.api.SlingRepository;

import au.com.auspost.startrack_corp.core.Constants;

/**
 * Service to search articles.
 */
@SlingServlet(paths="/bin/StartrackSalesForceAuthService", methods = "GET", metatype = true)
@Property(name = "sling.auth.requirements", value = "-/bin/StartrackSalesForceAuthService")
public class SalesForceAuthService extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(SalesForceAuthService.class);

    private final String SALESFORCE_JSON_PATH_FILE = "/etc/designs/startrack_corp/share/salesforce.json/jcr:content";
    private final String GRANT_TYPE = "grant_type"; 
    private final String CLIENT_ID = "client_id";
    private final String CLIENT_SECRET = "client_secret";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String TOKEN_URL = "tokenUrl";

    @Reference
    private SlingRepository repository;
    
    @Reference
    ResourceResolverFactory resourceResolverFactory;
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServerException, IOException {
        String res = "{ 'result': 'error' }";

        try {
            Node node = resourceResolverFactory.getAdministrativeResourceResolver(null).getResource(SALESFORCE_JSON_PATH_FILE).adaptTo(Node.class);
            String jsonData = node.getProperty(Constants.JCR_DATA).getString();
            JSONObject jsonObject = new JSONObject(jsonData);

            PostMethod post = new PostMethod(jsonObject.getString(TOKEN_URL));
            post.addParameter(GRANT_TYPE, jsonObject.getString(GRANT_TYPE));
            post.addParameter(CLIENT_ID, jsonObject.getString(CLIENT_ID));
            post.addParameter(CLIENT_SECRET, jsonObject.getString(CLIENT_SECRET));
            post.addParameter(USERNAME, jsonObject.getString(USERNAME));
            post.addParameter(PASSWORD, jsonObject.getString(PASSWORD));

            try {
                HttpClient httpclient = new HttpClient();
                httpclient.executeMethod(post);
                String result = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream()))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject authResponse = new JSONObject(new JSONTokener(result));
                res = authResponse.toString();
            } finally {
                post.releaseConnection();
            }


        } catch(org.apache.sling.api.resource.LoginException e) {
            log.error("LoginException:" + e.toString());
        } catch(RepositoryException e) {
            log.error("RepositoryException:" + e.toString());
        } catch(JSONException e) {
            log.error("JSONException:" + e.toString());
        }
        response.getWriter().write(res);
    }
}
