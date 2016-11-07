package org.strut.amway.core.servlets.article;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
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
import org.strut.amway.core.model.TagCriteria;
import org.strut.amway.core.services.ArticleQueryService;
import org.strut.amway.core.services.ArticleService;
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.services.TagService;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.AuthenticationConstants;
import org.strut.amway.core.util.CategoryConstants;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.core.stats.PageViewStatistics;

public class SearchArticleServletTest {

    SearchArticleServlet classUnderTest;

    @Mock
    ArticleQueryService articleQueryService;

    @Mock
    ArticleStatisticsService articleStatisticsService;

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

    ByteArrayOutputStream outputStream;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        createSearchArticleServlet();

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
    public void shouldReturnJsonProperlyForSearchFullText() throws Exception {
        final String searchText = "Events";
        final Long offset = 3L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page eventsBlog = createEventsArticle();
        searchResult.setPages(Arrays.asList(eventsBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(offset.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(limit.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(searchText);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(searchText, criteria.getFullText());
        assertEquals(1, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(SearchType.SEARCH_FULLTEXT, criteria.getSearchType());
        assertEquals(
                "{\"offset\":3,\"limit\":10,\"articles\":[{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,"
                        + "\"category\":\"Event Category\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog.html\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturnJsonProperlyForSearchFullTextWhenUserAlreadyLogined() throws Exception {
        final String searchText = "Events";
        final Long offset = 3L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page eventsBlog = createEventsArticle();
        searchResult.setPages(Arrays.asList(eventsBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(offset.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(limit.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(searchText);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);

        final Account loginedUser = new Account();
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(searchText, criteria.getFullText());
        assertEquals(2, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(SearchType.SEARCH_FULLTEXT, criteria.getSearchType());
        assertEquals(
                "{\"offset\":3,\"limit\":10,\"articles\":[{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,"
                        + "\"category\":\"Event Category\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog.html\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturnProperlyCriteriaForSearchFullTextWhenUserAlreadyLoginedWithIBOEntityType() throws Exception {
        final String searchText = "Events";
        final Long offset = 3L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page eventsBlog = createEventsArticle();
        searchResult.setPages(Arrays.asList(eventsBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(offset.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(limit.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(searchText);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);

        final Account loginedUser = new Account();
        loginedUser.setEntityType(EntityType.IBO);
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(searchText, criteria.getFullText());
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.IBO, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_FULLTEXT, criteria.getSearchType());
    }

    @Test
    public void shouldReturnProperlyCriteriaForSearchFullTextWhenUserAlreadyLoginedWithClientEntityType() throws Exception {
        final String searchText = "Events";
        final Long offset = 3L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page eventsBlog = createEventsArticle();
        searchResult.setPages(Arrays.asList(eventsBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(offset.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(limit.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(searchText);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);

        final Account loginedUser = new Account();
        loginedUser.setEntityType(EntityType.CLIENT);
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(searchText, criteria.getFullText());
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.CLIENT, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_FULLTEXT, criteria.getSearchType());
    }

    @Test
    public void shouldReturnJsonProperlyForSearchByTagId() throws Exception {
        final String tagId = "amway-today:events";
        final Long offset = 3L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page eventsBlog = createEventsArticle();
        searchResult.setPages(Arrays.asList(eventsBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(offset.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(limit.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.TAG_ID_PARAM))).thenReturn(tagId);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(tagId, criteria.getTagIds().get(0));
        assertEquals(1, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
        assertEquals(
                "{\"offset\":3,\"limit\":10,\"articles\":[{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,"
                        + "\"category\":\"Event Category\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog.html\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturnJsonProperlyForSearchByTagIdWhenUserAlreadyLogined() throws Exception {
        final String tagId = "amway-today:events";
        final Long offset = 3L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page eventsBlog = createEventsArticle();
        searchResult.setPages(Arrays.asList(eventsBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(offset.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(limit.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.TAG_ID_PARAM))).thenReturn(tagId);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);

        final Account loginedUser = new Account();
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(tagId, criteria.getTagIds().get(0));
        assertEquals(2, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
        assertEquals(
                "{\"offset\":3,\"limit\":10,\"articles\":[{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,"
                        + "\"category\":\"Event Category\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog.html\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturnProperlyCriteriaForSearchByTagIdWhenUserAlreadyLoginedWithIBOEntityType() throws Exception {
        final String tagId = "amway-today:events";
        final Long offset = 3L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page eventsBlog = createEventsArticle();
        searchResult.setPages(Arrays.asList(eventsBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(offset.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(limit.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.TAG_ID_PARAM))).thenReturn(tagId);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);

        final Account loginedUser = new Account();
        loginedUser.setEntityType(EntityType.IBO);
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(tagId, criteria.getTagIds().get(0));
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.IBO, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
    }

    @Test
    public void shouldReturnProperlyCriteriaForSearchByTagIdWhenUserAlreadyLoginedWithClientEntityType() throws Exception {
        final String tagId = "amway-today:events";
        final Long offset = 3L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page eventsBlog = createEventsArticle();
        searchResult.setPages(Arrays.asList(eventsBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(offset.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(limit.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.TAG_ID_PARAM))).thenReturn(tagId);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);

        final Account loginedUser = new Account();
        loginedUser.setEntityType(EntityType.CLIENT);
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertEquals(tagId, criteria.getTagIds().get(0));
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.CLIENT, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
    }

    @Test
    public void shouldReturnJsonProperlyForSearchByTagIdWithStartDateAndEndDate() throws Exception {
        final String tagId = "amway-today:events";
        final Long offset = 3L;
        final Long limit = 10L;
        final String startDate = null;
        final String endDate = "2014-12-03T02:06:07.000+07:00";
        final String order = "DESC";
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page eventsBlog = createEventsArticle();
        searchResult.setPages(Arrays.asList(eventsBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(offset.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(limit.toString());
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.START_DATE_PARAM))).thenReturn(startDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.END_DATE_PARAM))).thenReturn(endDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.ORDER_PARAM))).thenReturn(order);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.TAG_ID_PARAM))).thenReturn(tagId);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(offset, criteria.getOffset());
        assertEquals(limit, criteria.getLimit());
        assertNull(criteria.getStartDate());
        assertEquals(endDate, criteria.getEndDate());
        assertEquals(tagId, criteria.getTagIds().get(0));
        assertEquals(1, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
        assertEquals(
                "{\"offset\":3,\"limit\":10,\"articles\":[{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,"
                        + "\"category\":\"Event Category\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog.html\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturn500WhenRequestIsInvalid() throws Exception {
        final String searchText = null;
        final String tagId = null;

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(searchText);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.TAG_ID_PARAM))).thenReturn(tagId);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalStateException: search request is invalid\"}", outputStream.toString());
    }

    @Test
    public void shouldDestroyProperly() throws Exception {
        createEventsArticle();
        classUnderTest.destroy();
    }

    @SuppressWarnings("serial")
    private Page createEventsArticle() {
        Page eventsBlog = Mockito.mock(Page.class);
        final String path = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog";
        Resource eventsBlogImgResource = Mockito.mock(Resource.class);
        ValueMap eventsBlogValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                // 2014-12-03 09:06:07
                GregorianCalendar calendar = new GregorianCalendar(2014, 11, 3, 9, 6, 7);
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
                put(ArticleConstants.JCR_CREATED, calendar);
                put(ArticleConstants.PUBLISH_DATE, calendar);
            }
        });
        when(eventsBlog.getTitle()).thenReturn("Sport Blog");
        when(eventsBlog.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/event/event-blog");
        when(eventsBlog.getContentResource(eq("image"))).thenReturn(eventsBlogImgResource);
        when(eventsBlog.getProperties()).thenReturn(eventsBlogValueMap);

        Page category = Mockito.mock(Page.class);
        ValueMap categoryValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("categoryType", CategoryConstants.CORPORATE_CATEGORY);
            }
        });
        when(category.getTitle()).thenReturn("Event Category");
        when(category.getPath()).thenReturn(path);
        when(category.getProperties()).thenReturn(new ValueMapDecorator(categoryValueMap));
        when(eventsBlog.getParent(2)).thenReturn(category);
        return eventsBlog;
    }

    private Tag createMarketingTag() throws Exception {
        final Tag tag = Mockito.mock(Tag.class);
        when(tag.getTitle()).thenReturn("Marketing");
        when(tag.getTagID()).thenReturn("marketing");
        return tag;
    }

    private void createSearchArticleServlet() throws Exception {
        classUnderTest = new SearchArticleServlet();
        setQueryService();
        setTagService();
        setArticleLikeNumberService();
        classUnderTest.init();
    }

    private void setQueryService() throws Exception {
        final Field articleQueryServiceField = FieldUtils.getField(SearchArticleServlet.class, "articleQueryService", true);
        articleQueryServiceField.setAccessible(true);
        articleQueryServiceField.set(classUnderTest, articleQueryService);
    }

    private void setArticleLikeNumberService() throws Exception {
        final Field articleStatisticsServiceField = FieldUtils.getField(SearchArticleServlet.class, "articleStatisticsService", true);
        articleStatisticsServiceField.setAccessible(true);
        articleStatisticsServiceField.set(classUnderTest, articleStatisticsService);
    }

    private void setTagService() throws Exception {
        final Field tagServiceField = FieldUtils.getField(SearchArticleServlet.class, "tagService", true);
        tagServiceField.setAccessible(true);
        tagServiceField.set(classUnderTest, tagService);
    }

}
