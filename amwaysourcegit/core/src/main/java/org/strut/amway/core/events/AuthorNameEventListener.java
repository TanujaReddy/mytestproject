package org.strut.amway.core.events;

import com.day.cq.commons.jcr.JcrConstants;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.felix.scr.annotations.*;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.util.AEMUtils;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.Constants;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import java.util.Map;

@Component(immediate = true)
@Service(value = EventHandler.class)
@Property(name = EventConstants.EVENT_TOPIC, value = { SlingConstants.TOPIC_RESOURCE_ADDED })
public class AuthorNameEventListener extends BaseEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorNameEventListener.class);
    private static final String FAMILY_NAME = "profile/familyName";
    private static final String GIVEN_NAME = "profile/givenName";

    @Reference
    SlingRepository repository;

    @Reference
    SlingSettingsService slingSettingsService;

    @Activate
    private void activate(final Map<String, Object> properties) {
        LOGGER.info("AuthorNameEventListener is activated");
        setLogger(LOGGER);
    }

    @Deactivate
    private void deactivate() {
        LOGGER.info("AuthorNameEventListener is deactivated");
    }

    @Override
    protected boolean isEligibleEvent(final Event event) {
        final String path = (String) event.getProperty(SlingConstants.PROPERTY_PATH);
        final Object resourceType = event.getProperty(SlingConstants.PROPERTY_RESOURCE_TYPE);
        if (AEMUtils.isAuthor(slingSettingsService) && path != null && path.startsWith(Constants.CONTENT_ROOT)
                && path.contains(Constants.APPLICATION_NAME)
                && resourceType != null && (resourceType.equals(ArticleConstants.ARTICLE_RESOURCE_TYPE) ||resourceType.equals(ArticleConstants.SEA_ARTICLE_RESOURCE_TYPE) )
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
            updateAuthorName((String) path, session);
        } catch (Exception ex) {
            LOGGER.error("Error while process author name event with path " + path + " :{}", ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
            session.logout();
        }
    }

    private void updateAuthorName(String path, final Session session) throws Exception {
        if (session.nodeExists(path)) {
            if (!path.endsWith(JcrConstants.JCR_CONTENT)) {
                path = path + "/" + JcrConstants.JCR_CONTENT;
            }
            Node pageContentNode = session.getNode(path);
            if (!pageContentNode.hasProperty(ArticleConstants.AUTHOR_NAME)) {
                pageContentNode.setProperty(ArticleConstants.AUTHOR_NAME, createAuthorName(pageContentNode, session));
                session.save();
            }
        }
    }

    private String createAuthorName(final Node pageContentNode, final Session session) throws Exception {
        final Node pageContentParent = pageContentNode.getParent();
        final String author = pageContentParent.getProperty(JcrConstants.JCR_CREATED_BY).getString();
        final StringBuilder authorName = new StringBuilder();
        final UserManager userManager = ((JackrabbitSession) session).getUserManager();
        Authorizable authorizable = userManager.getAuthorizable(author);

        if (authorizable != null) {
            Value[] givenName = authorizable.getProperty(GIVEN_NAME);
            Value[] familyName = authorizable.getProperty(FAMILY_NAME);
            if (givenName != null && givenName[0] != null && givenName[0].getString() != null) {
                authorName.append(givenName[0].getString().trim());
            }
            authorName.append(" ");
            if (familyName != null && familyName[0] != null && familyName[0].getString() != null) {
                authorName.append(familyName[0].getString().trim());
            }
        }
        return authorName.toString().trim();
    }
}
