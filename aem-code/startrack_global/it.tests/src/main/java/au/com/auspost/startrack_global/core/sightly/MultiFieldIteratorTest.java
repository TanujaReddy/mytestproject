package au.com.auspost.startrack_global.core.sightly;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PrepareForTest({LoggerFactory.class,MultiFieldIterator.class})
@RunWith(PowerMockRunner.class)
public class MultiFieldIteratorTest {

	private final String PROPERTY="property";
	private final String PROPERTIES_PROP= "prop1";
	private MultiFieldIterator multiFieldIterator;
	private ValueMap properties;
	private Logger logger;
	
	private final String JSON_STRING="{\"linkText\":\"Help\"}";
	
	@Before
	public void setupMock() throws Exception{
		multiFieldIterator = Mockito.mock(MultiFieldIterator.class);
		PowerMockito.mockStatic(LoggerFactory.class);
		logger =Mockito.mock(Logger.class);
		when(multiFieldIterator.get(PROPERTY, String.class)).thenReturn(PROPERTIES_PROP);
		properties = new ValueMapDecorator(new HashMap());
		when(multiFieldIterator.getProperties()).thenReturn(properties);
		when(LoggerFactory.getLogger(Mockito.any(Class.class))).thenReturn(logger);
		doCallRealMethod().when(multiFieldIterator).activate();
		doCallRealMethod().when(multiFieldIterator).getLinkListItems();
		doCallRealMethod().when(multiFieldIterator).setLinkListItems(Mockito.any(String[].class));
		doCallRealMethod().when(multiFieldIterator).processLinkListHashMap(Mockito.any(String[].class));
		doNothing().when(logger).info(Mockito.anyString());
	}
	
	@Test
	public void returnEmptyResult() throws Exception{
		multiFieldIterator.activate();
		Assert.assertNull(multiFieldIterator.getLinkListItems());
	}
	
	@Test
	public void returnNotEmptyResults() throws Exception{
		properties.put(PROPERTIES_PROP, JSON_STRING);
		multiFieldIterator.activate();
		Assert.assertNotNull(multiFieldIterator.getLinkListItems());
	}
	
}
