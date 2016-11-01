package au.com.auspost.global.core.workflow;


import java.util.Iterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import au.com.auspost.global.utils.Constants;
import com.day.cq.workflow.WorkflowService;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.*;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
* 1.) This process step is used to validate publish, unpublish and delete approval workflows for global and ourpost sites.
* 2.) ACLs have been implemented via the utility of acs-commons.Filename is kept as acls-mypost-prod-1.0.0.zip in order to apply ACL changes accross
*     all environments.
* 3.) 6 main groups have been created.
*     ( eg: global-authors,global-approvers,global-publishers,ourpost-authors,ourpost-approvers and ourpost-publishers)
* 4.) The main requirement is to limit ourpost functions to ourpost sites and apply other functions to other sites at a global level.
* 5.) Each workflow contains 2 participant steps each. One for approval of the request and one for the publish the approved request.
* 6.) /content , /content/dam and /etc/tags have been considered as the main sources of paths for this workflow approval process.
* 7.) publish,unpublish and delete functions have been overlayed to /apps/cq/gui/components/siteadmin/admin/actions in order to customize
*     the touch UI customizations override the default functionality.
*
* */

@Component
@Service
@Properties({@Property(name = org.osgi.framework.Constants.SERVICE_DESCRIPTION, value = "Verify User Action"),
        @Property(name = "process.label", value = "Verify User Action")})
public class VerifyAction implements WorkflowProcess {

    private final String actionRequested = Constants.ACTION_REQUESTED;
    private static final Logger log = LoggerFactory.getLogger(VerifyAction.class);


    @Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY)
    private ResourceResolverFactory resourceResolverFactory;

    @Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY)
    private WorkflowService workflowService;


    public void execute(WorkItem item, WorkflowSession wfSession, MetaDataMap metadataArg) throws WorkflowException {

        WorkflowData workflowData = item.getWorkflowData();
        String path = workflowData.getPayload().toString();
        Session session = wfSession.getSession();
        String workflowInitiatorUser = item.getWorkflow().getInitiator();

        try {
            UserManager userManager = AccessControlUtil.getUserManager(session);
            Authorizable authorizable = userManager.getAuthorizable(workflowInitiatorUser);
            if (authorizable instanceof User) {
                Iterator<Group> groups = authorizable.declaredMemberOf();
                while (groups.hasNext()) {
                    Group group = (Group) groups.next();
                    if ((group.getID().contains(Constants.USER_CATEGORY_APPROVER) || group.getID().contains(Constants.USER_CATEGORY_AUTHOR) || group.getID().equals(Constants.USER_CATEGORY_ADMINISTRATORS)) && group.isMember(authorizable)) {
                        if (StringUtils.isNotEmpty(workflowData.getMetaDataMap().get(Constants.ACTION_REQUESTED).toString())) {
                                if (path.startsWith(Constants.PATH_CONTENT)) {
                                    session.getNode(path + Constants.STRING_JCR_CONTENT).setProperty(actionRequested, workflowData.getMetaDataMap().get(Constants.ACTION_REQUESTED).toString());
                                    break;
                                } else if (path.startsWith(Constants.PATH_ETC_TAGS)) {
                                    session.getNode(path).setProperty(actionRequested, workflowData.getMetaDataMap().get(Constants.ACTION_REQUESTED).toString());
                                    break;
                                } else {
                                    break;
                                }

                        }
                    } else {
                        log.error("Invalid User Request has been identified");
                        throw new WorkflowException("Invalid User Request has been identified");
                    }
                }
            }
            session.save();
        } catch (RepositoryException e) {
            log.error("Error on verifying user permission");
            throw new WorkflowException("Error on verifying user permission", e);
        }
    }

    private void persistDataRelatedToRejectActivationWorkflow(){

    }

}
