/*
 * This is OTB Image component, it is made available to share the Image capabilities in the Global project
 * It's overlay path /libs/wcm/foundation/components/image/image.js
 *
 */

"use strict";

/**
 * Image component JS Use-api script
 */
use(["/apps/global/components/utils//AuthoringUtils.js",
           "/apps/global/components/utils//Image.js",
     "/libs/sightly/js/3rd-party/q.js"], function (AuthoringUtils, Image, Q) {
    
    var image = new Image(granite.resource);
    var imageDefer = Q.defer();
    
    var CONST = {
        AUTHOR_IMAGE_CLASS: "cq-dd-image",
        PLACEHOLDER_SRC: "/etc/designs/default/0.gif",
        PLACEHOLDER_TOUCH_CLASS: "cq-placeholder file",
        PLACEHOLDER_CLASSIC_CLASS: "cq-image-placeholder"
    };
    
    // check if there's a local file image under the node
    granite.resource.resolve(granite.resource.path + "/file").then(function (localImageResource) {
        imageDefer.resolve(image);
    }, function() {
        // The drag & drop image CSS class name
        image.cssClass = CONST.AUTHOR_IMAGE_CLASS;
        
        // Modifying the image object to set the placeholder if the content is missing
        if (!image.fileReference()) {
            
            image.src = CONST.PLACEHOLDER_SRC;
            if (AuthoringUtils.isTouch) {
                image.cssClass = image.cssClass + " " + CONST.PLACEHOLDER_TOUCH_CLASS;
            } else if (AuthoringUtils.isClassic) {
                image.cssClass = image.cssClass + " " + CONST.PLACEHOLDER_CLASSIC_CLASS;
            }
        }
        
        imageDefer.resolve(image);
    });
    
    // Adding the constants to the exposed API
    image.CONST = CONST;
    
    // check for image available sizes
    if (image.width() <= 0) {
        image.width = "";
    }
    
    if (image.height() <= 0) {
        image.height = "";
    }
    
    return imageDefer.promise;
    
});
