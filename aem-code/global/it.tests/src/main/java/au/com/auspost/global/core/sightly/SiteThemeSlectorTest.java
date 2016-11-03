package au.com.auspost.global.core.sightly;

import com.adobe.cq.sightly.WCMUse;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
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

import java.util.HashMap;

import static org.mockito.Mockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(WCMUse.class)
@RunWith(PowerMockRunner.class)
public class SiteThemeSlectorTest {

	public static final String ABSOLUTE_PARENT="auspost_campaign";
	public static final String THEME="theme-campaign";

	private SiteThemeSelector siteThemeSelector;
	private Page page;
	private Page parent;
	
	@Before
	public void setupMock() throws Exception{
		siteThemeSelector = Mockito.mock(SiteThemeSelector.class);
		page = Mockito.mock(Page.class);
		parent = Mockito.mock(Page.class);
		doCallRealMethod().when(siteThemeSelector).activate();
		doCallRealMethod().when(siteThemeSelector).getTheme();
		doCallRealMethod().when(siteThemeSelector).getThemeClass(Mockito.anyString());
		when(siteThemeSelector.getCurrentPage()).thenReturn(page);
		when(page.getAbsoluteParent(1)).thenReturn(parent);
		when(parent.getName()).thenReturn(ABSOLUTE_PARENT);
	}
	
	@Test
	public void returnAuspostCamapignTheme() throws Exception{
		siteThemeSelector.activate();
		Assert.assertEquals(siteThemeSelector.getThemeClass(ABSOLUTE_PARENT), THEME);
	}
	
	@Test
	public void returnEmpty() throws Exception{
		siteThemeSelector.activate();
		Assert.assertEquals(siteThemeSelector.getThemeClass("auspost-campaign"), StringUtils.EMPTY);
	}

}
