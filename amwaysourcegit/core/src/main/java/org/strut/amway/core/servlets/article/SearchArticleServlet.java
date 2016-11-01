package org.strut.amway.core.servlets.article;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.CharSet;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.OrderType;
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.json.parser.JsonParser;
import org.strut.amway.core.json.parser.article.ArticleGridJsonParser;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.model.TagCriteria;
import org.strut.amway.core.services.ArticleQueryService;
import org.strut.amway.core.services.ArticleQueryServiceForPersonalization;
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.services.TagService;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.ArticleLabelUtils;


@SuppressWarnings("serial")
@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.search.universal", extensions = "json", methods = "GET")
public class SearchArticleServlet extends AbstractArticleJsonServlet {

    private static final boolean PARTIAL_MATCH = false;
    
    private static final Logger log = LoggerFactory.getLogger(SearchArticleServlet.class);

    @Reference
    private ArticleQueryService articleQueryService;
    
    /*********Start of Changes for AmwayToday v3.0**************/
    @Reference
	private ArticleQueryServiceForPersonalization articleQueryServiceForPersonalization;
    
    /*********End of Changes for AmwayToday v3.0**************/

    @Reference
    private TagService tagService;

    @Reference
    private ArticleStatisticsService articleStatisticsService;

    private JsonParser<ArticleSearchResult> jsonParser;

    @Override
    public void init() throws ServletException {
        super.init();
        jsonParser = new ArticleGridJsonParser(articleStatisticsService);
    }

    @Override
    public void destroy() {
        super.destroy();
        jsonParser = null;
    }

    @Override
    protected ArticleQueryService getArticleQueryService() {
        return articleQueryService;
    }
    
    /*********Start of Changes for AmwayToday v3.0**************/
    @Override
	protected ArticleQueryServiceForPersonalization getArticleQueryServiceForPersonalization() {
		return articleQueryServiceForPersonalization;
	}
    /*********End of Changes for AmwayToday v3.0**************/

    @Override
    protected TagService getTagService() {
        return tagService;
    }

    @Override
    protected JsonParser<ArticleSearchResult> getArticleJsonParser() {
        return jsonParser;
    }

    @Override
    protected ArticlesCriteria buildArticleCriteriaFromRequest(final SlingHttpServletRequest request) {
    	
        String searchText;
        String text = null;
        try {
        	String param = request.getParameter("text");
        	if(param != null){
        		text = URLDecoder.decode(request.getParameter("text").trim(),"UTF-8");
        	} 
            searchText = text; 
        } catch (IllegalArgumentException | UnsupportedEncodingException ex) {
            searchText = null;
        }

        String tagId;
        try {
            tagId = getTagId(request);
        } catch (IllegalArgumentException ex) {
            tagId = null;
        }

        final Long offset = getOffset(request);
        final Long limit = getLimit(request);
        final String pathOfHomePage = getPathOfHomepage(request);
        final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils.getArticleLabelBySession(request.getSession());
        /*********Start of Changes for AmwayToday v3.0**************/
        final List<String> articleLabels = ArticleLabelUtils
				.getArticleLabelByCookies(request);
        if (searchText != null) {
            return createCriteriaForSearchFullText(request, pathOfHomePage, searchText, articleLabelTypes,articleLabels, offset, limit);
        }
        if (tagId != null) {
            return createCriteriaForSearchByTagId(request, pathOfHomePage, tagId, articleLabelTypes, articleLabels, offset, limit);
        }        
        /*********End of Changes for AmwayToday v3.0**************/
        throw new IllegalStateException("search request is invalid");
    }

    /*********Start of Changes for AmwayToday v3.0**************/
    private ArticlesCriteria createCriteriaForSearchFullText(final SlingHttpServletRequest request, final String pathOfHomePage, final String searchFullText,
            final List<ArticleLabelType> articleLabelTypes, final List<String> articleLabels, final Long offset, final Long limit) {
        ArticlesCriteria criteria = null;
        final String localeCode = getLocaleCode(request);
        final List<String> tagIds =
                resovleTagIdsByTitle(request.getResourceResolver(),
                        new TagCriteria.Builder().setLocaleCode(localeCode).setTitle(searchFullText).setExactMatch(PARTIAL_MATCH).build());
        if (isQueryByMonth(request)) {
            final String startDate = getStartDate(request);
            final String endDate = getEndDate(request);
            final OrderType orderType = getOrder(request);
            criteria =
                    new ArticlesCriteria.Builder()
                            .setPath(pathOfHomePage).setOffset(offset).setLimit(limit).setFullText(searchFullText).setTagIds(tagIds).setStartDate(startDate)
                            .setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL).setEndDate(endDate)
                            .setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOrder(orderType.name()).setArticleLabelTypes(articleLabelTypes).setArticleLabels(articleLabels)
                            .setSearchType(SearchType.SEARCH_FULLTEXT).build();
        } else {
            criteria =
                    new ArticlesCriteria.Builder()
                            .setPath(pathOfHomePage).setOffset(offset).setLimit(limit).setFullText(searchFullText).setTagIds(tagIds)
                            .setArticleLabelTypes(articleLabelTypes).setArticleLabels(articleLabels).setSearchType(SearchType.SEARCH_FULLTEXT).build();
        }
        /*********End of Changes for AmwayToday v3.0**************/
        return criteria;
    }

    /*********Start of Changes for AmwayToday v3.0**************/
    private ArticlesCriteria createCriteriaForSearchByTagId(final SlingHttpServletRequest request, final String pathOfHomePage, final String tagId,
            final List<ArticleLabelType> articleLabelTypes,final List<String> articleLabels, final Long offset, final Long limit) {
        ArticlesCriteria criteria = null;
        final List<String> tagIds = Arrays.asList(tagId);
        if (isQueryByMonth(request)) {
            final String startDate = getStartDate(request);
            final String endDate = getEndDate(request);
            final OrderType orderType = getOrder(request);
            criteria =
                    new ArticlesCriteria.Builder()
                            .setPath(pathOfHomePage).setOffset(offset).setLimit(limit).setTagIds(tagIds).setStartDate(startDate)
                            .setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL).setEndDate(endDate)
                            .setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOrder(orderType.name()).setArticleLabelTypes(articleLabelTypes)
                            .setArticleLabels(articleLabels)
                            .setSearchType(SearchType.SEARCH_TAG).build();
        } else {
            criteria =
                    new ArticlesCriteria.Builder()
                            .setPath(pathOfHomePage).setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL)
                            .setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOffset(offset).setLimit(limit).setTagIds(tagIds)
                            .setArticleLabelTypes(articleLabelTypes)
                            .setArticleLabels(articleLabels)
                            .setSearchType(SearchType.SEARCH_TAG).build();
        }
        
        /*********End of Changes for AmwayToday v3.0**************/
        return criteria;
    }
    
    /*********Start of Changes for AmwayToday v3.0**************/
    /***********Override the handle() method for ATP v3.0***********/
    @Override
	protected void handle(final SlingHttpServletRequest request,
			final SlingHttpServletResponse response) throws Exception {
		final ArticlesCriteria criteria = buildArticleCriteriaFromRequest(request);
		ArticleSearchResult articleSearchResult = null;
		if (ArticleLabelUtils.isPersonalizationCookiePresent(request)) {
			articleSearchResult = getArticleQueryServiceForPersonalization()
					.search(request.getResourceResolver(), criteria, request);
		} else {
			articleSearchResult = getArticleQueryService().search(
					request.getResourceResolver(), criteria);
		}

		writeJsonToResponse(
				response,
				getArticleJsonParser().parse(request.getResourceResolver(),
						articleSearchResult));
	}

    /*********End of Changes for AmwayToday v3.0**************/

}
