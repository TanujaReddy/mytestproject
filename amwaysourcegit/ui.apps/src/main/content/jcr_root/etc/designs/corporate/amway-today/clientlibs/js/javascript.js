var checkMenuShow = true;
var highLightMonthFirst = true;
var articleLoaded = true;
var scrollUp = false;
var scrollDown = true;
var lazyLoadMoreArticles = true;
var getCurrentDateInMonthNav = true;
var startDate = "";
var endDate = "";
var orderASC = "ASC";
var orderDESC = "DESC";
var listView = "list-article";
var gridView = "grid-article";
var viewType = "";
var showMenu = false;
var subCategoryPage = "sub-category-page";
var searchResultPage = "search-result-page";
var mostPopularPage = "most-popular-page";
var windowHeight = 0;
var documentHeight = 0;

//Show video player popup when clicking play button in acticle
var assignShowVideo = function (title) {
    var width = 640;
    var height = 480;
    if ($(window).width() < 768) {
		width = $(window).width() - 30;
        height = 240;
    }
    $(".video-player").YouTubeModal({title: title, autoplay: 0, width: width, height: height});
    /*
        TODO: this is a hack to fix the issue that youtube popup plugin set an extra margin around youtube iframe
    */
    $(".video-player").each(function(index, elm){
        $(elm).bind("click.YouTubeModal", function(event){
            $("#YouTubeModalDialog").css({width:width})
        })
    })
}

//Check Session View Type
function checkSessionViewType() {
    if(typeof(Storage) !== "undefined") {
        if (sessionStorage.getItem("viewType") === null) {
            viewType = gridView;
        } else {
            viewType = sessionStorage.getItem("viewType");
        }
    } 
}

//Disable scrolling of browser
function disableScroll() {
    window.onmousewheel = document.onmousewheel = function(e) {
        e = e || window.event;
        if (e.preventDefault)
            e.preventDefault();
        e.returnValue = false;
    };
}

//Enable scrolling of browser
function enableScroll() {
    window.onmousewheel = document.onmousewheel = function(e) {
        e.returnValue = true;
    };
}

//Enable lazy load up
function enableLazyLoadUp() {
    scrollUp = true;
}

//Disable lazy load up
function disableLazyLoadUp() {
    scrollUp = false;
}

//Enable lazy load down
function enableLazyLoadDown() {
    scrollDown = true;
}

//Disable lazy load down
function disableLazyLoadDown() {
    scrollDown = false;
}

function disableLazyLoadMoreArticles() {
    lazyLoadMoreArticles = false;
}

//Covert UTC time to Client Time
function convertUTCToLocalDate (UTCDate) {
    if (typeof UTCDate !== "undefined") {
        var localDate = new Date(UTCDate);
        var dd = (localDate.getDate() < 10 ? "0" : "") + localDate.getDate();
        var mm = localDate.getMonth();
        var yyyy= localDate.getFullYear();
        var monthName = monthShortNames[mm];
        return dd+" "+monthName+" "+yyyy;
    }
}

//Set time out for being scrollable up
function timeOutPreventScrollUp(timeOut) {
	setTimeout(function() { enableLazyLoadUp(); }, timeOut);
}

//Set time out for being scrollable up
function timeOutPreventScrollDown(timeOut) {
	setTimeout(function() { enableLazyLoadDown(); }, timeOut);
}

//Check height site to set page height
var checkHeight = function() {
    if($("#main-content").height() < $("#navigation").height()) {
		var height= $("#inner-footer").height();
		$("#main-content").css("min-height", $("#navigation").height());
	} else {
		if (!showMenu) { $("#main-content").removeAttr("style"); }
	}
}

//Substring for article title
function subStringTitle(title, length) {
    if(title.length > length) { title = title.substring(0, length)+"..."; return title;}
    return title;
}

//Substring for article category
function subStringCategory(category, length) {
	if(category.length > length) { category = category.substring(0, length)+"..."; return category;}
    return category;
}

//Set color for month button in month filter bar
function highLightMonth(cur) {
    $(".month-nav a.btn").each(function(index, element) {
        if (cur === $.trim($(this).text().toUpperCase()))  {							
            $(".month-nav a.btn").removeClass("active-month");
            $(this).addClass("active-month");
            $('a.btn:contains("'+cur+'")').addClass("active-month");
            return false;
        } else {
			$(".month-nav a.btn").removeClass("active-month");
        }
    });
}

//Check article on top of page to highlight month button
function checkHighLightMonth() {	
    var cur = "";
    switch(viewType) {
        case gridView:
            $("#list-content .post").each(function(index, element) { 
                cur = $.trim($(this).find(".article-details .pull-left").text().substr(3, 3).toUpperCase());					
                highLightMonth(cur);
                return false;
            })
            break;
        case listView:
            $("#list-content .breaker-list").each(function(index, element) { 
                cur = $.trim($(this).text().substr(0, 3).toUpperCase());					
				highLightMonth(cur);
                return false;
            })
            break;
    }
}

//Add month group for list view in sub-category
function checkMonthGroup (order, monthGroup, target) {
	var cur = true;
    $("#list-content .month-group").each(function(index, element) { 
        if ($(this).attr("data-value") === monthGroup) {
            cur = false;
        }
    })
    if (cur) {
		var html = "";
        html += "<div class='breaker-list text-center text-uppercase'>"+monthGroup+"</div>";
        html += "<div id='month-"+monthGroup+"' class='month-group' data-value='"+monthGroup+"'></div>"
        if ( order === orderDESC) {
            $(html).appendTo(target);
        } else {
            $(html).prependTo(target);
        }
    }
}


//Add month group for list view in sub-category
function checkTableRow (order, monthGroup, target) {
    var cur = true;
    $("#list-content .month-group").each(function(index, element) {
        if ($(this).attr("data-value") === monthGroup) {
            cur = false;
        }
    })
    if (cur) {
        var html = "";
        html += "<div id='month-"+monthGroup+"' class='month-group' data-value='"+monthGroup+"'></div>"
        if ( order === orderDESC) {
            $(html).appendTo(target);
        } else {
            $(html).prependTo(target);
        }
    }
}


//Convert ISO8601 time format to client time
function getISO8601LocalDate(date) {
    var timezone = -date.getTimezoneOffset(),
        dif = timezone >= 0 ? "+" : "-",
            pad = function(num) {
                var norm = Math.abs(Math.floor(num));
                return (norm < 10 ? "0" : "") + norm;
            };
    return date.getFullYear() 
    + "-" + pad(date.getMonth()+1)
    + "-" + pad(date.getDate())
    + "T" + pad(date.getHours())
    + ":" + pad(date.getMinutes()) 
    + ":" + pad(date.getSeconds())
    + "." + pad(date.getMilliseconds())
    + dif + pad(timezone / 60) 
    + ":" + pad(timezone % 60);
}

//Get article data to render it in page
var loadArticle = function(urlFile, target, includingPage, exception, order, limit) {
    console.log("loadArticle called");
    $("#loading-img").show();
    var currentDate = new Date();
	var currentTime = currentDate.getTime();
    urlFile = urlFile + "&time=" + currentTime;

    if (includingPage === mostPopularPage) {
        disableLazyLoadMoreArticles();
    }
    
    $.ajax({
        url: urlFile,
        dataType: "json",
        timeout: 30000,
        async: false,
        success: function(data) {
            if(data.articles.length > 0) {
                articleLoaded = false;
                switch(viewType) {
                    case gridView:
                        $.each( data.articles, function( i, article ) {
                            var html = "";
                            var linkToDetail = article.link;
                            if (article.imageLink == null) { article.imageLink = exception; }
                            if (article.VideoUrl != null) { linkToDetail = article.VideoUrl; }
                            var title = subStringTitle(article.title, 70);
                            var category = subStringCategory(article.category, 20);
                            var likeNumber = article.likeNumber;
                            var articleSnippet = article.snippet;

                            html += "<div class='post w50pc pull-left pull-article-right'>"
                            html += "<div class='post-img clearfix'>"
                            if (article.VideoUrl == null) { html += "<a href='"+linkToDetail+"'><img src='"+article.imageLink+"'></a>" }
                            else { html += "<img src='"+article.imageLink+"'><a class='video-player video-player-button' href='"+linkToDetail+"'><span class='sprites icon-play-media'></span></a>" }
                            html += "</div>"

                            html += "<div class='post-content bubble bubble-right'>"
                            html += "<a href='"+article.categoryLink+"'><div class='post-category' title='"+article.category+"'>"+category+"</div></a>"
                            html += "<a "
                           // if (article.VideoUrl != null) { html += "class='video-player' " }
                            html += "href='"+article.link+"'><div class='article-title' title='"+article.title+"'>"+title+"</div>";
                            html += "<div class='article-snippet'>"+articleSnippet+"</div>";
                            html += "</a>"
                            var articleDate = new Date(article.lastPublishedDate);
                            var publishedDate = convertUTCToLocalDate(articleDate);
                            if(article.categoryType === "main-blogging" ) {
                                if (article.impression == null) { article.impression = 0; }
                                if (article.like == null) { article.like = 0; }
                                html += "<div class='article-details'>"
                                html += "<hr class='hr-article-box'>"
                                html += "<small class='text-uppercase pull-left'>"+publishedDate+"</small> <small class='pull-right hidden-xs'>"+article.impression+"</small>"
                                html += "<span class='pull-right img-social-sharing sprites icon-view hidden-xs'></span>"
                                html += "<small class='pull-right hidden-xs'>"+likeNumber+"</small>"
                                html += "<span class='pull-right img-social-sharing sprites icon-like hidden-xs'></span></div>"
                            } else {
                                html += "<div class='article-details hidden'><small class='text-uppercase pull-left'>"+publishedDate+"</small></div>"
                            }
                            html += "</div>"
                            html += "</div>"
                            if (order === orderDESC) { $(html).delay(2000*i/2).appendTo(target).animate({"margin-top": 0, "opacity": 1})
                            } else {$(html).delay(2000*i/2).prependTo(target).animate({"margin-top": 0, "opacity": 1})}
                            assignShowVideo(article.title);
                        });
                        break;
                    case listView:
                        $.each( data.articles, function( i, article ) {
                            var html = "";
                            var articleDate = new Date(article.lastPublishedDate);
                            var publishedDate = convertUTCToLocalDate(articleDate);
							var monthGroup = monthNames[articleDate.getMonth()];
                            checkMonthGroup(order, monthGroup, target);
							if (article.imageLink == null) { article.imageLink = exception; }
                            var title = subStringTitle(article.title, 70);
                            var category = subStringCategory(article.category, 15);
                            var targetMonthGroup = "#month-"+monthGroup;
                            html += "<div class='list-view-post'>"
                            html += "<a href='"+article.link+"' class='list-view-post-img'><img src='"+article.imageLink+"'></a>"
                            html += "<a href='"+article.categoryLink+"' class='list-view-post-category' title='"+article.category+"'>"+category+"</a><br>"
                            html += "<a href='"+article.link+"' class='list-view-post-title' title='"+article.title+"'>"+title+"</a>"
                            html += "</div>"
						    if (order === orderDESC) { $(html).delay(2000*i/2).appendTo(targetMonthGroup).animate({"margin-top": 0, "opacity": 1})	
                            } else { $(html).delay(2000*i/2).prependTo(targetMonthGroup).animate({"margin-top": 0, "opacity": 1})}
                            assignShowVideo(article.title);
                        });
                        break;
                }
                if (order === orderDESC) { enableLazyLoadDown(); }
            } else {
                if (includingPage === searchResultPage && data.articles.length === 0 && articleLoaded) { $(".no-search-result-notification").show(); }
            }
            if (data.articles.length <= 0 || data.articles.length < limit) {
                if (order === orderDESC) { disableLazyLoadDown(); } else if (order === orderASC) { disableLazyLoadUp(); }
                //$("#loading-img").hide();
            }
            if (includingPage === mostPopularPage) {
                disableLazyLoadDown();
				disableLazyLoadUp();
                //$("#loading-img").hide();
            }
            if (highLightMonthFirst) { checkHighLightMonth(); highLightMonthFirst = false; }
            $("#loading-img").hide();
            checkHeight();
            setHeightParbaseList();
        },
        error: function(err, status, m) {
            if(status === "timeout") {
                alert(serverTimeout);
            } else {
                //alert(serverError);
            }
        }
    });
}

//Get article search data to render it in page
var loadSearchArticle = function(urlFile, target, includingPage, exception, order, limit) {
    $("#loading-img").show();
    var currentDate = new Date();
    var currentTime = currentDate.getTime();
    urlFile = urlFile + "&time=" + currentTime;

    if (includingPage === mostPopularPage) {
        disableLazyLoadMoreArticles();
    }
    $.ajax({
        url: urlFile,
        dataType: "json",
        timeout: 30000,
        async: false,
        success: function(data) {
            if(data.articles.length > 0) {
                articleLoaded = false;
                viewType = gridView;
                switch(viewType) {
                    case gridView:
                        var monthGroup = "search-view";
                        var targetMonthGroup = "#month-"+monthGroup;
                        searchManager.initResults(data.articles)
                        searchManager.loadMoreSearchResults(target, targetMonthGroup, monthGroup, order)
                }
                if (order === orderDESC) { enableLazyLoadDown(); }
            } else {
                if (includingPage === searchResultPage && data.articles.length === 0 && articleLoaded) {
                    $(".no-search-result-notification").show();
                    $(".search-result-notification").hide();
                    $(".search-article-list-result").addClass("no-result")
                }
            }
            if (data.articles.length <= 0 || data.articles.length < limit) {
                if (order === orderDESC) { disableLazyLoadDown(); } else if (order === orderASC) { disableLazyLoadUp(); }
            }
            if (includingPage === mostPopularPage) {
                disableLazyLoadDown();
                disableLazyLoadUp();
            }
            $("#loading-img").hide();
            checkHeight();

        },
        error: function(err, status, m) {
            if(status === "timeout") {
                alert(serverTimeout);
            }
        }
    });
}

//Get article search result to render in search pop-up 
var loadSearchPopUp = function(urlFile, target, exception) {
    $("#search-icon-loading").show();
    $.ajax({
        url: urlFile,
        dataType: "json",
        timeout: 100000,
        success: function(data) {
            if(data.articles.length > 0) {	
                var html = "";	
                html += "<div class='search-result-popup'>"
                html += "<ul class='search-result-popup-list'>"	
                $.each( data.articles, function( i, article ) {							
                    var title = subStringTitle(article.title, 70);
                    var category = subStringCategory(article.category, 20);
                    if (article.imageLink == null) { article.imageLink = exception; }
                    html += "<li>"
                    html += "<a href='"+article.link+"' class='search-result-popup-thumb'><img src='"+article.imageLink+"'></a>"
                    html += "<a title='"+article.category+"' href='"+article.link+"' class='search-result-popup-category'>"+category+"</a><br>"
                    html += "<a title='"+article.title+"' href='"+article.link+"' class='search-result-popup-title text-uppercase'>"+title+"</a>"
                    html += "</li>" 		  													
                });
                html += "</ul></div>"
                $(html).appendTo(target).fadeIn();
                $("#search-icon-loading").hide();
            }else {
                $(".search-result-popup").empty();
                $("#search-icon-loading").hide();
            }
        },
        error: function(err, status, m) {
            if(status === "timeout") {
                alert(serverTimeout);
            } else {
                //alert(serverError);
                $(".search-result-popup").empty();	
            }
            $("#search-icon-loading").hide();
        }
    });
}

//Get month data to render it in month filter bar
var loadMonth = function(urlFile, target) {
    $.ajax({
        url: urlFile,
        dataType: "json",
        timeout: 30000,
        async: false,
        success: function(data) {
            $.each( data.months, function( i, month ) {
                if (data.months.length <= 1) { 
					$(".mobile-month-filter").addClass("hidden");
                    $("#main-content").addClass("top-content");
                }
                var html = "";
                html = "<a start-date='"+month.startDate+"' end-date='"+month.endDate+"' href='#' class='btn btn-default'>"+month.label+"</a>";
                if (getCurrentDateInMonthNav) {
                    startDate = month.startDate;  
                    endDate = month.endDate; 
                    getCurrentDateInMonthNav = false; 
                }
                $(html).delay(2000*i/2).appendTo(target).css({"margin-top": 0, "opacity": 1});
            });
        },
        error: function(err, status, m) {
            if(status === "timeout") {
                alert(serverTimeout);
            } else {
                //alert(serverError);
            }
        }
    });
}

//Get related article data to render it in article page
var loadRelatedArticle = function(urlFile, target, exception) {
    $.ajax({
        url: urlFile,
        dataType: "json",
        timeout: 30000,
        async: false,
        success: function(data) {
            var countRelatedArticle = 0;
            $.each( data.articles, function( i, article ) {
                var html = "";
                if (article.imageLink == null) { article.imageLink = exception; }
                var title = subStringTitle(article.title, 70);
                var category = subStringCategory(article.category, 20);
                var likeNumber = article.likeNumber;
                html += "<div class='related-article-box'><div class='post-article-content bubble bubble-bottom'>"
				html += "<a href='"+article.categoryLink+"'><div class='post-category' title='"+article.category+"'>"+category+"</div></a>"
                html += "<a href='"+article.link+"'><div class='article-title' title='"+article.title+"'>"+title+"</div></a>"
                var articleDate = new Date(article.lastPublishedDate);
                var publishedDate = convertUTCToLocalDate(articleDate);
                if(article.categoryType === "main-blogging" ) {
                    if (article.impression == null) { article.impression = 0; }
                    if (article.like == null) { article.like = 0; }
                    html += "<div class='article-details'>"
                    html += "<hr class='hr-article-box'>"
                    html += "<small class='text-uppercase pull-left'>"+publishedDate+"</small> <small class='pull-right hidden-xs'>"+article.impression+"</small>"
                    html += "<span class='pull-right img-social-sharing sprites icon-view hidden-xs'></span>"
                    html += "<small class='pull-right hidden-xs'>"+likeNumber+"</small>"
                    html += "<span class='pull-right img-social-sharing sprites icon-like hidden-xs'></span> </div>"
                } else {
                    html += "<div class='article-details hidden'><small class='text-uppercase pull-left'>"+publishedDate+"</small></div>"
                }
                html += "</div><div class='post-article-img'><a href='"+article.link+"'><img src='"+article.imageLink+"'></a></div></div>"
                $(html).delay(2000*i/2).appendTo(target).animate({"margin-top": 0, "opacity": 1});
				countRelatedArticle += 1;
            });
            if (countRelatedArticle >= 2 && $(window).width() > 768) { $("#main-content").css("min-height", 930); }
        },
        error: function(err, status, m) {
            if(status === "timeout") {
                alert(serverTimeout);
            }
        }
    });
}

//Execute login
var login = function (e) {
    var target = e.parents('.login-form');
	var urlPost = target.attr("data-value") + ".amway.login.html";
    var username = target.find(".ibo-number-input").val();
    var password = target.find(".password-input").val();
    $.ajax({
        url: urlPost,
        type: "POST",
        timeout: 30000,
        async: false,
        data: { username: username, password: password },
        success: function(data) {
            if (data.success === 1) {
                location.reload();
            } else {
                target.find(".btn-group").addClass("open")
                target.find(".login-description").removeClass("hidden");
                target.find(".login-description a").text(data.error);
            }
        },
        error: function(err, status, m) {
            console.log("error");
        }
    });

    // track login with analytics
    if (window.isPublishInstance == 'true') {
        console.log('send login event to analytics for publish instance');

        window.dataLayer = {
            site: buildDataLayerSite(),
            event: {name: ['login']}
        };

        _satellite.track('track_users');
    } else {
		console.log('do not send login event to analytics for author instance');
    }
}

//Remove class no-touch of body tag in device
$(document).on({ "touchstart" : function() {
    $("body").removeClass("no-touch"); 
} });

//Check website's working on device or desktop
var isMobile = {
    Android: function() {
        return navigator.userAgent.match(/Android/i);
    },
    iOS: function() {
        return navigator.userAgent.match(/iPhone|iPad|iPod/i);
    },
    Windows: function() {
        return navigator.userAgent.match(/IEMobile/i);
    },
    any: function() {
        return (isMobile.Android() || isMobile.iOS() || isMobile.Windows());
    }
};

//Delete blank at the top of page in mobile
function deleteBlankTop () {
    if($(window).width() < 768) {$("#main-content").addClass("top-content");}
}

$(document).ready(function(e) {	
	windowHeight = $(window).height();
    var scrollTop = $(window).scrollTop();
    var scrollPosition = 0;
    documentHeight = $(document).height();

    $(".dropdown-menu").find("form").click(function (e) {
        e.stopPropagation();
    });

    $(".has-sub ul li").click(function(e) {
        e.stopPropagation();
    });

    $( window ).load(function() {
		$("#loading-img").hide();
    });

    //Catch event when typing in search bar. Get text in input form
    $(".has-suggest input").keyup(function(e) {
        var target = $(this).parents(".header-search");
        var resourcePath = $(this).attr("resource-path");
        var exception = $(this).attr("link-no-image");
        if ($(this).val() !== "") {
            var text = encodeURIComponent($(this).val());
            var articlePopUpResult = resourcePath.concat(".amway.search.popup.json?text="+text);
            loadSearchPopUp(articlePopUpResult, target, exception);
        } else {
            $(".search-result-popup").empty();
            $("#search-icon-loading").hide();
        }
        
    });

	if ($(".active").length > 0) {
        $(".active").find(".explander").text("-");
        $(".active").find(".sub-menu").show();	 		
    }

    //Catch event when expending menu in navigation
    $(".has-sub").click(function() {
        $(this).siblings().removeClass("active").find(".explander").text("+");
        $(this).siblings().find(".sub-menu").slideUp();
        $(this).find(".sub-menu").slideToggle();
        $(this).toggleClass("active");
        if ($(this).find(".explander").text() === "+") { 
            $(this).find(".explander").text("-");
        } else { 
            $(this).find(".explander").text("+");
        }
        if((documentHeight - scrollPosition) / documentHeight === 0 && $("#navigation").height() > $(window).height()) {
            $("#navigation").css({"top":"auto", "bottom":145});
        } else {
            $("#navigation").css({"top":0, "bottom":"auto"});
        }
        //Reset height page when clicking menu to show list sub-category
        setTimeout(function(){ if(checkMenuShow){ checkHeight(); checkMenuShow = false;}}, 300);
    });

    //Catch event when clicking to show menu on the left in mobile
    $("#toggle-sidebar-left").click(function() {	
        $("html, body").animate({scrollTop: 1}, 200);        
        if($(window).width() < 768) {
            if(!showMenu) {	
                showMenu = true;	
                $("#navigation").css("left", 0);
                $("#main-content, #header-mobile").css("left", 242);
                $(".overlay").css({"left":242, "display":"block"});
            } else {
                showMenu = false;
                $("#navigation").css("left", -240);
                $("#main-content, #header-mobile").css("left", 0);				
                $(".overlay").css({"left":0, "display":"none"});		   
            }
        }
		checkHeight();
    });

    //Catch event when clicking to show menu on the right in mobile
    $("#toggle-sidebar-right").click(function() {	
        $("html, body").animate({scrollTop: 1}, 200);     
        if($(window).width() < 768) {			
            if(!showMenu) {	
                showMenu = true;	
                $("#settings").css("right", 0);
                $("#main-content, #header-mobile").css({"left":-242, "right":242});						
                $(".overlay").css({"left":-242, "display":"block"});			
                
            } else {
                $("#settings").css("right", -240);
                $("#main-content, #header-mobile").css({"left":0, "right":0});			
                $(".overlay").css({"left":0, "display":"none"});	
                showMenu = false;				
            }
        }
		checkHeight();
    });

    //Create a transparent div to cover in page when clicking show left/right menu in mobile
    $(".overlay").click(function(e) {		
        $("#navigation").css("left", -240);
        $("#settings").css("right", -240);
        $("#main-content, #header-mobile").css({"left":0, "right":0});
        $(".overlay").css({"left":0, "display":"none"});
        showMenu = false;
    });

    //Catch event when search bar is submited. When user doesn't type anything, form 's not submited
    $(".header-search").submit(function( e ) {
         var text = $(this).find("input:first").val().trim();
         if (text == "" || text.match(/^\s*$/)) {
             e.preventDefault();
             return false;
         }
    });

    //Catch event when focusing to user name input in mobile
    $("#setting-menu input.ibo-number-input").focus(function(e) {
        e.preventDefault();
        showMenu = false;
        setTimeout(function() { $("#toggle-sidebar-right").trigger("click"); }, 600);
    });

    //Catch event when focusing to password input in mobile
    $("#setting-menu input.password-input").focus(function(e) {
        e.preventDefault();
        showMenu = false;
        setTimeout(function() { $("#toggle-sidebar-right").trigger("click"); }, 600);
    });

    //Catch event when focusing to search bar in mobile
    $(".mobile-search .form-search").focus(function(e) {
        e.preventDefault();
        if (!$.browser.chrome) {
            if ($(window).scrollTop() > 52) {
               $("#header-mobile").css("top", -52);
            }
        }
    });

    //Catch event when bluring to search bar in mobile
    $(".mobile-search .form-search").blur(function(e) {
        e.preventDefault();
		if ( !$.browser.chrome) {
            $("#header-mobile").css("top", 0);
        }
    });

    //Catch event when scrolling in browser. Set fixed position for navigation when scrolling down
    $(window).scroll(function(e) {
		e.preventDefault();
		var windowHeight = $(window).height();
        scrollTop = $(window).scrollTop();
        scrollPosition = $(window).height() + $(window).scrollTop();		
        documentHeight = $(document).height();
        if(scrollTop > 70 && $(window).width() > 768) {			
            $("#navigation").addClass("fixed-nav")						
            if ((documentHeight - scrollPosition) / documentHeight === 0 && $("#navigation").height() > $(window).height()) {
                $("#navigation").css({"top":"auto", "bottom":145});
            } else {
                $("#navigation").css({"top":0, "bottom":"auto"});
            }
        } else {
            $("#navigation").removeClass("fixed-nav");
        }
    });

    //Catch event when resizing browser. Reset position of all items: navigation, setting, main-content
    $( window ).resize(function() {
        if ($(window).width() > 768) {
            $("#navigation").css("left", 0);
            $("#settings").css("right", 0);
            $("#main-content, #header-mobile").css({"left":0, "right":0});
            $(".overlay").css({"left":0, "display":"none"});
        } else {
            $("#navigation").attr("style", "");
            $("#settings").attr("style", "");
            $("#main-content, #header-mobile").attr("style", "");;
			$(".overlay").attr("style", "");
            showMenu = false;
        }
    });

    //Scroll to top when click Back to top button
    $("#back-to-top").click(function() {
        $("html, body").animate({scrollTop: 0}, 300);
    });

	//Send request username and password for login form when pressing enter button
    $(".login").submit(function(e) {
		e.preventDefault();
        login($(e.target));
    });

    //Send request username and password for login form when clicking login button
    $(".btn-login a").click(function(e) {
        e.preventDefault();
        login($(e.target));
    });

    //Execute logout
	$(".btn-logout a").click(function(e) {
        e.preventDefault();
        var target = $(this).parents('.login-form');
        var urlPost = target.attr("data-value") + ".amway.logout.html";
        $.ajax({
            url: urlPost,
            type: "POST",
            timeout: 30000,
            async: false,
            success: function() {
				location.reload();
            },
            error: function(err, status, m) {
                console.log("error");
            }
        });

        // track logout with analytics
        if (window.isPublishInstance == 'true') {
            console.log('send logout event to analytics for publish instance');

            window.dataLayer = {
                site: buildDataLayerSite(),
                event: {name: ['logout']}
            };

            _satellite.track('track_users');
        } else {
            console.log('do not send logout event to analytics for author instance');
        }
    });

    //Catch event to redirect previous/next article page when clicking previous/next button in article page
    $("div.navigate-button-group").delegate("a.navigates-blog", "click", function(e) {
        e.preventDefault();
        var linkData = $(this).attr("data-value");
        var typeButton = $(this).attr("data-type");
        $.ajax({
            url: linkData,
            dataType: "json",
            timeout: 30000,
            async: false,
            success: function(data) {
                if(data.articles.length > 0) {
                    var linkPage = data.articles[0].link;
                    window.location.href = linkPage;
                    $("a.navigates-blog").removeClass("disabled");
                } else {
                    $(".navigates-blog[data-type='"+typeButton+"']").addClass("disabled");
                    $(".navigates-blog[data-type='"+typeButton+"']").css("background", "#0167b1");
                }
            },
            error: function(err, status, m) {
                if(status === "timeout") {
                    alert(serverTimeout);
                }
            }
        });
    });
    
    //Find all article links and make them in a new browser window/tab
    $(".content-blog a").each(function() { 
        $(this).attr("target", "_blank");
    });
});

// ################ Cookie Handler ##### //
var getCookieByName = function(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }
    return "";
}

var isArticleLiked = function(articleId, likedArticlesCookie){
    var likedArticlesArray = likedArticlesCookie.split(',');
    for(var i=0; i<likedArticlesArray.length; i++){
        var likedArticle = likedArticlesArray[i];
        if(articleId === likedArticle) return true;
    }
    return false;
}

var addArticlePathToCookieString = function(articleId, likedArticlesCookie){
    if(isArticleLiked(articleId, likedArticlesCookie)) return;
    if(likedArticlesCookie === "") return articleId;
    return likedArticlesCookie + "," + articleId;
}

var setCookie = function(cname, cvalue) {
    var d = new Date();
    d.setTime(d.getTime() + (1000*24*60*60*1000)); // expires after 1000 days
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires + "; path=/";
}

// ################ Check Browser Compatibility ##### //

function checkBrowserCompatibility() {
    browser={};
    if (/(chrome\/[0-9]{2})/i.test(navigator.userAgent)) {
        browser.agent = navigator.userAgent.match(/(chrome\/[0-9]{2})/i)[0].split("/")[0];
        browser.version  = parseInt(navigator.userAgent.match(/(chrome\/[0-9]{2})/i)[0].split("/")[1]);
    } else if (/(firefox\/[0-9]{2})/i.test(navigator.userAgent)) {
        browser.agent = navigator.userAgent.match(/(firefox\/[0-9]{2})/i)[0].split("/")[0];
        browser.version  = parseInt(navigator.userAgent.match(/(firefox\/[0-9]{2})/i)[0].split("/")[1]);
    } else if (/(safari\/[0-9]{2})/i.test(navigator.userAgent)) {
        browser.agent = navigator.userAgent.match(/(safari\/[0-9]{2})/i)[0].split("/")[0];
        browser.version = 8;
    } else if (/(MSIE\ [0-9]{1})/i.test(navigator.userAgent)) {
        browser.agent = navigator.userAgent.match(/(MSIE\ [0-9]{1})/i)[0].split(" ")[0];
        browser.version  = parseInt(navigator.userAgent.match(/(MSIE\ [0-9]{1})/i)[0].split(" ")[1]);
        if (browser.version == 1) {
            browser.version = 11;
        }
    } else if (/(Trident\/[7]{1})/i.test(navigator.userAgent)) {
        browser.agent = "MSIE";
        browser.version  = 11;
    } else {
        browser.agent = false;
        browser.version  = false;
    }

    var supported = true;

    if (browser.agent == "MSIE" && browser.version < 9) {
        supported = false;
    }

    if (browser.agent == "Safari" && browser.version < 8) {
        supported = false;
    }

    if (browser.agent == "Firefox" && browser.version < 33) {
        supported = false;
    }

    if (browser.agent == "Chrome" && browser.version < 39) {
        supported = false;
    }


    if (!supported) {
        $(".global-warning-message").show();
    }
    else {
        $(".global-warning-message").hide();
    }
}


// ########### Advanced Search - ATO-80 #### //

var SearchManager = function(){
    this.allSearchResults = []
    this.loadedSearchResults = []
    this.allCategories = []
    this.displayedSearchResults = []

    this.mouseWheelImpl = null;

    //filters
    this.filterCategories = []
    this.sortSetting = null


    this.includingPage = "";
    this.articleQueryUrl = "";
    this.searchPageState = 1;//0 is loading, 1 is finished loading, 2 is loading error, 3 is no available articles
    this.scrollState = 0; // 0 is static, 1 is scrolling
    this.lazyLoadingNum = 8;
};


SearchManager.prototype.clear = function(){
    this.allSearchResults = []
    this.loadedSearchResults = []
    this.displayedSearchResults = []
    this.allCategories = []
    this.enableScroll()

    //filters
    this.filterCategories = []
    this.sortSetting = null;
    this.scrollState = 0;
}

SearchManager.prototype.initResults = function(results){
    var isFirstSearch = this.allSearchResults.length == 0

    if(isFirstSearch){
        this.allSearchResults = this.allSearchResults.concat(results)
        for(var i = 0; i < results.length; ++i){
            this.allCategories.push(results[i].category);
        }
        this.allCategories = $.unique(this.allCategories)
        this.filterCategories = this.allCategories.slice()
        this._initFiltersUI('.search-result-filter');
    }

}

SearchManager.prototype.loadMoreSearchResults = function(target, targetMonthGroup, monthGroup, order){
    var searchInstance = this;
    this.target = target;
    this.targetMonthGroup = targetMonthGroup;
    this.monthGroup = monthGroup;
    this.order = order;
    if(searchInstance.searchPageState == 1){
        searchInstance.searchPageState = 0;

        $.data(this, 'scrollTimer', setTimeout(function() {
            do{
                searchInstance.loadedSearchResults = searchInstance.loadedSearchResults.concat(
                    searchInstance.allSearchResults.slice(searchInstance.loadedSearchResults.length,
                        searchInstance.loadedSearchResults.length + searchInstance.lazyLoadingNum));
                var nextDisplaySearchResults = searchInstance.getDisplayResults();

                if(searchInstance.loadedSearchResults.length == searchInstance.allSearchResults.length){
                    searchInstance.searchPageState = 3;
                    break;
                }
            }while(nextDisplaySearchResults.length - searchInstance.displayedSearchResults < searchInstance.lazyLoadingNum)
            searchInstance.searchPageState = 1;
            searchInstance.updateUI()
        }, 250));
    }

}

SearchManager.prototype.updateUI = function(){
    var target = this.target;
    var targetMonthGroup = this.targetMonthGroup;
    var monthGroup = this.monthGroup;
    var order = this.order;
    $(targetMonthGroup).empty()
    var articleTemplate = '<div class="search-result-item-container">' +
        '<a class="search-result-item" href="<%= link %>">'+
        '<div class="image-container pull-left">'+
        //TODO: imageLink is not supposed to be undefined, end point implementation need to be changed
        '<% if(typeof imageLink != "undefined") {%> <img src="<%= imageLink %>" />' +
        '<% }else {%> <img src="/etc/designs/corporate/amway-today/images/no_image.jpg" /> <% } %>' +
        '</div>' +
        '<div class="detail-container pull-left">' +
        '<div class="title"><%= title %></div>'+
        '<div class="snippet"><%= snippet %></div>' +
        '<div class="classification">' +
        '<% if(tags.length > 0){ %><div class="tags">Tags: <%= tags %></div><%}%>'+
        '<div class="category">Category: <%= category %></div>'+
        '</div>' +
        '</div>' +
        '<div class="metadata-container pull-left">' +
        '<div class="lastPublishedDate"><% print(convertUTCToLocalDate(lastPublishedDate)) %></div>' +
        '<div class="metric">' +
        '<span class="img-social-sharing sprites icon-like"></span>' +
        '<span class="likeNumber"><%= likeNumber %></span>' +
        '<span class="img-social-sharing sprites icon-view"></span>' +
        '<span class="impression"><%= impression %></span>' +
        '</div>' +
        '</div>' +
        '</a>' +
        '</div>'
    var tpl = _.template(articleTemplate);
    this.displayedSearchResults = this.getDisplayResults();

    if(this.displayedSearchResults.length == 0)
        $(".search-article-list-result").addClass("no-result")
    else
        $(".search-article-list-result").removeClass("no-result");

    for(var i = 0; i < this.displayedSearchResults.length; ++i){
        var item = {};
        $.extend(true, item, this.displayedSearchResults[i]);
        checkTableRow(order, monthGroup, target);
        if(item.title.length > 43)
            item.title = item.title.substring(0,40) + "..."
        item.tags = item.tags.join(", ")
        $(tpl(item)).delay(2000*0/2).appendTo(targetMonthGroup).animate({"margin-top": 0, "opacity": 1});
    }

    this.searchPageState = 1;
}


SearchManager.prototype._initFiltersUI = function(target){
    var self = this;
    var searchResultSortHtml = '<select class="selectpicker search-sort-field" title="Sort by">'+
        '<option value="title">Title</option>' +
        '<option value="lastPublishedDate">Date</option>' +
        '<option value="category">Category</option>' +
        '</select>';
    var searchResultFilterHtml = '<select class="selectpicker search-filter-field" multiple title="Filter by" data-count-selected-text="Filter by" data-selected-text-format="count">'
    for(var i = 0; i < this.allCategories.length; ++i){
        searchResultFilterHtml += '<option value="' + this.allCategories[i] + '">' + this.allCategories[i] + "</option>";
    }
    searchResultFilterHtml += "</select>"


    $(searchResultSortHtml).appendTo(target).selectpicker().change(function () {
        var field = $(this).find("option:selected").val();
        if(field === "lastPublishedDate"){
            searchManager.sort('lastPublishedDate', function(str){return new Date(str)})
        }else{
            searchManager.sort(field)
        }
        self.updateUI(self.target, self.targetMonthGroup, self.monthGroup, self.order)
    });
    var filterComponent = $(searchResultFilterHtml).appendTo(target).selectpicker()
    filterComponent.selectpicker('val', this.filterCategories);
    filterComponent.change(function () {
        self.filter($(this).val());
        self.updateUI(self.target, self.targetMonthGroup, self.monthGroup, self.order)
    });

}

SearchManager.prototype.filter = function(categories){
    this.filterCategories = categories;
}

SearchManager.prototype.sort = function(field, parser){
    this.sortSetting = {
        field: field,
        parser: parser
    }

    if(this.sortSetting){
        var parser = this.sortSetting.parser;
        var field = this.sortSetting.field;
        this.loadedSearchResults = this.loadedSearchResults.sort(function(a,b){
            if(parser){
                return (parser(a[field]) > parser(b[field]))? 1 : -1
            }
            return (a[field] > b[field])? 1 : -1
        })
    }
}

SearchManager.prototype.enableScroll = function(){
    var self = this;
    this.mouseWheelImpl = self._mouseWheelEvent(self);
    $(window).scroll(this.mouseWheelImpl);
}

SearchManager.prototype.disableScroll = function(){
    var self = this;
    $(window).off('scroll', this.mouseWheelImpl);
    this.mouseWheelImpl = null;
}

SearchManager.prototype._mouseWheelEvent =function(self){
    self = self || this;
    return function(e){
        // added offset 3 to make sure this function will get called when user scroll to the bottom
        if($(window).scrollTop() + $(window).height() >= self._getDocHeight() - 3){
            self.loadMoreSearchResults(self.target, self.targetMonthGroup, self.monthGroup, self.order);
        }
    }
}

SearchManager.prototype._getDocHeight = function() {
    var D = document;
    return Math.max(
        D.body.scrollHeight, D.documentElement.scrollHeight,
        D.body.offsetHeight, D.documentElement.offsetHeight,
        D.body.clientHeight, D.documentElement.clientHeight
    );
}

SearchManager.prototype.getDisplayResults = function(){
    var self = this;
    var newSearchResults = this.loadedSearchResults.filter(function(obj){
        if (self.filterCategories && self.filterCategories.length > 0) {
            for (var i = 0; i < self.filterCategories.length; ++i) {
                if (obj.category === self.filterCategories[i]) {
                    return true;
                }
            }
        }
        return false;
    })
    return newSearchResults;
}
var searchManager = new SearchManager();
// ################ Check Browser Compatibility ##### //

function checkBrowserCompatibility() {
    browser={};
    if (/(chrome\/[0-9]{2})/i.test(navigator.userAgent)) {
        browser.agent = navigator.userAgent.match(/(chrome\/[0-9]{2})/i)[0].split("/")[0];
        browser.version  = parseInt(navigator.userAgent.match(/(chrome\/[0-9]{2})/i)[0].split("/")[1]);
    } else if (/(firefox\/[0-9]{2})/i.test(navigator.userAgent)) {
        browser.agent = navigator.userAgent.match(/(firefox\/[0-9]{2})/i)[0].split("/")[0];
        browser.version  = parseInt(navigator.userAgent.match(/(firefox\/[0-9]{2})/i)[0].split("/")[1]);
    } else if (/(safari\/[0-9]{2})/i.test(navigator.userAgent)) {
        browser.agent = navigator.userAgent.match(/(safari\/[0-9]{2})/i)[0].split("/")[0];
        browser.version = parseInt(navigator.userAgent.match(/(version\/[0-9]{1})/i)[0].split("/")[1]);;
    } else if (/(MSIE\ [0-9]{1})/i.test(navigator.userAgent)) {
        browser.agent = navigator.userAgent.match(/(MSIE\ [0-9]{1})/i)[0].split(" ")[0];
        browser.version  = parseInt(navigator.userAgent.match(/(MSIE\ [0-9]{1})/i)[0].split(" ")[1]);
        if (browser.version == 1) {
            browser.version = 11;
        }
    } else if (/(Trident\/[7]{1})/i.test(navigator.userAgent)) {
        browser.agent = "MSIE";
        browser.version  = 11;
    } else {
        browser.agent = false;
        browser.version  = false;
    }

    var supported = true;

    if (browser.agent == "MSIE" && browser.version < 9) {
        supported = false;
    }

    if (browser.agent == "Safari" && browser.version < 8) {
        supported = false;
    }

    if (browser.agent == "Firefox" && browser.version < 33) {
        supported = false;
    }

    if (browser.agent == "Chrome" && browser.version < 39) {
        supported = false;
    }


    if (!supported) {
        $(".global-warning-message").show();
    }
    else {
        $(".global-warning-message").hide();
    }
}
