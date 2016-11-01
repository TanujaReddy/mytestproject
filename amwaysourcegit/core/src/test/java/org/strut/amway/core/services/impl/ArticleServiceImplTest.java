package org.strut.amway.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.sling.jcr.api.SlingRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.util.ArticleConstants;

import com.day.cq.commons.jcr.JcrConstants;

public class ArticleServiceImplTest {

    @Mock
    SlingRepository repository;

    @Mock
    Session session;

    @Mock
    Node root;

    @Mock
    Node content;

    @Mock
    Property property;

    ArticleServiceImpl articleServiceImp;

    String articleName;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);

        articleServiceImp = new ArticleServiceImpl();
        articleName = "article1";

        setSlingRepositoryService();
        when(repository.loginAdministrative(null)).thenReturn(session);
        when(session.getRootNode()).thenReturn(root);
    }

    private void setSlingRepositoryService() throws Exception {
        final Field slingRepositoryField = FieldUtils.getField(ArticleServiceImpl.class, "repository", true);
        slingRepositoryField.setAccessible(true);
        slingRepositoryField.set(articleServiceImp, repository);
    }

    @Test
    public void testGetLikeNumberByArticleNameDoesNotContain() throws RepositoryException {

        StringBuilder articlePath = new StringBuilder();
        articlePath.append(ArticleConstants.PATH_STORED_NODE).append(ArticleConstants.NODE_REGEX).append(articleName);
        when(root.hasNode(articlePath.toString())).thenReturn(false);

        int likeNumber = articleServiceImp.getLikeNumberByArticleName(articleName);
        assertEquals(0, likeNumber);
    }

    @Test
    public void testGetLikeNumberByArticleNameContainNodePropertiesIsNull() throws RepositoryException {

        StringBuilder articlePath = new StringBuilder();
        articlePath.append(ArticleConstants.PATH_STORED_NODE).append(ArticleConstants.NODE_REGEX).append(articleName);
        when(root.hasNode(articlePath.toString())).thenReturn(true);
        when(root.getNode(articlePath.toString())).thenReturn(content);
        when(content.getProperty(ArticleConstants.NUMBER_OF_LIKE)).thenReturn(property);
        when(property.getString()).thenReturn(null);

        int likeNumber = articleServiceImp.getLikeNumberByArticleName(articleName);
        assertEquals(0, likeNumber);
    }

    @Test
    public void testGetLikeNumberByArticleNameContainNode() throws RepositoryException {

        StringBuilder articlePath = new StringBuilder();
        articlePath.append(ArticleConstants.PATH_STORED_NODE).append(ArticleConstants.NODE_REGEX).append(articleName);
        when(root.hasNode(articlePath.toString())).thenReturn(true);
        when(root.getNode(articlePath.toString())).thenReturn(content);
        when(content.getProperty(ArticleConstants.NUMBER_OF_LIKE)).thenReturn(property);
        when(property.getString()).thenReturn("7");

        int likeNumber = articleServiceImp.getLikeNumberByArticleName(articleName);
        assertEquals(7, likeNumber);
    }

    @Test
    public void testUpdateLikeNumberByArticleNameChildIsNull() throws RepositoryException {
        String[] pathStoredNode = (ArticleConstants.PATH_STORED_NODE + ArticleConstants.NODE_REGEX + articleName).split(ArticleConstants.NODE_REGEX);
        when(session.getRootNode()).thenReturn(root);
        when(root.getNode(pathStoredNode[0])).thenReturn(null);

        assertEquals(-1, articleServiceImp.updateLikeNumberByArticleName(articleName));
    }

    @Test
    public void testUpdateLikeNumberByArticleNameChildIsNotNull() throws RepositoryException {
        String[] pathStoredNode = (ArticleConstants.PATH_STORED_NODE + ArticleConstants.NODE_REGEX + articleName).split(ArticleConstants.NODE_REGEX);
        when(session.getRootNode()).thenReturn(root);
        when(root.getNode(pathStoredNode[0])).thenReturn(content);
        when(content.hasNode(pathStoredNode[0])).thenReturn(true);
        when(content.getNode(pathStoredNode[0])).thenReturn(content);
        
        when(content.addNode(pathStoredNode[1], JcrConstants.NT_UNSTRUCTURED)).thenReturn(content);
        when(content.addNode(pathStoredNode[2], JcrConstants.NT_UNSTRUCTURED)).thenReturn(content);
        when(content.addNode(pathStoredNode[3], JcrConstants.NT_UNSTRUCTURED)).thenReturn(content);
        when(content.addNode(pathStoredNode[4], JcrConstants.NT_UNSTRUCTURED)).thenReturn(content);
        
        when(content.hasProperty(ArticleConstants.NUMBER_OF_LIKE)).thenReturn(true);
        when(content.getProperty(ArticleConstants.NUMBER_OF_LIKE)).thenReturn(property);
        
        StringBuilder articlePath = new StringBuilder();
        articlePath.append(ArticleConstants.PATH_STORED_NODE).append(ArticleConstants.NODE_REGEX).append(articleName);
        when(root.hasNode(articlePath.toString())).thenReturn(true);
        when(root.getNode(articlePath.toString())).thenReturn(content);
        when(content.getProperty(ArticleConstants.NUMBER_OF_LIKE)).thenReturn(property);
        when(property.getString()).thenReturn("7");
        

        assertEquals(7, articleServiceImp.updateLikeNumberByArticleName(articleName));
    }
}
