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

use(function () {
    var touchMode, classicMode, currentMode;
    
    var AuthoringUtils = {
            CONST: {
                PROP_COMPONENT_TITLE: "jcr:title",
                COMPONENT_DEFAULT_TITLE: "Component"                
            }
    };
    
    try {
        touchMode = Packages.com.day.cq.wcm.api.AuthoringUIMode.TOUCH;
        classicMode = Packages.com.day.cq.wcm.api.AuthoringUIMode.CLASSIC;
        currentMode = Packages.com.day.cq.wcm.api.AuthoringUIMode.fromRequest(request);
    } catch (e) {
        log.debug("Could not detect authoring mode! " + e);
    }
    
    AuthoringUtils.isTouch = touchMode && touchMode.equals(currentMode);
    
    AuthoringUtils.isClassic = classicMode && classicMode.equals(currentMode);
    
    AuthoringUtils.componentTitle = function () {
        if (typeof component != "undefined") {
            return component.getProperties().get(AuthoringUtils.CONST.PROP_COMPONENT_TITLE,
                    AuthoringUtils.CONST.COMPONENT_DEFAULT_TITLE);
        }
        return AuthoringUtils.CONST.COMPONENT_DEFAULT_TITLE;
    };
    
    return AuthoringUtils;
});
