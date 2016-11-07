package org.strut.amway.core.json.parser.article;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.ArticleUtils;
import org.strut.amway.core.util.Constants;
import org.strut.amway.core.util.DateTimeUtils;
import org.strut.amway.core.util.LinkTransformerUtils;

import java.net.URISyntaxException;
import java.util.GregorianCalendar;

public class ArticleGridJsonParser extends AbstractArticleJsonParser {

    private static final String LIKE_NUMBER = "likeNumber";

    private final ArticleStatisticsService articleStatisticsService;

    public ArticleGridJsonParser(final ArticleStatisticsService articleStatisticsService) {
        this.articleStatisticsService = articleStatisticsService;
    }

    @Override
    public String parse(final ResourceResolver resourceResolver, final ArticleSearchResult articleSearchResult) throws JSONException, WCMException,
            URISyntaxException {
        final JSONObject json = new JSONObject();
        final JSONArray jsonArticles = new JSONArray();
        for (Page article : articleSearchResult.getPages()) {
            jsonArticles.put(createJsonForEachArticle(resourceResolver, article));
        }
        json.put("offset", articleSearchResult.getOffset());
        json.put("limit", articleSearchResult.getLimit());
        json.put("articles", jsonArticles);
        return json.toString();
    }

    private JSONObject createJsonForEachArticle(final ResourceResolver resourceResolver, final Page article) throws JSONException, WCMException,
            URISyntaxException {
        final JSONObject json = new JSONObject();
        json.put("title", article.getTitle());
        json.put("tags", ArticleUtils.extractTags(article));
        String snippet = ArticleUtils.extractPlaintextSnippet(article);
        json.put("snippet", StringUtils.defaultString(snippet, ""));
        json.put("link", LinkTransformerUtils.transformPath(resourceResolver, article.getPath()) + Constants.HTML_EXTENSION);
        setArticleImageLink(resourceResolver, article, json);
        setArticleVideoUrl(article, json);
        setLastPublishedDate(article, json);
        setArticleLikeNumber(article, json);

        final Page category = article.getParent(2);
        if (category != null) {
            setCategoryInfo(resourceResolver, category, json);
            if (isMainBloggingCategory(category)) {
                setArticleImpression(article, json);
            }
        } else {
            throw new IllegalStateException("Category can not null");
        }
        return json;
    }

    private void setArticleVideoUrl(final Page article, final JSONObject json) throws JSONException {
        final String videoUrl = article.getProperties().get("video_url", String.class);
        json.put("VideoUrl", videoUrl);
    }

    private void setLastPublishedDate(final Page article, final JSONObject json) throws JSONException {
        final GregorianCalendar lastPublishedDate = ArticleUtils.getPublishedDate(article);
        if (lastPublishedDate != null) {
            json.put("lastPublishedDate", DateTimeUtils.parseToUTC(lastPublishedDate));
        }
    }

    private void setArticleImpression(final Page article, final JSONObject json) throws WCMException, JSONException {
    	json.put("impression", articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, article.getPath()));
    }

    private void setArticleLikeNumber(final Page article, final JSONObject json) throws WCMException, JSONException {
    	json.put(LIKE_NUMBER, articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_LIKES, article.getPath()));
    }
}
