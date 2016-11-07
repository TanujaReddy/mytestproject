package org.strut.amway.core.controller;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.core.stats.PageViewStatistics;
import com.day.cq.wcm.foundation.Image;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.model.HeroArticle;
import org.strut.amway.core.util.ArticleConstants;
import org.strut.amway.core.util.ArticleUtils;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.DateTimeUtils;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class HeroArticleController {

    // In sync with custom-widget.js
    static final String FIELD_SEPARATOR = "*--*";
    private static Logger LOGGER = LoggerFactory.getLogger(HeroArticleController.class);

    private static final DateTimeFormatter pubDateFormatter = DateTimeFormat.forPattern("dd MMM YYYY");

    // TODO: delete after migration to carousel hero article
    public HeroArticle getHeroArticle(String noImageUrl, final PageManager pageManager, final Page currentPage, final PageViewStatistics pageViewStatistics) {
        String imageUrl = noImageUrl;
        String heroArticleUrl = "";
        String category = "";
        String categoryPath = "";
        String title = "";
        String publishedDate = "";
        Long impression = 0L;
        String type = "";

        try {
            Resource resource = currentPage.getContentResource(ArticleConstants.HERO_ARTICLE_NODE_ABS);

            if (resource != null) {
                Node node = resource.adaptTo(Node.class);

                if (node != null && node.hasNode(ArticleConstants.HERO_ARTICLE_NODE_CHILD)) {
                    Node nodeHero = node.getNode(ArticleConstants.HERO_ARTICLE_NODE_CHILD);

                    if (nodeHero != null && nodeHero.hasProperty(ArticleConstants.LINK_URL)) {

                        heroArticleUrl = nodeHero.getProperty(ArticleConstants.LINK_URL).getString();
                        Page heroArticlePage = pageManager.getPage(heroArticleUrl);

                        // If article is deactivated or deleted, don't render content
                        if (heroArticlePage == null) {
                            return null;
                        }
                        // Get url from hero image If any
                        Image heroImage =
                                new Image(currentPage.getContentResource(ArticleConstants.HERO_ARTICLE_NODE_ABS + "/"
                                        + ArticleConstants.HERO_ARTICLE_NODE_CHILD));
                        if (heroImage.hasContent()) {
                            imageUrl = nodeHero.getPath() + ArticleConstants.IMG_PNG + heroImage.getSuffix();

                        } else {
                            // Get url from thumbnail article
                            final Resource heroArticleImgResource = heroArticlePage.getContentResource(ArticleConstants.IMAGE_STRING);
                            if (heroArticleImgResource != null) {
                                heroImage = new Image(heroArticleImgResource);
                                if (heroImage.hasContent()) {
                                    imageUrl = heroArticleUrl + ArticleConstants.IMG_PNG + heroImage.getSuffix();
                                }
                            }
                        }

                        // Get title for article hero
                        title = heroArticlePage.getTitle();

                        // Get category and type for article hero
                        Page parent = heroArticlePage.getParent(CategoryConstants.ABS_LEVEL);
                        if (parent != null) {
                            category = parent.getTitle();
                            categoryPath = parent.getPath();
                            type = String.valueOf(parent.getProperties().get(CategoryConstants.CATEGORY_TYPE));
                        }

                        // Get published date for article hero
                        publishedDate = getPublishedDate(heroArticlePage);

                        // Get impression for article hero
                        Object[] values = pageViewStatistics.report(heroArticlePage);
                        if (values != null && values.length > 2) {
                            impression = (Long) values[2];
                        }
                    }
                }
            }

        } catch (WCMException | RepositoryException e) {
            LOGGER.error("Error while get  data from Hero Article" + ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
        }

        return new HeroArticle(title, category, "", categoryPath, heroArticleUrl, type, publishedDate, imageUrl, impression);
    }

    /**
     * @return an empty list if no articles are present. Dummy articles for rendering are a responsibility of the invoker
     */
    public List<HeroArticle> getHeroArticles(String noImageUrl, PageManager pageManager, ResourceResolver resourceResolver,
                                             Page currentPage, PageViewStatistics pageViewStatistics) {

        List<HeroArticle> result = new ArrayList<HeroArticle>();

        try {
            Resource resource = currentPage.getContentResource(ArticleConstants.HERO_ARTICLE_NODE_ABS);

            if (resource == null) {
                return result;
            }

            Node node = resource.adaptTo(Node.class);
            if (node == null) {
                return result;
            }

            if (!node.hasNode(ArticleConstants.HERO_ARTICLE_CAROUSEL_NODE_CHILD)) {
                return result;
            }

            Node nodeHero = node.getNode(ArticleConstants.HERO_ARTICLE_CAROUSEL_NODE_CHILD);
            if (!nodeHero.hasProperty(ArticleConstants.ARTICLES_PROP)) {
                return result;
            }

            List<String> articles = getArticlesPropertyValues(nodeHero);

            for (String value : articles) {
                String[] articleParams = StringUtils.splitByWholeSeparator(value, FIELD_SEPARATOR);

                String heroArticlePath = articleParams[0];
                Page heroArticlePage = pageManager.getPage(heroArticlePath);

                // If article is deactivated or deleted don't return it
                if (heroArticlePage == null) {
                    continue;
                }

                String category = "";
                String categoryPath = "";
                String type = "";
                Long impression = 0L;

                // Get url from hero image If any
                String heroArticleImagePath = articleParams[1];
                String imageUrl = getImageUrl(resourceResolver, noImageUrl, heroArticleImagePath, heroArticlePath, heroArticlePage);

                // Get title for article hero
                String title = heroArticlePage.getTitle();

                // Get category and type for article hero
                Page parent = heroArticlePage.getParent(2);
                if (parent != null) {
                    category = parent.getTitle();
                    categoryPath = parent.getPath();
                    type = String.valueOf(parent.getProperties().get(CategoryConstants.CATEGORY_TYPE));
                }

                // Get published date for article hero
                String publishedDate = getPublishedDate(heroArticlePage);

                // Get impression for article hero
                Object[] stats = pageViewStatistics.report(heroArticlePage);
                if (stats != null && stats.length > 2) {
                    impression = (Long) stats[2];
                }

                String snippet = ArticleUtils.extractPlaintextSnippet(heroArticlePage);

                HeroArticle heroArticle = new HeroArticle(title, category, snippet, categoryPath, heroArticlePath, type,
                        publishedDate, imageUrl, impression);
                result.add(heroArticle);
            }
        } catch (WCMException | RepositoryException e) {
            LOGGER.error("Error while getting data from Hero Article", e);
        }

        return result;
    }

    private String getImageUrl(ResourceResolver resourceResolver, String imageUrl, String heroArticleImagePath, String heroArticlePath, Page heroArticlePage) {
        Resource imgResource = resourceResolver.getResource(heroArticleImagePath);
        if (StringUtils.isNotEmpty(heroArticleImagePath) && imgResource != null) {
            // Custom image
            imageUrl = heroArticleImagePath;
        } else {
            // Get url from thumbnail article
            final Resource heroArticleImgResource = heroArticlePage.getContentResource(ArticleConstants.IMAGE_STRING);
            if (heroArticleImgResource != null) {
                Image heroImage = new Image(heroArticleImgResource);
                if (heroImage.hasContent()) {
                    imageUrl = heroArticlePath + ArticleConstants.IMG_PNG + heroImage.getSuffix();
                }
            }
        }
        return imageUrl;
    }

    /**
     * AEM changes the property type from String to String[] depending on the number of values.
     */
    private List<String> getArticlesPropertyValues(Node nodeHero) throws RepositoryException {
        List<String> articles = new ArrayList<>();
        Property articleProperty = nodeHero.getProperty(ArticleConstants.ARTICLES_PROP);
        if (articleProperty.isMultiple()) {
            for (Value value : articleProperty.getValues()) {
                articles.add(value.getString());
            }
        } else {
            if (StringUtils.isNotBlank(articleProperty.getString())) {
                articles.add(articleProperty.getString());
            }
        }
        return articles;
    }

    private String getPublishedDate(final Page article) {
        final GregorianCalendar publishedDate = ArticleUtils.getPublishedDate(article);
        if (publishedDate != null) {
            return DateTimeUtils.parseToUTC(publishedDate).toString(pubDateFormatter);
        }

        return StringUtils.EMPTY;
    }
}
