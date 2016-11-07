package org.strut.amway.core.builder;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.OrderType;
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.SearchStatementConstants;

public class ArticleQuerySearchBuilder {
    private static Logger LOGGER = LoggerFactory.getLogger(ArticleQuerySearchBuilder.class);

    public static final int DEFAULT_ARTICLE_PARAM_SIZE = 4;

    public static String buildSearchArticleQuery(ArticlesCriteria articlesCriteria) {

        SearchType searchType = articlesCriteria.getSearchType();
        if (searchType != null) {
            switch (searchType) {
            case SEARCH_BY_SUB_CATEGORY:
                return generateQueryBySubCategory(articlesCriteria);

            case SEARCH_DATE_TIME:
                return generateQueryByDateTime(articlesCriteria);

            case SEARCH_TAG:
                return generateQueryByTag(articlesCriteria);

            case SEARCH_FULLTEXT:
                return generateQueryUniversal(articlesCriteria);
            }
        }
        LOGGER.warn("SearchType is null : " + searchType);
        return "";
    }

    private static String generateQueryByTag(ArticlesCriteria articlesCriteria) {
        if (articlesCriteria != null) {
            // generate common statement
            String pathStatement = generatePathStatement(articlesCriteria.getPath());
            String labelStatement = generateLabelStatement(articlesCriteria.getArticleLabelTypes());
            String templateStatement = generateTemplateStatement();
            String orderStatement = generateOrderStatement(articlesCriteria.getOrder());

            // generate specify statement for query by tag
            String listTagIDsStatement = generateListTagIDsStatement(articlesCriteria.getTagIds(), false);
            String ignorePathStatement = generateIgnorePathStatement(articlesCriteria.getIgnorePath());
            String dateTimeStatement =
                    generateDateTimeStatement(articlesCriteria.getStartDate(), articlesCriteria.getOpsStartDate(), articlesCriteria.getEndDate(),
                            articlesCriteria.getOpsEndDate());

            String statement = listTagIDsStatement + ignorePathStatement + dateTimeStatement;

            return generateQuery(pathStatement, labelStatement, templateStatement, statement, orderStatement);
        }
        LOGGER.warn("ArticlesCriteria is null : " + articlesCriteria);
        return "";
    }

    private static String generateQueryByDateTime(ArticlesCriteria articlesCriteria) {
        if (articlesCriteria != null) {
            // generate common statement
            String pathStatement = generatePathStatement(articlesCriteria.getPath());
            String labelStatement = generateLabelStatement(articlesCriteria.getArticleLabelTypes());
            String templateStatement = generateTemplateStatement();
            String orderStatement = generateOrderStatement(articlesCriteria.getOrder());

            // generate specify statement for query by date time
            String dateTimeStatement =
                    generateDateTimeStatement(articlesCriteria.getStartDate(), articlesCriteria.getOpsStartDate(), articlesCriteria.getEndDate(),
                            articlesCriteria.getOpsEndDate());

            return generateQuery(pathStatement, labelStatement, templateStatement, dateTimeStatement, orderStatement);
        }
        return "";
    }

    private static String generateQueryUniversal(ArticlesCriteria articlesCriteria) {
        if (articlesCriteria != null) {
            // generate common statement
            String pathStatement = generatePathStatement(articlesCriteria.getPath());
            String labelStatement = generateLabelStatement(articlesCriteria.getArticleLabelTypes());
            String templateStatement = generateTemplateStatement();
            String orderStatement = generateOrderStatement(articlesCriteria.getOrder());

            // generate specify statement for query by universal
            String fullTextStatement = generateFullTextStatement(articlesCriteria.getFullText());
            String listTagIDsStatement = generateListTagIDsStatement(articlesCriteria.getTagIds(), true);
            String dateTimeStatement =
                    generateDateTimeStatement(articlesCriteria.getStartDate(), articlesCriteria.getOpsStartDate(), articlesCriteria.getEndDate(),
                            articlesCriteria.getOpsEndDate());

            return generateQueryByStatement(pathStatement, labelStatement, templateStatement, fullTextStatement, dateTimeStatement, listTagIDsStatement,
                    orderStatement);
        }
        return "";
    }

    private static String generateQueryByStatement(String pathStatement, String labelStatement, String templateStatement, String fullTextStatement,
            String dateTimeStatement, String listTagIDsStatement, String orderStatement) {

        if (StringUtils.isNotEmpty(listTagIDsStatement)) {
            String statementLeft = generateQuery(pathStatement, labelStatement, templateStatement, fullTextStatement + dateTimeStatement, "");
            String statementRight = generateQuery(pathStatement, labelStatement, templateStatement, listTagIDsStatement + dateTimeStatement, orderStatement);
            return MessageFormat.format(SearchStatementConstants.STATEMENT_AND_OR_UNION_PATTERN, statementLeft, ArticleConstants.OPS_UNION, statementRight);
        } else {
            return generateQuery(pathStatement, labelStatement, templateStatement, fullTextStatement + dateTimeStatement, orderStatement);
        }
    }

    private static String generateQueryBySubCategory(ArticlesCriteria articlesCriteria) {
        if (articlesCriteria != null) {
            // generate common statement
            String pathStatement = generatePathStatement(articlesCriteria.getPath());
            String labelStatement = generateLabelStatement(articlesCriteria.getArticleLabelTypes());
            String templateStatement = generateTemplateStatement();
            String orderStatement = generateOrderStatement(articlesCriteria.getOrder());

            // generate specify statement for query query by catagory
            String statement = "";

            return generateQuery(pathStatement, labelStatement, templateStatement, statement, orderStatement);
        }
        return "";
    }

    private static String generateFullTextStatement(String fullText) {
        if (StringUtils.isEmpty(fullText)) {
            return "";
        }
        fullText = SearchStatementConstants.addDoubleQuote(fullText);
        String query =
                MessageFormat.format(SearchStatementConstants.STATEMENT_CONTAINS_PATTERN, "", ArticleConstants.JCR_TITLE, fullText)
                        + MessageFormat.format(SearchStatementConstants.STATEMENT_CONTAINS_PATTERN, ArticleConstants.OPS_OR + " (",
                                ArticleConstants.JCR_CONTENT_ARTICLE_TEXT_CENTER, fullText)
                        + MessageFormat.format(SearchStatementConstants.STATEMENT_CONTAINS_PATTERN, ArticleConstants.OPS_OR,
                                ArticleConstants.JCR_CONTENT_ARTICLE_TEXT_FOOTER, fullText);
        return MessageFormat.format(SearchStatementConstants.STATEMENT_PARENTHESES_PATTERN, query + ")");
    }

    private static String generateDateTimeStatement(String startDate, String opsStartDate, String endDate, String opsEndDate) {
        String pStartDate = "";
        String pEndDate = "";

        opsStartDate = StringUtils.isEmpty(opsStartDate) ? ArticleConstants.OPS_GREATER_THAN_OR_EQUAL : opsStartDate;
        opsEndDate = StringUtils.isEmpty(opsEndDate) ? ArticleConstants.OPS_LESS_THAN_OR_EQUAL : opsEndDate;

        if (StringUtils.isNotEmpty(startDate)) {
            pStartDate =
                    MessageFormat.format(SearchStatementConstants.STATEMENT_SEARCH_ARTICLE_BY_DATE_PATTERN, opsStartDate,
                            SearchStatementConstants.addQuote(startDate));
        }

        if (StringUtils.isNotEmpty(endDate)) {
            pEndDate =
                    MessageFormat.format(SearchStatementConstants.STATEMENT_SEARCH_ARTICLE_BY_DATE_PATTERN, opsEndDate,
                            SearchStatementConstants.addQuote(endDate));
        }

        return pStartDate + pEndDate;
    }

    private static String generateIgnorePathStatement(String ignorePath) {
        if (StringUtils.isNotEmpty(ignorePath)) {
            return MessageFormat
                    .format(SearchStatementConstants.STATEMENT_SEARCH_ARTICLE_BY_IGNORE_PATH_PATTERN, SearchStatementConstants.addQuote(ignorePath));
        }
        return "";
    }

    private static String generateListTagIDsStatement(List<String> listTagIds, boolean isUniversalSearch) {
        String sParam = "";
        if (CollectionUtils.isNotEmpty(listTagIds)) {
            for (String tagId : listTagIds) {
                if (StringUtils.isNotEmpty(sParam)) {
                    sParam += " ,";
                }
                sParam += SearchStatementConstants.addQuote(tagId);
            }
        }
        if (isUniversalSearch && sParam.isEmpty()) {
            return "";
        }
        return MessageFormat.format(SearchStatementConstants.STATEMENT_SEARCH_ARTICLE_BY_TAG_PATTERN, sParam.isEmpty() ? "''" : sParam);
    }

    private static String generateLabelStatement(List<ArticleLabelType> listArticleLabelType) {
        String sParam = "";
        if (CollectionUtils.isNotEmpty(listArticleLabelType)) {
            for (ArticleLabelType articleLabelType : listArticleLabelType) {
                if (StringUtils.isNotEmpty(sParam)) {
                    sParam += " ,";
                }
                sParam += SearchStatementConstants.addQuote(articleLabelType.getLabel());
            }
        }
        return MessageFormat.format(SearchStatementConstants.STATEMENT_SEARCH_ARTICLE_BY_LABEL_PATTERN, sParam.isEmpty() ? "''" : sParam);
    }

    private static String generateQuery(String pathStatement, String labelStatement, String templateStatement, String statement, String orderStatement) {
        Object params[] = new Object[DEFAULT_ARTICLE_PARAM_SIZE];
        params[0] = pathStatement;
        params[1] = labelStatement;
        params[2] = templateStatement;
        params[3] = statement + orderStatement;

        return MessageFormat.format(SearchStatementConstants.ARTICLE_QUERY_PATTERN, params).trim();
    }

   
    private static String generateTemplateStatement() {
    	Object[] templates = {SearchStatementConstants.addQuote(ArticleConstants.ARTICLE_TEMPLATE),SearchStatementConstants.addQuote(ArticleConstants.SEA_ARTICLE_TEMPLATE)};
    	LOGGER.info("Template clause Old>>"+MessageFormat.format(SearchStatementConstants.STATEMENT_SEARCH_ARTICLE_BY_TEMPLATE_PATTERN,
        		templates));
        return MessageFormat.format(SearchStatementConstants.STATEMENT_SEARCH_ARTICLE_BY_TEMPLATE_PATTERN,
        		templates);
        
        
    }

    private static String generateOrderStatement(String order) {
        if (StringUtils.isEmpty(order)) {
            order = OrderType.DESC.name();
        }
        return MessageFormat.format(SearchStatementConstants.ORDER_BY_PATTERN, order);
    }

    private static String generatePathStatement(String path) {
        if (StringUtils.isEmpty(path)) {
            throw new RuntimeException("Search path can no longer be empty, since there's no default.");
        }
        return path;
    }
}
