package org.strut.amway.core.builder;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.strut.amway.core.enumeration.OrderType;
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.util.ArticleConstants;

public class ArticleQuerySearchBuilderTest {

    private List<String> tagIds;

    private Long OFFSET = 0L;
    private Long LIMIT = 10L;

    @Before
    public void init() {
        tagIds = new ArrayList<String>();
        tagIds.add("Tag1");
        tagIds.add("Tag2");
        tagIds.add("Tag3");
    }

    @Test
    public void testBuildSearchArticleQuery() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_FULLTEXT).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setFullText("abc").setTagIds(tagIds)
                        .setStartDate("2014-11-18T11:56:21.801+07:00").setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL)
                        .setEndDate("2014-11-20T11:56:21.801+07:00").setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOffset(OFFSET).setLimit(LIMIT)
                        .setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND( CONTAINS(page.[jcr:title], '\"abc\"') OR ( CONTAINS(page.[article-content-text-center/*/text], '\"abc\"') OR CONTAINS(page.[article-content-text-footer/*/text], '\"abc\"') )) AND page.[publishedDate] >= CAST('2014-11-18T11:56:21.801+07:00' AS date) AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) UNION SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('Tag1' ,'Tag2' ,'Tag3') AND page.[publishedDate] >= CAST('2014-11-18T11:56:21.801+07:00' AS date) AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryWithNoMonthFilter() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_FULLTEXT).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setFullText("abc").setTagIds(tagIds).setOffset(OFFSET)
                        .setLimit(LIMIT).setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND( CONTAINS(page.[jcr:title], '\"abc\"') OR ( CONTAINS(page.[article-content-text-center/*/text], '\"abc\"') OR CONTAINS(page.[article-content-text-footer/*/text], '\"abc\"') )) UNION SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('Tag1' ,'Tag2' ,'Tag3') ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryWithStartDateIsEmpty() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_FULLTEXT).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setFullText("abc").setTagIds(tagIds)
                        .setEndDate("2014-11-20T11:56:21.801+07:00").setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOffset(OFFSET).setLimit(LIMIT)
                        .setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND( CONTAINS(page.[jcr:title], '\"abc\"') OR ( CONTAINS(page.[article-content-text-center/*/text], '\"abc\"') OR CONTAINS(page.[article-content-text-footer/*/text], '\"abc\"') )) AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) UNION SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('Tag1' ,'Tag2' ,'Tag3') AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryWithEndDateIsNull() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_FULLTEXT).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setFullText("abc").setTagIds(tagIds)
                        .setEndDate("2014-11-20T11:56:21.801+07:00").setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOffset(OFFSET).setLimit(LIMIT)
                        .setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND( CONTAINS(page.[jcr:title], '\"abc\"') OR ( CONTAINS(page.[article-content-text-center/*/text], '\"abc\"') OR CONTAINS(page.[article-content-text-footer/*/text], '\"abc\"') )) AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) UNION SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('Tag1' ,'Tag2' ,'Tag3') AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryWithTagIdsNull() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_FULLTEXT).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setFullText("abc")
                        .setStartDate("2014-11-18T11:56:21.801+07:00").setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL)
                        .setEndDate("2014-11-20T11:56:21.801+07:00").setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOffset(OFFSET).setLimit(LIMIT)
                        .setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND( CONTAINS(page.[jcr:title], '\"abc\"') OR ( CONTAINS(page.[article-content-text-center/*/text], '\"abc\"') OR CONTAINS(page.[article-content-text-footer/*/text], '\"abc\"') )) AND page.[publishedDate] >= CAST('2014-11-18T11:56:21.801+07:00' AS date) AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryWithTagIdsEmpty() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_FULLTEXT).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setFullText("abc").setTagIds(new ArrayList<String>())
                        .setOffset(OFFSET).setLimit(LIMIT).setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND( CONTAINS(page.[jcr:title], '\"abc\"') OR ( CONTAINS(page.[article-content-text-center/*/text], '\"abc\"') OR CONTAINS(page.[article-content-text-footer/*/text], '\"abc\"') )) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryWithFullTextEmpty() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_FULLTEXT).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setOffset(OFFSET).setLimit(LIMIT).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryWithStartDateIsNull() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_DATE_TIME).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setEndDate("2014-11-20T11:56:21.801+07:00")
                        .setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOffset(OFFSET).setLimit(LIMIT).setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryWithEndDateIsEmpty() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_DATE_TIME).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setStartDate("2014-11-18T11:56:21.801+07:00")
                        .setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL).setEndDate("").setOffset(OFFSET).setLimit(LIMIT)
                        .setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[publishedDate] >= CAST('2014-11-18T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryByDate() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_DATE_TIME).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setStartDate("2014-11-18T11:56:21.801+07:00")
                        .setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL).setEndDate("2014-11-20T11:56:21.801+07:00")
                        .setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOffset(OFFSET).setLimit(LIMIT).setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[publishedDate] >= CAST('2014-11-18T11:56:21.801+07:00' AS date) AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryByTag() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_TAG).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setTagIds(tagIds).setStartDate("2014-11-18T11:56:21.801+07:00")
                        .setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL).setEndDate("2014-11-20T11:56:21.801+07:00")
                        .setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOffset(OFFSET).setLimit(LIMIT).setOrder(OrderType.DESC.name())
                        .setIgnorePath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/e-card/gnuohp/jcr:content").build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('Tag1' ,'Tag2' ,'Tag3') AND page.[jcr:path] <> '/content/asia-pac/australia-new-zealand/australia/amway-today/en_au/events/e-card/gnuohp/jcr:content' AND page.[publishedDate] >= CAST('2014-11-18T11:56:21.801+07:00' AS date) AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryByTagWithIgnorePathIsNull() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_TAG).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setTagIds(tagIds).setStartDate("2014-11-18T11:56:21.801+07:00")
                        .setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL).setEndDate("2014-11-20T11:56:21.801+07:00")
                        .setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOffset(OFFSET).setLimit(LIMIT).setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('Tag1' ,'Tag2' ,'Tag3') AND page.[publishedDate] >= CAST('2014-11-18T11:56:21.801+07:00' AS date) AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryByTagWithEndDateIsEmpty() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_TAG).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setTagIds(tagIds).setStartDate("2014-11-18T11:56:21.801+07:00")
                        .setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL).setOffset(OFFSET).setLimit(LIMIT).setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('Tag1' ,'Tag2' ,'Tag3') AND page.[publishedDate] >= CAST('2014-11-18T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryByTagStartDateIsEmpty() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_TAG).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setTagIds(tagIds).setStartDate("")
                        .setEndDate("2014-11-20T11:56:21.801+07:00").setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOffset(OFFSET).setLimit(LIMIT)
                        .setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('Tag1' ,'Tag2' ,'Tag3') AND page.[publishedDate] <= CAST('2014-11-20T11:56:21.801+07:00' AS date) ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryByTagWithdrawDateTime() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_TAG).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setTagIds(tagIds).setOffset(OFFSET).setLimit(LIMIT)
                        .setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('Tag1' ,'Tag2' ,'Tag3') ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryByTagIsNull() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder().setSearchType(SearchType.SEARCH_TAG).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setOffset(OFFSET).setLimit(LIMIT).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('') ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchArticleQueryByTagIsEmpty() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_TAG).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setTagIds(new ArrayList<String>()).setOffset(OFFSET)
                        .setLimit(LIMIT).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' AND page.[cq:tags] IN ('') ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchLastedArticleQuery() {
        ArticlesCriteria articlesCriteria =
                new ArticlesCriteria.Builder()
                        .setSearchType(SearchType.SEARCH_BY_SUB_CATEGORY).setPath("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au").setOffset(OFFSET).setLimit(LIMIT)
                        .setOrder(OrderType.DESC.name()).build();

        String expected =
                "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [/content/asia-pac/australia-new-zealand/australia/amway-today/en_au]) AND page.[label] IN ('') AND page.[cq:template] = '/apps/corporate/amway-today/templates/article-page' ORDER BY page.[publishedDate] DESC";

        String actual = ArticleQuerySearchBuilder.buildSearchArticleQuery(articlesCriteria);

        assertEquals(expected, actual);
    }

}
