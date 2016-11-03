/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2014 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
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
