package org.strut.amway.core.json.parser.article;

import java.net.URISyntaxException;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.util.Constants;
import org.strut.amway.core.util.LinkTransformerUtils;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;

public class SearchArticlePopupJsonParser extends AbstractArticleJsonParser {

    @Override
    public String parse(final ResourceResolver resourceResolver, final ArticleSearchResult articleSearchResult) throws Exception {
        final JSONObject json = new JSONObject();
        final JSONArray jsonArticles = new JSONArray();
        if (articleSearchResult.getPages() != null) {
            for (Page article : articleSearchResult.getPages()) {
                jsonArticles.put(createJsonForEachArticle(resourceResolver, article));
            }
        }
        json.put("articles", jsonArticles);
        return json.toString();
    }

    private JSONObject createJsonForEachArticle(final ResourceResolver resourceResolver, final Page article) throws JSONException, WCMException,
            URISyntaxException {
        final JSONObject json = new JSONObject();
        json.put("title", article.getTitle());
        json.put("link", LinkTransformerUtils.transformPath(resourceResolver, article.getPath()) + Constants.HTML_EXTENSION);
        setArticleImageLink(resourceResolver, article, json);

        final Page category = article.getParent(2);
        if (category != null) {
            setCategoryInfo(resourceResolver, category, json);
        } else {
            throw new IllegalStateException("Category can not null");
        }
        return json;
    }

}
