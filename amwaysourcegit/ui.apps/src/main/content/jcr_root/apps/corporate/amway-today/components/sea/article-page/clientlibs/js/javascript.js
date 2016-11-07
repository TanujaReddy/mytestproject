//Config enable email button to open default mail of OS
function articleEmailSharing(e, articleLocation, componentPath, category, subCategory) {
    e.setAttribute('href', "mailto:?body=" + window.location.href);

    // track share article via email with analytics
    if (window.isPublishInstance == 'true') {
        console.log('send share via Email event to analytics for publish instance');

        window.dataLayer = {
            site: buildDataLayerSite(),
            page: buildDataLayerPage(),
            event: {name: ['socialShare']},
            social: {type: 'email'}
        };

        _satellite.track('track_social');
    } else {
		console.log('do not send share via Email event to analytics for author instance');
    }
}

//Parse UTC time to client time for article title in article page
function renderMainPublicationDateArticle(){
    $(".main-article-publication").each(function(index, elm){
        var UTCDate = $(elm).attr("data-value");
        if (typeof UTCDate !== "undefined") {
            var publishedDate = convertUTCToLocalDate(UTCDate);
            $(elm).text(publishedDate);
        }
    })
}

//Excute to parse utc time to client time in article page when article page open
$(window).load(function() {
    renderMainPublicationDateArticle();
});
