package au.com.auspost.global.core.sightly;

import static org.mockito.Mockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.when;

import javax.jcr.Node;
import javax.jcr.Property;

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
public class CardTest {
	
	private Card card;
	private Resource resource;
	private Node node;
	private Resource parent;
	private Resource grandParent;
	private ResourceResolver resourceResolver;
	private Property property;
	
	@Before
	public void setupMock() throws Exception{
		card = Mockito.mock(Card.class);
		resource = Mockito.mock(Resource.class);
		parent = Mockito.mock(Resource.class);
		grandParent = Mockito.mock(Resource.class);
		resourceResolver = Mockito.mock(ResourceResolver.class);
		node = Mockito.mock(Node.class);
		property = Mockito.mock(Property.class);
		when(card.getResource()).thenReturn(resource);
		when(card.getResourceResolver()).thenReturn(resourceResolver);
		when(resource.getParent()).thenReturn(parent);
		when(parent.getParent()).thenReturn(grandParent);
		when(resource.adaptTo(Node.class)).thenReturn(node);
		when(node.hasProperty(Constants.IMAGE_PATH_REF)).thenReturn(true);
		when(node.getProperty(Constants.IMAGE_PATH_REF)).thenReturn(property);
		when(property.getString()).thenReturn("/path/to/property.jpg");
		doCallRealMethod().when(card).activate();
	}
	
	@Test
	public void assertNullImage() throws Exception{
		card.activate();
		Assert.assertNull(card.getImageScrForCard());
	}
	
}
