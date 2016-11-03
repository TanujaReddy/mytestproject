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

  /* Auspost custom workflow for ourpost and global projects - unpublish asset action-DAM asset page-list view */


(function(document, $) {
    "use strict";
    window.assetsChecked = true;;
    window.renditionsChecked = false;
    window.subassetsChecked = false;
    var cmd =  'Deactivate';
    var ui = $(window).adaptTo("foundation-ui");
    var replicationUrl = '/bin/replicate.json';
    var actSuccessModal, actErrModal;
    var multipleText;
    var rel = ".cq-damadmin-admin-actions-unpublish";
    var rel1 = ".cq-damadmin-admin-actions-unpublishlater";	
    var $modal, activator, later;

    function unpublishAssets(assetsPathsList) {
        if (assetsPathsList.length > 0) {
            /* forward the request to auspost custom workflow.*/
            CQ_UI_siteadmin_quickUnpublish(assetsPathsList);
        }
    }
     CQ_UI_siteadmin_quickUnpublish = function(paths) {

                     var admin = this;
                     var model= "";

                     if (paths.toString().includes('ourpost') == true){
                     model = "/etc/workflow/models/auspost-ourpost-activate-workflow/jcr:content/model";
                     }else{
                     model = "/etc/workflow/models/auspost-global-activate-workflow/jcr:content/model";
                     }

                     auspostWorkflowTrigger_unpublish(paths, "", model);

         };


function auspostWorkflowTrigger_unpublish(paths, startComment, model) {
    var admin = this;
    var startComment = startComment;
    var params = {
        "_charset_":"UTF-8",
        "model":model,
        "payload":paths,
        "payloadType":"JCR_PATH",
        "startComment":startComment,
        "actionRequested":"unpublish"
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
    	        "actionRequested":"unpublish"
    	   },
    	   error: function() {
    	     alert('Error, Please contact the admin !!!');
    	   },
    	   success: function(data) {

           ui.notify(null, Granite.I18n.get("Request for asset unpublication has been forwarded for an approval workflow"));
           setTimeout(location.reload(), 5000);
    	   },
    	   type: 'POST'
    	});

};



    function replicationStarted(xhr, status) {
        if (status === "success") {
            if (!actSuccessModal) {
                actSuccessModal = new CUI.Modal({
                    element: $("#unpublish-success"),
                    type: "success"
                });
            } else {
                actSuccessModal.set({ visible: true })
            }
        } else {

                $(rel + "-error").modal({
                    type: "error"
                }).modal("show");
            return;
        }
    }




    $(document).on("click." + rel, rel + " button.coral-Button--primary", function(e) {
        activator = $(rel+'-activator')
        $("#unpublish").modal('hide');
        var items = $(".foundation-collection").find(".foundation-selections-item");
        var quickactionsItem = $modal.data("foundationCollectionItem");
        var paths;
        if (quickactionsItem){
            paths = [$(quickactionsItem).data("path")];
        }
        else {
            paths=[];
            for (var i = 0; i < items.length; i++) {
                paths.push($(items[i]).data("path"));
            }
        }
        unpublishAssets(paths);

    });
	
    $(document).on("click" + rel1, rel1 + "-activator", function(e) {
        activator = $(rel1+'-activator');
        later = activator.data("later") || false;
        var editMode = activator.data("edit") || false;		
        var items = $(".foundation-collection").find(".foundation-selections-item");
        var url = Granite.HTTP.externalize($('.unpublishwizard-url').data('url'));
        var paths, params = "";
            paths=[];
            for (var i = 0; i < items.length; i++) {
                var path = $(items[i]).data("path");
                paths.push(path);
                if (params.length > 0) {
                    params += "&";
                }
                params += "item=" + encodeURI(path);
            }
        if(later) {
        	if (params.length > 0) {
            	location.href = url + "?_charset_=UTF-8&" + params + (later?("&later"):"") + (editMode?("&editmode"):"");
        	} 
        }
    });	

    $(document).on("beforeshow." + rel, ".coral-Modal" + rel, function(e) {
        $('.coral-Popover').popover("hide");
        $modal = $(this);
        var $multiple = $modal.find(".multiple");
        var selectedItems = $(".foundation-collection article.foundation-selections-item");
        if (selectedItems.length == 1) {
            $modal.find(".single").removeClass("hidden");
            $multiple.addClass("hide");
        } else {
            $modal.find(".single").addClass("hide");
            $multiple.removeClass("hide");
        }
        if (!multipleText) {
            multipleText = $multiple.text();
        }
        $multiple.text(multipleText.replace(/%no%/gi, selectedItems.length));
        var $pageList = $modal.find(".page-list");
        $pageList.empty();
        var MAX_ENTRIES = 10;
        var itemCnt = Math.min(selectedItems.length, MAX_ENTRIES);
        for (var i = 0; i < itemCnt; i++) {
            var $item = $(selectedItems[i]);
            if (i > 0) {
                $pageList.append($("<br>"));
            }
            $pageList.append(_g.shared.XSS.getXSSValue($item.find("h4").text()));
        }
        if (selectedItems.length > itemCnt) {
            $pageList.append($("<br>")).append(" …");
        }
    });

})(document, Granite.$);
