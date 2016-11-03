package au.com.auspost.startrack_global.core.sightly;

import static org.mockito.Mockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.adobe.cq.sightly.WCMUse;

@PrepareForTest(WCMUse.class)
@RunWith(PowerMockRunner.class)
public class ColorMappingReaderTest {
	
	private ColorMappingReader colorMappingReader;
    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String TRANSPARENT_BACKGROUND = "transparentBg";
    private static final String BOOLEAN_TRUE = "true";
    private static final String TRANSPARENT_COLOR = "transparent";
    private ValueMap properties;
	
	@Before
	public void setupMock() throws Exception{
		colorMappingReader = Mockito.mock(ColorMappingReader.class);
		doCallRealMethod().when(colorMappingReader).activate();
		doCallRealMethod().when(colorMappingReader).getColorClass();
		doCallRealMethod().when(colorMappingReader).getColorMapping(Mockito.anyString(),Mockito.anyString());
		properties = new ValueMapDecorator(new HashMap());
		when(colorMappingReader.getProperties()).thenReturn(properties);
	}
	
	@Test
	public void returnDarkYellowColor() throws Exception{
		properties.put(BACKGROUND_COLOR, "rgba(234,176,42,1)");
		properties.put(TRANSPARENT_BACKGROUND, StringUtils.EMPTY);
		colorMappingReader.activate();
		Assert.assertEquals(colorMappingReader.getColorMapping("rgba(234,176,42,1)",StringUtils.EMPTY), "--dark-yellow");
	}
	
	@Test
	public void returnTransparentColor() throws Exception{
		properties.put(BACKGROUND_COLOR, "rgba(234,176,42,1)");
		properties.put(TRANSPARENT_BACKGROUND, StringUtils.EMPTY);
		colorMappingReader.activate();
		Assert.assertEquals(colorMappingReader.getColorMapping("rgba(234,176,42,1)",BOOLEAN_TRUE), TRANSPARENT_COLOR);
	}

}
