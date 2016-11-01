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

  /* Auspost custom workflow for ourpost and global projects - publish asset action-DAM asset page-list view */


(function(document, $) {
    "use strict";
    window.assetsChecked = true;;
    window.renditionsChecked = false;
    window.subassetsChecked = false;
    var cmd =  'Activate';
    var ui = $(window).adaptTo("foundation-ui");
    var replicationUrl = '/bin/replicate.json';
    var actSuccessModal, actErrModal, actInfoModal, actSelectModal;
    var rel = ".cq-damadmin-admin-actions-publish";
    var mustPublish = [];
    var later;	

    function publishAssets(assetsPathsList,responseHandler) {
        if (assetsPathsList.length > 0) {
            var url = Granite.HTTP.externalize(replicationUrl);
            CQ_UI_siteadmin_quickPublish(assetsPathsList);
        }
        else {
            if (!actInfoModal) {
                actInfoModal = new CUI.Modal({
                    element: $("#activation-info")
                });
            } else {
                actInfoModal.set({ visible: true })
            }
        }
    }

    function replicationStarted(xhr, status) {
        if (status === "success") {
            if (!actSuccessModal) {
                actSuccessModal = new CUI.Modal({
                    element: $("#activation-success"),
                    type: "success"
                });
            } else {
                actSuccessModal.set({ visible: true })
            }
        } else {
            if (!actErrModal) {
                actErrModal = new CUI.Modal({
                    element: $("#activation-error"),
                    type: "error"
                });
            } else {
                actErrModal.set({ visible: true })
            }

        }
    }

    function replicateToPublish(activator) {
        var items = $(".foundation-collection").find(".foundation-selections-item");
        var quickactionsItem = activator.data("foundationCollectionItem");
        var paths; var params = "";
        later = activator.data("later") || false;		
        var url = Granite.HTTP.externalize($('.publishwizard-url').data('url'));
        var gPath = Granite.HTTP.getPath();
        var lastDot = gPath.lastIndexOf(".");
        var extSuffixe = gPath.substr(lastDot);
        var suffixeSlash = extSuffixe.indexOf('/');
        var contentPath = extSuffixe.substr(suffixeSlash);
        if (suffixeSlash == -1) {
            contentPath = '/content/dam';
        }
        var type = $(".foundation-selections-item").data("type");
        if (quickactionsItem){
            var path = $(quickactionsItem).data("path");
            paths = [path];
            mustPublish.push(path);
            params += "item=" + encodeURI(path);

        }
        else {
            paths=[];
            for (var i = 0; i < items.length; i++) {
                var path =$(items[i]).data("path");
                paths.push(path);
                mustPublish.push(path);
                if (params.length > 0) {
                    params += "&";
                }
                params += "item=" + encodeURI(path);
            }
        }
        if(later) {
            if (params.length > 0) {
                location.href = url + "?_charset_=UTF-8&" + params + (later?("&later"):"") + (type?("&" + type):"");
            } 
        } else if (type == "directory") {
            publishAssets(paths);
        } else {
            activate();
        }

        function activate() {
            var url = Granite.HTTP.externalize("/libs/wcm/core/content/reference.json");
            var prm = "path";
            var data = { };
            data[prm] = paths;

            $.ajax(url, {
                "data": data,
                "cache": false,
                "dataType": "json",
                "beforeSend": function() {
                	var spinner = $(window).adaptTo("foundation-ui");
                	spinner.wait();
                	$('.coral-Popover').popover("hide");
                },
                "complete": [clearMask, referencesRetrieved]
            });
        }

        function referencesRetrieved(xhr, status) {
            if (status === "success") {
                var json = $.parseJSON(xhr.responseText);
                var tableData = checkReferences(json);
                if (tableData.hasReferences) {
                    location.href = url + contentPath + "?" + params + "&_charset_=utf-8&referrer="+location.pathname;
                } else {
                    var assetPath = [ ];
                    // start replication directly if no references were found
                    for (var c = 0; c < mustPublish.length; c++) {
                        if (assetPath.indexOf(mustPublish[c])==-1)
                            assetPath.push(mustPublish[c]);
                    }
                    publishAssets(assetPath);
                }
            } else {

            }
        }

        function checkReferences(json) {
            var assets = json["assets"];
            var hasReferences = false;
            for (var a = 0; a < assets.length; a++) {
                var asset = assets[a];
                //if server returns a list containing at least one asset which is not yet published
                //and is not one of the selected nodes to publish, then display the wizard explicitly
                if ((!asset.published || asset.outdated) && params.indexOf(asset.path) == -1 && 
                (asset.type == "asset" || asset.type == "tag" || asset.type == "s7set")){
                    hasReferences = true;
                }
                else {
                    mustPublish.push(asset.path);
                }
            }
            return {
                "hasReferences": hasReferences
            };
    	}
    }
    
    function publishToS7() {
    	var paths = [];
    	$(".foundation-collection").find(".foundation-selections-item").each (function(index, value) {
    		paths.push($(value).data('path'));
    	});
    	
    	$.ajax({
            type: 'post',
            url: '/content/dam.s7publish.json',
            data: {
            	'_charset_' : 'utf-8',
            	'srcPath' : paths,
            	'cmd' : 'setPublishSubfolderControl',
            	'integrity': true,
            	':status' : 'browser'
            },
            "beforeSend": function() {
            	var spinner = $(window).adaptTo("foundation-ui");
            	spinner.wait();
            	$('.coral-Popover').popover("hide");
            },
            complete: [clearMask, replicationStarted]
        });
    }

    CQ_UI_siteadmin_quickPublish = function(paths) {

            // Map the workflow based on the content path
            var admin = this;
            var model= "";

            if (paths.toString().includes('ourpost') == true){
            model = "/etc/workflow/models/auspost-ourpost-activate-workflow/jcr:content/model";
            }else{
            model = "/etc/workflow/models/auspost-global-activate-workflow/jcr:content/model";
            }

            auspostWorkflowTrigger_publish(paths, "", model);


        };

        // Auspost custom workflow for ourpost and global projects
        function auspostWorkflowTrigger_publish(paths, startComment, model) {
            var admin = this;
            var startComment = startComment;
            var params = {
                "_charset_":"UTF-8",
                "model":model,
                "payload":paths,
                "payloadType":"JCR_PATH",
                "startComment":startComment,
                "actionRequested":"publish"
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
            	        "actionRequested":"publish"
            	   },
            	   error: function() {
            	     alert('Error, Please contact the admin !!!');
            	   },
            	   success: function(data) {

            		   ui.notify(null, Granite.I18n.get("Request for asset publication has been forwarded for an approval workflow"));
            		   setTimeout(location.reload(), 5000);


            	   },
            	   type: 'POST'
            	});


        };

    
    function clearMask() {
    	$(window).adaptTo("foundation-ui").clearWait();
    }

    $(document).fipo("tap" + rel, "click" + rel, rel+'-activator', function(e) {
    	var activator = $(this);
    	if ($(this).hasClass('s7on')) {
    		if (!actSelectModal) {
        		actSelectModal = new CUI.Modal({
                    element: $("#selectpublish")
                });
            } else {
            	actSelectModal.set({ visible: true });
            }
    	} else {
    		replicateToPublish(activator);
    	}
    });
    
    $(document).fipo("tap." + "#selectpublish #publish-asset", "click." + "#selectpublish #publish-asset", "#selectpublish #publish-asset", function(e) {
    	var activator = $(this);
		if ($('#scene7cb').is(":checked")) {
    		publishToS7();
    	}
    	if ($('#publishcb').is(":checked")) {
    		replicateToPublish(activator);
    	}
    });

    $.fn.CQ_UI_damadmin_postReplicationReq = function(paths, handler){
        publishAssets(paths, handler);
    }

})(document, Granite.$);
