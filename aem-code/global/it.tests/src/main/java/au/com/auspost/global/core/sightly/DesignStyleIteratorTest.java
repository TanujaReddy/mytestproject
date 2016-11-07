package au.com.auspost.global.core.sightly;

import static org.mockito.Mockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.adobe.cq.sightly.WCMUse;
import com.day.cq.wcm.api.designer.Style;

@PrepareForTest(WCMUse.class)
@RunWith(PowerMockRunner.class)
public class DesignStyleIteratorTest {
	
	private DesignStyleIterator designStyleIterator;
	private final String PROPERTY="property";
	private final String PROPERTIES_PROP= "prop1";
	private Style style;
	
	@Before
	public void setupMock() throws Exception{
		designStyleIterator = Mockito.mock(DesignStyleIterator.class);
		style = Mockito.mock(Style.class);
		doCallRealMethod().when(designStyleIterator).activate();
		doCallRealMethod().when(designStyleIterator).getSocialHtml();
	}
	
	@Test
	public void returnEmptyResult() throws Exception{
		Assert.assertNull(designStyleIterator.getSocialHtml());
	}
	
	@Test
	public void returnNonEmptyResult() throws Exception{
		when(designStyleIterator.get(PROPERTY, String.class)).thenReturn(PROPERTIES_PROP);
		when(designStyleIterator.getCurrentStyle()).thenReturn(style);
		when(style.get(Mockito.anyString())).thenReturn(PROPERTIES_PROP);
		designStyleIterator.activate();
		Assert.assertNotNull(designStyleIterator.getSocialHtml());
	}

}
