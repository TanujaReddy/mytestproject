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
 * Helper JS object having convenience methods to retrieve image properties
 * 
 * @constructor
 */
use(["ResourceUtils.js", "/libs/sightly/js/3rd-party/q.js"], function (ResourceUtils, Q) {
    
    var that;
    
    var Image = function (resource) {
        that = this;
        that.resource = resource;
        that.properties = resource.properties || {};
        that.timestamp = (new Date()).getTime();
    };
    
    var CONST = {
        PROP_FILE_REFERENCE: "fileReference",
        PROP_ALT: "alt",
        PROP_IS_DECORATIVE: "isDecorative",
        PROP_TITLE: "jcr:title",
        PROP_WIDTH: "width",
        PROP_HEIGHT: "height",
        PROP_LINK_URL: "linkURL",
        PROP_IMAGE_MAP: "imageMap",
        PROP_JCR_CONTENT: "jcr:content",
        PROP_JCR_MIME_TYPE: "jcr:mimeType",
        PROP_JCR_CREATED: "jcr:created",
        PROP_JCR_LAST_MODIFIED: "jcr:lastModified",

        RENDITIONS_PATH: "renditions",
        WEB_RENDITION_SELECTOR: "img",
        WEB_RENDITION_PREFIX: "cq5dam.web.",
    
        DEFAULT_MIME_TYPE: "image/png",
        MIME_TYPES: {
            "image/png": "png",
            "image/jpg": "jpg",
            "image/jpeg": "jpg",
            "image/gif": "gif"
        }
    };
    
    // Adding the constants to the exposed API
    Image.CONST = CONST;

    Image.prototype.forcedMimeType = function (mimeType) {
        if(mimeType) {
            that._forcedMimeType = mimeType;
        }
        return that._forcedMimeType;
    };

    Image.prototype.fileReference = function () {
        return that.properties[CONST.PROP_FILE_REFERENCE];
    };

    Image.prototype.fileName = function () {
        var filePath = that.fileReference() || that.resource.path;
        var lastIndex = filePath.lastIndexOf("/");
        
        return (lastIndex >= 0) ? filePath.substring(lastIndex + 1) : filePath;
    };

    Image.prototype.src = function () {
        return that.resource.path + "." + CONST.WEB_RENDITION_SELECTOR + "."
                + getExtension() + getCachingSuffix();
    };

    Image.prototype.alt = function () {
        if (that.isDecorative()) {
            // declared decorative image: null alt attribute
            return true;
        } else {
            // use alt or remove the alt attribute completely (latter allows accessibility checking tools to
            // report missing alt attributes)
            return that.properties[CONST.PROP_ALT] || false;
        }
    };

    Image.prototype.isDecorative = function () {
        return that.properties[CONST.PROP_IS_DECORATIVE];
    };

    Image.prototype.title = function () {
        return that.properties[CONST.PROP_TITLE];
    };

    Image.prototype.width = function () {
        return that.properties[CONST.PROP_WIDTH];
    };

    Image.prototype.height = function () {
        return that.properties[CONST.PROP_HEIGHT];
    };

    Image.prototype.linkUrl = function () {
        return that.properties[CONST.PROP_LINK_URL];
    };

    Image.prototype.imageMaps = function () {
        var imgMaps = null;
        var mapInfo = that.properties[CONST.PROP_IMAGE_MAP];
        
        if (mapInfo) {
            var name = "map-" + that.timestamp;
            imgMaps = {
                name: name,
                hash: "#" + name,
                maps: []
            };

            var mapInfoRegex = /\[([^(]*)\(([^)]*)\)([^|]*)\|([^|]*)\|([^\]]*)\]/g;
            while (match = mapInfoRegex.exec(mapInfo)) {
                imgMaps.maps.push({
                        type: match[1],
                        coords: match[2],
                        href: match[3].replace(/\"([^\"]*)\"/g, "$1"),
                        target: match[4].replace(/\"([^\"]*)\"/g, "$1"),
                        text: match[5].replace(/\"([^\"]*)\"/g, "$1")
                });
            }
        }
        
        return imgMaps;
    };

    function getExtension() {
        var extensionPromise = Q.defer();
        var fileReference = that.fileReference();
        var defaultMimeType = that.forcedMimeType() ? that.forcedMimeType() : CONST.DEFAULT_MIME_TYPE;
        
        if (fileReference) {
            ResourceUtils.getResource(fileReference + "/" + CONST.PROP_JCR_CONTENT + "/" + CONST.RENDITIONS_PATH)
            .then(function (fileRefRenditions) {
                log.debug("Returning rendition children promise");
                return fileRefRenditions.getChildren();
            }, function (error) {
                log.error(error);
            })
            .then(function (imgRenditions) {
                log.debug("Searching rendition children for the web rendition " + imgRenditions.length);
                searchWebRendition(imgRenditions, 0, extensionPromise);
            }, function (error) {
                log.error(error);
            });
        } else {
            log.debug("Using default mime type for image " + fileReference + " : " + defaultMimeType);
            extensionPromise.resolve(CONST.MIME_TYPES[defaultMimeType]);
        }
        
        return extensionPromise.promise;
    }

    function getCachingSuffix() {
        var cachingSuffixPromise = Q.defer();
        var fileReference = that.fileReference();
        var resourceLastModif = that.properties["jcr:lastModified"] || that.properties["jcr:created"],
            suffix;
        if (typeof resourceLastModif === 'undefined') {
           suffix = 0
        } else {
            suffix = resourceLastModif.getTimeInMillis();
        }

        if (fileReference) {
            ResourceUtils.getResource(fileReference + "/" + CONST.PROP_JCR_CONTENT)
                .then(function (fileResourceContent) {
                    var fileLastModif = fileResourceContent.properties[CONST.PROP_JCR_LAST_MODIFIED]
                        || fileResourceContent.properties[CONST.PROP_JCR_CREATED];


                    if (fileLastModif && fileLastModif > resourceLastModif) {
                        resourceLastModif = fileLastModif;
                    }
                    if (typeof resourceLastModif !== 'undefined') {
                        suffix = resourceLastModif.getTimeInMillis();
                    }
                    if (suffix) {
                        getExtension().then(function (extension) {
                            suffix = "/" + suffix + "." + extension;
                            log.debug("Computed image caching suffix: " + suffix);
                            cachingSuffixPromise.resolve(suffix);
                        });
                    } else {
                        log.debug("Using empty image caching suffix");
                        cachingSuffixPromise.resolve("");
                    }
                });
        } else {
            log.debug("Using local image caching suffix");
            getExtension().then(function (extension) {
                suffix = "/" + suffix + "." + extension;
                log.debug("Computed image caching suffix: " + suffix);
                cachingSuffixPromise.resolve(suffix);
            });
        }

        return cachingSuffixPromise.promise;
    }

    function searchWebRendition(renditions, currentIndex, promise) {
        var defaultMimeType = that.forcedMimeType() ? that.forcedMimeType() : CONST.DEFAULT_MIME_TYPE;

        if (currentIndex >= renditions.length) {
            log.debug("No web rendition found, using default mime type " + defaultMimeType);
            promise.resolve(CONST.MIME_TYPES[defaultMimeType]);
            return;
        }
        
        var currentRendition = renditions[currentIndex];
        
        if (currentRendition.name.indexOf(CONST.WEB_RENDITION_PREFIX) >= 0) {
            ResourceUtils.getResource(currentRendition.path + "/" + CONST.PROP_JCR_CONTENT)
            .then(function (renditionContentResource) {
                log.debug("Found image information resource: " + renditionContentResource.path);
                
                mimeType = that.forcedMimeType() ? that.forcedMimeType() : (renditionContentResource.properties[CONST.PROP_JCR_MIME_TYPE] || defaultMimeType);
                
                log.debug("Found mime type for image " + that.fileReference() + " : " + mimeType);
                
                promise.resolve(CONST.MIME_TYPES[mimeType]);
            });
        } else {
            log.debug("Not a web rendition " + currentRendition.path);
            searchWebRendition(renditions, currentIndex + 1, promise);
        }
    }

    return Image;

});