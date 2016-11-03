<%@page contentType="text/html; charset=utf-8"
        import="com.day.cq.wcm.core.stats.PageViewStatistics,
                org.apache.commons.lang3.StringUtils,javax.servlet.http.Cookie,
                org.apache.sling.settings.SlingSettingsService,
                org.strut.amway.core.controller.HeroArticleController,
                org.strut.amway.core.model.HeroArticle,
                org.strut.amway.core.services.ArticleService,org.strut.amway.core.util.ArticleConstants,
                org.strut.amway.core.util.AEMUtils,
				org.strut.amway.core.util.ArticleLabelUtils" %>
<%@ page import="org.strut.amway.core.util.CategoryConstants" %>
<%@ page import="org.strut.amway.core.util.LinkTransformerUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>

<%@include file="/libs/foundation/global.jsp" %>
<%@page session="false" %>
<%

    boolean isLoggedIn = ArticleLabelUtils.isPersonalizationCookiePresent(slingRequest);
	String target = properties.get("target",(String) null);
	boolean targetContent = false;
    if(target !=null && target.equalsIgnoreCase("true")){
        targetContent = true;
    }
//out.println("targetContent"+targetContent);
    Locale pageLocale = currentPage.getLanguage(false);
    //If above bool is set to true. CQ looks in to page path rather than jcr:language property.
    ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

    Integer maxLengthTitle = Integer.valueOf(resourceBundle.getString("maxLengthTitle"));
    Integer maxLengthCategory = Integer.valueOf(resourceBundle.getString("maxLengthCategory"));

    final ArticleService articleService = sling.getService(ArticleService.class);
    final PageViewStatistics pageViewStatistics = sling.getService(PageViewStatistics.class);
    String noImageUrl = currentDesign.getPath() + resourceBundle.getString("linkNoHeroImage");

    HeroArticleController heroArticleController = new HeroArticleController();
    List<HeroArticle> heroArticles = heroArticleController.getHeroArticles(noImageUrl, pageManager,
            resourceResolver, currentPage, pageViewStatistics);

//    if (heroArticles.size() == 0 && WCMMode.fromRequest(request) == WCMMode.EDIT) {
    if (heroArticles.size() == 0 && AEMUtils.isAuthor(sling.getService(SlingSettingsService.class))) {
        //Put a fake article to display the component
        heroArticles.add(new HeroArticle("Fake hero article for carousel edition", "", "", "", "", "", "", "", 0L));
    }

    // Subtle things happening here: cq.widgets is not a dependency, because that fails in the publisher. On the author
    // it is assumed to have already been loaded.
    // Different categories for auth-pub & author only javascript.
    // The author part of this component is not included in clientlibs-all, the other part is.

    // Only on author 
    if (WCMMode.fromRequest(request) != WCMMode.DISABLED) { %>
        <cq:includeClientLib categories="apps.strut-amway.hero-art-carousel-author" /> <%
    }
%>
    <cq:includeClientLib categories="apps.strut-amway.hero-art-carousel" />
<%

    if (heroArticles.size() > 0) {
%>
<div class="hero-container">
    <div class="hero-article-container">
        <%
            for (int i = 0; i < heroArticles.size(); i++) {
                HeroArticle heroArticle = heroArticles.get(i);

                String title = heroArticle.getTitle();
                String category = heroArticle.getCategory();
                int numberOfLike = 0;
                if (!StringUtils.isEmpty(heroArticle.getUrl())) {
                    String articleName = heroArticle.getUrl().replace("/", "-").substring(1);
                    numberOfLike = articleService.getLikeNumberByArticleName(articleName);
                }

                if (title.length() > maxLengthTitle) {
                    title = title.substring(0, maxLengthTitle) + "...";
                }
                if (category.length() > maxLengthCategory) {
                    category = category.substring(0, maxLengthCategory) + "...";
                }

                String heroArticleImage = LinkTransformerUtils.transformPath(slingRequest.getResourceResolver(), heroArticle.getImageUrl());
        			String articleLabel = "";

    			Node articleNode = (resourceResolver.getResource(heroArticle.getUrl()+"/jcr:content")).adaptTo(Node.class);
    			if(articleNode.hasProperty("label")){
                if(articleNode.getProperty("label").isMultiple()){

					Value[] values = articleNode.getProperty("label").getValues();
  					articleLabel = values[0].getString();  
                }
                else
                {
					articleLabel = articleNode.getProperty("label").getValue().getString();

    			}
                }
    		boolean showArticle = true;

            if(targetContent){

                showArticle = false;
        
                    if(isLoggedIn){
        


                        String cookieValue = null;

                        Cookie[] cookies = request.getCookies();
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                //out.println("Cookie Name:"+cookie.getName());
                                if (cookie.getName().equals(ArticleConstants.PERSONALIZATION_COOKIE)) {
									cookieValue = cookie.getValue();

                                }
                            }
                        }

                        if(cookieValue!=null){

                            // out.println("cookieValue"+cookieValue);

                            if(!articleLabel.equalsIgnoreCase("")){

                                if(cookieValue.equals("ABO")){

                                    if(articleLabel.equals("ABO") || articleLabel.equals("Public") )
                                    {
										showArticle = true;
                                    }

                                }

                                else if(cookieValue.equals("Diamond")){

                                    if(articleLabel.equals("ABO") || articleLabel.equals("Public") || articleLabel.equals("Diamond") || articleLabel.equals("Platinum") )
                                    {
										showArticle = true;
                                    }
                                }

                                else if(cookieValue.equals("Platinum")){

                                     if(articleLabel.equals("ABO") || articleLabel.equals("Public") || articleLabel.equals("Platinum"))
                                    {
										showArticle = true;
                                    }

                                }
                            }
                        }
        
        
                    }

                else{

						if(!articleLabel.equalsIgnoreCase("") && (articleLabel.equalsIgnoreCase("Public"))){

							showArticle = true;
                        }
                }




            }


        %>

        <!-- Desktop -->
        <%if(showArticle){%>
        <div class="post visible-lg visible-md hero-post">
            <div class="hero-post-img clearfix"><a href="<%= heroArticle.getUrl() %>.html" title="<%= heroArticle.getTitle() %>"><img src="<%= heroArticleImage %>"></a></div>
            <div class="post-content hero-post-content">
                <a href="<%= heroArticle.getCategoryPath() %>.html">
                    <div class="post-category" title="<%= heroArticle.getCategory() %>"><%= category %></div>
                </a>
                <a href="<%= heroArticle.getUrl() %>.html" title="<%= heroArticle.getTitle() %>">
                    <div class="article-title"><%= title %></div>
                </a>
                <div class="article-snippet hero-article-snippet"><%= heroArticle.getSnippet() %></div>
                <%
                    if (heroArticle.getType().equals(CategoryConstants.MAIN_BLOGGING_CATEGORY)) {
                %>
                <div class="main-article-details">
                    <hr class="hr-article-box">
                    <small id="main-hero-article-publication"
                           class="text-uppercase w50pc pull-left"><%= heroArticle.getPublishedDate() %></small>
                    <small class="pull-right"><%= heroArticle.getImpression() %></small>
                    <span class="pull-right img-social-sharing sprites icon-view"></span>
                    <small class="pull-right"><%= numberOfLike %></small>
                    <span class="pull-right img-social-sharing sprites icon-like"></span>
                </div>
                <% } %>
            </div>
        </div>

        <!-- Mobile and tablet -->
        <div class="post w50pc hidden-lg hidden-md hero-post">
            <div class="hero-post-img"><a href="<%= heroArticle.getCategoryPath() %>.html"><img src="<%= heroArticleImage %>"></a></div>
            <div class="post-content hero-post-content">
                <a href="<%= heroArticle.getCategoryPath() %>.html">
                    <div class="post-category" title="<%= heroArticle.getCategory() %>"><%= category %></div>
                </a>
                <a href="<%= heroArticle.getUrl() %>.html">
                    <div class="article-title" title="<%= heroArticle.getTitle() %>"><%= title %></div>
                </a>
                <div class="article-snippet hero-article-snippet"><%= heroArticle.getSnippet() %></div>
                <%
                    if (heroArticle.getType().equals(CategoryConstants.MAIN_BLOGGING_CATEGORY)) {
                %>
                <div class="article-details">
                    <hr class="hr-article-box">
                    <small id="sub-hero-article-publication"
                           class="text-uppercase pull-left w50pc-fixed"><%= heroArticle.getPublishedDate() %></small>
                    <small class="pull-right hidden-xs"><%= heroArticle.getImpression() %></small>
                    <span class="pull-right img-social-sharing sprites icon-view hidden-xs"></span>
                    <small class="pull-right hidden-xs"><%= numberOfLike %></small>
                    <span class="pull-right img-social-sharing sprites icon-like hidden-xs"></span>
                </div>
                <% } %>
            </div>
        </div>

        <%
                         }}
        %>
    </div>

    <script type="text/javascript">
        $(document).ready(function () {
            $('.hero-article-container').slick({
                infinite: true,
                speed: 500,
                autoplay: true,
                autoplaySpeed: 6000,
                accessibility: true,
                arrows: true,
                dots: true,
                mobileFirst: true
            });

            <%--
            The code above renders 2 versions (divs) of the article, which are then alternatively hidden by media queries.
            However the carousel component picks the 2 divs per article, which is problematic. To avoid this a filtering
            function is provided. Unfortunately nothing really worked to determine if the div was being displayed by
            querying its properties, so a mimic of the media queries is used to filter.

            $('.hero-article-container').slick('slickFilter', function (index, element) {
                return $(element).css('display') != "none";  //would have been nice
            });

            Note neither option is 100% responsive and needs a page refresh.
            --%>

            if ($(window).width() < 992) {
                $('.hero-article-container').slick('slickFilter', ':odd');
            } else {
                $('.hero-article-container').slick('slickFilter', ':even');
            }
        });
    </script>
</div>
<%
    }
%>

