package org.strut.amway.core.util;

public final class ArticleConstants {

    public static String OFFSET_PARAM = "offset";
    public static Long DEFAULT_OFFSET_VALUE = 0L;
    public static Long DEFAULT_LIMIT_VALUE = 10L;
    public static Long DEFAULT_SEARCH_POPUP_LIMIT_VALUE = 3L;
    public static Long DEFAULT_NEXT_OR_PREV_ARTICLE_LIMIT_VALUE = 1L;

    public static Integer DEFAULT_MINUS_MONTH = 2;

    public static String LIMIT_PARAM = "limit";
    public static String SEARCH_TEXT_PARAM = "text";
    public static String TAG_ID_PARAM = "tagId";
    public static String START_DATE_PARAM = "startDate";
    public static String END_DATE_PARAM = "endDate";
    public static String ORDER_PARAM = "order";
    
    /*********Start of Changes for AmwayToday v3.0**************/
    public static String PERSONALIZATION_COOKIE = "amway-today-login-type";
    /*********End of Changes for AmwayToday v3.0**************/

    public static String TYPE_PARAM = "type";
    public static String CREATED_DATE_PARAM = "createdDate";

    public static final String CQ_TEMPLATE = "cq:template";
    public static final String JCR_CREATED = "jcr:created";
    public static final String INPUT_PUBLISH_DATE = "inputPublishedDate";
    public static final String AUTHOR_NAME = "authorName";
    public static final String PUBLISH_DATE = "publishedDate";
    public static final String RELATED_ARTICLE = "relatedArticlePath";
    public static final String POPULAR_ARTICLE = "popularArticlePath";
    public static final String CQ_TAGS = "cq:tags";
    public static final String JCR_TITLE = "jcr:title";
    public static final String JCR_PATH = "jcr:path";
    public static final String RESOURCE_TYPE = "sling:resourceType";

    public static final String ARTICLE_RESOURCE_TYPE = "corporate/amway-today/components/article-page";
    public static final String IFRAME_RESOURCE_TYPE = "corporate/amway-today/components/iframe-page";
    public static final String SEA_ARTICLE_RESOURCE_TYPE = "/apps/corporate/amway-today/components/sea/article-page";
    public static final String ARTICLE_TEMPLATE = "/apps/corporate/amway-today/templates/article-page";
    public static final String SEA_ARTICLE_TEMPLATE = "/apps/corporate/amway-today/templates/sea-article-page";
    public static final String SUB_CATEGORY_TEMPLATE = "/apps/corporate/amway-today/templates/sub-category";
    public static final String IFRAME_TEMPLATE = "/apps/corporate/amway-today/templates/iframe-page";
    public static final String HOMEPAGE_RESOURCE_TYPE = "corporate/amway-today/components/home-page";
    public static final String SEA_HOMEPAGE_RESOURCE_TYPE = "corporate/amway-today/components/sea/home-page";
    public static final String SUB_MOST_POPULAR_TEMPLATE = "/apps/corporate/amway-today/templates/most-popular";

    public static final String JCR_CONTENT_ARTICLE_TEXT_FOOTER = "article-content-text-footer/*/text";
    public static final String JCR_CONTENT_ARTICLE_TEXT_CENTER = "article-content-text-center/*/text";
    /**
     * For Hero Article
     */
    public static final String LINK_URL = "linkURL";
    public static final String IMAGE_STRING = "image";
    public static final String IMG_PNG = ".img.png";
    public static final String HERO_ARTICLE_CAROUSEL_NODE_CHILD = "hero_art_carousel";
    public static final String HERO_ARTICLE_NODE_CHILD = "hero_article";
    public static final String HERO_ARTICLE_NODE_ABS = "hero-article";
    public static final String ARTICLES_PROP = "articles";

    public static final String OPS_LESS_THAN_OR_EQUAL = "<=";
    public static final String OPS_GREATER_THAN_OR_EQUAL = ">=";
    public static final String OPS_LESS_THAN = "<";
    public static final String OPS_GREATER_THAN = ">";
    public static final String OPS_EQUAL = "=";
    public static final String OPS_AND = "AND";
    public static final String OPS_OR = "OR";
    public static final String OPS_UNION = "UNION";
    public static final String OPS_LIKE = "LIKE";
    public static final String OPS_PERCENT_SIGN = "%";

    public static final String OPS_QUOTE = "'";
    public static final String OPS_DOUBLE_QUOTE = "\"";
    public static final String LABEL_PROPERTY = "label";

    /**
     * For like number servlet
     */
    public static final String NODE_REGEX = "/";
    public static final String NUMBER_OF_LIKE = "numberOfLike";
    public static final String STATUS_GET = "get";
    public static final String STATUS_UPDATE = "update";
    public static final String ARTICLE_PATH_PARAM = "articlePath";
    public static final String STATUS_PARAM = "status";
    public static final String PATH_STORED_NODE = "content/usergenerated/content/amway-today";

    /**
     * Article Statistics
     */
    public static final String STATISTIC_LIKES = "likes";
    public static final String STATISTIC_VIEWS = "views";
}
