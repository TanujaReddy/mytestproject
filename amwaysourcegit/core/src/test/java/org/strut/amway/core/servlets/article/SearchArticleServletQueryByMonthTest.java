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
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.services.TagService;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.AuthenticationConstants;
import org.strut.amway.core.util.CategoryConstants;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.core.stats.PageViewStatistics;

public class SearchArticleServletQueryByMonthTest {

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
    TagService tagService;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Page currentPage;

    @Mock
    Page homePage;

    @Mock
    Resource resource;

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

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);
    }

    @Test
    public void shouldQueryByMonthProperly() throws Exception {
        final String searchText = "article";
        final String startDate = "2014-12-03T02:06:07.000+07:00";
        final String endDate = "2014-12-03T02:06:07.000+07:00";
        final String order = "ASC";
        final Long offset = 4L;
        final Long limit = 15L;
        final ArticleSearchResult searchResult = createArticleSearchResult(offset, limit);

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(String.valueOf(searchText));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(String.valueOf(offset));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(String.valueOf(limit));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.START_DATE_PARAM))).thenReturn(startDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.END_DATE_PARAM))).thenReturn(endDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.ORDER_PARAM))).thenReturn(order);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(searchText, criteria.getFullText());
        assertEquals(Long.valueOf(offset), criteria.getOffset());
        assertEquals(Long.valueOf(limit), criteria.getLimit());
        assertEquals(startDate, criteria.getStartDate());
        assertEquals(endDate, criteria.getEndDate());
        assertEquals(1, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(SearchType.SEARCH_FULLTEXT, criteria.getSearchType());
        assertEquals(OrderType.ASC.name(), criteria.getOrder());
    }

    @Test
    public void shouldQueryByMonthProperlyWhenUserAlreadyLogined() throws Exception {
        final String searchText = "article";
        final String startDate = "2014-12-03T02:06:07.000+07:00";
        final String endDate = "2014-12-03T02:06:07.000+07:00";
        final String order = "ASC";
        final Long offset = 4L;
        final Long limit = 15L;
        final ArticleSearchResult searchResult = createArticleSearchResult(offset, limit);

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(String.valueOf(searchText));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(String.valueOf(offset));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(String.valueOf(limit));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.START_DATE_PARAM))).thenReturn(startDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.END_DATE_PARAM))).thenReturn(endDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.ORDER_PARAM))).thenReturn(order);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final Account loginedUser = new Account();
        final HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);
        when(slingHttpServletRequest.getSession()).thenReturn(session);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(searchText, criteria.getFullText());
        assertEquals(Long.valueOf(offset), criteria.getOffset());
        assertEquals(Long.valueOf(limit), criteria.getLimit());
        assertEquals(startDate, criteria.getStartDate());
        assertEquals(endDate, criteria.getEndDate());
        assertEquals(2, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(SearchType.SEARCH_FULLTEXT, criteria.getSearchType());
        assertEquals(OrderType.ASC.name(), criteria.getOrder());
    }

    @Test
    public void shouldQueryByMonthProperlyWhenUserAlreadyLoginedWithIBOEntityType() throws Exception {
        final String searchText = "article";
        final String startDate = "2014-12-03T02:06:07.000+07:00";
        final String endDate = "2014-12-03T02:06:07.000+07:00";
        final String order = "ASC";
        final Long offset = 4L;
        final Long limit = 15L;
        final ArticleSearchResult searchResult = createArticleSearchResult(offset, limit);

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(String.valueOf(searchText));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(String.valueOf(offset));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(String.valueOf(limit));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.START_DATE_PARAM))).thenReturn(startDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.END_DATE_PARAM))).thenReturn(endDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.ORDER_PARAM))).thenReturn(order);

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
        assertEquals(searchText, criteria.getFullText());
        assertEquals(Long.valueOf(offset), criteria.getOffset());
        assertEquals(Long.valueOf(limit), criteria.getLimit());
        assertEquals(startDate, criteria.getStartDate());
        assertEquals(endDate, criteria.getEndDate());
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.IBO, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_FULLTEXT, criteria.getSearchType());
        assertEquals(OrderType.ASC.name(), criteria.getOrder());
    }

    @Test
    public void shouldQueryByMonthProperlyWhenUserAlreadyLoginedWithClientEntityType() throws Exception {
        final String searchText = "article";
        final String startDate = "2014-12-03T02:06:07.000+07:00";
        final String endDate = "2014-12-03T02:06:07.000+07:00";
        final String order = "ASC";
        final Long offset = 4L;
        final Long limit = 15L;
        final ArticleSearchResult searchResult = createArticleSearchResult(offset, limit);

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(String.valueOf(searchText));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(String.valueOf(offset));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(String.valueOf(limit));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.START_DATE_PARAM))).thenReturn(startDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.END_DATE_PARAM))).thenReturn(endDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.ORDER_PARAM))).thenReturn(order);

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
        assertEquals(searchText, criteria.getFullText());
        assertEquals(Long.valueOf(offset), criteria.getOffset());
        assertEquals(Long.valueOf(limit), criteria.getLimit());
        assertEquals(startDate, criteria.getStartDate());
        assertEquals(endDate, criteria.getEndDate());
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.CLIENT, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_FULLTEXT, criteria.getSearchType());
        assertEquals(OrderType.ASC.name(), criteria.getOrder());
    }

    @Test
    public void shouldQueryByMonthWhenEndDateParamIsMissing() throws Exception {
        final String searchText = "article";
        final String startDate = "2014-12-03T02:06:07.000+07:00";
        final String endDate = null;
        final String order = "DESC";
        final Long offset = 4L;
        final Long limit = 15L;
        final ArticleSearchResult searchResult = createArticleSearchResult(offset, limit);

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(String.valueOf(searchText));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(String.valueOf(offset));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(String.valueOf(limit));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.START_DATE_PARAM))).thenReturn(startDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.END_DATE_PARAM))).thenReturn(endDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.ORDER_PARAM))).thenReturn(order);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(searchText, criteria.getFullText());
        assertEquals(Long.valueOf(offset), criteria.getOffset());
        assertEquals(Long.valueOf(limit), criteria.getLimit());
        assertEquals(startDate, criteria.getStartDate());
        assertNull(criteria.getEndDate());
        assertEquals(SearchType.SEARCH_FULLTEXT, criteria.getSearchType());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
    }

    @Test
    public void shouldQueryByMonthWhenStartDateParamIsMissing() throws Exception {
        final String searchText = "article";
        final String startDate = null;
        final String endDate = "2014-12-03T02:06:07.000+07:00";
        final String order = "DESC";
        final Long offset = 4L;
        final Long limit = 15L;
        final ArticleSearchResult searchResult = createArticleSearchResult(offset, limit);

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(String.valueOf(searchText));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.OFFSET_PARAM))).thenReturn(String.valueOf(offset));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.LIMIT_PARAM))).thenReturn(String.valueOf(limit));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.START_DATE_PARAM))).thenReturn(startDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.END_DATE_PARAM))).thenReturn(endDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.ORDER_PARAM))).thenReturn(order);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(searchText, criteria.getFullText());
        assertEquals(Long.valueOf(offset), criteria.getOffset());
        assertEquals(Long.valueOf(limit), criteria.getLimit());
        assertNull(criteria.getStartDate());
        assertEquals(endDate, criteria.getEndDate());
        assertEquals(SearchType.SEARCH_FULLTEXT, criteria.getSearchType());
        assertEquals(OrderType.DESC.name(), criteria.getOrder());
    }

    @Test
    public void shouldThrowExceptionWhenDateTimeIsInvalid() throws Exception {
        final String searchText = "article";
        final String startDate = "2014-12-03T02:06:07.00007:00";
        final ArticleSearchResult searchResult = createArticleSearchResult(0L, 5L);

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(String.valueOf(searchText));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.START_DATE_PARAM))).thenReturn(startDate);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalArgumentException: datetime Param is invalid\"}", outputStream.toString());
    }

    @Test
    public void shouldThrowExceptionWhenOrderParamIsInvalid() throws Exception {
        final String searchText = "article";
        final String startDate = "2014-12-03T02:06:07.0000+07:00";
        final String order = "ASCabc";
        final ArticleSearchResult searchResult = createArticleSearchResult(0L, 5L);

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(String.valueOf(searchText));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.START_DATE_PARAM))).thenReturn(startDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.ORDER_PARAM))).thenReturn(order);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalArgumentException: No enum constant org.strut.amway.core.enumeration.OrderType.ASCabc\"}", outputStream.toString());
    }

    @Test
    public void shouldThrowExceptionWhenOrderParamIsMissing() throws Exception {
        final String searchText = "article";
        final String startDate = "2014-12-03T02:06:07.000+07:00";
        final String endDate = "2014-12-03T02:06:07.000+07:00";
        final String order = null;
        final ArticleSearchResult searchResult = createArticleSearchResult(0L, 5L);

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(String.valueOf(searchText));
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.START_DATE_PARAM))).thenReturn(startDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.END_DATE_PARAM))).thenReturn(endDate);
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.ORDER_PARAM))).thenReturn(order);

        final ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalArgumentException: orderParam Param is invalid\"}", outputStream.toString());
    }

    private ArticleSearchResult createArticleSearchResult(Long offset, Long limit) {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(sportBlog));
        return searchResult;
    }

    @SuppressWarnings("serial")
    private Page createSportBlogArticle() {
        Page sportBlog = Mockito.mock(Page.class);
        Resource sportBlogImgResource = Mockito.mock(Resource.class);
        ValueMap sportBlogValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                // 2014-12-03 09:06:07
                final GregorianCalendar calendar = new GregorianCalendar(2014, 11, 3, 9, 6, 7);
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
                put(ArticleConstants.JCR_CREATED, calendar);
            }
        });
        when(sportBlog.getTitle()).thenReturn("Sport Blog");
        when(sportBlog.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog");
        when(sportBlog.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog");
        when(sportBlog.getContentResource(eq("image"))).thenReturn(sportBlogImgResource);
        when(sportBlog.getProperties()).thenReturn(sportBlogValueMap);

        Page category = Mockito.mock(Page.class);
        ValueMap categoryValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("categoryType", CategoryConstants.CORPORATE_CATEGORY);
            }
        });
        when(category.getTitle()).thenReturn("SportCategory");
        when(category.getProperties()).thenReturn(new ValueMapDecorator(categoryValueMap));
        when(sportBlog.getParent(2)).thenReturn(category);
        return sportBlog;
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
        setArticleStatisticsService();
        setTagService();
        classUnderTest.init();
    }

    private void setQueryService() throws Exception {
        final Field articleQueryServiceField = FieldUtils.getField(SearchArticleServlet.class, "articleQueryService", true);
        articleQueryServiceField.setAccessible(true);
        articleQueryServiceField.set(classUnderTest, articleQueryService);
    }

    private void setArticleStatisticsService() throws Exception {
        final Field articleStatisticsField = FieldUtils.getField(SearchArticleServlet.class, "articleStatisticsService", true);
        articleStatisticsField.setAccessible(true);
        articleStatisticsField.set(classUnderTest, articleStatisticsService);
    }

    private void setTagService() throws Exception {
        final Field tagServiceField = FieldUtils.getField(SearchArticleServlet.class, "tagService", true);
        tagServiceField.setAccessible(true);
        tagServiceField.set(classUnderTest, tagService);
    }

}
