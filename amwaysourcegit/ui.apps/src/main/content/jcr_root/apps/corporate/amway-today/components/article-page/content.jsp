<%-- Article Page component.--%>
<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8"
        import="com.day.cq.i18n.I18n,
        org.apache.commons.lang3.StringUtils,
        org.strut.amway.core.controller.ArticleLabelFilterController,
        org.strut.amway.core.services.ArticleStatisticsService,
        org.strut.amway.core.util.*,
        java.util.GregorianCalendar,
        java.util.Locale,
        java.util.ResourceBundle"%>

<%@page import="org.strut.amway.core.util.AnalyticsUtils" %>
<cq:setContentBundle />
<%
    boolean isPublishInstance = (Boolean) pageContext.getAttribute(AEMUtils.IS_PUBLISH_INSTANCE, PageContext.REQUEST_SCOPE);
    final ArticleLabelFilterController secFilter = new ArticleLabelFilterController();
    secFilter.checkUserIsEligibleForViewArticlePage(currentPage, slingRequest, slingResponse, isPublishInstance);

    Locale pageLocale = currentPage.getLanguage(false);
    ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
    I18n i18n = new I18n(resourceBundle);
%>
<%
    Integer maxLengthTitle = Integer.valueOf(resourceBundle.getString("maxLengthTitle"));
    Integer maxLengthCategory = Integer.valueOf(resourceBundle.getString("maxLengthCategory"));
    String linkNoImage = resourceBundle.getString("linkNoImage");
    String publishedDate = "";
    Long views = 0L;
    final Page category = currentPage.getParent(2);
    String categoryTitle = category.getTitle();
    String title = currentPage.getTitle();
    if (title.length() > maxLengthTitle) { title = title.substring(0, maxLengthTitle) + "...";}
    if (categoryTitle.length() > maxLengthCategory) { categoryTitle = categoryTitle.substring(0, maxLengthCategory)+"..."; }
    String type = String.valueOf(currentPage.getParent(2).getProperties().get(CategoryConstants.CATEGORY_TYPE));
    final GregorianCalendar lastPublishedDate =  ArticleUtils.getPublishedDate(currentPage);
    if(lastPublishedDate != null) {
        publishedDate = DateTimeUtils.parseToUTC(lastPublishedDate).toString();
    }
    final ArticleStatisticsService articleStatsService = sling.getService(ArticleStatisticsService.class);
    views = articleStatsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS , currentPage.getPath());

    String[] label = (String[]) currentPage.getProperties().get("label");
    String urlVideo = String.valueOf(currentPage.getProperties().get("video_url"));
    String authorName = "";
    if( currentPage.getProperties().get(ArticleConstants.AUTHOR_NAME) != null) {
        authorName = currentPage.getProperties().get(ArticleConstants.AUTHOR_NAME, String.class);
    }

    String mappedPagePath = LinkTransformerUtils.transformPath(slingRequest.getResourceResolver(), currentPage.getPath());

	String video_width = String.valueOf(currentPage.getProperties().get("video_width"));
	String video_height = String.valueOf(currentPage.getProperties().get("video_height"));

    // Analytics parameters
    pageContext.setAttribute(AnalyticsUtils.SECTION, AnalyticsUtils.SECTION_ARTICLES, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.CATEGORY, CategoryUtils.getCategoryName(currentPage), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.SUBCATEGORY, CategoryUtils.getSubCategoryName(currentPage), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.DETAIL, currentPage.getName(), PageContext.REQUEST_SCOPE);

    // Social sharing
    Boolean enableWhatsapp = ArticleUtils.enableWhatsapp(currentPage);
    pageContext.setAttribute("enableWhatsapp", enableWhatsapp, PageContext.REQUEST_SCOPE);

    Boolean enableLine = ArticleUtils.enableLine(currentPage);
    pageContext.setAttribute("enableLine", enableLine, PageContext.REQUEST_SCOPE);
%>
<cq:includeClientLib js="apps.strut-amway.article-page"/>
<script type="text/javascript">
    var currentDate = new Date();
    var currentTime = currentDate.getTime();
    var resourceRelatedArticlePath = "<%= mappedPagePath %>.amway.related.popular.json?time=" + currentTime;
    var exception = "<%= currentDesign.getPath()%><%= linkNoImage %>";

    $(window).load(function() {
        //checkHeight();
        deleteBlankTop();
        loadRelatedArticle(resourceRelatedArticlePath, "div.related-article", exception);
    });

    $(function(){
        getArticleStatistics();

        function getArticleStatistics() {
            var articlePath = '<%= mappedPagePath %>';
            $.ajax({
                url: articlePath.concat(".amway.article.update.views.json"),
                type: "GET",
                success: updateArticleStatistics,
                error: function(err, status, m) {
                    if(status === "timeout") {
                        alert(serverTimeout);
                    } else {
                        alert(serverError);
                    }
                }
            });
        }

        function incrementArticleLikes () {
            var articlePath = '<%= mappedPagePath %>';
            $.ajax({
                url: articlePath.concat(".amway.article.likes.json"),
                type: "POST",
                success: updateArticleStatistics,
                error: function(err, status, m) {
                    if(status === "timeout") {
                        alert(serverTimeout);
                    } else {
                        alert(serverError);
                    }
                }
            });
        }

        function updateArticleStatistics(data) {
            $(".number-of-like-text").each(function(index, elm){
                $(elm).text(data.likes)
            });
            $(".number-of-views-text").each(function(index, elm){
                $(elm).text(data.views)
            });
        }

        $(".number-of-like-btn").each(function(index, elm){
            $(elm).click(function () {
                var articlePath = '<%= mappedPagePath %>';
                var likedArticlesCookie = getCookieByName("likedArticles");

                if (isArticleLiked(articlePath, likedArticlesCookie))
                    return;

                var newLikedArticlesCookieString = addArticlePathToCookieString(articlePath, likedArticlesCookie);

                setCookie("likedArticles", newLikedArticlesCookieString);

                incrementArticleLikes();

                // track article like with analytics
                if (window.isPublishInstance == 'true') {
                    console.log('send article like event to analytics for publish instance');

                    window.dataLayer = {
                        site: buildDataLayerSite(),
                        page: buildDataLayerPage(),
                        event: {name: ['like']}
                    };

                    _satellite.track('track_like');
                } else {
                    console.log('do not send article like event to analytics for author instance');
                }
            });
        })


        $(".btn-icon-facebook").click(function(e){
            window.open('https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(window.location.href));

            // track share article via Facebook with analytics
            if (window.isPublishInstance == 'true') {
                console.log('send share via Facebook event to analytics for publish instance');

                window.dataLayer = {
                    site: buildDataLayerSite(),
                    page: buildDataLayerPage(),
                    event: {name: ['socialShare']},
                    social: {type: 'facebook'}
                };

                _satellite.track('track_social');
            } else {
                console.log('do not send share via Facebook event to analytics for author instance');
            }

            return false;
        });

        // Enable on mobile
        var isMobilePhone = (navigator.userAgent.match(/Android|iPhone/i) && !navigator.userAgent.match(/iPod|iPad/i));
        if (isMobilePhone) {
            $(".btn-icon-whatsapp").each(function (i, elm) {
                elm.style.display = null; //display it
            });
        }

        $(".icon-whatsapp").click(function () {
            // based on https://github.com/kriskbx/whatsapp-sharing/ v1.3.3

            var url = "whatsapp://send?text=" + encodeURIComponent(window.location.href);
            window.open(url, '');

            if (window.isPublishInstance == 'true') {
                console.log('send share via whatsapp event to analytics for publish instance');

                window.dataLayer = {
                    site: buildDataLayerSite(),
                    page: buildDataLayerPage(),
                    event: {name: ['socialShare']},
                    social: {type: 'whatsapp'}
                };

                _satellite.track('track_social');
            } else {
                console.log('do not send share via whatsapp event to analytics for author instance');
            }

            return false;
        });

        $(".icon-line").click(function () {
            window.open("http://line.me/R/msg/text/?" + encodeURIComponent(window.location.href), '');

            if (window.isPublishInstance == 'true') {
                console.log('send share via Line event to analytics for publish instance');

                window.dataLayer = {
                    site: buildDataLayerSite(),
                    page: buildDataLayerPage(),
                    event: {name: ['socialShare']},
                    social: {type: 'line'}
                };

                _satellite.track('track_social');
            } else {
                console.log('do not send share via Line event to analytics for author instance');
            }

            return false;
        });

        $(".icon-twitter").click(function () {
            window.open("https://twitter.com/share?url=" + encodeURIComponent(window.location.href) + "&text=" + document.title, '');

            // track share article via Twitter with analytics
            if (window.isPublishInstance == 'true') {
                console.log('send share via Twitter event to analytics for publish instance');

                window.dataLayer = {
                    site: buildDataLayerSite(),
                    page: buildDataLayerPage(),
                    event: {name: ['socialShare']},
                    social: {type: 'twitter'}
                };

                _satellite.track('track_social');
            } else {
                console.log('do not send share via Twitter event to analytics for author instance');
            }

            return false;
        });

        $("a.btn-icon-pinterest").click(function (e) {
            var title = '<%=currentPage.getTitle()%>';
            var locationHref = window.location.href;
            var imgUrl = locationHref.split('/');
            var imgsrc = imgUrl[0] + "//" +  imgUrl[2] + getImage();
            var href ="//www.pinterest.com/pin/create/button/?url=" + encodeURIComponent(locationHref)
                    + "&description=" + encodeURIComponent(title)
                    + "&media=" + encodeURIComponent(imgsrc);
            window.open(href);

            // track share article via Pinterest with analytics
            if (window.isPublishInstance == 'true') {
                console.log('send share via Pinterest event to analytics for publish instance');

                window.dataLayer = {
                    site: buildDataLayerSite(),
                    page: buildDataLayerPage(),
                    event: {name: ['socialShare']},
                    social: {type: 'pinterest'}
                };

                _satellite.track('track_social');
            } else {
                console.log('do not send share via Pinterest event to analytics for author instance');
            }
        });

        function getImage () {
            var srcImage = "";
            var flag = true;
            $(".main-blog img").each(function(index, element) {
                if ($(this).attr("src") != "") {
                    srcImage = $(this).attr("src");
                    return false;
                }
            });
            if (srcImage === "") {
                srcImage = exception;
            }
            return srcImage;
        }
    });

</script>
<!--Breadcrumb Desktop-->
<cq:include path="breadcrumb" resourceType="corporate/amway-today/components/breadcrumb"/>
<!--End Breadcrumb Desktop-->

<div class="article-side pull-left">
    <div class="header-article-box">
        <div class="post-article-content bubble">
            <a href="<%= category.getPath() %>.html"><div class="post-category" title="<%= category.getTitle() %>"><%= categoryTitle %></div></a>
            <a href="<%= currentPage.getPath() %>.html"><h1 class="article-title h1-article-title"><%= title %></h1></a>
            <%
                if (type.equals("main-blogging")) {
            %>

            <div class="article-details">
                <div class="article-date hidden-sm hidden-xs">
                    <small class="main-article-publication text-uppercase pull-left" data-value="<%= publishedDate %>"></small>
                </div>
                <hr class="hr-article-box">

                <small class="main-article-publication text-uppercase pull-left hidden-md hidden-lg" data-value="<%= publishedDate %>"></small>
                <small class="author-blog ml-15 hidden-md hidden-lg">
                    <%
                        if(!StringUtils.isEmpty(authorName)) {
                    %>
                    <fmt:message key="lbByAuthor"/><span class="authorName">&nbsp;<%= authorName %></span>
                    <%
                        }
                    %>
                </small>
                <div class="hidden-md hidden-lg">
                    <small id="" class="number-of-views-text pull-right">0</small>
                    <span class="pull-right img-social-sharing sprites icon-view"></span>
                    <%
                        if (label != null && label.length > 0 && label[0].equals("Public")) {
                    %>
                    <small id="" class="number-of-like-text pull-right">0</small>
                    <span id="" class="number-of-like-btn pull-right img-social-sharing sprites icon-like"></span>
                    <%
                        }
                    %>
                </div>
                <div class="hidden-sm hidden-xs">
                    <span class="pull-left img-social-sharing sprites icon-view" style="margin-left:0px"></span>
                    <small id="" class="number-of-views-text pull-left">0</small>
                    <%
                        if (label != null && label.length > 0 && label[0].equals("Public")) {
                    %>
                    <span id="" class="number-of-like-btn pull-left img-social-sharing sprites icon-like"></span>
                    <small id="" class="number-of-like-text pull-left">0</small>
                    <%
                        }
                    %>
                </div>

            </div>

            <%
                }
            %>
        </div>
        <div class="information-blog hidden-print">
            <div class="toolbar-information">
                <div class="author-blog text-center visible-md visible-lg">
                    <small>
                        <%
                            if(!StringUtils.isEmpty(authorName)) {
                        %>
                        <fmt:message key="lbByAuthor"/><span class="authorName">&nbsp;<%= authorName %></span>
                        <%
                            }
                        %>
                    </small>
                </div>
                <div class="btn-group btn-group-justified navigate-button-group">
                    <%
                        if (label != null && label.length > 0 && label[0].equals("Public")) {
                    %>

                    <c:if test="${enableWhatsapp}">
                        <a href="#" class="btn btn-social-sharing btn-icon-whatsapp" style="display:none">
                            <span class="sprites icon-whatsapp"></span>
                        </a>
                    </c:if>
                    <c:if test="${enableLine}">
                        <a href="#" class="btn btn-social-sharing btn-icon-line"><span class="sprites icon-line"></span></a>
                    </c:if>
                    <a href="#" class="btn btn-social-sharing btn-icon-twitter"><span class="sprites icon-twitter"></span></a>
                    <a href="#" class="btn btn-social-sharing btn-icon-facebook"><span class="sprites icon-facebook"></span></a>
                    <a href="#" class="btn btn-social-sharing btn-icon-pinterest" data-pin-do="buttonPin" data-pin-shape="round">
                        <span class="sprites icon-pinterest"></span>
                    </a>
                    <a href="" class="btn btn-social-sharing" onclick="articleEmailSharing(this, '<%= mappedPagePath %>.html',
                            '<%= resource.getResourceType() %>', '<%= CategoryUtils.getCategoryName(currentPage) %>',
                            '<%= CategoryUtils.getSubCategoryName(currentPage) %>')">
                        <span class="sprites icon-email"></span>
                    </a>
                    <%
                        }
                    %>
                    <a href="javascript:window.print()" class="btn btn-social-sharing hidden-sm hidden-md hidden-xs"><span class="sprites icon-print"></span></a>
                    <a href="#" data-type="previous" data-value="<%= mappedPagePath %>.amway.article.nextAndPrev.json?type=PREV&createdDate=<%=publishedDate %>" class="btn navigates-blog hidden-md hidden-lg"><fmt:message key="btnPrevious"/></a>
                    <a href="#" data-type="next" data-value="<%= mappedPagePath %>.amway.article.nextAndPrev.json?type=NEXT&createdDate=<%=publishedDate %>" class="btn navigates-blog navigates-blog-next hidden-md hidden-lg"><fmt:message key="btnNext"/></a>
                </div>
                <div class="btn-group btn-group-justified hidden-xs hidden-sm navigate-button-group">
                    <a href="#" data-type="previous" data-value="<%= mappedPagePath %>.amway.article.nextAndPrev.json?type=PREV&createdDate=<%=publishedDate %>" class="btn navigates-blog"><fmt:message key="btnPrevious"/></a>
                    <a href="#" data-type="next" data-value="<%= mappedPagePath %>.amway.article.nextAndPrev.json?type=NEXT&createdDate=<%=publishedDate %>" class="btn navigates-blog navigates-blog-next"><fmt:message key="btnNext"/></a>
                </div>
            </div>
        </div>
    </div>
    <div class="main-blog">
        <div class="media-header-blog">
            <%
                if (!(urlVideo == null || "".equals(urlVideo) || "null".equals(urlVideo))) {
            %>
        		
            <iframe src="<%=urlVideo%>" frameborder="0"></iframe>
            
        <% } %>
            <cq:include path="article-header" resourceType="foundation/components/parsys"/>
        </div>
        <div class="content-blog">
            <cq:include path="article-content-text-center" resourceType="foundation/components/parsys"/>
         
            <cq:include path="article-content-image" resourceType="foundation/components/parsys"/>
            <cq:include path="article-content-text-footer" resourceType="foundation/components/parsys"/>
        </div>

        <!--Article Tag-->
        <cq:include path="article-tag" resourceType="corporate/amway-today/components/article-tag"/>
        <!--End Article Tag -->

        <div class="vertical-toolbar-information hidden-print">
            <div class="btn-group w100pc-fixed btn-group-justified pull-left">
                <%
                    if (label != null && label.length > 0 && label[0].equals("Public")) {
                %>
                <c:if test="${enableWhatsapp}">
                    <a href="whatsapp://send" class="btn btn-social-sharing btn-icon-whatsapp" style="display:none">
                        <span class="sprites icon-whatsapp"></span>
                    </a>
                </c:if>
                <c:if test="${enableLine}">
                    <a href="#" class="btn btn-social-sharing btn-icon-line"><span class="sprites icon-line"></span></a>
                </c:if>
                <a href="#" class="btn btn-social-sharing btn-icon-twitter"><span class="sprites icon-twitter"></span></a>
                <a href="#" class="btn btn-social-sharing btn-icon-facebook"><span class="sprites icon-facebook facebook"></span></a>
                <a href="#" class="btn btn-social-sharing btn-icon-pinterest" data-pin-do="buttonPin" data-pin-shape="round">
                    <span class="sprites icon-pinterest"></span>
                </a>
                <a href="" class="btn btn-social-sharing" onclick="articleEmailSharing(this, '<%= mappedPagePath %>.html',
                        '<%= resource.getResourceType() %>',
                        '<%= CategoryUtils.getCategoryName(currentPage) %>',
                        '<%= CategoryUtils.getSubCategoryName(currentPage) %>')">
                    <span class="sprites icon-email"></span>
                </a>
                <%
                    }
                %>
                <a href="javascript:window.print()" class="btn btn-social-sharing hidden-sm hidden-md hidden-xs"><span class="sprites icon-print"></span></a>
            </div>
            <div class="btn-group w100pc-fixed btn-group-justified pull-left navigate-button-group">
                <a href="#" data-type="previous" data-value="<%= mappedPagePath %>.amway.article.nextAndPrev.json?type=PREV&createdDate=<%=publishedDate %>" class="btn navigates-blog"><fmt:message key="btnPrevious"/></a>
                <a href="#" data-type="next" data-value="<%= mappedPagePath %>.amway.article.nextAndPrev.json?type=NEXT&createdDate=<%=publishedDate %>" class="btn navigates-blog navigates-blog-next"><fmt:message key="btnNext"/></a>
                <a id="back-to-top" href="#" class="btn navigates-blog"><fmt:message key="btnBack2Top"/></a>
            </div>
        </div>
    </div>
    <div class="related-article hidden-print">
        <div class="related-article-title"><small><fmt:message key="lbRelatedArticles"/></small></div>
    </div>
</div>
