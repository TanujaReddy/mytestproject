/*
 * %%
 * aem-authoring-extension-header-finalpreview
 * %%
 * 
 */
 (function(window, document, Granite, $) {
    "use strict";

    var FINAL_PREVIEW = ".cq-final-preview-action";
    var WCM_MODE = "wcmmode=disabled" ;

    $(document).on("click", FINAL_PREVIEW, function() {
        var $button = $(this);
        var path = Granite.author.ContentFrame.currentLocation();
        var dest = path.replace('/editor.html' , '') + '?' + WCM_MODE ;
        window.open(dest) ;
    });

})(window, document, Granite, Granite.$);
