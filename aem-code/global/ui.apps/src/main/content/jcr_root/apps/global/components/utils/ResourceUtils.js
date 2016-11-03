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

use(["/libs/sightly/js/3rd-party/q.js"], function (Q) {
    var ResourceUtils = {};
    
    ResourceUtils.getAbsoluteParent = function (path, level) {
        var idx = 0;
        var length = path.length;
        if (isNaN(length)) {
            length = path.length();
        }
        
        while (level >= 0 && idx < length) {
            idx = path.indexOf("/", idx + 1);
            if (idx < 0) {
                idx = length;
            }
            level--;
        }
        
        return level >= 0 ? "" : path.substring(0, idx);
    };
    
    ResourceUtils.getRelativeParent = function (path, level) {
        var idx = path.length;
        if (isNaN(idx)) {
            idx = path.length();
        }

        while (level > 0) {
            idx = path.lastIndexOf("/",idx-1);
            if (idx < 0) {
                return "";
            }
            level--;
        }
        return path.substring(0,idx);
    };

    /**
     * Returns a promise
     */
    ResourceUtils.getContainingPage = function (childResource, containingPagePromise) {
        if (!containingPagePromise) {
            containingPagePromise = Q.defer();
        }
        
        log.debug("Resolving containing page for resource " + childResource.path);
        if (childResource.name == "jcr:content") {
            log.debug("Resource " + childResource.path + " is a page content node");
            childResource.getParent().then(function (parentRes) {
                log.debug("Found " + parentRes.path + " as containing page of " + childResource.path);
                containingPagePromise.resolve(parentRes);
                return;
            });
        } else {
            log.debug("Searching through all children of " + childResource.path + " for a jcr:content node ");
            childResource.getChildren().then(function (childItems) {
                log.debug("Found " + childItems.length + " children of " + childResource.path);
                for (var childItemIdx = 0 ; childItemIdx < childItems.length ; childItemIdx++) {
                    if (childItems[childItemIdx].name == "jcr:content") {
                        log.debug("Found child jcr:content node, " + childResource.path + " is a page ");
                        containingPagePromise.resolve(childResource);
                        return;
                    }
                }
                var parentPromise = childResource.getParent();
                if (parentPromise) {
                    log.debug(" No child jcr:content found, moving to parent ");
                    parentPromise.then(function (parentRes) {
                        log.debug(" Recursing into getContainingPage() for parent " + parentRes.path);
                        ResourceUtils.getContainingPage(parentRes, containingPagePromise);                        
                    });
                } else {
                    log.debug("No parent available, could not determine a containing page");
                    containingPagePromise.resolve(null);
                }

            });
        }
        
        return containingPagePromise.promise;
    };
    
    /**
     * Returns a promise
     */
    ResourceUtils.getPageProperties = function (pageResource) {
        var pagePropertiesPromise = Q.defer();
        
        pageResource.resolve(pageResource.path + "/jcr:content")
            .then(function (pageContentResource) {
                pagePropertiesPromise.resolve(pageContentResource.properties);
            }, function () {
                pagePropertiesPromise.reject();
            });
        
        return pagePropertiesPromise.promise;
    };
    
    ResourceUtils.getInheritedPageProperty = function (pageResource, propName, inheritedPropertyPromise) {
        if (!inheritedPropertyPromise) {
            log.debug("Fetching inherited property " + propName + " of page " + pageResource.path);
            inheritedPropertyPromise = Q.defer();
        }

        var failPromise = function (inheritedPropertyPromise) {
            log.debug("Did not find inherited property " + propName + ", returning 'undefined'");
            inheritedPropertyPromise.resolve(undefined);
        }
        
        ResourceUtils.getResource(pageResource.path + "/jcr:content").then(function (pageContentResource) {
            var propValue = pageContentResource.properties[propName];
            if (propValue) {
                log.debug("Found inherited property " + propName + " = " + propValue);
                inheritedPropertyPromise.resolve(propValue);
            } else {
                var parentPromise = pageResource.getParent();
                if (parentPromise) {
                    parentPromise.then(function (parentResource) {
                        log.debug("parent res " + parentResource);
                        if (parentResource) {
                            log.debug("Searching inherited property " + propName + " on parent page " + parentResource.path);
                            ResourceUtils.getInheritedPageProperty(parentResource, propName, inheritedPropertyPromise);
                        } else {
                            failPromise(inheritedPropertyPromise);
                        }
                    }, function () {
                        failPromise(inheritedPropertyPromise);
                    });
                } else {
                    failPromise(inheritedPropertyPromise);
                }
            }
        }, function() {
            failPromise(inheritedPropertyPromise);
        });
        
        return inheritedPropertyPromise.promise;
    };
    
    /**
     * Resolves a resource
     * 
     * @returns promise as returned by the granite.resource.resolve or undefined if
     *          the 'granite' cross-platform API is not available
     */
    ResourceUtils.getResource = function (resourcePath) {
        var resolvedResource = undefined;
        if (typeof granite != "undefined") {
            // resolve based on cross platform API
            log.debug("Found 'granite' cross-platform namespace, using it to resolve " + resourcePath + " path");
            resolvedResource = granite.resource.resolve(resourcePath);
            log.debug("Resolved resource " + resourcePath);
        } else {
            log.error("Can't find 'granite' cross-platform namespace!");
        }
        
        return resolvedResource;
    }
    
    return ResourceUtils;
});
