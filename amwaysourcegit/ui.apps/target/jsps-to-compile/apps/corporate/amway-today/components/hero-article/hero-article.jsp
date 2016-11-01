<%@page contentType="text/html; charset=utf-8" 
	    import="java.util.ResourceBundle, 
	    		com.day.cq.i18n.I18n, 
				java.util.Locale, 
				com.day.cq.wcm.core.stats.PageViewStatistics,
				org.strut.amway.core.controller.HeroArticleController,
				org.strut.amway.core.model.HeroArticle,
				org.strut.amway.core.util.CategoryConstants,
				org.strut.amway.core.services.ArticleService,
				org.apache.commons.lang3.StringUtils,
				org.strut.amway.core.util.LinkTransformerUtils" %>

<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

<%
    Locale pageLocale = currentPage.getLanguage(false);
	//If above bool is set to true. CQ looks in to page path rather than jcr:language property.
	ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
	I18n i18n = new I18n(resourceBundle);

	Integer maxLengthTitle = Integer.valueOf(resourceBundle.getString("maxLengthTitle"));
	Integer maxLengthCategory = Integer.valueOf(resourceBundle.getString("maxLengthCategory"));

    final ArticleService articleService = sling.getService(ArticleService.class);
	final PageViewStatistics pageViewStatistics = sling.getService(PageViewStatistics.class);	
	String noImageUrl = currentDesign.getPath() + resourceBundle.getString("linkNoHeroImage");

	HeroArticleController heroArticleController = new HeroArticleController();
	HeroArticle heroArticle = heroArticleController.getHeroArticle(noImageUrl, pageManager, currentPage, pageViewStatistics);

	if(heroArticle != null) {
        String title = heroArticle.getTitle();
        String category = heroArticle.getCategory();
        int numberOfLike = 0;
        if (!StringUtils.isEmpty(heroArticle.getUrl())) {
            String articleName = heroArticle.getUrl().replace("/", "-").substring(1);
            numberOfLike = articleService.getLikeNumberByArticleName(articleName);
        }

        if (title.length() > maxLengthTitle) { title = title.substring(0, maxLengthTitle) + "...";}
        if (category.length() > maxLengthCategory) { category = category.substring(0, maxLengthCategory)+"..."; }

        String heroArticleImage = LinkTransformerUtils.transformPath(slingRequest.getResourceResolver(), heroArticle.getImageUrl());
%>
<div class="post post-2x visible-lg visible-md">
    <div class="main-post-content bubble bubble-left pull-article-right">
        <a href="<%= heroArticle.getCategoryPath() %>.html"><div class="main-post-category" title="<%= heroArticle.getCategory() %>"><%= category %></div></a>
        <a href="<%= heroArticle.getUrl() %>.html" title="<%= heroArticle.getTitle() %>"><div class="hero-article-title"><%= title %></div></a>
        <%
            if (heroArticle.getType().equals(CategoryConstants.MAIN_BLOGGING_CATEGORY)) {
        %>
        <div class="main-article-details"> 
            <hr class="hr-article-box">
            <small id="main-hero-article-publication" data-value="<%= heroArticle.getPublishedDate() %>" class="text-uppercase w50pc pull-left"></small>
            <small class="pull-right"><%= heroArticle.getImpression() %></small>
            <span class="pull-right img-social-sharing sprites icon-view"></span> 
            <small class="pull-right"><%= numberOfLike %></small> 
            <span class="pull-right img-social-sharing sprites icon-like"></span>
        </div>
        <% } %>
    </div>
    <div class="main-post-img"><img src="<%= heroArticleImage %>"></div>
</div>
<div class="post w50pc pull-left pull-article-right hidden-lg hidden-md">
    <div class="post-content bubble bubble-right">
        <a href="<%= heroArticle.getCategoryPath() %>.html"><div class="post-category" title="<%= heroArticle.getCategory() %>"><%= category %></div></a>
        <a href="<%= heroArticle.getUrl() %>.html">
            <div class="article-title" title="<%= heroArticle.getTitle() %>"><%= title %></div>
        </a>
        <%
            if (heroArticle.getType().equals(CategoryConstants.MAIN_BLOGGING_CATEGORY)) {
        %>
        <div class="article-details">
            <hr class="hr-article-box">
            <small id="sub-hero-article-publication" class="text-uppercase pull-left w50pc-fixed"></small> 
            <small class="pull-right hidden-xs"><%= heroArticle.getImpression() %></small>
            <span class="pull-right img-social-sharing sprites icon-view hidden-xs"></span>
            <small class="pull-right hidden-xs"><%= numberOfLike %></small>
            <span class="pull-right img-social-sharing sprites icon-like hidden-xs"></span>
        </div>
        <% } %>
    </div>
    <div class="post-img"><img src="<%= heroArticleImage %>"></div>
</div>
 <% } %>
