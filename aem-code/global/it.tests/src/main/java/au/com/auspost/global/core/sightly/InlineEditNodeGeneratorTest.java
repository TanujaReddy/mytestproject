package au.com.auspost.global.core.sightly;

import static org.mockito.Mockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.when;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.day.cq.commons.jcr.JcrUtil;

import au.com.auspost.global.utils.Constants;

@PrepareForTest({JcrUtil.class})
@RunWith(PowerMockRunner.class)
public class InlineEditNodeGeneratorTest {
	
	private InlineEditNodeGenerator inlineNodegen;
	private Resource resource;
	private ResourceResolver resourceResolver;
	private Session session;
	private Node node;
	private JcrUtil jcrUtil;
	private final String NODE_CREATED ="/content/dam/abcd";
	private final String PARAM_NODE ="/content/dam/abcd";
	
	@Before
	public void setupMock() throws Exception{
		inlineNodegen = Mockito.mock(InlineEditNodeGenerator.class);
		resource = Mockito.mock(Resource.class);
		resourceResolver = Mockito.mock(ResourceResolver.class);
		node = Mockito.mock(Node.class);
		session = Mockito.mock(Session.class);
		PowerMockito.mockStatic(JcrUtil.class);
		jcrUtil = Mockito.mock(JcrUtil.class);
		doCallRealMethod().when(inlineNodegen).activate();
		doCallRealMethod().when(inlineNodegen).createJcrNode((Resource)Mockito.any());
		when(inlineNodegen.getResource()).thenReturn(resource);
		when(resource.getResourceResolver()).thenReturn(resourceResolver);
		when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
		when(inlineNodegen.get(Constants.NODE_TO_BE_CREATED, String.class)).thenReturn(NODE_CREATED);
		when(inlineNodegen.get(Constants.PARAM_NODE_TYPE, String.class)).thenReturn(PARAM_NODE);
		
	}
	
	@Test
	public void assertTrueCase() throws Exception{
		when(resource.adaptTo(Node.class)).thenReturn(node);
		when(node.hasNode(Mockito.anyString())).thenReturn(true);
		inlineNodegen.activate();
		Assert.assertTrue(inlineNodegen.createJcrNode(resource));
	}
	
	@Test(expected=RuntimeException.class)
	public void assertFalseCase() throws Exception{
		inlineNodegen.activate();
	}

}
