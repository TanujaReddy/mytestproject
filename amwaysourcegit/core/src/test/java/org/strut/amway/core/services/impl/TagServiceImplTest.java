package org.strut.amway.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.collections.CollectionUtils;
import org.apache.jackrabbit.commons.iterator.NodeIteratorAdapter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.model.TagCriteria;

import com.day.cq.tagging.Tag;

public class TagServiceImplTest {

    private TagServiceImpl tagServiceImpl;
    private NodeIterator nodeIterator;
    private List<Tag> tags;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Session session;

    @Mock
    private Query query;

    @Mock
    private QueryResult queryResult;

    @Mock
    private QueryManager queryManager;

    @Mock
    private Node node;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        tagServiceImpl = new TagServiceImpl();
        nodeIterator = new NodeIteratorAdapter(dummyData());

    }

    private void mockDataForTest() {
        try {
            when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
            when(session.getWorkspace()).thenReturn(mock(Workspace.class));

            when(session.getWorkspace().getQueryManager()).thenReturn(queryManager);

            when(queryManager.createQuery(anyString(), eq(Query.JCR_SQL2))).thenReturn(query);
            when(query.execute()).thenReturn(queryResult);

            when(node.getPath()).thenReturn("");

            when(resourceResolver.getResource(anyString())).thenReturn(mock(Resource.class));
            when(resourceResolver.getResource(anyString()).adaptTo(Tag.class)).thenReturn(mock(Tag.class));
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private List<Node> dummyData() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(node);
        nodes.add(node);
        nodes.add(node);
        return nodes;
    }

    @Test
    public void testSearchTagWithExactMatch() throws RepositoryException {

        mockDataForTest();
        when(queryResult.getNodes()).thenReturn(nodeIterator);

        TagCriteria tagCriteria = new TagCriteria.Builder().setTitle("title").setExactMatch(true).build();
        tags = tagServiceImpl.search(resourceResolver, tagCriteria);

        assertEquals(nodeIterator.getSize(), tags.size());
    }

    @Test
    public void testSearchTagWithPartialMatch() throws RepositoryException {
        mockDataForTest();
        when(queryResult.getNodes()).thenReturn(nodeIterator);

        TagCriteria tagCriteria = new TagCriteria.Builder().setTitle("title").setExactMatch(false).build();
        tags = tagServiceImpl.search(resourceResolver, tagCriteria);

        assertEquals(nodeIterator.getSize(), tags.size());
    }

    @Test
    public void testSearchTagWithNodesIsEmpty() throws RepositoryException {
        mockDataForTest();
        when(queryResult.getNodes()).thenReturn(nodeIterator);

        nodeIterator = new NodeIteratorAdapter(new ArrayList<>());
        when(queryResult.getNodes()).thenReturn(nodeIterator);

        TagCriteria tagCriteria = new TagCriteria.Builder().setTitle("title").setExactMatch(true).build();
        tags = tagServiceImpl.search(resourceResolver, tagCriteria);

        assertTrue(CollectionUtils.isEmpty(tags));
    }

    @Test
    public void testSearchTagWithNodesIsNull() throws RepositoryException {
        mockDataForTest();

        TagCriteria tagCriteria = new TagCriteria.Builder().setTitle("title").setExactMatch(true).build();
        tags = tagServiceImpl.search(resourceResolver, tagCriteria);

        assertTrue(CollectionUtils.isEmpty(tags));
    }

    @Test
    public void testSearchTagWithTagCriteriaIsEmpty() {
        mockDataForTest();

        TagCriteria tagCriteria = new TagCriteria.Builder().build();

        tags = tagServiceImpl.search(resourceResolver, tagCriteria);

        assertTrue(CollectionUtils.isEmpty(tags));
    }

    @Test
    public void testSearchTagWithTagCriteriaAndResourceResolverIsNull() {
        tags = tagServiceImpl.search(null, null);

        assertTrue(CollectionUtils.isEmpty(tags));
    }

}
