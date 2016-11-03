package au.com.auspost.startrack_corp.core.launcher;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.sling.testing.tools.http.Request;
import org.apache.sling.testing.tools.http.RequestBuilder;
import org.apache.sling.testing.tools.http.RequestExecutor;
import org.apache.sling.testing.tools.sling.SlingTestBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlingServerSideTestsBase extends SlingTestBase {
    private static boolean servletOk;

    /** Path to node that maps to Sling JUnit servlet */
    public static final String SERVLET_PATH =  "/bin/StartrackSearchService";
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    /** Before running tests, setup a node that gives access to the Sling JUnit servlet,
     *  and check (with timeout) that the servlet is ready */
    public SlingServerSideTestsBase() {
        if(!servletOk) {
            final RequestExecutor executor = new RequestExecutor(new DefaultHttpClient());
            final RequestBuilder builder = new RequestBuilder(getServerBaseUrl());
    
            HttpResponse response = null;
            try {
                HttpGet httpGet = new HttpGet(builder.buildUrl(SERVLET_PATH));
                Request request = builder.buildOtherRequest(httpGet);
                log.debug("request: " + request);
                response = executor.execute(request).getResponse();
            } catch (ClientProtocolException e) {
                log.error("ClientProtocolException: " + e);
            } catch (IOException e) {
                log.error("ClientProtocolException: " + e);
            }
            int status = response.getStatusLine().getStatusCode();
            log.debug("SlingServerSideTestsBase: " + status);
            Assert.assertTrue("Expected status code 201 or 202, received: "+status,status == 201 || status == 201);
            servletOk = true;
        }
    }
}
