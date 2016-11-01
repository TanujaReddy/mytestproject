package org.strut.amway.core.servlets.article;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
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
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.model.Account;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.services.ArticleQueryService;
import org.strut.amway.core.services.ArticleService;
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.services.TagService;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.AuthenticationConstants;
import org.strut.amway.core.util.CategoryConstants;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.core.stats.PageViewStatistics;

public class ArticleGridServletTest {
	
    ArticleGridServlet classUnderTest;

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

    ByteArrayOutputStream outputStream;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        createArticleGridServlet();

        outputStream = new ByteArrayOutputStream(1024);
        PrintWriter writer = new PrintWriter(outputStream);

        when(slingHttpServletRequest.getMethod()).thenReturn("GET");
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(slingHttpServletResponse.getWriter()).thenReturn(writer);
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
    }

    @Test
    public void shouldReturnJsonProperly() throws Exception {
        final Long impression = 20L;
        final Long offset = 4L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page healthBlog = createHealthBlogArticle();
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog, sportBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(String.valueOf(offset));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(String.valueOf(limit));

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        final String resourcePath = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        when(resource.getPath()).thenReturn(resourcePath);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog")).thenReturn(impression);

        Field field = ArticleGridServlet.class.getDeclaredField("articleStatisticsService");
        field.setAccessible(true);
        field.set(classUnderTest, articleStatisticsService);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(resourcePath, criteria.getPath());
        assertEquals(1, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(SearchType.SEARCH_BY_SUB_CATEGORY, criteria.getSearchType());
        assertEquals(
                "{\"offset\":"
                        + offset
                        + ",\"limit\":"
                        + limit
                        + ",\"articles\":[{\"title\":\"Health Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"healthCategory\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"impression\":20},"
                        + "{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\"}]}",
                outputStream.toString());

    }

    @Test
    public void shouldReturnProperArticleCriteriaWhenUserAlreadyLogin() throws Exception {
        final Long impression = 20L;
        final Long offset = 4L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page healthBlog = createHealthBlogArticle();
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog, sportBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(String.valueOf(offset));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(String.valueOf(limit));

        final Account loginedUser = new Account();
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        final String resourcePath = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        when(resource.getPath()).thenReturn(resourcePath);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(resourcePath, criteria.getPath());
        assertEquals(2, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(SearchType.SEARCH_BY_SUB_CATEGORY, criteria.getSearchType());
    }

    @Test
    public void shouldReturnProperArticleCriteriaWhenUserAlreadyLoginWithIBOEntityType() throws Exception {
        final Long impression = 20L;
        final Long offset = 4L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page healthBlog = createHealthBlogArticle();
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog, sportBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(String.valueOf(offset));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(String.valueOf(limit));

        final Account loginedUser = new Account();
        loginedUser.setEntityType(EntityType.IBO);
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        final String resourcePath = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        when(resource.getPath()).thenReturn(resourcePath);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(resourcePath, criteria.getPath());
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.IBO, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_BY_SUB_CATEGORY, criteria.getSearchType());
    }

    @Test
    public void shouldReturnProperArticleCriteriaWhenUserAlreadyLoginWithClientEntityType() throws Exception {
        final Long impression = 20L;
        final Long offset = 4L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page healthBlog = createHealthBlogArticle();
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog, sportBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(String.valueOf(offset));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(String.valueOf(limit));

        final Account loginedUser = new Account();
        loginedUser.setEntityType(EntityType.CLIENT);
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        final String resourcePath = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au";
        when(resource.getPath()).thenReturn(resourcePath);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(resourcePath, criteria.getPath());
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.CLIENT, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_BY_SUB_CATEGORY, criteria.getSearchType());
    }

    @Test
    public void shouldSetDefaultValueWhenOffsetValueIsInvalid() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(ArticleConstants.DEFAULT_OFFSET_VALUE);
        searchResult.setLimit(ArticleConstants.DEFAULT_LIMIT_VALUE);
        Page healthBlog = createHealthBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog));

        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn("invalid_offset");

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertTrue(outputStream.toString().contains("\"offset\":" + ArticleConstants.DEFAULT_OFFSET_VALUE));
    }

    @Test
    public void shouldSetDefaultValueWhenLimitValueIsInvalid() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(ArticleConstants.DEFAULT_OFFSET_VALUE);
        searchResult.setLimit(ArticleConstants.DEFAULT_LIMIT_VALUE);
        Page healthBlog = createHealthBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog));

        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn("invalid_limit");

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertTrue(outputStream.toString().contains("\"limit\":" + ArticleConstants.DEFAULT_LIMIT_VALUE));
    }

    @Test
    public void shouldReturn500WhenCategoryIsNull() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(ArticleConstants.DEFAULT_OFFSET_VALUE);
        searchResult.setLimit(ArticleConstants.DEFAULT_LIMIT_VALUE);
        Page healthBlog = createHealthBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog));

        when(healthBlog.getParent(2)).thenReturn(null);
        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalStateException: Category can not null\"}", outputStream.toString());
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldNotSetPublishedDateWhenItIsNotPresent() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(ArticleConstants.DEFAULT_OFFSET_VALUE);
        searchResult.setLimit(ArticleConstants.DEFAULT_LIMIT_VALUE);
        Page healthBlog = createHealthBlogArticle();
        searchResult.setPages(Arrays.asList(healthBlog));

        ValueMap healthBlogValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
            }
        });
        when(healthBlog.getProperties()).thenReturn(healthBlogValueMap);
        when(articleQueryService.search(any(ResourceResolver.class), any(ArticlesCriteria.class))).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertFalse(outputStream.toString().contains("\"lastPublishedDate\":\"03 Dec 2014\""));
    }

    @Test
    public void shouldDestroyProperly() throws Exception {
        createArticleGridServlet();
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
        when(resourceResolver.map(eq(path))).thenReturn(path);
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
        when(resourceResolver.map(eq(path))).thenReturn(path);
        return sportBlog;
    }

    private void createArticleGridServlet() throws Exception {
        classUnderTest = new ArticleGridServlet();
        setQueryService();
        setArticleStatisticsService();
        setTagService();
        classUnderTest.init();
    }

    private void setQueryService() throws Exception {
        final Field articleQueryServiceField = FieldUtils.getField(ArticleGridServlet.class, "articleQueryService", true);
        articleQueryServiceField.setAccessible(true);
        articleQueryServiceField.set(classUnderTest, articleQueryService);
    }

    private void setArticleStatisticsService() throws Exception {
        final Field articleStatisticsServiceField = FieldUtils.getField(ArticleGridServlet.class, "articleStatisticsService", true);
        articleStatisticsServiceField.setAccessible(true);
        articleStatisticsServiceField.set(classUnderTest, articleStatisticsService);
    }

    private void setTagService() throws Exception {
        final Field tagServiceField = FieldUtils.getField(ArticleGridServlet.class, "tagService", true);
        tagServiceField.setAccessible(true);
        tagServiceField.set(classUnderTest, tagService);
    }

}
