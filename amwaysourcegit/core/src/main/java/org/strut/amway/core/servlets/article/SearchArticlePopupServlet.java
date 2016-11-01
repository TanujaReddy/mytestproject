package org.strut.amway.core.servlets.article;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.SearchType;
import org.strut.amway.core.json.parser.JsonParser;
import org.strut.amway.core.json.parser.article.SearchArticlePopupJsonParser;
import org.strut.amway.core.model.ArticleSearchResult;
import org.strut.amway.core.model.ArticlesCriteria;
import org.strut.amway.core.model.TagCriteria;
import org.strut.amway.core.services.ArticleQueryService;
import org.strut.amway.core.services.ArticleQueryServiceForPersonalization;
import org.strut.amway.core.services.TagService;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.ArticleLabelUtils;

@SuppressWarnings("serial")
@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.search.popup", extensions = "json", methods = "GET")
public class SearchArticlePopupServlet extends AbstractArticleJsonServlet {

	private static final boolean EXACT_MATCH = true;

	@Reference
	private ArticleQueryService articleQueryService;
	
	/*********Start of Changes for AmwayToday v3.0**************/
	@Reference
	private ArticleQueryServiceForPersonalization articleQueryServiceForPersonalization;
	
	/*********End of Changes for AmwayToday v3.0**************/

	@Reference
	private TagService tagService;

	private JsonParser<ArticleSearchResult> jsonParser;

	@Override
	public void init() throws ServletException {
		super.init();
		jsonParser = new SearchArticlePopupJsonParser();
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
	protected ArticlesCriteria buildArticleCriteriaFromRequest(
			final SlingHttpServletRequest request) {
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
		final String localeCode = getLocaleCode(request);
		final List<String> tagIds = resovleTagIdsByTitle(
				request.getResourceResolver(), new TagCriteria.Builder()
						.setLocaleCode(localeCode).setTitle(searchText)
						.setExactMatch(EXACT_MATCH).build());
		final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils
				.getArticleLabelBySession(request.getSession());
		/*********Start of Changes for AmwayToday v3.0**************/
		final List<String> articleLabels = ArticleLabelUtils
				.getArticleLabelByCookies(request);

		final ArticlesCriteria criteria = new ArticlesCriteria.Builder()
				.setPath(getPathOfHomepage(request))
				.setOpsStartDate(ArticleConstants.OPS_GREATER_THAN_OR_EQUAL)
				.setOpsEndDate(ArticleConstants.OPS_LESS_THAN_OR_EQUAL)
				.setOffset(ArticleConstants.DEFAULT_OFFSET_VALUE)
				.setLimit(ArticleConstants.DEFAULT_SEARCH_POPUP_LIMIT_VALUE)
				.setTagIds(tagIds).setArticleLabelTypes(articleLabelTypes)
				.setArticleLabels(articleLabels)
				.setSearchType(SearchType.SEARCH_TAG).build();
		return criteria;
		/*********End of Changes for AmwayToday v3.0**************/
	}
	
	/*********Start of Changes for AmwayToday v3.0**************/

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
