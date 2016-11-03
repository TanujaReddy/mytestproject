package au.com.auspost.global.core.sightly;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.when;

import javax.jcr.Node;
import javax.jcr.Property;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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
public class StripTagsRTETest {
	private final String FINAL_STRING="node1,node2";

	private StripTagsRTE stripTagsRTE;
	private Resource resource;
	private Node currentNode;
	private Node subnode;
	private Property property;

	@Before
	public void setupMock() throws Exception{
		stripTagsRTE = Mockito.mock(StripTagsRTE.class);
		resource = Mockito.mock(Resource.class);
		currentNode = Mockito.mock(Node.class);
		subnode = Mockito.mock(Node.class);

		property = Mockito.mock(Property.class);
		when(stripTagsRTE.get("subNode", String.class)).thenReturn(FINAL_STRING);
		when(stripTagsRTE.getResource()).thenReturn(resource);
		when(resource.adaptTo(Node.class)).thenReturn(currentNode);
		when(currentNode.getNode("node1")).thenReturn(subnode);
		when(subnode.hasProperty(Constants.TEXT)).thenReturn(true);
		when(subnode.getProperty(Constants.TEXT)).thenReturn(property);
		when(property.getString()).thenReturn("<h2></h2>");
		doCallRealMethod().when(stripTagsRTE).activate();
		doCallRealMethod().when(stripTagsRTE).getRteValue();
	}

	@Test
	public void returnEmptyResult() throws Exception{
		StripTagsRTE stripTagsRTE = new StripTagsRTE();
		stripTagsRTE.getRteValue();
		Assert.assertNull(stripTagsRTE.getRteValue());
	}

	@Test
	public void returnNotEmptyResults() throws Exception{
		stripTagsRTE.activate();
		Assert.assertNotNull(stripTagsRTE.getRteValue());
	}
}
