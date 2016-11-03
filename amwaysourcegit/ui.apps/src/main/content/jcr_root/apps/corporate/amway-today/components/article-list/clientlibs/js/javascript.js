//Set height for parsys of div.article-list
var setHeightParbaseList = function(){	
    $('.article-list').height($('#main-content').height()-$(".main-post-img").height()-$(".container-breadcrumb").height());
}