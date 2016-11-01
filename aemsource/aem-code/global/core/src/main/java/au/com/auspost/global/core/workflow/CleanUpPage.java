package au.com.auspost.global.core.workflow;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import au.com.auspost.global.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service
@Properties({ @Property(name = org.osgi.framework.Constants.SERVICE_DESCRIPTION, value = "Clean up Page Properties"),
		@Property(name = "process.label", value = "Clean up Page Properties") })
public class CleanUpPage implements WorkflowProcess{


	private static final Logger log = LoggerFactory.getLogger(CleanUpPage.class);

	public void execute(WorkItem item, WorkflowSession wfSession, MetaDataMap metadataArg) throws WorkflowException {

		WorkflowData workflowData = item.getWorkflowData();
		String path = workflowData.getPayload().toString();
		Session session = wfSession.getSession();

		try {
			if(StringUtils.isNotBlank(path)&& (path.startsWith(Constants.PATH_CONTENT))
					&& session.nodeExists(path + Constants.STRING_JCR_CONTENT) && session.getNode(path + Constants.STRING_JCR_CONTENT).hasProperty(Constants.ACTION_REQUESTED)){
				session.getNode(path + Constants.STRING_JCR_CONTENT).getProperty(Constants.ACTION_REQUESTED).remove();
				session.save();
			}
		} catch (RepositoryException e) {
			log.error("Some problem is accessing user permissions");
			throw new WorkflowException("Some problem is accessing user permissions",e);
		}
	}

}