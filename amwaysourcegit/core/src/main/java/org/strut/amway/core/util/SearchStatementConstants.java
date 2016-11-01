package org.strut.amway.core.util;

public final class SearchStatementConstants {

    public static String ARTICLE_QUERY_PATTERN = "SELECT DISTINCT [cq:PageContent] FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE(page, [{0}]){1}{2}{3}";

    public static String TAG_QUERY_PATTERN = "SELECT [cq:Tag] FROM [cq:Tag] AS tag WHERE ISDESCENDANTNODE([/etc/tags]) {0}";

    public static final String STATEMENT_AND_OR_UNION_PATTERN = "{0} {1} {2}";

    public static final String STATEMENT_PARENTHESES_PATTERN = " AND({0})";

    public static final String STATEMENT_CONTAINS_PATTERN = "{0} CONTAINS(page.[{1}], {2}) ";

    public static final String STATEMENT_SEARCH_ARTICLE_BY_TAG_PATTERN = " AND page.[" + ArticleConstants.CQ_TAGS + "] IN ({0})";

    public static final String STATEMENT_SEARCH_ARTICLE_BY_LABEL_PATTERN = " AND page.[" + ArticleConstants.LABEL_PROPERTY + "] IN ({0})";

    public static final String STATEMENT_SEARCH_ARTICLE_BY_IGNORE_PATH_PATTERN = " AND page.[" + ArticleConstants.JCR_PATH + "] <> {0}";

    public static final String STATEMENT_SEARCH_ARTICLE_BY_DATE_PATTERN = " AND page.[" + ArticleConstants.PUBLISH_DATE + "] {0} CAST({1} AS date)";

    public static final String STATEMENT_SEARCH_ARTICLE_BY_TEMPLATE_PATTERN = " AND (page.[" + ArticleConstants.CQ_TEMPLATE + "] = {0} OR page.[" + ArticleConstants.CQ_TEMPLATE + "] = {1} )";

    public static final String ORDER_BY_PATTERN = " ORDER BY page.[" + ArticleConstants.PUBLISH_DATE + "] {0}";

    public static final String DEFAULT_LOCALE_CODE = "en_au";

    public static final String STATEMENT_SEARCH_TAG_BY_ID_PATTERN = "AND (LOWER(tag.[" + ArticleConstants.JCR_TITLE
            + ".{0}]) {1} {2} OR (tag.[jcr:title.{0}] IS NULL AND LOWER(tag.[" + ArticleConstants.JCR_TITLE + "]) {1} {2}))";

    public static String addQuote(String text) {
        return ArticleConstants.OPS_QUOTE + text + ArticleConstants.OPS_QUOTE;
    }

    public static String addDoubleQuote(String text) {
        return ArticleConstants.OPS_QUOTE + ArticleConstants.OPS_DOUBLE_QUOTE + text + ArticleConstants.OPS_DOUBLE_QUOTE + ArticleConstants.OPS_QUOTE;
    }

}
