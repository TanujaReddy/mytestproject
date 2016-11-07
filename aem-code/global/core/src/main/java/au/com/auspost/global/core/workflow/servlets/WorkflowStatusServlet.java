package au.com.auspost.global.core.workflow.servlets;

import com.day.cq.workflow.status.WorkflowStatus;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import com.day.cq.wcm.api.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * This workflow checks if the current payload is in a workflow.
 */
@Component(immediate = true)
@Service
@Properties(value = {
        @Property(name = "sling.servlet.extensions", value = "json"),
        @Property(name = "sling.servlet.paths", value = "/bin/auspost-workflow-status"),
        @Property(name = "sling.servlet.methods", value = {"POST"})
})
public class WorkflowStatusServlet extends SlingAllMethodsServlet {
    static private final Logger log = LoggerFactory.getLogger(WorkflowStatus.class);

    private static final long serialVersionUID = -6451265809590855585L;
    private static boolean isInWorkflow = false;
    WorkflowStatus wfState = null;

    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {

        try {

            String pagePath = request.getParameter("pagePath");
            Resource pageResource = request.getResourceResolver().resolve(pagePath);
            if (pageResource != null) {
                Page targetPage = pageResource.adaptTo(Page.class);
                if (targetPage != null) {
                    wfState = targetPage.adaptTo(WorkflowStatus.class);
                }
                isInWorkflow = (wfState != null && wfState.isInRunningWorkflow(true));
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("workflowStatus", isInWorkflow);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(jsonResponse.toString());
            }


        } catch (final JSONException e) {
            throw new ServletException("Can't determine the workflow status");
        }
    }

}
