<%-- Article Page component.--%>
<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8"
    import="com.day.cq.i18n.I18n,
    org.apache.commons.lang3.StringUtils,
    org.strut.amway.core.controller.ArticleLabelFilterControllerSeo,
    org.strut.amway.core.services.ArticleStatisticsService,
    org.strut.amway.core.model.AuthorUserDetails,
     org.apache.commons.lang3.StringEscapeUtils,
     org.apache.commons.lang3.StringUtils,
    org.strut.amway.core.util.*,
     java.util.ArrayList,
    java.util.GregorianCalendar,
    org.strut.amway.core.services.ArticleService,
    java.util.Locale,
    java.util.ResourceBundle,
    org.strut.amway.core.controller.LoginController, 
    com.day.cq.wcm.api.WCMMode"%>
<%@page import="org.strut.amway.core.util.AnalyticsUtils" %>
<style>
		.profile-container{
			margin: 30px 0 20px;
		    border: 1px solid #d7d8d8;
		    display: inline-block;
		    border-left: 0;
		    border-right: 0;
		    width: 100%;
		    padding: 20px 0;
		}
		
		.profile-container .user-photo-container{
			background: no-repeat;
			width: 150px;
			height: 150px;
			position: relative;
			float: left;
		    border-radius: 100px;
			background-size: cover;
			background-image: url(/libs/granite/security/clientlib/themes/default/resources/sample-user-thumbnail.36.png);
		}
		.profile-container .text{
			padding: 5px 35px 20px;
			position: relative;
			float: left;
			width:75%;
			-webkit-box-sizing: border-box;
			-moz-box-sizing: border-box;
			box-sizing: border-box;
		}
		.profile-container h4{
			padding: 0 0 5px;
		    color: #696969;
			margin: 0;
			font-weight: normal;
			width: 100%;
		}
		.profile-container p{
			padding: 0 0 5px;
		    color: #696969;
			margin: 0;
			font-weight: normal;
			width: 100%;
			font-size: 12px;
		}
		
		@media screen and (max-width: 655px){
			.profile-container .user-photo-container{
			    width: 100px;
    			height: 100px;
    		}
    		
    		.profile-container .text{
    			width: 70%;
    		}
		}
		}
	</style>
<%
  response.setHeader("Dispatcher", "no-cache");
%>
<cq:setContentBundle />

<%
 
    final boolean isEdit = WCMMode.fromRequest(request) == WCMMode.EDIT;
    final ArticleLabelFilterControllerSeo secFilter = new ArticleLabelFilterControllerSeo();
   secFilter.checkUserIsEligibleForViewArticlePage(currentPage, slingRequest, slingResponse, !isEdit);
    
    Locale pageLocale = currentPage.getLanguage(false);
    ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
    I18n i18n = new I18n(resourceBundle);
%>
<%
    // Integer maxLengthTitle = Integer.valueOf(resourceBundle.getString("maxLengthTitle"));
	String userId = "";	
	String firstName= "";
	String lastName= "";
	String email= "";
	String aboutMe= "";
	String imagePath= "";
	String authorName = "";
	String authorProfileName = "";
    Integer maxLengthCategory = Integer.valueOf(resourceBundle.getString("maxLengthCategory"));
    String linkNoImage = resourceBundle.getString("linkNoImage");
    String publishedDate = "";
    Long views = 0L;
    final Page category = currentPage.getParent(2);
    String categoryTitle = category.getTitle();
    String title = currentPage.getTitle();
    //if (title.length() > maxLengthTitle) { title = title.substring(0, maxLengthTitle) + "...";}
    if (categoryTitle.length() > maxLengthCategory) { categoryTitle = categoryTitle.substring(0, maxLengthCategory)+"..."; }
    String type = String.valueOf(currentPage.getParent(2).getProperties().get(CategoryConstants.CATEGORY_TYPE));
    final GregorianCalendar lastPublishedDate =  ArticleUtils.getPublishedDate(currentPage);
    if(lastPublishedDate != null) {
        publishedDate = DateTimeUtils.parseToUTC(lastPublishedDate).toString();
    }
    final ArticleStatisticsService articleStatsService = sling.getService(ArticleStatisticsService.class);
    views = articleStatsService.getAggregatedStatisticsLong(ArticleConstants.STATISTIC_VIEWS , currentPage.getPath());
    
    

    String[] label = (String[]) currentPage.getProperties().get("label");
    String[] authorProfileNames = (String[]) currentPage.getProperties().get("authorProfileName", new String[0]); // default is empty array
    String urlVideo = String.valueOf(currentPage.getProperties().get("video_url"));
	String video_width = String.valueOf(currentPage.getProperties().get("video_width"));
	String video_height = String.valueOf(currentPage.getProperties().get("video_height"));
    
    if( currentPage.getProperties().get(ArticleConstants.AUTHOR_NAME) != null) {
        authorName = currentPage.getProperties().get(ArticleConstants.AUTHOR_NAME, String.class);
    }
    if (authorProfileNames != null && authorProfileNames.length > 0){
    	for(int i = 0; i < authorProfileNames.length; ++i){
    		authorProfileName = authorProfileNames[i];
    	}
    }
    
    
    final ArticleService articleService = sling.getService(ArticleService.class);
    ArrayList<AuthorUserDetails> users = new ArrayList();
    
    users = articleService.getUserDetails(slingRequest);
    
    for (AuthorUserDetails authoruser: users) {
    	
    		
    	if(authorProfileName.equalsIgnoreCase(authoruser.getLastName())) {
    		userId = authoruser.getUserId();
   	 	 firstName= authoruser.getFirstName();
   	 	 lastName= authoruser.getLastName();
   	 	 email= authoruser.getEmail();
   	 	 aboutMe= authoruser.getAboutMe();
   	 	 imagePath= authoruser.getImage();
    	}
    }
    String mappedPagePath = LinkTransformerUtils.transformPath(slingRequest.getResourceResolver(), currentPage.getPath());

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


<div class="sea-article-wrapper">
<div class="article-v2">
  <h1 class="maintitle"><%= title %></h1>
	<%
                if (type.equals("main-blogging")) {
            %>
  <div class="row">
    <div class="col-md-3">
      <div class="byline pull-left">
			 		<%
                        if(!StringUtils.isEmpty(authorProfileName) && !authorProfileName.contains("--")) {
                    %>
                    <span class="authorName">&nbsp;<%= authorProfileName %></span>
                    <%
                        }
                    %>
              
        </div>
    </div>
    <div class="col-md-9">
      <div class="author-blogNEW pull-right hidden-xs">

                    <!-- Disable Line for Desktop - SEAAMT-20 - start -->
<!--           <c:if test="${enableLine}"> -->
<!--         	<a href="#" class="btn btn-social-sharing btn-icon-line"><span class="sprites icon-line"></span></a> -->
<!--               </c:if> -->
              
              <!-- Disable Line for Desktop - SEAAMT-20 - end -->
        <a href="#" class="btn btn-social-sharing btn-icon-twitter"><span class="sprites icon-twitter"></span></a>
        <a href="#" class="btn btn-social-sharing btn-icon-facebook"><span class="sprites icon-facebook"></span></a>
        <a href="#" class="btn btn-social-sharing btn-icon-pinterest" data-pin-do="buttonPin" data-pin-shape="round"><span class="sprites icon-pinterest"></span></a>
        <a href="#" class="btn btn-social-sharing" onclick="articleEmailSharing(this,
                                                                                       '<%= mappedPagePath %>.html',
                                                                                      '<%= resource.getResourceType() %>', '<%= CategoryUtils.getCategoryName(currentPage) %>',
                           															 '<%= CategoryUtils.getSubCategoryName(currentPage) %>')">
          <span class="sprites icon-email"></span>
        </a>

        <a href="javascript:window.print()" class="btn btn-social-sharing"><span class="sprites icon-print"></span></a>
      </div>
    </div>
  </div>



	<%
                }
            %>
  <div class="row mt-15 mb-15">
    <div class="col-md-12 article-content">
        	<%
                if (!(urlVideo == null || "".equals(urlVideo) || "null".equals(urlVideo))) {
            %>
        		
            <iframe src="<%=urlVideo%>" frameborder="0"></iframe>
            
        <% } %>
            <cq:include path="article-header" resourceType="foundation/components/parsys"/>
        <%
                if (type.equals("main-blogging")) {
            %>
        	<div class="row">
    <div class="col-md-12">
      <!-- Article Details -->
      <div class="article-details">
        <div class="article-date pull-left">
          <small class="main-article-publication text-uppercase pull-left" data-value="<%= publishedDate %>"></small>
        </div>
        <div class="pull-right">

          <span class="pull-left img-social-sharing sprites icon-view"></span>
          <small id="" class="number-of-views-text pull-left ml-5">0</small>
          <span id="" class="number-of-like-btn pull-left img-social-sharing sprites icon-like ml-15"></span>
          <small id="" class="number-of-like-text pull-left ml-5">0</small>

        </div>
      </div>
      <!-- End Article Details -->
    </div>
  </div>
         <%
                        }
                    %>

            <cq:include path="article-content-image" resourceType="foundation/components/parsys"/>
       	 	<cq:include path="article-content-text-center" resourceType="foundation/components/parsys"/>
            <cq:include path="article-content-text-footer" resourceType="foundation/components/parsys"/>
    </div>
  </div>


 <div class="row">
    <div class="col-md-12">
      <!--Article Tag-->
      <cq:include path="article-tag" resourceType="corporate/amway-today/components/article-tag"/>
      <!--End Article Tag -->
    </div>
  </div>
  
<% if(authorProfileName != null && authorProfileName != "" && !authorProfileName.contains("--")) { %>
<div class="profile-container">
		<div class="user-photo-container user-photo-detailed" data-background-image="/libs/granite/security/clientlib/themes/default/resources/sample-user-thumbnail.36.png" style="background-image: url('<%=imagePath%>');"></div>
		<div class="text">
	       <h4> <%=firstName %> <%=lastName %> </h4>
	       <p>
		        <%= aboutMe %> <br>
	       </p>
		</div>
	</div>
<%} %>
	


  <div class="row">
    <div class="col-md-12">
      <div class="vertical-toolbar-information hidden-print article-page-button-group">

        <div class="btn-group w100pc-fixed btn-group-justified pull-left hidden-md hidden-lg">
            <c:if test="${enableWhatsapp}">
          <a href="whatsapp://send" class="btn btn-social-sharing btn-icon-whatsapp">
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
                            '<%= resource.getResourceType() %>', '<%= CategoryUtils.getCategoryName(currentPage) %>',
                            '<%= CategoryUtils.getSubCategoryName(currentPage) %>')">
                        <span class="sprites icon-email"></span>
                    </a>
            </div>


        <div class="btn-group w100pc-fixed btn-group-justified pull-left navigate-button-group">
          <a href="#" data-type="previous" data-value="<%= mappedPagePath %>.amway.article.nextAndPrev.json?type=PREV&createdDate=<%=publishedDate %>" class="btn navigates-blog"><fmt:message key="btnPrevious"/></a>
                    <a href="#" data-type="next" data-value="<%= mappedPagePath %>.amway.article.nextAndPrev.json?type=NEXT&createdDate=<%=publishedDate %>" class="btn navigates-blog navigates-blog-next"><fmt:message key="btnNext"/></a>
          <a id="back-to-top" href="#" class="btn navigates-blog">Back to top</a>
        </div>        
      </div>
    </div>
  </div>


</div>

<!-- .article -->
<!-- Related Articles -->
<div class="sea-article-side">
  <div class="related-article hidden-print">
    <div class="related-article-title"><small><fmt:message key="lbRelatedArticles"/></small></div>
  </div>
</div>
  <!-- End Related Articles -->
</div>

