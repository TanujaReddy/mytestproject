package org.strut.amway.core.servlets.article;

import java.util.List;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.ButtonType;
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
@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.article.nextAndPrev", extensions = "json", methods = "GET")
public class NextAndPrevArticleServlet extends AbstractArticleJsonServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(NextAndPrevArticleServlet.class);

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
        String createDate = getCreatedDate(request);
        String path = getPathOfHomepage(request);
        ButtonType type = getType(request);
        final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils.getArticleLabelBySession(request.getSession());
        /*********Start of Changes for AmwayToday v3.0**************/
        final List<String> articleLabels = ArticleLabelUtils.getArticleLabelByCookies(request);
        switch (type) {
        case NEXT:
            return new ArticlesCriteria.Builder()
                    .setPath(path).setOffset(ArticleConstants.DEFAULT_OFFSET_VALUE).setLimit(ArticleConstants.DEFAULT_NEXT_OR_PREV_ARTICLE_LIMIT_VALUE)
                    .setStartDate(createDate).setOpsStartDate(ArticleConstants.OPS_GREATER_THAN).setOrder(OrderType.ASC.name())
                    .setArticleLabelTypes(articleLabelTypes).setArticleLabels(articleLabels).setSearchType(SearchType.SEARCH_DATE_TIME).build();

        case PREV:
            return new ArticlesCriteria.Builder()
                    .setPath(path).setOffset(ArticleConstants.DEFAULT_OFFSET_VALUE).setLimit(ArticleConstants.DEFAULT_NEXT_OR_PREV_ARTICLE_LIMIT_VALUE)
                    .setEndDate(createDate).setOpsEndDate(ArticleConstants.OPS_LESS_THAN).setOrder(OrderType.DESC.name())
                    .setArticleLabelTypes(articleLabelTypes).setArticleLabels(articleLabels).setSearchType(SearchType.SEARCH_DATE_TIME).build();
        }
        
        /*********End of Changes for AmwayToday v3.0**************/
        LOGGER.warn("Button type is null :" + type);
        return null;
    }
    
    /*********Start of Changes for AmwayToday v3.0**************/
    /*********Override handle method for AmwayToday v3.0**************/
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
