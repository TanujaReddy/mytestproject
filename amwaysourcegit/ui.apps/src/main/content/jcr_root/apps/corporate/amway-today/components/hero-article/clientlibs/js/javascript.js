function renderHeroPublicationDateArticle(){
    var UTCDate = $("#main-hero-article-publication").attr("data-value");
    if (typeof UTCDate !== "undefined") {
		var publishedDate = convertUTCToLocalDate(UTCDate);
    	$("#main-hero-article-publication").text(publishedDate);
		$("#sub-hero-article-publication").text(publishedDate);
    }
}
$(window).load(function() {
    renderHeroPublicationDateArticle();
});