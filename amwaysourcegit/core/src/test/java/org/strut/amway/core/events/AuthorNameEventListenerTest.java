package org.strut.amway.core.events;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Value;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.service.event.Event;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.Constants;

import com.day.cq.commons.jcr.JcrConstants;
import org.strut.amway.core.util.ContentConstants;

public class AuthorNameEventListenerTest {

    AuthorNameEventListener classUnderTest;

    @Mock
    SlingRepository repository;

    @Mock
    SlingSettingsService slingSettingsService;

    @Mock
    JackrabbitSession session;

    @Mock
    UserManager userManager;

    String path = ContentConstants.CONTENT_FULL_PATH_AUS_EN + "/health/fitness/article-1/jcr:content";

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new AuthorNameEventListener();
        setSlingRepositoryService();
        setSlingSettingsService();
        callActivateMethod();

        final Set<String> runModes = new HashSet<String>();
        runModes.add("author");
        when(repository.loginAdministrative(null)).thenReturn(session);
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(session.getUserManager()).thenReturn(userManager);
    }

    @Test
    public void shouldHandleProperEvent() throws Exception {
        final Map<String, Object> properties = createProperties();
        final Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        classUnderTest.handleEvent(event);
    }

    @Test
    public void shouldNotProcessEventThatIsInvalid() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, "/etc/design");
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        String path = ContentConstants.CONTENT_FULL_PATH_AUS_EN + "/health/fitness/article-1";
        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, "corporate/amway-today/components/sub-category-page");
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        path = ContentConstants.CONTENT_FULL_PATH_AUS_EN + "/health/fitness/article-1";
        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        // for changed event
        path = ContentConstants.CONTENT_FULL_PATH_AUS_EN + "/health/fitness/article-1";
        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceAddedAttributes", new String[] { "jcr:title" });
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceChangedAttributes", new String[] { "jcr:title" });
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceRemovedAttributes", new String[] { "jcr:title" });
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceAddedAttributes", new String[] { "jcr:title" });
        properties.put("resourceChangedAttributes", new String[] { "jcr:title" });
        properties.put("resourceRemovedAttributes", new String[] { "jcr:title" });
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_REMOVED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);
    }

    @Test
    public void shouldNotProcessEventWhenEnvironmentIsPublish() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        final Set<String> runModes = new HashSet<String>();
        runModes.add("publish");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessAddedEventWhenAuthorNameFieldIsNotPresent() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        String author = "hadoan";
        String givenName = "ha";
        String familyName = "doan";
        Node pageContentParent = Mockito.mock(Node.class);
        Node pageContentNode = Mockito.mock(Node.class);
        Property jcrCreatedByProp = Mockito.mock(Property.class);
        Authorizable authorizable = Mockito.mock(Authorizable.class);
        Value givenNameValue = Mockito.mock(Value.class);
        Value familyNameValue = Mockito.mock(Value.class);
        when(jcrCreatedByProp.getString()).thenReturn(author);
        when(givenNameValue.getString()).thenReturn(givenName);
        when(familyNameValue.getString()).thenReturn(familyName);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.getParent()).thenReturn(pageContentParent);
        when(pageContentNode.hasProperty(eq(ArticleConstants.AUTHOR_NAME))).thenReturn(false);
        when(pageContentParent.getProperty(JcrConstants.JCR_CREATED_BY)).thenReturn(jcrCreatedByProp);
        when(userManager.getAuthorizable(eq(author))).thenReturn(authorizable);
        when(authorizable.getProperty(eq("profile/givenName"))).thenReturn(new Value[] { givenNameValue });
        when(authorizable.getProperty(eq("profile/familyName"))).thenReturn(new Value[] { familyNameValue });

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(1)).setProperty(ArticleConstants.AUTHOR_NAME, givenName + " " + familyName);
        verify(session, times(1)).save();
        verify(session, times(1)).logout();
    }

    @Test
    public void shouldNotSaveAuthorNameFieldWhenAuthorNameFieldIsPresent() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        String givenName = "ha";
        String familyName = "doan";
        Node pageContentParent = Mockito.mock(Node.class);
        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.getParent()).thenReturn(pageContentParent);
        when(pageContentNode.hasProperty(eq(ArticleConstants.AUTHOR_NAME))).thenReturn(true);

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(0)).setProperty(ArticleConstants.AUTHOR_NAME, givenName + " " + familyName);
        verify(session, times(0)).save();
        verify(session, times(1)).logout();
    }

    @Test
    public void shouldSaveBlankWhenThereIsNoAuthoriableUser() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        String author = "hadoan";
        String givenName = "ha";
        String familyName = "doan";
        Node pageContentParent = Mockito.mock(Node.class);
        Node pageContentNode = Mockito.mock(Node.class);
        Property jcrCreatedByProp = Mockito.mock(Property.class);
        Authorizable authorizable = null;
        Value givenNameValue = Mockito.mock(Value.class);
        Value familyNameValue = Mockito.mock(Value.class);
        when(jcrCreatedByProp.getString()).thenReturn(author);
        when(givenNameValue.getString()).thenReturn(givenName);
        when(familyNameValue.getString()).thenReturn(familyName);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.getParent()).thenReturn(pageContentParent);
        when(pageContentNode.hasProperty(eq(ArticleConstants.AUTHOR_NAME))).thenReturn(false);
        when(pageContentParent.getProperty(JcrConstants.JCR_CREATED_BY)).thenReturn(jcrCreatedByProp);
        when(userManager.getAuthorizable(eq(author))).thenReturn(authorizable);

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(0)).setProperty(ArticleConstants.AUTHOR_NAME, givenName + " " + familyName);
        verify(session, times(1)).save();
        verify(session, times(1)).logout();
    }

    @Test
    public void shouldSaveGivenNameAuthorNameField() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        String author = "hadoan";
        String givenName = "ha";
        String familyName = null;
        Node pageContentParent = Mockito.mock(Node.class);
        Node pageContentNode = Mockito.mock(Node.class);
        Property jcrCreatedByProp = Mockito.mock(Property.class);
        Authorizable authorizable = Mockito.mock(Authorizable.class);
        Value givenNameValue = Mockito.mock(Value.class);
        Value familyNameValue = Mockito.mock(Value.class);
        when(jcrCreatedByProp.getString()).thenReturn(author);
        when(givenNameValue.getString()).thenReturn(givenName);
        when(familyNameValue.getString()).thenReturn(familyName);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.getParent()).thenReturn(pageContentParent);
        when(pageContentNode.hasProperty(eq(ArticleConstants.AUTHOR_NAME))).thenReturn(false);
        when(pageContentParent.getProperty(JcrConstants.JCR_CREATED_BY)).thenReturn(jcrCreatedByProp);
        when(userManager.getAuthorizable(eq(author))).thenReturn(authorizable);
        when(authorizable.getProperty(eq("profile/givenName"))).thenReturn(new Value[] { givenNameValue });
        when(authorizable.getProperty(eq("profile/familyName"))).thenReturn(new Value[] { familyNameValue });

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(1)).setProperty(ArticleConstants.AUTHOR_NAME, givenName);
        verify(session, times(1)).save();
        verify(session, times(1)).logout();
    }

    @Test
    public void shouldSaveFamilyNameAuthorNameField() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        String author = "hadoan";
        String givenName = null;
        String familyName = "doan";
        Node pageContentParent = Mockito.mock(Node.class);
        Node pageContentNode = Mockito.mock(Node.class);
        Property jcrCreatedByProp = Mockito.mock(Property.class);
        Authorizable authorizable = Mockito.mock(Authorizable.class);
        Value givenNameValue = Mockito.mock(Value.class);
        Value familyNameValue = Mockito.mock(Value.class);
        when(jcrCreatedByProp.getString()).thenReturn(author);
        when(givenNameValue.getString()).thenReturn(givenName);
        when(familyNameValue.getString()).thenReturn(familyName);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.getParent()).thenReturn(pageContentParent);
        when(pageContentNode.hasProperty(eq(ArticleConstants.AUTHOR_NAME))).thenReturn(false);
        when(pageContentParent.getProperty(JcrConstants.JCR_CREATED_BY)).thenReturn(jcrCreatedByProp);
        when(userManager.getAuthorizable(eq(author))).thenReturn(authorizable);
        when(authorizable.getProperty(eq("profile/givenName"))).thenReturn(new Value[] { givenNameValue });
        when(authorizable.getProperty(eq("profile/familyName"))).thenReturn(new Value[] { familyNameValue });

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(1)).setProperty(ArticleConstants.AUTHOR_NAME, familyName);
        verify(session, times(1)).save();
        verify(session, times(1)).logout();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRetryWhenExceptionOccurs() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        String author = "hadoan";
        String givenName = null;
        String familyName = "doan";
        Node pageContentParent = Mockito.mock(Node.class);
        Node pageContentNode = Mockito.mock(Node.class);
        Property jcrCreatedByProp = Mockito.mock(Property.class);
        Authorizable authorizable = Mockito.mock(Authorizable.class);
        Value givenNameValue = Mockito.mock(Value.class);
        Value familyNameValue = Mockito.mock(Value.class);
        when(jcrCreatedByProp.getString()).thenReturn(author);
        when(givenNameValue.getString()).thenReturn(givenName);
        when(familyNameValue.getString()).thenReturn(familyName);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.getParent()).thenReturn(pageContentParent);
        when(pageContentNode.hasProperty(eq(ArticleConstants.AUTHOR_NAME))).thenReturn(false);
        when(pageContentParent.getProperty(JcrConstants.JCR_CREATED_BY)).thenReturn(jcrCreatedByProp);
        when(userManager.getAuthorizable(eq(author))).thenReturn(authorizable);
        when(authorizable.getProperty(eq("profile/givenName"))).thenReturn(new Value[] { givenNameValue });
        when(authorizable.getProperty(eq("profile/familyName"))).thenReturn(new Value[] { familyNameValue });
        when(pageContentNode.setProperty(ArticleConstants.AUTHOR_NAME, familyName)).thenThrow(RuntimeException.class);

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(5)).loginAdministrative(null);
        verify(pageContentNode, times(5)).setProperty(ArticleConstants.AUTHOR_NAME, familyName);
        verify(session, times(0)).save();
        verify(session, times(5)).logout();
    }

    @Test
    public void shouldActivateSuccessfully() throws Exception {
        callActivateMethod();
    }

    @Test
    public void shouldDeactivateSuccessfully() throws Exception {
        callDeactivateMethod();
    }

    private void callActivateMethod() throws Exception {
        final Method activateMethod = AuthorNameEventListener.class.getDeclaredMethod("activate", Map.class);
        activateMethod.setAccessible(true);
        activateMethod.invoke(classUnderTest, Collections.EMPTY_MAP);
    }

    private void callDeactivateMethod() throws Exception {
        final Method deactivateMethod = AuthorNameEventListener.class.getDeclaredMethod("deactivate");
        deactivateMethod.setAccessible(true);
        deactivateMethod.invoke(classUnderTest);
    }

    private Map<String, Object> createProperties() {
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        return properties;
    }

    private Event createPageEvent(final String topic, Map<String, Object> properties) {
        return new Event(topic, properties);
    }

    private void setSlingRepositoryService() throws Exception {
        final Field slingRepositoryField = FieldUtils.getField(AuthorNameEventListener.class, "repository", true);
        slingRepositoryField.setAccessible(true);
        slingRepositoryField.set(classUnderTest, repository);
    }

    private void setSlingSettingsService() throws Exception {
        final Field slingSettingsServiceField = FieldUtils.getField(AuthorNameEventListener.class, "slingSettingsService", true);
        slingSettingsServiceField.setAccessible(true);
        slingSettingsServiceField.set(classUnderTest, slingSettingsService);
    }

}
