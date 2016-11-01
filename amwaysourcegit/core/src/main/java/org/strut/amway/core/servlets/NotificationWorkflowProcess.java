package org.strut.amway.core.servlets;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.PropertiesUtil;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Service
@Component(immediate = true)
public final class NotificationWorkflowProcess implements WorkflowProcess {

    @Reference
    private MessageGatewayService messageService;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class); // or get admin resolver here
        UserManager userManager = resolver.adaptTo(UserManager.class);

        MessageGateway<HtmlEmail> messageGateway = messageService.getGateway(HtmlEmail.class);
        try {
            String initiator = workItem.getWorkflow().getInitiator();
            Authorizable authorizable = userManager.getAuthorizable(initiator);
            String userEmail = PropertiesUtil.toString(authorizable.getProperty("profile/email"), "");
            if(StringUtils.isBlank(userEmail)) {
                return;
            }
            HtmlEmail email = new HtmlEmail();
            email.setCharset(CharEncoding.UTF_8);
            email.addTo(userEmail);
            email.setSubject("test email subject");
            email.setMsg("text email body");
            email.setHtmlMsg("<!DOCTYPE html><html><head></head><body><p>html email body</p></body></html>");
            messageGateway.send(email);
        } catch(Exception e) {
            // cannot send email. print some error
            e.printStackTrace();
        }
    }
}