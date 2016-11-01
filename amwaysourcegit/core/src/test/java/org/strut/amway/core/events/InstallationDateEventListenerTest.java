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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
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
import org.strut.amway.core.util.Constants;

import com.day.cq.commons.jcr.JcrConstants;
import org.strut.amway.core.util.ContentConstants;

public class InstallationDateEventListenerTest {

    InstallationDateEventListener classUnderTest;

    @Mock
    SlingRepository repository;

    @Mock
    SlingSettingsService slingSettingsService;

    @Mock
    JackrabbitSession session;

    @Mock
    UserManager userManager;

    String path = ContentConstants.CONTENT_FULL_PATH_AUS_EN + "/jcr:content";

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new InstallationDateEventListener();
        setSlingRepositoryService();
        setSlingSettingsService();
        callActivateMethod();

        final Set<String> runModes = new HashSet<String>();
        runModes.add("author");
        when(repository.loginAdministrative(null)).thenReturn(session);
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
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
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
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
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
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
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
        properties.put("resourceAddedAttributes", new String[] { "jcr:title" });
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
        properties.put("resourceChangedAttributes", new String[] { "jcr:title" });
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
        properties.put("resourceRemovedAttributes", new String[] { "jcr:title" });
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
        properties.put("resourceAddedAttributes", new String[] { "jcr:title" });
        properties.put("resourceChangedAttributes", new String[] { "jcr:title" });
        properties.put("resourceRemovedAttributes", new String[] { "jcr:title" });
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_CHANGED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);

        properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
        event = createPageEvent(SlingConstants.TOPIC_RESOURCE_REMOVED, properties);
        result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);
    }

    @Test
    public void shouldNotProcessEventWhenEnvironmentIsPublish() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        final Set<String> runModes = new HashSet<String>();
        runModes.add("publish");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(0)).loginAdministrative(null);
    }

    @Test
    public void shouldProcessAddedEventWhenInstallationFieldIsNotPresent() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        final Property jcrCreatedProperty = createJcrCreatedProperty();

        Node pageContentParent = Mockito.mock(Node.class);
        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.getParent()).thenReturn(pageContentParent);
        when(pageContentNode.hasProperty(eq(Constants.INSTALLATION_DATE_PROPERTY))).thenReturn(false);
        when(pageContentParent.getProperty(eq(JcrConstants.JCR_CREATED))).thenReturn(jcrCreatedProperty);

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(1)).setProperty(Constants.INSTALLATION_DATE_PROPERTY, jcrCreatedProperty.getDate());
        verify(session, times(1)).save();
        verify(session, times(1)).logout();
    }

    @Test
    public void shouldNotSaveInstallationDateFieldWhenItIsPresent() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        final Property jcrCreatedProperty = createJcrCreatedProperty();

        Node pageContentParent = Mockito.mock(Node.class);
        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.getParent()).thenReturn(pageContentParent);
        when(pageContentNode.hasProperty(eq(Constants.INSTALLATION_DATE_PROPERTY))).thenReturn(true);
        when(pageContentParent.getProperty(eq(JcrConstants.JCR_CREATED))).thenReturn(jcrCreatedProperty);

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(1)).loginAdministrative(null);
        verify(pageContentNode, times(0)).setProperty(Constants.INSTALLATION_DATE_PROPERTY, jcrCreatedProperty.getDate());
        verify(session, times(0)).save();
        verify(session, times(1)).logout();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRetryWhenExceptionOccurs() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.HOME_PAGE_RESOURCE_TYPE);
        Event event = createPageEvent(SlingConstants.TOPIC_RESOURCE_ADDED, properties);

        final Property jcrCreatedProperty = createJcrCreatedProperty();

        Node pageContentParent = Mockito.mock(Node.class);
        Node pageContentNode = Mockito.mock(Node.class);
        when(session.nodeExists(eq(path))).thenReturn(true);
        when(session.getNode(eq(path))).thenReturn(pageContentNode);
        when(pageContentNode.getParent()).thenReturn(pageContentParent);
        when(pageContentNode.hasProperty(eq(Constants.INSTALLATION_DATE_PROPERTY))).thenReturn(false);
        when(pageContentParent.getProperty(eq(JcrConstants.JCR_CREATED))).thenReturn(jcrCreatedProperty);
        when(pageContentNode.setProperty(Constants.INSTALLATION_DATE_PROPERTY, jcrCreatedProperty.getDate())).thenThrow(RuntimeException.class);

        boolean result = classUnderTest.process(event);
        assertTrue(result);
        verify(repository, times(5)).loginAdministrative(null);
        verify(pageContentNode, times(5)).setProperty(Constants.INSTALLATION_DATE_PROPERTY, jcrCreatedProperty.getDate());
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
        final Method activateMethod = InstallationDateEventListener.class.getDeclaredMethod("activate", Map.class);
        activateMethod.setAccessible(true);
        activateMethod.invoke(classUnderTest, Collections.EMPTY_MAP);
    }

    private void callDeactivateMethod() throws Exception {
        final Method deactivateMethod = InstallationDateEventListener.class.getDeclaredMethod("deactivate");
        deactivateMethod.setAccessible(true);
        deactivateMethod.invoke(classUnderTest);
    }

    private Property createJcrCreatedProperty() throws Exception {
        final Calendar jcrCreated = new GregorianCalendar(2014, 8, 28);
        final Property jcrCreatedProperty = Mockito.mock(Property.class);
        when(jcrCreatedProperty.getDate()).thenReturn(jcrCreated);
        return jcrCreatedProperty;
    }

    private Map<String, Object> createProperties() {
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(SlingConstants.PROPERTY_PATH, path);
        properties.put(SlingConstants.PROPERTY_RESOURCE_TYPE, Constants.INSTALLATION_DATE_PROPERTY);
        return properties;
    }

    private Event createPageEvent(final String topic, Map<String, Object> properties) {
        return new Event(topic, properties);
    }

    private void setSlingRepositoryService() throws Exception {
        final Field slingRepositoryField = FieldUtils.getField(InstallationDateEventListener.class, "repository", true);
        slingRepositoryField.setAccessible(true);
        slingRepositoryField.set(classUnderTest, repository);
    }

    private void setSlingSettingsService() throws Exception {
        final Field slingSettingsServiceField = FieldUtils.getField(InstallationDateEventListener.class, "slingSettingsService", true);
        slingSettingsServiceField.setAccessible(true);
        slingSettingsServiceField.set(classUnderTest, slingSettingsService);
    }

}
