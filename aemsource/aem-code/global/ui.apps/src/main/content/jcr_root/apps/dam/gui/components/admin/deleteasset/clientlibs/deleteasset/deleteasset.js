/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2012 Adobe Systems Incorporated
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

 /* Auspost custom workflow for ourpost and global projects - delete asset action-DAM asset page-list view */

(function(document, $) {
    "use strict";

    var rel = ".cq-damadmin-admin-actions-delete";
    var delActivator = ".cq-damadmin-admin-actions-delete-activator";
    var ui = $(window).adaptTo("foundation-ui");
    var quickselections = null;
    var quickactionsItem = null;
    function deletePages(paths, force) {
        var items = $(".foundation-collection").find(".foundation-selections-item");
        hideModal();
        if (paths == null) {
            var paths = [];
            if (items.length == 0 && quickselections) {
                paths.push(quickselections);
                quickselections = null;
            } else if (items.length) {
                for (var i = 0; i < items.length; i++) {
                    var dataPath = $(items[i]).data("path");
                    if (dataPath.indexOf("jcr:content/renditions/original") < 0) {
                        paths.push(dataPath);
                    }

                }
            }
            if (!paths || paths.length ===0) {
            	//get from the foundation-content-path for the renditions
            	paths.push($(".foundation-content-path").data("foundationContentPath"));
            }
        }

        CQ_UI_siteadmin_quickDelete(paths);


    }


        function auspostWorkflowTrigger_delete(paths, startComment, model) {
            var admin = this;
            var startComment = startComment;
            var params = {
                "_charset_":"UTF-8",
                "model":model,
                "payload":paths,
                "payloadType":"JCR_PATH",
                "startComment":startComment,
                "actionRequested":"deletepage"
            };
            $.ajax({
            	   url: '/etc/workflow/instances',
            	   async: false,
            	   data: {
            		   "_charset_":"UTF-8",
            	        "model":model,
            	        "payload":paths,
            	        "payloadType":"JCR_PATH",
            	        "startComment":startComment,
            	        "actionRequested":"deletepage"
            	   },
            	   error: function() {
            	     alert('Error, Please contact the admin !!!');
            	   },
            	    success: function(data) {
            		ui.notify(null, Granite.I18n.get("Request for asset deletion has been forwarded for an approval workflow"));

            	   },
            	   type: 'POST'
            	});

        };

        function CQ_UI_siteadmin_quickDelete(paths){

                var gPath = Granite.HTTP.getPath();
                var lastDot = gPath.lastIndexOf(".");
                var extSuffixe = gPath.substr(lastDot);
                var suffixeSlash = extSuffixe.indexOf('/');
                var contentPath="";
                contentPath = extSuffixe.substr(suffixeSlash);
                if (suffixeSlash == -1) {
                    contentPath = '/content';
                }
                var admin = this;
                var model= "";

                if (paths.toString().includes('ourpost') == true){
                model = "/etc/workflow/models/auspost-ourpost-activate-workflow/jcr:content/model";
                }else{
                model = "/etc/workflow/models/auspost-global-activate-workflow/jcr:content/model";
                }

                auspostWorkflowTrigger_delete(paths, "", model);


        }

    function hideModal() {
        if (location.pathname.indexOf("/assetdetails.html") > -1) {
            $(rel+"-renditions").modal("hide");
        } else {
            $(rel).hide();
            $(rel).modal("hide");
            $(rel + "-error").modal("hide");
        }
    }

    $(document).fipo("tap." + delActivator, "click." + delActivator, delActivator, function(e) {
        var activator = $(this);
        var config = activator.data("foundationCollectionAction");
        quickactionsItem = activator.data("foundationCollectionItem");

        if (!(config || quickactionsItem)) return;

        if (config) {
            var collection = $(config.target).first();
        }

        if (quickactionsItem) {
            quickselections = $(quickactionsItem).data("path");
        }

    });

    $(document).on("click." + rel, rel + " button.coral-Button--primary", function(e) {
        deletePages();
    });

    $(document).fipo("tap" + rel+"-renditions", "click." + rel+"-renditions", rel+"-renditions" + " button.coral-Button--primary", function(e) {
        deletePages();
    });
    $(document).on("beforeshow" + rel, ".coral-Modal" + rel, function () {
        var selectedItems = $(".foundation-selections-item");
        var singleContentMessage, multipleContentMessage;
        var type = $(selectedItems[0]).data("type");
        if (type == 'collection') {
            singleContentMessage = Granite.I18n.get("You are going to delete the following collection:");
            multipleContentMessage = Granite.I18n.get("You are going to delete the following {0} collections:", selectedItems.length);
        } else {
            singleContentMessage = Granite.I18n.get("You are going to delete the following asset:");
            multipleContentMessage = Granite.I18n.get("You are going to delete the following {0} assets:", selectedItems.length);
        }
        Dam.Util.populateModalWithSelectedItems($(this), selectedItems, singleContentMessage, multipleContentMessage);
    });

})(document, Granite.$);