package org.strut.amway.core.servlets.article;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.EntityType;
import org.strut.amway.core.enumeration.OrderType;
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.model.Account;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.services.ArticleQueryService;
import org.strut.amway.core.services.ArticleService;
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.services.TagService;
import org.strut.amway.core.servlets.AbstractJsonServlet;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.AuthenticationConstants;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.PageUtils;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.core.stats.PageViewStatistics;

public class RelatedAndPopularArticleServletTest {
	
    RelatedAndPopularArticleServlet classUnderTest;

    @Mock
    ArticleQueryService articleQueryService;

    @Mock
    ArticleStatisticsService articleStatisticsService;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    @Mock
    TagService tagService;

    @Mock
    Resource resource;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Page currentPage;

    @Mock
    Tag tagOfCurrentPage;

    String tagOfCurrentPageId;

    String pathOfCurrentPage;

    @Mock
    Page homePage;

    @Mock
    HttpSession session;

    String pathOfHomePage;

    ByteArrayOutputStream outputStream;

    @SuppressWarnings("serial")
    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        createRelatedAndPopularArticleServlet();

        outputStream = new ByteArrayOutputStream(1024);
        PrintWriter writer = new PrintWriter(outputStream);

        pathOfCurrentPage = "/content/en/blog";
        pathOfHomePage = "/content/en";
        when(slingHttpServletRequest.getMethod()).thenReturn("GET");
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(slingHttpServletResponse.getWriter()).thenReturn(writer);
        when(resource.adaptTo(Page.class)).thenReturn(currentPage);
        when(currentPage.getPath()).thenReturn(pathOfCurrentPage);
        when(currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL)).thenReturn(homePage);
        when(currentPage.getTags()).thenReturn(new Tag[] { tagOfCurrentPage });
        tagOfCurrentPageId = "events";
        when(tagOfCurrentPage.getTagID()).thenReturn(tagOfCurrentPageId);
        when(homePage.getPath()).thenReturn(pathOfHomePage);

        ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put(ArticleConstants.RELATED_ARTICLE, null);
                put(ArticleConstants.POPULAR_ARTICLE, null);
            }
        });
        when(currentPage.getProperties()).thenReturn(pageValueMap);
        when(slingHttpServletRequest.getSession()).thenReturn(session);
    }

    @Test
    public void shouldBuildProperArticleCriteria() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(0L);
        searchResult.setLimit(Long.MAX_VALUE);
        final Page healthBlog = createHealthBlogArticle();
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog, sportBlog));

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(Long.MAX_VALUE), criteria.getLimit());
        assertEquals(pathOfHomePage, criteria.getPath());
        assertEquals(PageUtils.getContentPath(currentPage), criteria.getIgnorePath());
        assertEquals(tagOfCurrentPageId, criteria.getTagIds().get(0));
        assertEquals(1, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
    }

    @Test
    public void shouldBuildProperArticleCriteriaWhenUserAlreadyLogined() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(0L);
        searchResult.setLimit(Long.MAX_VALUE);
        final Page healthBlog = createHealthBlogArticle();
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog, sportBlog));

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final Account loginedUser = new Account();
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(Long.MAX_VALUE), criteria.getLimit());
        assertEquals(pathOfHomePage, criteria.getPath());
        assertEquals(PageUtils.getContentPath(currentPage), criteria.getIgnorePath());
        assertEquals(tagOfCurrentPageId, criteria.getTagIds().get(0));
        assertEquals(2, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
    }

    @Test
    public void shouldBuildProperArticleCriteriaWhenUserAlreadyLoginedWithIBOEntityType() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(0L);
        searchResult.setLimit(Long.MAX_VALUE);
        final Page healthBlog = createHealthBlogArticle();
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog, sportBlog));

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final Account loginedUser = new Account();
        loginedUser.setEntityType(EntityType.IBO);
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(Long.MAX_VALUE), criteria.getLimit());
        assertEquals(pathOfHomePage, criteria.getPath());
        assertEquals(PageUtils.getContentPath(currentPage), criteria.getIgnorePath());
        assertEquals(tagOfCurrentPageId, criteria.getTagIds().get(0));
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.IBO, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
    }

    @Test
    public void shouldBuildProperArticleCriteriaWhenUserAlreadyLoginedWithClientEntityType() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(0L);
        searchResult.setLimit(Long.MAX_VALUE);
        final Page healthBlog = createHealthBlogArticle();
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog, sportBlog));

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final Account loginedUser = new Account();
        loginedUser.setEntityType(EntityType.CLIENT);
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(Long.MAX_VALUE), criteria.getLimit());
        assertEquals(pathOfHomePage, criteria.getPath());
        assertEquals(PageUtils.getContentPath(currentPage), criteria.getIgnorePath());
        assertEquals(tagOfCurrentPageId, criteria.getTagIds().get(0));
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.CLIENT, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
    }

    @Test
    public void shouldReturnRelatedAndPopularArticleProperly() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(0L);
        searchResult.setLimit(Long.MAX_VALUE);
        final Page healthBlog = createHealthBlogArticle();
        final Long healthBlogImpression = 30L;
        final Page sportBlog = createSportBlogArticle();
        final Long sportBlogImpression = 500L;
        final Page eventsBlog = createEventsBlogArticle();
        final Long eventsBlogImpression = 1000L;
        searchResult.setPages(Arrays.asList(healthBlog, sportBlog, eventsBlog));

        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, healthBlog.getPath())).thenReturn(healthBlogImpression);
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, sportBlog.getPath())).thenReturn(sportBlogImpression);
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, eventsBlog.getPath())).thenReturn(eventsBlogImpression);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals(
                "{\"offset\":"
                        + 0L
                        + ",\"limit\":"
                        + Long.MAX_VALUE
                        + ",\"articles\":[{\"title\":\"Health Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"healthCategory\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"impression\":"
                        + healthBlogImpression
                        + "},"
                        + "{\"title\":\"Event Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"Event Category\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"impression\":"
                        + eventsBlogImpression + "}]}", outputStream.toString());
    }

    @Test
    public void shouldReturnEmptyCollectionWhenNoArticleForSpecialTag() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(0L);
        searchResult.setLimit(Long.MAX_VALUE);
        searchResult.setPages(new ArrayList<Page>());

        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"offset\":" + 0L + ",\"limit\":" + Long.MAX_VALUE + ",\"articles\":[]}", outputStream.toString());
    }

    @Test
    public void shouldReturnOneElementWhenHasOneArticleForSpecialTag() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(0L);
        searchResult.setLimit(Long.MAX_VALUE);
        final Page healthBlog = createHealthBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog));

        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals(
                "{\"offset\":"
                        + 0L
                        + ",\"limit\":"
                        + Long.MAX_VALUE
                        + ",\"articles\":[{\"title\":\"Health Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"healthCategory\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"impression\":0}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturnLatestPopularArticleWhenThereAreMoreSamePageView() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(0L);
        searchResult.setLimit(Long.MAX_VALUE);
        final Page healthBlog = createHealthBlogArticle();
        final Long healthBlogImpression = 30L;
        final Page eventsBlog = createEventsBlogArticle();
        final Long eventsBlogImpression = 500L;
        final Page sportBlog = createSportBlogArticle();
        final Long sportBlogImpression = 500L;
        searchResult.setPages(Arrays.asList(healthBlog, eventsBlog, sportBlog));

        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, healthBlog.getPath())).thenReturn(healthBlogImpression);
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, sportBlog.getPath())).thenReturn(sportBlogImpression);
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, eventsBlog.getPath())).thenReturn(eventsBlogImpression);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals(
                "{\"offset\":"
                        + 0L
                        + ",\"limit\":"
                        + Long.MAX_VALUE
                        + ",\"articles\":[{\"title\":\"Health Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"healthCategory\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"impression\":"
                        + healthBlogImpression
                        + "},"
                        + "{\"title\":\"Event Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"Event Category\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"impression\":"
                        + eventsBlogImpression + "}]}", outputStream.toString());
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldReturnRelatedAndPopularArticleSetByUser() throws Exception {
        final Page healthBlog = createHealthBlogArticle();
        final Page eventsBlog = createEventsBlogArticle();
        ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put(ArticleConstants.RELATED_ARTICLE, healthBlog.getPath());
                put(ArticleConstants.POPULAR_ARTICLE, eventsBlog.getPath());
            }
        });
        Resource healthBlogResource = Mockito.mock(Resource.class);
        Resource eventsBlogResource = Mockito.mock(Resource.class);
        when(currentPage.getProperties()).thenReturn(pageValueMap);
        when(resourceResolver.getResource(eq(healthBlog.getPath()))).thenReturn(healthBlogResource);
        when(healthBlogResource.adaptTo(Page.class)).thenReturn(healthBlog);
        when(resourceResolver.getResource(eq(eventsBlog.getPath()))).thenReturn(eventsBlogResource);
        when(eventsBlogResource.adaptTo(Page.class)).thenReturn(eventsBlog);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals(
                "{\"offset\":"
                        + 0L
                        + ",\"limit\":"
                        + Long.MAX_VALUE
                        + ",\"articles\":[{\"title\":\"Health Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"healthCategory\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"impression\":0},"
                        + "{\"title\":\"Event Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"Event Category\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"impression\":0}]}",
                outputStream.toString());
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldReturnRelatedArticleSetByUser() throws Exception {
        final Page healthBlog = createHealthBlogArticle();
        final Page eventsBlog = createEventsBlogArticle();
        ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put(ArticleConstants.RELATED_ARTICLE, healthBlog.getPath());
                put(ArticleConstants.POPULAR_ARTICLE, null);
            }
        });
        Resource healthBlogResource = Mockito.mock(Resource.class);
        when(currentPage.getProperties()).thenReturn(pageValueMap);
        when(resourceResolver.getResource(eq(healthBlog.getPath()))).thenReturn(healthBlogResource);
        when(healthBlogResource.adaptTo(Page.class)).thenReturn(healthBlog);

        ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setPages(Arrays.asList(eventsBlog));
        Long eventsBlogImpression = 100L;
        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, eventsBlog.getPath())).thenReturn(eventsBlogImpression);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals(
                "{\"offset\":"
                        + 0L
                        + ",\"limit\":"
                        + Long.MAX_VALUE
                        + ",\"articles\":[{\"title\":\"Health Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"healthCategory\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"impression\":0},"
                        + "{\"title\":\"Event Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"Event Category\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"impression\":"
                        + eventsBlogImpression + "}]}", outputStream.toString());
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldReturnPopularArticleSetByUser() throws Exception {
        final Page healthBlog = createHealthBlogArticle();
        final Page eventsBlog = createEventsBlogArticle();
        ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put(ArticleConstants.RELATED_ARTICLE, null);
                put(ArticleConstants.POPULAR_ARTICLE, eventsBlog.getPath());
            }
        });
        Resource eventsBlogResource = Mockito.mock(Resource.class);
        when(currentPage.getProperties()).thenReturn(pageValueMap);
        when(resourceResolver.getResource(eq(eventsBlog.getPath()))).thenReturn(eventsBlogResource);
        when(eventsBlogResource.adaptTo(Page.class)).thenReturn(eventsBlog);

        ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setPages(Arrays.asList(healthBlog));
        Long healthBlogImpression = 100L;
        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, healthBlog.getPath())).thenReturn(healthBlogImpression);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals(
                "{\"offset\":"
                        + 0L
                        + ",\"limit\":"
                        + Long.MAX_VALUE
                        + ",\"articles\":[{\"title\":\"Health Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"healthCategory\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"impression\":"
                        + healthBlogImpression
                        + "},"
                        + "{\"title\":\"Event Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"Event Category\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"impression\":0}]}",
                outputStream.toString());
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldNotReturnIdenticalPage() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(0L);
        searchResult.setLimit(Long.MAX_VALUE);
        final Page eventsBlog = createEventsBlogArticle();
        final Long eventsBlogImpression = 500L;
        final Page sportBlog = createSportBlogArticle();
        final Page healthBlog = createHealthBlogArticle();
        searchResult.setPages(Arrays.asList(eventsBlog, sportBlog, healthBlog));

        ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put(ArticleConstants.RELATED_ARTICLE, null);
                put(ArticleConstants.POPULAR_ARTICLE, eventsBlog.getPath());
            }
        });
        Resource eventsBlogResource = Mockito.mock(Resource.class);
        when(currentPage.getProperties()).thenReturn(pageValueMap);
        when(resourceResolver.getResource(eq(eventsBlog.getPath()))).thenReturn(eventsBlogResource);
        when(eventsBlogResource.adaptTo(Page.class)).thenReturn(eventsBlog);
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, eventsBlog.getPath())).thenReturn(eventsBlogImpression);
        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals(
                "{\"offset\":"
                        + 0L
                        + ",\"limit\":"
                        + Long.MAX_VALUE
                        + ",\"articles\":[{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\"},"
                        + "{\"title\":\"Event Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"Event Category\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog.html\",\"impression\":"
                        + eventsBlogImpression + "}]}", outputStream.toString());
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldNotReturnRelatedAndPopularArticleSetByUserWhenBothHaveNoPermission() throws Exception {
        final Page eventsBlog = createEventsBlogArticle();
        final Page sportBlog = createSportBlogArticle();
        eventsBlog.getProperties().put(ArticleConstants.LABEL_PROPERTY, new String[] { ArticleLabelType.IBO.getLabel() });
        sportBlog.getProperties().put(ArticleConstants.LABEL_PROPERTY, new String[] { ArticleLabelType.CLIENT.getLabel() });
        ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put(ArticleConstants.RELATED_ARTICLE, sportBlog.getPath());
                put(ArticleConstants.POPULAR_ARTICLE, eventsBlog.getPath());
            }
        });
        Resource eventsBlogResource = Mockito.mock(Resource.class);
        Resource sportBlogResource = Mockito.mock(Resource.class);
        when(currentPage.getProperties()).thenReturn(pageValueMap);
        when(resourceResolver.getResource(eq(eventsBlog.getPath()))).thenReturn(eventsBlogResource);
        when(eventsBlogResource.adaptTo(Page.class)).thenReturn(eventsBlog);
        when(resourceResolver.getResource(eq(sportBlog.getPath()))).thenReturn(sportBlogResource);
        when(sportBlogResource.adaptTo(Page.class)).thenReturn(sportBlog);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"offset\":" + 0L + ",\"limit\":" + Long.MAX_VALUE + ",\"articles\":[]}", outputStream.toString());
    }

    @Test
    public void shouldDestroyProperly() throws Exception {
        createRelatedAndPopularArticleServlet();
        classUnderTest.destroy();
    }

    @SuppressWarnings("serial")
    private Page createHealthBlogArticle() {
        Page healthBlog = Mockito.mock(Page.class);
        final String path = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog";
        final String imgResourcePath = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.img.png";
        Resource healthBlogImgResource = Mockito.mock(Resource.class);
        ValueMap healthBlogValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                // 2014-12-03 09:06:07
                final GregorianCalendar calendar = new GregorianCalendar(2014, 11, 3, 9, 6, 7);
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
                put(ArticleConstants.JCR_CREATED, calendar);
                put(ArticleConstants.PUBLISH_DATE, calendar);
                put(ArticleConstants.LABEL_PROPERTY, new String[] { ArticleLabelType.PUBLIC.getLabel() });
            }
        });
        when(healthBlog.getTitle()).thenReturn("Health Blog");
        when(healthBlog.getPath()).thenReturn(path);
        when(healthBlogImgResource.getPath()).thenReturn(imgResourcePath);
        when(healthBlog.getContentResource(eq("image"))).thenReturn(healthBlogImgResource);
        when(healthBlog.getProperties()).thenReturn(healthBlogValueMap);

        Page category = Mockito.mock(Page.class);
        ValueMap categoryValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("categoryType", CategoryConstants.MAIN_BLOGGING_CATEGORY);
            }
        });
        when(category.getTitle()).thenReturn("healthCategory");
        when(category.getPath()).thenReturn(path);
        when(category.getProperties()).thenReturn(new ValueMapDecorator(categoryValueMap));
        when(healthBlog.getParent(2)).thenReturn(category);
        return healthBlog;
    }

    @SuppressWarnings("serial")
    private Page createSportBlogArticle() {
        Page sportBlog = Mockito.mock(Page.class);
        final String path = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog";
        Resource sportBlogImgResource = Mockito.mock(Resource.class);
        ValueMap sportBlogValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                // 2014-12-03 09:06:07
                final GregorianCalendar calendar = new GregorianCalendar(2014, 11, 3, 9, 6, 7);
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
                put(ArticleConstants.JCR_CREATED, calendar);
                put(ArticleConstants.PUBLISH_DATE, calendar);
                put(ArticleConstants.LABEL_PROPERTY, new String[] { ArticleLabelType.PUBLIC.getLabel() });
            }
        });
        when(sportBlog.getTitle()).thenReturn("Sport Blog");
        when(sportBlog.getPath()).thenReturn(path);
        when(sportBlog.getContentResource(eq("image"))).thenReturn(sportBlogImgResource);
        when(sportBlog.getProperties()).thenReturn(sportBlogValueMap);

        Page category = Mockito.mock(Page.class);
        ValueMap categoryValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("categoryType", CategoryConstants.CORPORATE_CATEGORY);
            }
        });
        when(category.getTitle()).thenReturn("SportCategory");
        when(category.getPath()).thenReturn(path);
        when(category.getProperties()).thenReturn(new ValueMapDecorator(categoryValueMap));
        when(sportBlog.getParent(2)).thenReturn(category);
        return sportBlog;
    }

    @SuppressWarnings("serial")
    private Page createEventsBlogArticle() {
        Page eventBlog = Mockito.mock(Page.class);
        final String path = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/event/event-1/event-blog";
        Resource eventBlogImgResource = Mockito.mock(Resource.class);
        ValueMap eventBlogValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                // 2014-12-03 09:06:07
                final GregorianCalendar calendar = new GregorianCalendar(2014, 11, 3, 9, 6, 7);
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
                put(ArticleConstants.JCR_CREATED, calendar);
                put(ArticleConstants.PUBLISH_DATE, calendar);
                put(ArticleConstants.LABEL_PROPERTY, new String[] { ArticleLabelType.PUBLIC.getLabel() });
            }
        });
        when(eventBlog.getTitle()).thenReturn("Event Blog");
        when(eventBlog.getPath()).thenReturn(path);
        when(eventBlog.getContentResource(eq("image"))).thenReturn(eventBlogImgResource);
        when(eventBlog.getProperties()).thenReturn(eventBlogValueMap);

        Page category = Mockito.mock(Page.class);
        ValueMap categoryValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("categoryType", CategoryConstants.MAIN_BLOGGING_CATEGORY);
            }
        });
        when(category.getTitle()).thenReturn("Event Category");
        when(category.getPath()).thenReturn(path);
        when(category.getProperties()).thenReturn(new ValueMapDecorator(categoryValueMap));
        when(eventBlog.getParent(2)).thenReturn(category);
        return eventBlog;
    }

    private void createRelatedAndPopularArticleServlet() throws Exception {
        classUnderTest = new RelatedAndPopularArticleServlet();
        setQueryService();
        setTagService();
        setArticleStatisticsService();
        classUnderTest.init();
    }

    private void setQueryService() throws Exception {
        final Field articleQueryServiceField = FieldUtils.getField(RelatedAndPopularArticleServlet.class, "articleQueryService", true);
        articleQueryServiceField.setAccessible(true);
        articleQueryServiceField.set(classUnderTest, articleQueryService);
    }

    private void setArticleStatisticsService() throws Exception {
        final Field articleStatisticsServiceField = FieldUtils.getField(RelatedAndPopularArticleServlet.class, "articleStatisticsService", true);
        articleStatisticsServiceField.setAccessible(true);
        articleStatisticsServiceField.set(classUnderTest, articleStatisticsService);
    }

    private void setTagService() throws Exception {
        final Field tagServiceField = FieldUtils.getField(RelatedAndPopularArticleServlet.class, "tagService", true);
        tagServiceField.setAccessible(true);
        tagServiceField.set(classUnderTest, tagService);
    }

}
