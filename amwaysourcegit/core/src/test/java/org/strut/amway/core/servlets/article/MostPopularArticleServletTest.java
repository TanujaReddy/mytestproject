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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
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
import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.EntityType;
import org.strut.amway.core.enumeration.OrderType;
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.model.Account;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.services.ArticleQueryService;
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.services.TagService;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.AuthenticationConstants;
import org.strut.amway.core.util.CategoryConstants;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;

public class MostPopularArticleServletTest {

    MostPopularArticleServlet mostPopularArticleServlet;

    @Mock
    ArticleQueryService articleQueryService;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    @Mock
    Resource resource;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    TagService tagService;

    @Mock
    Page currentPage;

    @Mock
    Page homePage;

    @Mock
    ArticleStatisticsService articleStatisticsService;

    ByteArrayOutputStream outputStream;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        createMostPopularArticleServlet();

        outputStream = new ByteArrayOutputStream(1024);
        PrintWriter writer = new PrintWriter(outputStream);

        when(slingHttpServletRequest.getMethod()).thenReturn("GET");
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(slingHttpServletResponse.getWriter()).thenReturn(writer);
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resource.adaptTo(Page.class)).thenReturn(currentPage);
        when(currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL)).thenReturn(homePage);
    }

    @Test
    public void shouldReturnJsonProperlyAlreadyLoginedWithNoArticle() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setPages(new ArrayList<Page>());

        ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final Account loginedUser = new Account();
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        mostPopularArticleServlet.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(Long.MAX_VALUE), criteria.getLimit());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
        assertEquals(2, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(SearchType.SEARCH_DATE_TIME, criteria.getSearchType());
        assertEquals("{\"limit\":10,\"articles\":[]}", outputStream.toString());
    }

    @Test
    public void shouldReturnJsonProperlyAlreadyLogined() throws Exception {
        List<Page> pages = new ArrayList<Page>();
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html", 5L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html", 5L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html", 10L));

        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setPages(pages);

        ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final Account loginedUser = new Account();
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        mostPopularArticleServlet.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(Long.MAX_VALUE), criteria.getLimit());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
        assertEquals(2, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(SearchType.SEARCH_DATE_TIME, criteria.getSearchType());
        assertEquals(
                "{\"limit\":10,\"articles\":[{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html.html\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturnJsonProperlyAlreadyLoginedWithdrawImpression() throws Exception {
        List<Page> pages = new ArrayList<Page>();
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html", 5L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html", 5));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html", 10L));

        ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setPages(pages);

        ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final Account loginedUser = new Account();
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        mostPopularArticleServlet.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(Long.MAX_VALUE), criteria.getLimit());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
        assertEquals(2, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(SearchType.SEARCH_DATE_TIME, criteria.getSearchType());
        assertEquals(
                "{\"limit\":10,\"articles\":[{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html.html\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturnProperCriteriaWithAlreadyLoginedUserWithIBOEntityType() throws Exception {
        List<Page> pages = new ArrayList<Page>();
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html", 5L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html", 5));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html", 10L));

        ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setPages(pages);

        ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final Account loginedUser = new Account();
        loginedUser.setEntityType(EntityType.IBO);
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        mostPopularArticleServlet.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(Long.MAX_VALUE), criteria.getLimit());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.IBO, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_DATE_TIME, criteria.getSearchType());
    }

    @Test
    public void shouldReturnProperCriteriaWithAlreadyLoginedUserWithClientEntityType() throws Exception {
        List<Page> pages = new ArrayList<Page>();
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html", 5L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html", 5));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html", 10L));

        ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setPages(pages);

        ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final Account loginedUser = new Account();
        loginedUser.setEntityType(EntityType.CLIENT);
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        mostPopularArticleServlet.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(Long.MAX_VALUE), criteria.getLimit());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.CLIENT, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_DATE_TIME, criteria.getSearchType());
    }

    @Test
    public void shouldReturnJsonProperlyAlreadyLoginedMoreResult() throws Exception {
        List<Page> pages = new ArrayList<Page>();
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html", 5L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html", 5L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html", 5L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog4.html", 5L));

        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog5.html", 6L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog6.html", 6L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog7.html", 6L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog8.html", 6L));

        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog9.html", 10L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog10.html", 10L));
        pages.add(createSportBlogArticle("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog10.html", 10L));

        ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setPages(pages);

        ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final Account loginedUser = new Account();
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        mostPopularArticleServlet.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(Long.MAX_VALUE), criteria.getLimit());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
        assertEquals(2, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(SearchType.SEARCH_DATE_TIME, criteria.getSearchType());
        assertEquals(
                "{\"limit\":10,\"articles\":[{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog9.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog9.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog10.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog10.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog10.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog10.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog5.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog5.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog6.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog6.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog7.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog7.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog8.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog8.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog2.html.html\"},{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog3.html.html\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldDestroyProperly() throws Exception {
        createMostPopularArticleServlet();
        mostPopularArticleServlet.destroy();
        assertEquals(mostPopularArticleServlet.getArticleJsonParser(), null);
    }

    @SuppressWarnings("serial")
    private Page createSportBlogArticle(String path, long impression) throws WCMException {
        Page sportBlog = Mockito.mock(Page.class);
        Resource sportBlogImgResource = Mockito.mock(Resource.class);
        final Calendar calendar = new GregorianCalendar(2014, 11, 3, 9, 6, 7);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
        ValueMap sportBlogValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                // 2014-12-03 09:06:07
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
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, path)).thenReturn(impression);
        return sportBlog;
    }

    private void createMostPopularArticleServlet() throws Exception {
        mostPopularArticleServlet = new MostPopularArticleServlet();
        setQueryService();
        setArticleStatisticsService();
        setTagService();
        mostPopularArticleServlet.init();
    }

    private void setQueryService() throws Exception {
        final Field articleQueryServiceField = FieldUtils.getField(MostPopularArticleServlet.class, "articleQueryService", true);
        articleQueryServiceField.setAccessible(true);
        articleQueryServiceField.set(mostPopularArticleServlet, articleQueryService);
    }

    private void setArticleStatisticsService() throws Exception {
        final Field articleStatisticsField = FieldUtils.getField(MostPopularArticleServlet.class, "articleStatisticsService", true);
        articleStatisticsField.setAccessible(true);
        articleStatisticsField.set(mostPopularArticleServlet, articleStatisticsService);
    }

    private void setTagService() throws Exception {
        final Field tagServiceField = FieldUtils.getField(MostPopularArticleServlet.class, "tagService", true);
        tagServiceField.setAccessible(true);
        tagServiceField.set(mostPopularArticleServlet, tagService);
    }
}
