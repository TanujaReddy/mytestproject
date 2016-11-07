package org.strut.amway.core.servlets.article;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.strut.amway.core.util.ArticleConstants;

@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.article.update.views", extensions = "json", methods = {"GET"})
public class ArticleUpdateViewsServlet extends ArticleStatisticsServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void handle(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws Exception {
		articleStatisticsService.incrementStatistic(ArticleConstants.STATISTIC_VIEWS, request.getResource().getPath());
    	
		writeArticleStatsJson(request, response);
    }
}
