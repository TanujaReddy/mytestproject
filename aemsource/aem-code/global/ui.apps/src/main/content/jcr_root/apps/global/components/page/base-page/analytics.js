"use strict";

use( function () {

    var pageDepth = currentPage.getDepth();
    var pageName = currentPage.properties.get("pageName");
    var pageType = currentPage.properties.get("pageType");
    if (pageDepth <= 3) {
        var siteName = currentPage.properties.get("siteName");
        var sitePrefix = currentPage.properties.get("sitePrefix");
    } else {
        var siteName = currentPage.getAbsoluteParent(2).properties.get("siteName");
        var sitePrefix = currentPage.getAbsoluteParent(2).properties.get("sitePrefix");
    }

    return {
        pageName: pageName,
        pageType: pageType,
        siteName : siteName,
        sitePrefix : sitePrefix
    };
});

