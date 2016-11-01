package au.com.auspost.global.core.sightly;

import static org.mockito.Mockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.when;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.adobe.cq.sightly.WCMUse;

import au.com.auspost.global.utils.Constants;

@PrepareForTest(WCMUse.class)
@RunWith(PowerMockRunner.class)
public class AnalyticsTest {
	
	private final String FINAL_STRING="Some Random String";
	private Analytics accordion;
	
	@Before
	public void setupMock() throws Exception{
		accordion = Mockito.mock(Analytics.class);
		doCallRealMethod().when(accordion).activate();
		doCallRealMethod().when(accordion).getFormattedNodePath(Mockito.anyString());
		doCallRealMethod().when(accordion).getFormattedNodePath();
	}
	
	@Test
	public void returnEmptyResult() throws Exception{
		Analytics accordion = new Analytics();
		accordion.getFormattedNodePath(StringUtils.EMPTY);
		Assert.assertNull(accordion.getFormattedNodePath());
	}
	
	@Test
	public void returnNotEmptyResults() throws Exception{
		when(accordion.get(Constants.CURRENT_NODE_PATH, String.class)).thenReturn(FINAL_STRING);
		accordion.activate();
		Assert.assertNotNull(accordion.getFormattedNodePath());
	}
	
}
