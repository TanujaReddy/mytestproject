package org.strut.amway.core.json.parser.article;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

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
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.CategoryConstants;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.core.stats.PageViewStatistics;

public class ArticleGridJsonParserTest {

    ArticleGridJsonParser classUnderTest;

    @Mock
    ArticleStatisticsService articleStatisticsService;

    @Mock
    ResourceResolver resourceResolver;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new ArticleGridJsonParser(articleStatisticsService);
    }

    @Test
    public void shouldReturnJsonProperly() throws Exception {
        final Long impression = 20L;
        final Long offset = 4L;
        final Long limit = 10L;
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(offset);
        searchResult.setLimit(limit);
        final Page healthBlog = createHealthBlogArticle(impression);
        final Page sportBlog = createSportBlogArticle(impression);
        searchResult.setPages(Arrays.asList(healthBlog, sportBlog));

        final String result = classUnderTest.parse(resourceResolver, searchResult);

        assertEquals(
                "{\"offset\":"
                        + offset
                        + ",\"limit\":"
                        + limit
                        + ",\"articles\":[{\"title\":\"Health Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"healthCategory\",\"categoryType\":\"main-blogging\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/health-blog.html\",\"impression\":20},"
                        + "{\"title\":\"Sport Blog\",\"tags\":[],\"snippet\":\"\",\"link\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\",\"lastPublishedDate\":\"2014-12-03T02:06:07.000Z\",\"likeNumber\":0,\"category\":\"SportCategory\",\"categoryType\":\"corporate\",\"categoryLink\":\"/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/health/Fitness/sport-blog.html\"}]}",
                result);
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldNotSetPublishedDateWhenItIsNotPresent() throws Exception {
        final ArticleSearchResult searchResult = new ArticleSearchResult();
        searchResult.setOffset(ArticleConstants.DEFAULT_OFFSET_VALUE);
        searchResult.setLimit(ArticleConstants.DEFAULT_LIMIT_VALUE);
        Page healthBlog = createHealthBlogArticle(0L);
        searchResult.setPages(Arrays.asList(healthBlog));

        ValueMap healthBlogValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
            }
        });
        when(healthBlog.getProperties()).thenReturn(healthBlogValueMap);

        final String result = classUnderTest.parse(resourceResolver, searchResult);

        assertFalse(result.toString().contains("\"lastPublishedDate\":\"03 Dec 2014\""));
    }

    @SuppressWarnings("serial")
    private Page createHealthBlogArticle(long impression) {
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
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, healthBlog.getPath())).thenReturn(impression);
        return healthBlog;
    }

    @SuppressWarnings("serial")
    private Page createSportBlogArticle(long impresssion) {
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
        when(articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, sportBlog.getPath())).thenReturn(impresssion);
        return sportBlog;
    }

}
