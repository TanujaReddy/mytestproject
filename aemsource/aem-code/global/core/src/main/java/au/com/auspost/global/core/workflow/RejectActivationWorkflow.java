package au.com.auspost.global.core.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import au.com.auspost.global.core.workflow.util.ResourceResolverUtils;
import au.com.auspost.global.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.day.cq.workflow.model.WorkflowModel;

@Component
@Service
@Properties({@Property(name = org.osgi.framework.Constants.SERVICE_DESCRIPTION, value = "Reject Activation workflow"),
        @Property(name = "process.label", value = "Reject Activation workflow")})
public class RejectActivationWorkflow implements WorkflowProcess {

    static private final Logger log = LoggerFactory.getLogger(RejectActivationWorkflow.class);

    private List<String> assets = new ArrayList<String>();

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private ResourceResolverFactory resourceResolverFactory;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private WorkflowService workflowService;


    @Override
    public void execute(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metadataMap) throws WorkflowException {
        boolean assetReferncedResolved = false;
        boolean isitemTerminated = false;
        WorkflowData workflowData = workItem.getWorkflowData();

        String path = workflowData.getPayload().toString();
        ResourceResolver resourceResolver = null;
        Session session = null;
        try {
            //TODO ; deperecated getAdministrativeResourceResolver method must be replaced with ResourceResolver getResourceResolver(Map<String,Object> authenticationInfo) with the support of config changes for the default system user
            resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            session = resourceResolver.adaptTo(Session.class);
            String workflowInitiatorUser = workItem.getWorkflow().getInitiator();
            //Forcing admin session because otherwise workitems aren't getting resolved.
            wfSession = workflowService.getWorkflowSession(session);
            WorkItem[] workItems = wfSession.getActiveWorkItems();
            for (WorkItem item : workItems) {
                WorkflowModel wfModel = item.getWorkflow().getWorkflowModel();
                log.debug("Workflow Model {}", wfModel);

                if (StringUtils.isNotEmpty(workflowData.getMetaDataMap().get("workflowWorkItem").toString())) {
                    if (item.getId().toString().equalsIgnoreCase(workflowData.getMetaDataMap().get("workflowWorkItem").toString())) {

                        path = item.getWorkflowData().getPayload().toString();

                        UserManager userManager = AccessControlUtil.getUserManager(session);
                        Authorizable authorizable = userManager.getAuthorizable(workflowInitiatorUser);
                        if (authorizable instanceof User) {
                            Iterator<Group> groups = authorizable.declaredMemberOf();
                            while (groups.hasNext()) {
                                Group group = (Group) groups.next();

                                    if (!path.contains("ourpost") == true && (group.getID().contains(Constants.OURPOST))) {
                                        throw new WorkflowException(String.format("User is not authorized to approve content in path %s", path));
                                    } else if(path.contains("ourpost") == true && ((group.getID().contains(Constants.OURPOST)) || group.getID().equals(Constants.USER_CATEGORY_ADMINISTRATORS) && group.isMember(authorizable))) {
                                        terminateWorkflow(wfSession, item);
                                    }else if(!path.contains("ourpost") == true && ((group.getID().contains(Constants.GLOBAL)) || group.getID().equals(Constants.USER_CATEGORY_ADMINISTRATORS) && group.isMember(authorizable))) {
                                        terminateWorkflow(wfSession, item);
                                    }

                            }

                        }
                    }


                }

            }

        } catch (RepositoryException e) {
            throw new WorkflowException("Exception in getting respository instance");
        } catch (LoginException e) {
            throw new WorkflowException("Exception in Login");
        } finally {
            ResourceResolverUtils.closeResourceResolver(resourceResolver);
        }


    }

    /**
     * Terminate Workflow
     *
     * @param wfSession
     * @param itemsToTerminate
     * @throws WorkflowException
     */
    private boolean terminateWorkflow(WorkflowSession wfSession, WorkItem itemsToTerminate) throws WorkflowException {
        boolean isItemTerminated = false;
        wfSession.terminateWorkflow(itemsToTerminate.getWorkflow());
        isItemTerminated = true;
        return isItemTerminated;
    }

}
