package org.strut.amway.core.builder;

import java.text.MessageFormat;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.model.TagCriteria;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.SearchStatementConstants;

public class TagQueryBuilder {
    private static Logger LOGGER = LoggerFactory.getLogger(TagQueryBuilder.class);

    private static final int DEFAULT_TAG_PARAM_SIZE = 3;

    public static String buildSearchTagQuery(TagCriteria tagCriterias) {
        if (tagCriterias != null) {
            Object params[] = new Object[DEFAULT_TAG_PARAM_SIZE];

            String param = buildSearchTitleParam(tagCriterias);

            params[0] = StringUtils.isEmpty(param) ? "" : param;

            return MessageFormat.format(SearchStatementConstants.TAG_QUERY_PATTERN, params).trim();
        }
        LOGGER.warn("TagCriteria is null : " + tagCriterias);
        return "";
    }

    private static String buildSearchTitleParam(TagCriteria tagCriterias) {
        String localeCode = tagCriterias.getLocaleCode();
        String title = tagCriterias.getTitle();
        Boolean exactMatch = tagCriterias.getExactMatch();

        Object[] params = buildParams(localeCode, title, exactMatch);

        return MessageFormat.format(SearchStatementConstants.STATEMENT_SEARCH_TAG_BY_ID_PATTERN, params);
    }

    private static Object[] buildParams(String localeCode, String title, Boolean exactMatch) {
        Object[] params = new Object[DEFAULT_TAG_PARAM_SIZE];

        if (StringUtils.isNotEmpty(title)) {
            title = title.toLowerCase();
            params[0] = StringUtils.isEmpty(localeCode) ? SearchStatementConstants.DEFAULT_LOCALE_CODE : localeCode;
            if (BooleanUtils.isTrue(exactMatch)) {
                params[1] = ArticleConstants.OPS_EQUAL;
                params[2] = SearchStatementConstants.addQuote(title);
            } else {
                params[1] = ArticleConstants.OPS_LIKE;
                params[2] = SearchStatementConstants.addQuote(ArticleConstants.OPS_PERCENT_SIGN + title + ArticleConstants.OPS_PERCENT_SIGN);
            }
        }

        return params;
    }

}
