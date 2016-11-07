package org.strut.amway.core.servlets.article;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.servlets.AbstractJsonServlet;
import org.strut.amway.core.util.ArticleConstants;

@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.article.get.statistics", extensions = "json", methods = "GET")
public class ArticleStatisticsServlet extends AbstractJsonServlet {
	private static final long serialVersionUID = 1L;

    protected static final Logger LOGGER = LoggerFactory.getLogger(ArticleStatisticsServlet.class);
	
	@Reference
	protected ArticleStatisticsService articleStatisticsService;

	@Override
	protected void handle(SlingHttpServletRequest request, SlingHttpServletResponse response) throws Exception {
		writeArticleStatsJson(request, response);
    }
	
	protected void writeArticleStatsJson(SlingHttpServletRequest request, SlingHttpServletResponse response) throws Exception {
        String articlePath = request.getResource().getPath();
        if (articlePath != null && !StringUtils.EMPTY.equals(articlePath)) {

            long likes = articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_LIKES, articlePath);
            long views = articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, articlePath);
            
            JSONObject articleStatsJSON = new JSONObject();
            articleStatsJSON.put(ArticleConstants.STATISTIC_LIKES, likes);
            articleStatsJSON.put(ArticleConstants.STATISTIC_VIEWS, views);
            
            String articleStats = articleStatsJSON.toString();
            
            LOGGER.debug("amway.article.get.statistics : {} : {}", articlePath, articleStats);
            
            writeJsonToResponse(response, articleStats);
        }	
	}
}
