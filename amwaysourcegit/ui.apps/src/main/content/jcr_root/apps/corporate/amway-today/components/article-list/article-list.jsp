<%--

  Article List component.


--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>
<%@page contentType="text/html; charset=utf-8"
        import="java.lang.Integer,
        java.util.ResourceBundle,
        com.day.cq.i18n.I18n,
        java.util.Locale,java.net.URLEncoder,java.net.URLDecoder,
        org.strut.amway.core.util.LinkTransformerUtils" %><%
%>
<cq:setContentBundle />
<%
    Locale pageLocale = currentPage.getLanguage(false);
	ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
	I18n i18n = new I18n(resourceBundle);
	String term = slingRequest.getParameter("text");
if(term != null){
    // term = URLEncoder.encode( term, "ISO-8859-1" );
        term = URLDecoder.decode( term, "UTF-8" );
    //out.println("ih"+term);

}
%>
<%
    Boolean notificationGridViewFlag = false;
	Boolean notificationListViewFlag = false;
	String notificationLimitGridView = resourceBundle.getString("limitGridViewArticlePerPage");
	String notificationLimitListView = resourceBundle.getString("limitListViewArticlePerPage");
	String linkNoImage = resourceBundle.getString("linkNoImage");
	String serverTimeout = resourceBundle.getString("serverTimeout");
	String serverError = resourceBundle.getString("serverError");
    if(currentPage.getContentResource("article-list") != null) {
        Node node = currentPage.getContentResource("article-list").adaptTo(Node.class);
        if (!node.hasProperty("limitGridView") || Integer.valueOf(node.getProperty("limitGridView").getString()) < 4) {
            node.setProperty("limitGridView", 4);
            node.save();
            notificationGridViewFlag = true;
        }
        if (!node.hasProperty("limitListView") || Integer.valueOf(node.getProperty("limitListView").getString()) < 30) {
            node.setProperty("limitListView", 30);
            node.save();
            notificationListViewFlag = true;
        }
    }

%>
<script type="text/javascript">
    $(document).ready(function(e) {
        var articleLimitGridViewDefault = 10;
        var articleLimitListViewDefault = 50;
        var articleLimitSearchViewDefault = 50;
        var articleLimit = 0;
        var jump = 0;
        var offsetUp = 0;
        var offsetDown = 0;
        var startLoadingDown = true;
        var startLoadingUp = true;
        var searchColumn = "";
        var searchOrder = "";
        var flagRefreshArticle = true;
        var resourcePath = "<%= LinkTransformerUtils.transformPath(slingRequest.getResourceResolver(), currentPage.getPath()) %>";
        var articleQueryUrl = "";
        var includingPage = "<c:out value='${requestScope.includingPage}'/>";
        var articleQueryUrl = "<c:out value='${requestScope.articleQueryUrl}'/>";
        var articleListURL ="";
        var target = "div.article-list";
        var exception = "<%= currentDesign.getPath()%><%= linkNoImage %>";
        var flagNotification = true;
        var windowWidth = $(window).width();
        searchManager.clear();

        //Check article limit per page for list-view or grid-view
        function checkArticleLimit () {
            $(".btn-view").removeClass("active-type-view");
            if(viewType === gridView) {
                articleLimit = <%=properties.get("limitGridView")%>;
                if (articleLimit == null) { articleLimit = articleLimitGridViewDefault;}
				$("#btn-grid-view").addClass("active-type-view");
            } else {
                articleLimit = <%=properties.get("limitListView")%>;
                if (articleLimit == null) { articleLimit = articleLimitListViewDefault;}
				$("#btn-list-view").addClass("active-type-view");
            }
            if (includingPage == searchResultPage) {
                articleLimit = <%=properties.get("limitSearchView")%>;
                if (articleLimit == null) { articleLimit = articleLimitSearchViewDefault;
                }
            }
            jump = articleLimit;
        }

        //Catch event to sort search results based on columns
        function checkSearchColumns() {
            $( ".column-view" ).live( "click", function (e) {
                searchColumn = $(this).attr("column-data");
                searchOrder =  $(this).attr("column-order");
                if (searchOrder == "asc") {
                    $(this).attr("column-order","desc");
                }
                else {
                    $(this).attr("column-order","asc");
                }
                renewVariable();
                windowHeight = $(window).height();
                documentHeight = $(document).height();
                viewType = gridView;
                $("#fixed-footer").removeAttr("style");
                sessionStorage.setItem("viewType", viewType);
                sessionStorage.setItem("searchColumn", searchColumn);
                sessionStorage.setItem("searchOrder", searchOrder);
                refreshListArticle();
                timeOutPreventScrollUp(1000);
                timeOutPreventScrollDown(1000);
            });
        }

        //Get link to get article list for each page type
        function getArticleListURL(offset,startDate, endDate, order) {
            checkSessionViewType();
            checkArticleLimit();
            switch(includingPage) {
                case subCategoryPage:
                    articleListURL = resourcePath.concat("."+articleQueryUrl+"?offset="+offset+"&limit="+articleLimit+"&startDate="+encodeURIComponent(startDate)+"&endDate="+encodeURIComponent(endDate)+"&order="+order);
                    break;
                case searchResultPage:
                    // var text = encodeURIComponent("<c:out value='${param[\'text\']}'/>");
                    	var text = encodeURIComponent('<%=term%>');
                    var tagId = encodeURIComponent("<c:out value='${param[\'tagId\']}'/>");
                    if(text != null && text != '') {
                        articleListURL = resourcePath.concat("."+articleQueryUrl+"?text="+text+"&offset="+offset+"&limit="+articleLimit+"&startDate="+encodeURIComponent(startDate)+"&endDate="+encodeURIComponent(endDate)+"&order="+order+'&_charset_=UTF-8');
                        console.log("text"+text);
                        console.log("articleListURL"+articleListURL);
                    }
                    if(tagId != null && tagId != '') {
                        articleListURL = resourcePath.concat("."+articleQueryUrl+"?tagId="+tagId+"&offset="+offset+"&limit="+articleLimit+"&startDate="+encodeURIComponent(startDate)+"&endDate="+encodeURIComponent(endDate)+"&order="+order);
                    }
                    break;
                default:
                    viewType = gridView;
                    checkArticleLimit();
                    articleListURL = resourcePath.concat("." + articleQueryUrl+"?offset="+offset+"&limit="+articleLimit+"&startDate="+encodeURIComponent(startDate)+"&endDate="+encodeURIComponent(endDate)+"&order="+order);
            }
        }

        //Start to render article data in page when accessing the page
        if (startLoadingDown) {
            if(<%=notificationGridViewFlag%> && flagNotification) {
                alert("<%=notificationLimitGridView%>");
                flagNotification = false;
            }
            getArticleListURL(offsetDown, "", endDate, orderDESC);
            console.log("articleListURL start: " + articleListURL);
            if (includingPage == searchResultPage)
            {
                loadSearchArticle(articleListURL, target, includingPage, exception, orderDESC, articleLimit);
            }
            else {
                loadArticle(articleListURL, target, includingPage, exception, orderDESC, articleLimit);
            }
            startLoadingDown = false;
            if (includingPage !== mostPopularPage) { timeOutPreventScrollUp(1000); }
        }

        //Reset variable for reloading article list
        function renewVariable () {
            disableLazyLoadUp();
			disableLazyLoadDown();
            $("div.article-list").empty();
            $("div.article-list").css("height", "auto");
            $("#loading-img").show();
            checkHeight();
        }

        //Reload article list when switch view type or clicking for filtering article by month
        function refreshListArticle () {
            offsetDown = 0;
            offsetUp = 0;
            //enableLazyLoadDown();
            startLoadingUp = true;
            highLightMonthFirst = true;
            getArticleListURL(offsetDown, "", endDate, orderDESC);
            console.log("articleListURL refresh: " + articleListURL);
            $(".month-nav a.btn").each(function(index, element) {
                $(".month-nav a.btn").removeClass("active-month");
            });
            if (includingPage == searchResultPage)
            {
                loadSearchArticle(articleListURL, target, includingPage, exception, orderDESC, articleLimit);
            }
            else {
                loadArticle(articleListURL, target, includingPage, exception, orderDESC, articleLimit);
            }
        }

        //Catch event when clicking button in month filter bar for filtering article by month
        $("div.month-nav").delegate("a", "click", function(e){
        	e.preventDefault();
            endDate = $(this).attr("end-date");
            var getDate = new Date(endDate);
            var now = new Date();
            var paddingTop = 10;
            if (getDate.getMonth() === now.getMonth()) { paddingTop = 0;}
			renewVariable();
            $("html, body").stop().animate({ scrollTop: 10 }, 200,function() {
                if (flagRefreshArticle) { refreshListArticle (); flagRefreshArticle = false; enableLazyLoadUp(); enableLazyLoadDown();}
                else { flagRefreshArticle = true;  }
            });
            return false
        });

        //Load more articles when scrolling down
        function loadMoreDown() {
            if (lazyLoadMoreArticles && includingPage != searchResultPage) {
                offsetDown += jump;
                getArticleListURL(offsetDown, "", endDate, orderDESC);
                console.log("articleListURL down: " + articleListURL);
                disableLazyLoadDown();
                if (includingPage == searchResultPage)
                {
                    loadSearchArticle(articleListURL, target, includingPage, exception, orderDESC, articleLimit);
                }
                else {
                    loadArticle(articleListURL, target, includingPage, exception, orderDESC, articleLimit);
                }
            }
        }

        //Lazy load up: Load more articles when scrolling up
        function loadMoreUp() {
            if (lazyLoadMoreArticles && includingPage != searchResultPage) {
                var followingEndDate = new Date (endDate);
                followingEndDate = new Date (followingEndDate.getTime() + 1000);
                if (startLoadingUp) { startLoadingUp = false; } else { offsetUp += jump; }
                getArticleListURL(offsetUp, getISO8601LocalDate(followingEndDate), "", orderASC);
                console.log("articleListURL up: " + articleListURL);
                disableScroll();
                if (includingPage == searchResultPage)
                {
                    loadSearchArticle(articleListURL, target, includingPage, exception, orderDESC, articleLimit);
                }
                else {
                    loadArticle(articleListURL, target, includingPage, exception, orderDESC, articleLimit);
                }
                $("html, body").stop().animate({ scrollTop: 10 }, 200,function() { enableScroll(); });
            }
        }


        //Catch event to change list-view or grid-view in breadcumb
    	$(".btn-view").click(function(e) {
            if (!$(this).hasClass("active-type-view")) {
                if(<%=notificationListViewFlag%> && flagNotification) {
                    alert("<%=notificationLimitListView%>");
                    flagNotification = false;
                }
                renewVariable();
                windowHeight = $(window).height();
				documentHeight = $(document).height();
                if ($(this).attr("data-view") === "list") {
                	viewType = listView;
                    console.log(documentHeight + " - " + windowHeight)
                } else {
                	viewType = gridView;
                    console.log(documentHeight + " - " + windowHeight)
                }
                $("#fixed-footer").removeAttr("style");
                sessionStorage.setItem("viewType", viewType);
                refreshListArticle();
                timeOutPreventScrollUp(1000);
                timeOutPreventScrollDown(1000);
            }
        });

        checkSearchColumns();

        //Catch event when scrolling in browser
        var previousScroll = 0;
        $(window).scroll(function(e) {
            e.preventDefault();
        	windowHeight = $(window).height();
        	scrollTop = $(window).scrollTop();
            scrollPosition = $(window).height() + $(window).scrollTop();
            documentHeight = $(document).height();

            //Lazy load down: Load more articles when scrolling down
            if (scrollTop + windowHeight > documentHeight - 150)  {
                if (scrollDown) {
					loadMoreDown();
                }
            }

            //Lazy load up: Load more articles when scrolling up
            if (scrollTop === 0) {
                if (scrollUp) {
					loadMoreUp();
                }
            }

            if (includingPage == searchResultPage) {
                //Check article on top of page to highlight month button in month filter bar
                var on = true;
                var cur = "";
                if (viewType === gridView) {
                    $("#list-content .post").each(function (index, element) {
                        if ($(this).offset().top > scrollTop) {
                            cur = $.trim($(this).find(".article-details .pull-left").text().substr(3, 3).toUpperCase());
                            return false;
                        }
                    })
                } else {
                    $("#list-content .breaker-list").each(function (index, element) {
                        if ($(this).offset().top > scrollTop) {
                            cur = $.trim($(this).text().substr(0, 3).toUpperCase());
                            return false;
                        }
                    })
                }

                highLightMonth(cur);
            }
            //Set fixed position and relative position footer when scrolling down
            if ($(window).width() > 750) {
                if (scrollTop > previousScroll ) {
                    $("#fixed-footer").css("position", "relative");
                    //$("#fixed-footer").css({position:"fixed"});
                    if (scrollTop + windowHeight > documentHeight - 150) {
                        $("#fixed-footer").css("position", "fixed");
						if ($(window).width() < 992) {
                        	$("body").css("padding-bottom", 180);
                        } else {
                            $("body").css("padding-bottom", 150);
                        }
                    }
                } else {
                    $("#fixed-footer").css("position", "fixed");
                }
                previousScroll = scrollTop;
            }
        });

        //Catch event when resizing browser with width less than 750px
        $( window ).resize(function() {
            var newWindowWidth = $(window).width()

            /*
                check window is actually resized, especially on mobile, the show and hide of scrollbar could
                trigger unexpected resize call
            */
            if(newWindowWidth != windowWidth){
                windowWidth = newWindowWidth;

                if (windowWidth < 750 || isMobile.any()) {
                    sessionStorage.setItem("viewType", gridView);
                    renewVariable();
                    refreshListArticle();
                    enableLazyLoadUp();
                    enableLazyLoadDown();
                }
            }
        });

        //Catch event to load more article when scrolling on mouse in large screen
        $('body').on('DOMMouseScroll mousewheel', function (e) {
            if(e.originalEvent.detail > 0 || e.originalEvent.wheelDelta < 0) {
                if (documentHeight == windowHeight) {
                    console.log("Wheel down")
					loadMoreDown();
                }
            } else {
                if (documentHeight == windowHeight) {
                    console.log("Wheel up")
					loadMoreUp();
                }
            }
        });
    });
</script>


