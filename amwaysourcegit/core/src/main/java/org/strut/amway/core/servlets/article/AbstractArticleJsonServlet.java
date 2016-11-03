package org.strut.amway.core.servlets.article;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.joda.time.DateTime;
import org.strut.amway.core.enumeration.ButtonType;
import org.strut.amway.core.enumeration.OrderType;
import org.strut.amway.core.json.parser.JsonParser;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.model.TagCriteria;
import org.strut.amway.core.services.ArticleQueryService;
import org.strut.amway.core.services.ArticleQueryServiceForPersonalization;

import org.strut.amway.core.services.TagService;
import org.strut.amway.core.servlets.AbstractJsonServlet;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.DateTimeUtils;
import org.strut.amway.core.util.SearchStatementConstants;
import org.strut.amway.core.util.TagUtils;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;

@SuppressWarnings("serial")
public abstract class AbstractArticleJsonServlet extends AbstractJsonServlet {

    protected abstract ArticleQueryService getArticleQueryService();
    
    protected abstract ArticleQueryServiceForPersonalization getArticleQueryServiceForPersonalization();

    protected abstract TagService getTagService();

    protected abstract JsonParser<ArticleSearchResult> getArticleJsonParser();

    protected abstract ArticlesCriteria buildArticleCriteriaFromRequest(final SlingHttpServletRequest request);

    @Override
    protected void handle(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws Exception {
        final ArticlesCriteria criteria = buildArticleCriteriaFromRequest(request);
        final ArticleSearchResult articleSearchResult = getArticleQueryService().search(request.getResourceResolver(), criteria);
        writeJsonToResponse(response, getArticleJsonParser().parse(request.getResourceResolver(), articleSearchResult));
    }

    protected Long getOffset(final SlingHttpServletRequest request) {
        final String offsetParam = request.getParameter(ArticleConstants.OFFSET_PARAM);
        Long offset = ArticleConstants.DEFAULT_OFFSET_VALUE;
        try {
            if (offsetParam != null) {
                offset = Long.valueOf(offsetParam);
            }
        } catch (NumberFormatException e) {
            // skip
        }
        return offset;
    }

    protected Long getLimit(final SlingHttpServletRequest request) {
        final String limitParam = request.getParameter(ArticleConstants.LIMIT_PARAM);
        Long limit = ArticleConstants.DEFAULT_LIMIT_VALUE;
        try {
            if (limitParam != null) {
                limit = Long.valueOf(limitParam);
            }
        } catch (NumberFormatException e) {
            // skip
        }
        return limit;
    }

    protected String getSearchText(final SlingHttpServletRequest request) {
    	String searchTextParam = "";
    	
       searchTextParam = StringUtils.defaultString(request.getParameter(ArticleConstants.SEARCH_TEXT_PARAM));
        if (StringUtils.isEmpty(searchTextParam)) {
            throw new IllegalArgumentException("search Text Param can not be empty");
        }
        return searchTextParam.trim();
    	
    	
    }

    protected String getTagId(final SlingHttpServletRequest request) {
        final String tagIdParam = request.getParameter(ArticleConstants.TAG_ID_PARAM);
        if (StringUtils.isEmpty(tagIdParam)) {
            throw new IllegalArgumentException("tag id param can not be empty");
        }
        return tagIdParam.trim();
    }

    protected String getStartDate(final SlingHttpServletRequest request) {
        String startDateParam = request.getParameter(ArticleConstants.START_DATE_PARAM);
        if (StringUtils.isEmpty(startDateParam)) {
            startDateParam = null;
        } else {
            validateDateTime(startDateParam);
        }
        return startDateParam;
    }

    protected String getEndDate(final SlingHttpServletRequest request) {
        String endDateParam = request.getParameter(ArticleConstants.END_DATE_PARAM);
        if (StringUtils.isEmpty(endDateParam)) {
            endDateParam = null;
        } else {
            validateDateTime(endDateParam);
        }
        return endDateParam;
    }

    protected OrderType getOrder(final SlingHttpServletRequest request) {
        final String orderParam = request.getParameter(ArticleConstants.ORDER_PARAM);
        if (StringUtils.isEmpty(orderParam)) {
            throw new IllegalArgumentException("orderParam Param is invalid");
        }
        final OrderType orderType = OrderType.valueOf(orderParam);
        return orderType;
    }

    protected boolean isQueryByMonth(final SlingHttpServletRequest request) {
        final String startDateParam = request.getParameter(ArticleConstants.START_DATE_PARAM);
        final String endDateParam = request.getParameter(ArticleConstants.END_DATE_PARAM);
        return startDateParam != null || endDateParam != null;
    }

    protected List<String> resovleTagIdsByTitle(ResourceResolver resourceResolver, TagCriteria tagCriteria) {
        final List<Tag> tags = getTagService().search(resourceResolver, tagCriteria);
        return TagUtils.getTagIds(tags);
    }

    protected String getPathOfHomepage(final SlingHttpServletRequest request) {
        final Page currentPage = request.getResource().adaptTo(Page.class);
        final Page homePage = currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL);
        return homePage.getPath();
    }

    protected String getCreatedDate(final SlingHttpServletRequest request) {
        String createdDateParam = request.getParameter(ArticleConstants.CREATED_DATE_PARAM);
        if (StringUtils.isEmpty(createdDateParam)) {
            return null;
        }
        return DateTimeUtils.toServerTime(createdDateParam).toString();
    }

    protected ButtonType getType(final SlingHttpServletRequest request) {
        String typeParam = request.getParameter(ArticleConstants.TYPE_PARAM);
        if (StringUtils.isEmpty(typeParam)) {
            throw new IllegalArgumentException("type Param is invalid");
        }
        final ButtonType type = ButtonType.valueOf(typeParam);
        return type;
    }

    protected String getLocaleCode(final SlingHttpServletRequest request) {
        final Page currentPage = request.getResource().adaptTo(Page.class);
        final Locale locale = currentPage.getLanguage(false);
        if (locale != null) {
            return locale.getLanguage();
        }
        return SearchStatementConstants.DEFAULT_LOCALE_CODE;
    }

    private void validateDateTime(final String datetimeInStr) {
        try {
            // ensure the date time format
            new DateTime(datetimeInStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("datetime Param is invalid");
        }
    }

}
