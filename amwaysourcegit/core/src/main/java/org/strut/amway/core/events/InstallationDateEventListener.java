package org.strut.amway.core.events;

import java.util.Calendar;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.util.AEMUtils;
import org.strut.amway.core.util.Constants;

import com.day.cq.commons.jcr.JcrConstants;

@Component(immediate = true)
@Service(value = EventHandler.class)
@Property(name = EventConstants.EVENT_TOPIC, value = {SlingConstants.TOPIC_RESOURCE_ADDED})
public class InstallationDateEventListener extends BaseEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallationDateEventListener.class);

    @Reference
    SlingRepository repository;

    @Reference
    SlingSettingsService slingSettingsService;

    @Activate
    private void activate(final Map<String, Object> properties) {
        LOGGER.info("InstallationDateEventListener is activated");
        setLogger(LOGGER);
    }

    @Deactivate
    private void deactivate() {
        LOGGER.info("InstallationDateEventListener is deactivated");
    }

    @Override
    protected boolean isEligibleEvent(final Event event) {
        final String path = (String) event.getProperty(SlingConstants.PROPERTY_PATH);
        final Object resourceType = event.getProperty(SlingConstants.PROPERTY_RESOURCE_TYPE);
        if (AEMUtils.isAuthor(slingSettingsService) && path != null && path.startsWith(Constants.CONTENT_ROOT) && path.contains(Constants.APPLICATION_NAME)
                && resourceType != null && resourceType.equals(Constants.HOME_PAGE_RESOURCE_TYPE)
                && SlingConstants.TOPIC_RESOURCE_ADDED.equals(event.getTopic())) {
            return true;
        }
        return false;
    }

    @Override
    protected void processEvent(final Event event) throws Exception {
        Session session = null;
        Object path = null;
        try {
            session = repository.loginAdministrative(null);
            path = event.getProperty(SlingConstants.PROPERTY_PATH);
            updateInstallationDate((String) path, session);
        } catch (Exception ex) {
            LOGGER.error("Error while process installation date event with path " + path + " :{}", ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
            session.logout();
        }
    }

    private void updateInstallationDate(String path, final Session session) throws Exception {
        if (session.nodeExists(path)) {
            if (!path.endsWith(JcrConstants.JCR_CONTENT)) {
                path = path + "/" + JcrConstants.JCR_CONTENT;
            }
            Node pageContentNode = session.getNode(path);
            if (!pageContentNode.hasProperty(Constants.INSTALLATION_DATE_PROPERTY)) {
                Node pageContentParent = pageContentNode.getParent();
                Calendar jcrCreatedProp = pageContentParent.getProperty(JcrConstants.JCR_CREATED).getDate();
                pageContentNode.setProperty(Constants.INSTALLATION_DATE_PROPERTY, jcrCreatedProp);
                session.save();
            }
        }
    }

}
