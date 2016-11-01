package org.strut.amway.core.events;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Session;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.jcr.api.SlingRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.service.event.Event;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.Constants;
import org.strut.amway.core.util.ContentConstants;

public class PageEventListenerTest {

    PageEventListener classUnderTest;

    @Mock
    SlingRepository repository;

    @Mock
    Session session;

    String path = ContentConstants.CONTENT_FULL_PATH_AUS_EN + "/health/fitness/article-1/jcr:content";

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new PageEventListener();
        setSlingRepositoryService();
        callActivateMethod();

        when(repository.loginAdministrative(null)).thenReturn(session);
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
    public void shouldProcessChangedEventThatAttributeContainsInputPublishDate() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceChangedAttributes", new String[] { ArticleConstants.INPUT_PUBLISH_DATE });
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessChangedEventThatAttributeContainsPublishDate() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceChangedAttributes", new String[] { ArticleConstants.PUBLISH_DATE });
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessChangedEventThatAttributeContainsJcrCreatedDate() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceChangedAttributes", new String[] { JcrConstants.JCR_CREATED });
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessAddedEventThatAttributeContainsInputPublishDate() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceAddedAttributes", new String[] { ArticleConstants.INPUT_PUBLISH_DATE });
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessAddedEventThatAttributeContainsPublishDate() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceAddedAttributes", new String[] { ArticleConstants.PUBLISH_DATE });
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessAddedEventThatAttributeContainsJcrCreatedDate() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceAddedAttributes", new String[] { JcrConstants.JCR_CREATED });
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessRemovedEventThatAttributeContainsInputPublishDate() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceRemovedAttributes", new String[] { ArticleConstants.INPUT_PUBLISH_DATE });
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessRemovedEventThatAttributeContainsPublishDate() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceRemovedAttributes", new String[] { ArticleConstants.PUBLISH_DATE });
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessRemovedEventThatAttributeContainsJcrCreatedDate() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, ArticleConstants.ARTICLE_RESOURCE_TYPE);
        properties.put("resourceRemovedAttributes", new String[] { JcrConstants.JCR_CREATED });
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessEventWhenInputPublishedDateIsNotPresentAndPublishedDateIsNotPresent() throws Exception {
        final Map<String, Object> properties = createProperties();
        final Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);
        final Property jcrCreatedProperty = createJcrCreatedProperty();

        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.hasProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(false);
        when(pageContentNode.getProperty(eq(JcrConstants.JCR_CREATED))).thenReturn(jcrCreatedProperty);

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(1)).setProperty(ArticleConstants.PUBLISH_DATE, jcrCreatedProperty.getDate());
        verify(session, times(1)).save();
        verify(session, times(1)).logout();
    }

    @Test
    public void shouldProcessEventWhenInputPublishedDateIsNotPresentAndPublishedDateIsDifferentFromCreatedDate() throws Exception {
        final Map<String, Object> properties = createProperties();
        final Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);
        final Property jcrCreatedProperty = createJcrCreatedProperty();
        final Property publishedDateProperty = createPublishedDateProperty();

        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.hasProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(false);
        when(pageContentNode.getProperty(eq(JcrConstants.JCR_CREATED))).thenReturn(jcrCreatedProperty);
        when(pageContentNode.hasProperty(eq(ArticleConstants.PUBLISH_DATE))).thenReturn(true);
        when(pageContentNode.getProperty(eq(ArticleConstants.PUBLISH_DATE))).thenReturn(publishedDateProperty);

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(1)).setProperty(ArticleConstants.PUBLISH_DATE, jcrCreatedProperty.getDate());
        verify(session, times(1)).save();
        verify(session, times(1)).logout();
    }

    @Test
    public void shouldProcessEventWhenInputPublishedDateIsNotPresentAndPublishedDateIsTheSameAsCreatedDate() throws Exception {
        final Map<String, Object> properties = createProperties();
        final Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);
        final Property jcrCreatedProperty = createJcrCreatedProperty();
        final Property publishedDateProperty = jcrCreatedProperty;

        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.hasProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(false);
        when(pageContentNode.getProperty(eq(JcrConstants.JCR_CREATED))).thenReturn(jcrCreatedProperty);
        when(pageContentNode.hasProperty(eq(ArticleConstants.PUBLISH_DATE))).thenReturn(true);
        when(pageContentNode.getProperty(eq(ArticleConstants.PUBLISH_DATE))).thenReturn(publishedDateProperty);

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(0)).setProperty(ArticleConstants.PUBLISH_DATE, jcrCreatedProperty.getDate());
        verify(session, times(0)).save();
        verify(session, times(1)).logout();
    }

    @Test
    public void shouldProcessEventWhenInputPublishedDateIsPresentAndPublishedDateIsNotPresent() throws Exception {
        final Map<String, Object> properties = createProperties();
        final Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);
        final Property inputPublishedDateProperty = createInputPublishedDateProperty();

        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.hasProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(true);
        when(pageContentNode.getProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(inputPublishedDateProperty);

        boolean result = classUnderTest.process(event);

        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(1)).setProperty(ArticleConstants.PUBLISH_DATE, inputPublishedDateProperty.getDate());
        verify(session, times(1)).save();
        verify(session, times(1)).logout();
    }

    @Test
    public void shouldProcessEventWhenInputPublishedDateIsPresentAndPublishedDateIsDifferentFrom() throws Exception {
        final Map<String, Object> properties = createProperties();
        final Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);
        final Property inputPublishedDateProperty = createInputPublishedDateProperty();
        final Property publishedDateProperty = createPublishedDateProperty();

        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.hasProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(true);
        when(pageContentNode.getProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(inputPublishedDateProperty);
        when(pageContentNode.hasProperty(eq(ArticleConstants.PUBLISH_DATE))).thenReturn(true);
        when(pageContentNode.getProperty(eq(ArticleConstants.PUBLISH_DATE))).thenReturn(publishedDateProperty);

        boolean result = classUnderTest.process(event);

        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(1)).setProperty(ArticleConstants.PUBLISH_DATE, inputPublishedDateProperty.getDate());
        verify(session, times(1)).save();
        verify(session, times(1)).logout();
    }

    @Test
    public void shouldProcessEventWhenInputPublishedDateIsPresentAndPublishedDateIsTheSame() throws Exception {
        final Map<String, Object> properties = createProperties();
        final Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);
        final Property inputPublishedDateProperty = createInputPublishedDateProperty();
        final Property publishedDateProperty = inputPublishedDateProperty;

        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.hasProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(true);
        when(pageContentNode.getProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(inputPublishedDateProperty);
        when(pageContentNode.hasProperty(eq(ArticleConstants.PUBLISH_DATE))).thenReturn(true);
        when(pageContentNode.getProperty(eq(ArticleConstants.PUBLISH_DATE))).thenReturn(publishedDateProperty);

        boolean result = classUnderTest.process(event);

        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(0)).setProperty(ArticleConstants.PUBLISH_DATE, inputPublishedDateProperty.getDate());
        verify(session, times(0)).save();
        verify(session, times(1)).logout();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRetryWhenUnexpectedExceptionOccurs() throws Exception {
        final Map<String, Object> properties = createProperties();
        final Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenThrow(PathNotFoundException.class);

        boolean result = classUnderTest.process(event);

        assertTrue(result);
        verify(repository, times(5)).loginAdministrative(null);
        verify(session, times(0)).save();
        verify(session, times(5)).logout();
    }

    @Test
    public void shouldNotSaveWhenPropertyIsNull() throws Exception {
        final Map<String, Object> properties = createProperties();
        final Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        final Property inputPublishedDateProperty = Mockito.mock(Property.class);
        when(inputPublishedDateProperty.getDate()).thenReturn(null);

        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.hasProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(true);
        when(pageContentNode.getProperty(eq(ArticleConstants.INPUT_PUBLISH_DATE))).thenReturn(inputPublishedDateProperty);

        boolean result = classUnderTest.process(event);

        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(0)).setProperty(ArticleConstants.PUBLISH_DATE, inputPublishedDateProperty.getDate());
        verify(session, times(0)).save();
        verify(session, times(1)).logout();
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
        final Method activateMethod = PageEventListener.class.getDeclaredMethod("activate", Map.class);
        activateMethod.setAccessible(true);
        activateMethod.invoke(classUnderTest, Collections.EMPTY_MAP);
    }

    private void callDeactivateMethod() throws Exception {
        final Method deactivateMethod = PageEventListener.class.getDeclaredMethod("deactivate");
        deactivateMethod.setAccessible(true);
        deactivateMethod.invoke(classUnderTest);
    }

    private Property createJcrCreatedProperty() throws Exception {
        final Calendar jcrCreated = new GregorianCalendar(2014, 8, 28);
        final Property jcrCreatedProperty = Mockito.mock(Property.class);
        when(jcrCreatedProperty.getDate()).thenReturn(jcrCreated);
        return jcrCreatedProperty;
    }

    private Property createInputPublishedDateProperty() throws Exception {
        final Calendar inputPublishedDate = new GregorianCalendar(2014, 3, 20);
        final Property inputPublishedDateProperty = Mockito.mock(Property.class);
        when(inputPublishedDateProperty.getDate()).thenReturn(inputPublishedDate);
        return inputPublishedDateProperty;
    }

    private Property createPublishedDateProperty() throws Exception {
        final Calendar publishedDate = new GregorianCalendar(2014, 5, 20);
        final Property publishedDateProperty = Mockito.mock(Property.class);
        when(publishedDateProperty.getDate()).thenReturn(publishedDate);
        return publishedDateProperty;
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
        final Field slingRepositoryField = FieldUtils.getField(PageEventListener.class, "repository", true);
        slingRepositoryField.setAccessible(true);
        slingRepositoryField.set(classUnderTest, repository);
    }

}
