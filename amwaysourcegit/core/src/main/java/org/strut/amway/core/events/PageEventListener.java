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
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.Constants;

@Component(immediate = true)
@Service(value = EventHandler.class)
@Property(name = EventConstants.EVENT_TOPIC, value = { SlingConstants.TOPIC_RESOURCE_ADDED, SlingConstants.TOPIC_RESOURCE_CHANGED })
public class PageEventListener extends BaseEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageEventListener.class);
    private static final String RESOURCE_ADDED_ATTRIBUTES = "resourceAddedAttributes";
    private static final String RESOURCE_CHANGED_ATTRIBUTES = "resourceChangedAttributes";
    private static final String RESOURCE_REMOVED_ATTRIBUTES = "resourceRemovedAttributes";
    private static final String[] LISTENER_ATTRIBUTES = new String[] { ArticleConstants.INPUT_PUBLISH_DATE, JcrConstants.JCR_CREATED,
            ArticleConstants.PUBLISH_DATE };

    @Reference
    SlingRepository repository;

    @Activate
    private void activate(final Map<String, Object> properties) {
        LOGGER.info("PageEventListener is activated");
        setLogger(LOGGER);
    }

    @Deactivate
    private void deactivate() {
        LOGGER.info("PageEventListener is deactivated");
    }

    @Override
    protected boolean isEligibleEvent(Event event) {
        final String path = (String) event.getProperty(SlingConstants.PROPERTY_PATH);
        final Object resourceType = event.getProperty(SlingConstants.PROPERTY_RESOURCE_TYPE);
        if (path != null && path.startsWith(Constants.CONTENT_ROOT) && path.contains(Constants.APPLICATION_NAME)
                && resourceType != null && (resourceType.equals(ArticleConstants.ARTICLE_RESOURCE_TYPE) ||resourceType.equals(ArticleConstants.SEA_ARTICLE_RESOURCE_TYPE))) {
            switch (event.getTopic()) {
            case SlingConstants.TOPIC_RESOURCE_ADDED:
                return true;

            case SlingConstants.TOPIC_RESOURCE_CHANGED:
                return isEligibleAttributes(event);

            default:
                break;
            }
        }
        return false;
    }

    @Override
    protected void processEvent(Event event) throws Exception {
        Session session = null;
        Object path = null;
        try {
            session = repository.loginAdministrative(null);
            path = event.getProperty(SlingConstants.PROPERTY_PATH);
            updatePublishedDateOfPage((String) path, session);
        } catch (Exception ex) {
            LOGGER.error("Error while process page event with path " + path + " :{}", ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
            session.logout();
        }
    }

    private void updatePublishedDateOfPage(String path, final Session session) throws Exception {
        if (session.nodeExists(path)) {
            if (!path.endsWith(JcrConstants.JCR_CONTENT)) {
                path = path + "/" + JcrConstants.JCR_CONTENT;
            }
            boolean change = false;
            Node pageContentNode = session.getNode(path);
            if (pageContentNode.hasProperty(ArticleConstants.INPUT_PUBLISH_DATE)) {
                change = updatePublishedDateField(pageContentNode, pageContentNode.getProperty(ArticleConstants.INPUT_PUBLISH_DATE).getDate());
            } else {
                change = updatePublishedDateField(pageContentNode, pageContentNode.getProperty(JcrConstants.JCR_CREATED).getDate());
            }
            if (change == true) {
                session.save();
            }
        }
    }

    private boolean updatePublishedDateField(final Node pageContentNode, final Calendar value) throws Exception {
        boolean isChange = false;
        if (value == null) {
            return isChange;
        }

        if (pageContentNode.hasProperty(ArticleConstants.PUBLISH_DATE)) {
            Calendar oldValue = pageContentNode.getProperty(ArticleConstants.PUBLISH_DATE).getDate();
            if (oldValue.compareTo(value) != 0) {
                isChange = true;
            }
        } else {
            isChange = true;
        }

        if (isChange == true) {
            pageContentNode.setProperty(ArticleConstants.PUBLISH_DATE, value);
        }
        return isChange;
    }

    private boolean isEligibleAttributes(final Event event) {
        Object resourceAddedAttributes = event.getProperty(RESOURCE_ADDED_ATTRIBUTES);
        Object resourceChangedAttributes = event.getProperty(RESOURCE_CHANGED_ATTRIBUTES);
        Object resourceRemovedAttributes = event.getProperty(RESOURCE_REMOVED_ATTRIBUTES);

        if ((resourceAddedAttributes != null && containAnyAttribute((String[]) resourceAddedAttributes, LISTENER_ATTRIBUTES))
                || (resourceChangedAttributes != null && containAnyAttribute((String[]) resourceChangedAttributes, LISTENER_ATTRIBUTES))
                || (resourceRemovedAttributes != null && containAnyAttribute((String[]) resourceRemovedAttributes, LISTENER_ATTRIBUTES))) {
            return true;
        }
        return false;
    }

    private boolean containAnyAttribute(final String[] resourceAttributeModifications, final String[] attributes) {
        for (String attribute : attributes) {
            for (String attributeModification : resourceAttributeModifications) {
                if (attributeModification.equals(attribute)) {
                    return true;
                }
            }
        }
        return false;
    }

}
