package org.strut.amway.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.util.ArticleConstants;

import com.day.cq.tagging.Tag;

public class ArticleQueryServiceImplTest {

    private ArticleQueryServiceImpl articleQueryServiceImpl;

    private ArticleSearchResult articleSearchResult;

    private Long OFFSET = 0L;
    private Long LIMIT = 10L;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Session session;

    private NodeIterator nodeIterator;

    @Mock
    private Query query;

    @Mock
    private QueryResult queryResult;

    @Mock
    private QueryManager queryManager;

    @Mock
    private Node node;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);

        articleQueryServiceImpl = new ArticleQueryServiceImpl();
        nodeIterator = new NodeIteratorAdapter(dummyData());

    }

    private void mockDataForTest() {
        try {
            when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
            when(session.getWorkspace()).thenReturn(mock(Workspace.class));

            when(session.getWorkspace().getQueryManager()).thenReturn(queryManager);

            when(queryManager.createQuery(anyString(), eq(Query.JCR_SQL2))).thenReturn(query);
            when(query.execute()).thenReturn(queryResult);

            when(node.getParent()).thenReturn(node);
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
    public void testSearchLastedArticleForHomePage() throws RepositoryException {
        mockDataForTest();
        when(queryResult.getNodes()).thenReturn(nodeIterator);

        articleSearchResult = articleQueryServiceImpl.search(resourceResolver, dummyLastedArticleForHomePage());

        assertNotNull(articleSearchResult);
        assertEquals(articleSearchResult.getPages().size(), nodeIterator.getSize());
    }

    @Test
    public void testSearchLastedArticleForSubCategoryPage() throws RepositoryException {
        mockDataForTest();
        when(queryResult.getNodes()).thenReturn(nodeIterator);

        articleSearchResult = articleQueryServiceImpl.search(resourceResolver, dummyLastedArticleForSubCategoryPage());

        assertNotNull(articleSearchResult);
        assertEquals(articleSearchResult.getPages().size(), nodeIterator.getSize());
    }

    @Test
    public void testSearchArticleByTag() throws RepositoryException {
        mockDataForTest();
        when(queryResult.getNodes()).thenReturn(nodeIterator);

        articleSearchResult = articleQueryServiceImpl.search(resourceResolver, dummySearchArticleByTag());

        assertNotNull(articleSearchResult);
    }

    @Test
    public void testSearchArticleByDate() throws RepositoryException {
        mockDataForTest();
        when(queryResult.getNodes()).thenReturn(nodeIterator);

        articleSearchResult = articleQueryServiceImpl.search(resourceResolver, dummySearchArticleByDate());

        assertNotNull(articleSearchResult);
        assertEquals(articleSearchResult.getPages().size(), nodeIterator.getSize());
    }

    @Test
    public void testSearchArticleByTitle() throws RepositoryException {
        mockDataForTest();
        when(queryResult.getNodes()).thenReturn(nodeIterator);

        articleSearchResult = articleQueryServiceImpl.search(resourceResolver, dummySearchArticleByTitle());

        assertNotNull(articleSearchResult);
        assertEquals(articleSearchResult.getPages().size(), nodeIterator.getSize());
    }

    @Test
    public void testSearchArticleWithSearchTypeIsNull() throws RepositoryException {
        mockDataForTest();

        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setStartDate("2014-10-10").setEndDate("2014-10-20").setOffset(OFFSET).setLimit(LIMIT).build();

        articleSearchResult = articleQueryServiceImpl.search(resourceResolver, articlesCriteria);

        assertNotNull(articleSearchResult);
        assertTrue(CollectionUtils.isEmpty(articleSearchResult.getPages()));
    }

    @Test
    public void testSearchArticleWithArticlesCriteriaIsNull() {

        articleSearchResult = articleQueryServiceImpl.search(resourceResolver, null);

        assertNotNull(articleSearchResult);
        assertTrue(CollectionUtils.isEmpty(articleSearchResult.getPages()));
    }

    @Test
    public void testSearchArticleWithOffsetAndLimitAreNull() {

        mockDataForTest();

        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_BY_SUB_CATEGORY).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setStartDate("2014-10-10").setEndDate("2014-10-20")
                        .setOffset(null).setLimit(null).build();

        articleSearchResult = articleQueryServiceImpl.search(resourceResolver, articlesCriteria);

        assertNotNull(articleSearchResult);
        assertTrue(CollectionUtils.isEmpty(articleSearchResult.getPages()));
    }

    private ArticlesCriteria dummyLastedArticleForHomePage() {
        return new ArticlesCriteria.Builder()
                .setSearchType(SearchType.SEARCH_BY_SUB_CATEGORY).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setStartDate("2014-10-10").setEndDate("2014-10-20")
                .setOffset(OFFSET).setLimit(LIMIT).build();
    }

    private ArticlesCriteria dummyLastedArticleForSubCategoryPage() {
        return new ArticlesCriteria.Builder()
                .setSearchType(SearchType.SEARCH_BY_SUB_CATEGORY).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/all").setStartDate("2014-10-10")
                .setEndDate("2014-10-20").setOffset(OFFSET).setLimit(LIMIT).build();
    }

    private ArticlesCriteria dummySearchArticleByTag() {
        return new ArticlesCriteria.Builder()
                .setSearchType(SearchType.SEARCH_TAG).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setTagIds(new ArrayList<String>()).setOffset(OFFSET).setLimit(LIMIT)
                .build();
    }

    private ArticlesCriteria dummySearchArticleByDate() {
        return new ArticlesCriteria.Builder()
                .setSearchType(SearchType.SEARCH_DATE_TIME).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setStartDate("2014-10-10")
                .setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL).setEndDate("2014-10-20").setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL)
                .setOffset(OFFSET).setLimit(LIMIT).build();
    }

    private ArticlesCriteria dummySearchArticleByTitle() {
        return new ArticlesCriteria.Builder()
                .setSearchType(SearchType.SEARCH_FULLTEXT).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setFullText("abc").setTagIds(new ArrayList<String>())
                .setOffset(OFFSET).setLimit(LIMIT).build();
    }
}
