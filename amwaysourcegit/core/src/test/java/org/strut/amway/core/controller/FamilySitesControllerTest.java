package org.strut.amway.core.controller;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class FamilySitesControllerTest {

    private FamilySitesController familySitesController;
    @Mock
    private Node currentNode;
    @Mock
    private Property property;
    @Mock
    private Value value;

    @Before
    public void mockObject() {
        MockitoAnnotations.initMocks(this);

        familySitesController = new FamilySitesController();
    }

    @Test
    public void getContentValidData() throws RepositoryException {
        Mockito.doReturn(true).when(currentNode).hasProperty("map");
        Mockito.doReturn(property).when(currentNode).getProperty("map");
        Mockito.doReturn(value).when(property).getValue();
        Mockito.doReturn("{\"title\":\"google\",\"href\":\"http://google.com.vn\"}").when(value).getString();

        Map<String, String> content = familySitesController.getContent(currentNode);
        Assert.assertNotNull(content);

        Map<String, String> expectedResult = new HashMap<String, String>();
        expectedResult.put("google", "http://google.com.vn");

        Assert.assertEquals(expectedResult, content);

    }

    @Test
    public void getContentNullNodeValueEmpty() throws RepositoryException {

        Map<String, String> content = familySitesController.getContent(null);
        Assert.assertEquals(0, content.size());
    }

}
