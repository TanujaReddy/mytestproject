package org.strut.amway.core.servlets.article;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.model.Account;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.model.TagCriteria;
import org.strut.amway.core.services.ArticleQueryService;
import org.strut.amway.core.services.TagService;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.AuthenticationConstants;
import org.strut.amway.core.util.CategoryConstants;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;

public class SearchArticlePopupServletTest {

    SearchArticlePopupServlet classUnderTest;

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

    ByteArrayOutputStream outputStream;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        createSearchArticlePopupServlet();

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
    public void shouldReturnJsonProperly() throws Exception {
        final String searchText = "Marketing";
        final Long offset = 0L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(sportBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(searchText);

        ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(articleQueryService.search(any(ResourceResolver.class), captor.capture())).thenReturn(searchResult);

        final List<Tag> tags = Arrays.asList(createMarketingTag());
        when(tagService.search(eq(resourceResolver), any(TagCriteria.class))).thenReturn(tags);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final ArticlesCriteria criteria = captor.getValue();
        assertNotNull(criteria);
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(3), criteria.getLimit());
        assertEquals("marketing", criteria.getTagIds().get(0));
        assertEquals(1, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
        assertEquals(
                "{\"articles\":[{\"title\":\"Sport Blog\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\","
                        + "\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturnJsonProperlyWhenUserAlreadyLogined() throws Exception {
        final String searchText = "Marketing";
        final Long offset = 0L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(sportBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(searchText);

        ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
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
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(3), criteria.getLimit());
        assertEquals("marketing", criteria.getTagIds().get(0));
        assertEquals(2, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
        assertEquals(
                "{\"articles\":[{\"title\":\"Sport Blog\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\","
                        + "\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturnProperCriteriaWhenUserAlreadyLoginedWithIBOEntityType() throws Exception {
        final String searchText = "Marketing";
        final Long offset = 0L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(sportBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(searchText);

        ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
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
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(3), criteria.getLimit());
        assertEquals("marketing", criteria.getTagIds().get(0));
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.IBO, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
    }

    @Test
    public void shouldReturnProperCriteriaWhenUserAlreadyLoginedWithClientEntityType() throws Exception {
        final String searchText = "Marketing";
        final Long offset = 0L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(sportBlog));

        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(searchText);

        ArgumentCaptor<ArticlesCriteria> captor = ArgumentCaptor.forClass(ArticlesCriteria.class);
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
        assertEquals(Long.valueOf(0), criteria.getOffset());
        assertEquals(Long.valueOf(3), criteria.getLimit());
        assertEquals("marketing", criteria.getTagIds().get(0));
        assertEquals(3, criteria.getArticleLabelTypes().size());
        assertEquals(ArticleLabelType.PUBLIC, criteria.getArticleLabelTypes().get(0));
        assertEquals(ArticleLabelType.PRIVATE, criteria.getArticleLabelTypes().get(1));
        assertEquals(ArticleLabelType.CLIENT, criteria.getArticleLabelTypes().get(2));
        assertEquals(SearchType.SEARCH_TAG, criteria.getSearchType());
    }

    @Test
    public void shouldThrowExceptionWhenSearchParamIsMissing() throws Exception {
        final String searchText = null;
        when(slingHttpServletRequest.getParameter(eq(ArticleConstants.SEARCH_TEXT_PARAM))).thenReturn(searchText);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalArgumentException: search Text Param can not be empty\"}", outputStream.toString());
    }

    @Test
    public void shouldDestroyProperly() throws Exception {
        createSearchArticlePopupServlet();
        classUnderTest.destroy();
    }

    private Tag createMarketingTag() throws Exception {
        final Tag tag = Mockito.mock(Tag.class);
        when(tag.getTitle()).thenReturn("Marketing");
        when(tag.getTagID()).thenReturn("marketing");
        return tag;
    }

    @SuppressWarnings("serial")
    private Page createSportBlogArticle() {
        Page sportBlog = Mockito.mock(Page.class);
        final String path = "/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog";
        Resource sportBlogImgResource = Mockito.mock(Resource.class);
        ValueMap sportBlogValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                // 2014-12-03 09:06:07
                put(ArticleConstants.JCR_CREATED, new GregorianCalendar(2014, 11, 3, 9, 6, 7));
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

    private void createSearchArticlePopupServlet() throws Exception {
        classUnderTest = new SearchArticlePopupServlet();
        setQueryService();
        setTagService();
        classUnderTest.init();
    }

    private void setQueryService() throws Exception {
        final Field articleQueryServiceField = FieldUtils.getField(SearchArticlePopupServlet.class, "articleQueryService", true);
        articleQueryServiceField.setAccessible(true);
        articleQueryServiceField.set(classUnderTest, articleQueryService);
    }

    private void setTagService() throws Exception {
        final Field tagServiceField = FieldUtils.getField(SearchArticlePopupServlet.class, "tagService", true);
        tagServiceField.setAccessible(true);
        tagServiceField.set(classUnderTest, tagService);
    }

}
