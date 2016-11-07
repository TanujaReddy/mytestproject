package org.strut.amway.core.json.parser.article;

import java.net.URISyntaxException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.strut.amway.core.json.parser.JsonParser;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.Constants;
import org.strut.amway.core.util.LinkTransformerUtils;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.foundation.Image;

public abstract class AbstractArticleJsonParser implements JsonParser<ArticleSearchResult> {

    protected void setCategoryInfo(final ResourceResolver resourceResolver, final Page category, final JSONObject json) throws JSONException,
            URISyntaxException {
        json.put("category", category.getTitle());
        json.put("categoryType", getCategoryType(category));
        json.put("categoryLink", LinkTransformerUtils.transformPath(resourceResolver, category.getPath()) + Constants.HTML_EXTENSION);
    }

    protected String getCategoryType(final Page category) {
        return category.getProperties().get(CategoryConstants.CATEGORY_TYPE, String.class);
    }

    protected boolean isMainBloggingCategory(final Page category) throws WCMException, JSONException {
        final String categoryType = getCategoryType(category);
        if (CategoryConstants.MAIN_BLOGGING_CATEGORY.equalsIgnoreCase(categoryType)) {
            return true;
        }
        return false;
    }

    protected void setArticleImageLink(final ResourceResolver resourceResolver, final Page article, final JSONObject json) throws JSONException,
            URISyntaxException {
        final Resource imgResource = article.getContentResource("image");
        if (imgResource != null) {
            Image image = new Image(imgResource);
            if (image.hasContent()) {
                json.put("imageLink", LinkTransformerUtils.transformPath(resourceResolver, article.getPath()) + ".img.png" + image.getSuffix());
            }
        }
    }

}
