package org.strut.amway.core.servlets.article;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.strut.amway.core.util.ArticleConstants;

import javax.servlet.ServletException;
import java.io.IOException;

@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.article.likes", extensions = "json", methods = {"GET","POST"})
public class ArticleLikesServlet extends ArticleStatisticsServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            writeArticleStatsJson(request, response);
        } catch (final Exception e) {
            LOGGER.error("Error while processing the GET request " + ExceptionUtils.getStackTrace(e));
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            articleStatisticsService.incrementStatistic(ArticleConstants.STATISTIC_LIKES, request.getResource().getPath());
            writeArticleStatsJson(request, response);
        } catch (final Exception e) {
            LOGGER.error("Error while processing the POST request " + ExceptionUtils.getStackTrace(e));
            handleError(response, e);
        }
    }
}
