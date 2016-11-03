
/**
 * SalesForceAuthService
 *
 * @author Andrei
 *
 */
package org.strut.amway.core.servlets;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.email.EmailService;

/**
 * Service to search articles.
 */
@SlingServlet(paths="/bin/CommentsEmailServlet", methods = "GET", metatype = true)
@Property(name = "sling.auth.requirements", value = "-/bin/CommentsEmailServlet")
public class CommentsEmailService extends SlingSafeMethodsServlet {
    private static final String TEMPLATE_PATH = "/etc/designs/corporate/amway-today/email.txt";

    private static final long serialVersionUID = 1L;
   
    protected static final Logger LOGGER = LoggerFactory.getLogger(CommentsEmailService.class);

    @Reference
    EmailService emailService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServerException, IOException {
    	
    	  LOGGER.info("CommentsEmailService Activated 1 ");
    	  
        String res = "{ 'result': 'error' }";

        Map<String, String> emailParams = new HashMap<String,String>();
        emailParams.put("body", "Test body");
        emailParams.put("senderEmailAddress", "tanuja.reddy@vml.com.au");
        emailParams.put("senderName", "Test");

        String[] recipients = { "tanuja.reddy@vml.com.au" };

        List<String> failureList = emailService.sendEmail(TEMPLATE_PATH, emailParams, recipients);
        LOGGER.info("CommentsEmailService Activated");
        
        if (failureList.size() > 0) {
            res = "{ 'result': 'error', 'message': '" + failureList.get(0) + "'}";
        } else {
        	  LOGGER.info("CommentsEmailService Activated else");
            res = "{ 'result': 'ok' }";
        }
        response.getWriter().write(res);
    }
}
