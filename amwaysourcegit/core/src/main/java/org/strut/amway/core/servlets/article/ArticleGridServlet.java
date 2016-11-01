package org.strut.amway.core.servlets.article;

import java.util.List;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.OrderType;
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.json.parser.JsonParser;
import org.strut.amway.core.json.parser.article.ArticleGridJsonParser;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.services.ArticleQueryService;
import org.strut.amway.core.services.ArticleQueryServiceForPersonalization;
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.services.TagService;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.ArticleLabelUtils;


@SuppressWarnings("serial")
@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.article.grid", extensions = "json", methods = "GET")
public class ArticleGridServlet extends AbstractArticleJsonServlet {

    @Reference
    private ArticleQueryService articleQueryService;

    @Reference
    private TagService tagService;

    @Reference
    private ArticleStatisticsService articleStatisticsService;
    
    /*********Start of Changes for AmwayToday v3.0**************/
    
    @Reference
    private ArticleQueryServiceForPersonalization articleQueryServiceForPersonalization;
    
    /*********End of Changes for AmwayToday v3.0**************/

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
		return this.articleQueryServiceForPersonalization;
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
        final Long offset = getOffset(request);
        final Long limit = getLimit(request);
        final String path = request.getResource().getPath();

        ArticlesCriteria criteria = null;
        final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils.getArticleLabelBySession(request.getSession());
        /*********Start of Changes for AmwayToday v3.0**************/
        final List<String> articleLabels = ArticleLabelUtils.getArticleLabelByCookies(request);
        /*********End of Changes for AmwayToday v3.0**************/
        if (isQueryByMonth(request)) {
            final String startDate = getStartDate(request);
            final String endDate = getEndDate(request);
            final OrderType orderType = getOrder(request);
            /*********Start of Changes for AmwayToday v3.0**************/
            criteria =
                    new ArticlesCriteria.Builder()
                            .setPath(path).setOffset(offset).setLimit(limit).setStartDate(startDate)
                            .setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL).setEndDate(endDate)
                            .setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL).setOrder(orderType.name()).setArticleLabelTypes(articleLabelTypes).setArticleLabels(articleLabels)
                            .setSearchType(SearchType.SEARCH_DATE_TIME).build();
            /*********End of Changes for AmwayToday v3.0**************/
        } else {
        	/*********Start of Changes for AmwayToday v3.0**************/
            criteria =
                    new ArticlesCriteria.Builder()
                            .setPath(path).setOffset(offset).setLimit(limit).setArticleLabelTypes(articleLabelTypes).setArticleLabels(articleLabels)
                            .setSearchType(SearchType.SEARCH_BY_SUB_CATEGORY).build();
            /*********End of Changes for AmwayToday v3.0**************/
        }
        return criteria;
    }

    /*********Start of Changes for AmwayToday v3.0**************/
    @Override
    protected void handle(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws Exception {
        final ArticlesCriteria criteria = buildArticleCriteriaFromRequest(request);
        
        ArticleSearchResult articleSearchResult = null;
        if(ArticleLabelUtils.isPersonalizationCookiePresent(request)){        	
        	articleSearchResult = getArticleQueryServiceForPersonalization().search(request.getResourceResolver(), criteria,request);
        }
        else
        {
        	articleSearchResult = getArticleQueryService().search(request.getResourceResolver(), criteria);
        }
        
        writeJsonToResponse(response, getArticleJsonParser().parse(request.getResourceResolver(), articleSearchResult));
    }
    
    /*********End of Changes for AmwayToday v3.0**************/
}
