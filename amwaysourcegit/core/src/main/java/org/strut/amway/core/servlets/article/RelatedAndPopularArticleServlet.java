package org.strut.amway.core.servlets.article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.strut.amway.core.util.PageUtils;
import org.strut.amway.core.util.TagUtils;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;

@SuppressWarnings("serial")
@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.related.popular", extensions = "json", methods = "GET")
public class RelatedAndPopularArticleServlet extends AbstractArticleJsonServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatedAndPopularArticleServlet.class);

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
    protected void handle(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws Exception {
        // 0-index: related article. 1-index: popular article
        final Result[] relatedAndPopularArticles = new Result[2];
        extractRelatedAndPopularArticleSetByUser(request, relatedAndPopularArticles);
        extractRelatedAndPopularAutomatically(request, relatedAndPopularArticles);

        final List<Page> resultPages = new ArrayList<Page>();
        for (int i = 0; i < relatedAndPopularArticles.length; i++) {
            if (relatedAndPopularArticles[i] != null && relatedAndPopularArticles[i].isPermit()) {
                resultPages.add(relatedAndPopularArticles[i].getPage());
            }
        }
        final ArticleSearchResult result = new ArticleSearchResult();
        result.setOffset(0L);
        result.setLimit(Long.MAX_VALUE);
        result.setPages(resultPages);
        writeJsonToResponse(response, getArticleJsonParser().parse(request.getResourceResolver(), result));
    }

    @Override
    protected ArticlesCriteria buildArticleCriteriaFromRequest(final SlingHttpServletRequest request) {
        final Page currentPage = request.getResource().adaptTo(Page.class);
        final String pathOfCurrentPage = PageUtils.getContentPath(currentPage);
        final Tag[] tags = currentPage.getTags();
        final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils.getArticleLabelBySession(request.getSession());
        /*********Start of Changes for AmwayToday v3.0**************/
        final List<String> articleLabels = ArticleLabelUtils.getArticleLabelByCookies(request);
        final ArticlesCriteria criteria =
                new ArticlesCriteria.Builder()
                        .setPath(getPathOfHomepage(request)).setOffset(0L).setLimit(Long.MAX_VALUE).setTagIds(TagUtils.getTagIds(tags))
                        .setIgnorePath(pathOfCurrentPage).setOrder(OrderType.DESC.name()).setArticleLabelTypes(articleLabelTypes).setArticleLabels(articleLabels)
                        .setSearchType(SearchType.SEARCH_TAG).build();
        /*********End of Changes for AmwayToday v3.0**************/
        return criteria;
    }

    private void extractRelatedAndPopularArticleSetByUser(final SlingHttpServletRequest request, final Result[] relatedAndPopularArticles) {
        final Page currentPage = request.getResource().adaptTo(Page.class);
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final String relatedArticlePath = currentPage.getProperties().get(ArticleConstants.RELATED_ARTICLE, String.class);
        final String popularArticlePath = currentPage.getProperties().get(ArticleConstants.POPULAR_ARTICLE, String.class);

        final List<ArticleLabelType> articleLabelTypesForSession = ArticleLabelUtils.getArticleLabelBySession(request.getSession());
        if (isExistingPath(resourceResolver, relatedArticlePath)) {
            relatedAndPopularArticles[0] = buildResult(resourceResolver, articleLabelTypesForSession, relatedArticlePath);
        }
        if (isExistingPath(resourceResolver, popularArticlePath)) {
            relatedAndPopularArticles[1] = buildResult(resourceResolver, articleLabelTypesForSession, popularArticlePath);
        }
    }

    private void extractRelatedAndPopularAutomatically(final SlingHttpServletRequest request, final Result[] relatedAndPopularArticles) throws Exception {
        if (isHaveBothRelatedAndPopularArticle(relatedAndPopularArticles)) {
            return;
        }

        final ArticlesCriteria criteria = buildArticleCriteriaFromRequest(request);
        /*********Start of Changes for AmwayToday v3.0**************/
        ArticleSearchResult articleByTagResult = null;
        
        if(ArticleLabelUtils.isPersonalizationCookiePresent(request)){
        	LOGGER.info("Cookie Present");
        	articleByTagResult = getArticleQueryServiceForPersonalization().search(request.getResourceResolver(), criteria,request);
        }
        else
        {
        	articleByTagResult = getArticleQueryService().search(request.getResourceResolver(), criteria);
        }
        
        /*********End of Changes for AmwayToday v3.0**************/
        
        //getArticleQueryService().search(request.getResourceResolver(), criteria);
        Page excludePage = null;
        if (isNoRelatedAndPopularArticle(relatedAndPopularArticles)) {
        	
            extractRelatedArticle(articleByTagResult.getPages(), relatedAndPopularArticles, null);
            excludePage = relatedAndPopularArticles[0] != null ? relatedAndPopularArticles[0].getPage() : null;
            extractMostViewArticle(articleByTagResult.getPages(), relatedAndPopularArticles, excludePage);
        } else if (isNoRelatedArticle(relatedAndPopularArticles)) {
            excludePage = relatedAndPopularArticles[1] != null ? relatedAndPopularArticles[1].getPage() : null;
            extractRelatedArticle(articleByTagResult.getPages(), relatedAndPopularArticles, excludePage);
        } else if (isNoPopularArticle(relatedAndPopularArticles)) {
            excludePage = relatedAndPopularArticles[0] != null ? relatedAndPopularArticles[0].getPage() : null;
            extractMostViewArticle(articleByTagResult.getPages(), relatedAndPopularArticles, excludePage);
        }
    }

    private boolean isHaveBothRelatedAndPopularArticle(final Result[] relatedAndPopularArticles) {
        return relatedAndPopularArticles[0] != null && relatedAndPopularArticles[1] != null;
    }

    private boolean isNoRelatedAndPopularArticle(final Result[] relatedAndPopularArticles) {
        return relatedAndPopularArticles[0] == null && relatedAndPopularArticles[1] == null;
    }

    private boolean isNoRelatedArticle(final Result[] relatedAndPopularArticles) {
        return relatedAndPopularArticles[0] == null;
    }

    private boolean isNoPopularArticle(final Result[] relatedAndPopularArticles) {
        return relatedAndPopularArticles[1] == null;
    }

    private boolean isPermitToViewArticle(List<ArticleLabelType> articleLabelTypesForSession, Page articlePage) {
        final String[] articleLabels = (String[]) articlePage.getProperties().get(ArticleConstants.LABEL_PROPERTY);
        final List<ArticleLabelType> articleLabelTypes = ArticleLabelType.resolve(articleLabels);
        if (CollectionUtils.containsAny(articleLabelTypesForSession, articleLabelTypes)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isExistingPath(final ResourceResolver resourceResolver, final String path) {
        if (path != null && resourceResolver.getResource(path) != null) {
            return true;
        }
        return false;
    }

    private Result buildResult(final ResourceResolver resourceResolver, final List<ArticleLabelType> articleLabelTypesForSession, final String path) {
        boolean isPermit = false;
        Page page = resourceResolver.getResource(path).adaptTo(Page.class);
        if (isPermitToViewArticle(articleLabelTypesForSession, page)) {
            isPermit = true;
        }
        return new Result(isPermit, page);
    }

    private void extractRelatedArticle(final List<Page> articlesByTag, final Result[] relatedAndPopularArticles, final Page excludePage) throws Exception {
        if (!articlesByTag.isEmpty()) {
        	LOGGER.info("empty");
            if (excludePage == null) {
                relatedAndPopularArticles[0] = new Result(true, articlesByTag.get(0));
            } else {
                for (Page page : articlesByTag) {
                    if (!PageUtils.isTheSameContentPath(page, excludePage)) {
                        relatedAndPopularArticles[0] = new Result(true, page);
                        break;
                    }
                }
            }
        }
    }

    private void extractMostViewArticle(final List<Page> articlesByTag, final Result[] relatedAndPopularArticles, final Page excludePage) throws Exception {
        if (!articlesByTag.isEmpty()) {

            final TreeMap<Long, List<Page>> mostView = new TreeMap<Long, List<Page>>(Collections.reverseOrder());
            Long impression;
            for (int i = 0; i < articlesByTag.size(); i++) {
            	impression = articleStatisticsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS, articlesByTag.get(i).getPath());
            	
                if (mostView.containsKey(impression)) {
                    mostView.get(impression).add(articlesByTag.get(i));
                } else {
                    List<Page> pages = new ArrayList<Page>();
                    pages.add(articlesByTag.get(i));
                    mostView.put(impression, pages);
                }
            }

            if (!mostView.isEmpty()) {
                if (excludePage == null) {
                    relatedAndPopularArticles[1] = new Result(true, mostView.firstEntry().getValue().get(0));
                } else {
                    boolean isFound = false;
                    for (Entry<Long, List<Page>> entry : mostView.entrySet()) {
                        for (Page page : entry.getValue()) {
                            if (!PageUtils.isTheSameContentPath(page, excludePage)) {
                                relatedAndPopularArticles[1] = new Result(true, page);
                                isFound = true;
                                break;
                            }
                        }
                        if (isFound) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private static class Result {

        private final boolean isPermit;
        private final Page page;

        public Result(boolean isPermit, Page page) {
            this.isPermit = isPermit;
            this.page = page;
        }

        public boolean isPermit() {
            return isPermit;
        }

        public Page getPage() {
            return page;
        }

    }

	
}
