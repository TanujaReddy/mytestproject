package org.strut.amway.core.servlets.article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;

import org.apache.commons.collections.CollectionUtils;
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
import org.strut.amway.core.util.DateTimeUtils;

import com.day.cq.wcm.api.Page;

@SuppressWarnings("serial")
@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.most.popular", extensions = "json", methods = "GET")
public class MostPopularArticleServlet extends AbstractArticleJsonServlet {


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
    protected void handle(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws Exception {
        final ArticlesCriteria criteria = buildArticleCriteriaFromRequest(request);
        /*********Start of Changes for AmwayToday v3.0**************/
        ArticleSearchResult articleSearchResult = null;
        
        if(ArticleLabelUtils.isPersonalizationCookiePresent(request)){        	
        	articleSearchResult = getArticleQueryServiceForPersonalization().search(request.getResourceResolver(), criteria,request);
        }
        else
        {
        	articleSearchResult = getArticleQueryService().search(request.getResourceResolver(), criteria);
        }
        
        /*********End of Changes for AmwayToday v3.0**************/

        final Long limit = getLimit(request);

        List<Page> mostPopularArticles = articleSearchResult.getPages();
        sortArtilceByImpression(mostPopularArticles, limit);

        final ArticleSearchResult result = new ArticleSearchResult();
        result.setOffset(articleSearchResult.getOffset());
        result.setLimit(limit);
        result.setPages(mostPopularArticles);

        writeJsonToResponse(response, getArticleJsonParser().parse(request.getResourceResolver(), result));
    }

    @Override
    protected ArticlesCriteria buildArticleCriteriaFromRequest(final SlingHttpServletRequest request) {
        String startDate = DateTimeUtils.getStartDateTime(ArticleConstants.DEFAULT_MINUS_MONTH).toString();
        final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils.getArticleLabelBySession(request.getSession());
        /*********Start of Changes for AmwayToday v3.0**************/
        final List<String> articleLabels = ArticleLabelUtils.getArticleLabelByCookies(request);
        final ArticlesCriteria criteria =
                new ArticlesCriteria.Builder()
                        .setPath(getPathOfHomepage(request)).setOffset(0L).setLimit(Long.MAX_VALUE).setStartDate(startDate)
                        .setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL).setOrder(OrderType.DESC.name()).setArticleLabelTypes(articleLabelTypes).setArticleLabels(articleLabels)
                        .setSearchType(SearchType.SEARCH_DATE_TIME).build();
        /*********End of Changes for AmwayToday v3.0**************/
        return criteria;
    }

    private void sortArtilceByImpression(List<Page> articlesByDateTime, Long limit) {
        if (CollectionUtils.isEmpty(articlesByDateTime)) {
            return;
        }

        Map<Long, List<Page>> mostView = new TreeMap<Long, List<Page>>(Collections.reverseOrder());

        for (Page page : articlesByDateTime) {
            Long impression = articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, page.getPath());

            if (mostView.containsKey(impression)) {
                mostView.get(impression).add(page);
            } else {
                List<Page> list = new ArrayList<Page>();
                list.add(page);
                mostView.put(impression, list);
            }
        }

        articlesByDateTime.clear();
        for (List<Page> pages : mostView.values()) {
            if (pages.size() <= limit) {
                articlesByDateTime.addAll(pages);
                limit = limit - pages.size();
                if (limit <= 0) {
                    return;
                }
            } else {
                for (Page page : pages) {
                    articlesByDateTime.add(page);
                    limit--;
                    if (limit <= 0) {
                        return;
                    }
                }
            }

        }
    }

	
}
