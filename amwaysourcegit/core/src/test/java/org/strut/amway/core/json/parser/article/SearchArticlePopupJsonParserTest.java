package org.strut.amway.core.json.parser.article;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.CategoryConstants;

import com.day.cq.wcm.api.Page;

public class SearchArticlePopupJsonParserTest {

    SearchArticlePopupJsonParser classUnderTest;

    @Mock
    ResourceResolver resourceResolver;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new SearchArticlePopupJsonParser();
    }

    @Test
    public void shouldReturnJsonProperly() throws Exception {
        final Long offset = 0L;
        final Long limit = 3L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);

        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(sportBlog));

        final String result = classUnderTest.parse(resourceResolver, searchResult);

        assertEquals(
                "{\"articles\":[{\"title\":\"Sport Blog\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\","
                        + "\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\"}]}",
                result);
    }

    @Test
    public void shouldReturnEmptyWhenResultNotFound() throws Exception {
        final Long offset = 0L;
        final Long limit = 3L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);

        final String result = classUnderTest.parse(resourceResolver, searchResult);

        assertEquals("{\"articles\":[]}", result);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenCategoryIsNull() throws Exception {
        final Long offset = 0L;
        final Long limit = 3L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page sportBlog = createSportBlogArticle();
        searchResult.setPages(Arrays.asList(sportBlog));
        when(sportBlog.getParent(2)).thenReturn(null);

        classUnderTest.parse(resourceResolver, searchResult);
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

}
