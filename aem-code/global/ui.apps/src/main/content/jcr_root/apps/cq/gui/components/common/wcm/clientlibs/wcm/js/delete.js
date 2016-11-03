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

 This file has been overlayed to facilitate the Auspost custom workflow integration

 */
(function(window, document, $, Granite) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");
    var COMMAND_URL= Granite.HTTP.externalize("/bin/wcmcommand");
    var deleteText = Granite.I18n.get("Delete");
    var cancelText = Granite.I18n.get("Cancel");
     var editMode, later, params, paths, activator, contentPath;

    function deletePages(paths, force) {
        ui.wait();
        
        $.ajax({
            url: COMMAND_URL,
            type: "POST",
            data: {
                _charset_: "UTF-8",
                cmd: "deletePage",
                path: paths,
                force: !!force
            },
            success: function() {
                ui.clearWait();
                
                var contentApi = $(".foundation-content").adaptTo("foundation-content");
                contentApi.refresh();
            },
            error: function(xhr) {
                ui.clearWait();
                
                var message = Granite.I18n.getVar($(xhr.responseText).find("#Message").html());
                
                if (xhr.status == 412) {
                    ui.prompt(deleteText, message, "notice", [{
                        text: cancelText
                    }, {
                        text: Granite.I18n.get("Force Delete"),
                        warning: true,
                        handler: function() {
                            deletePages(paths, true);
                        }
                    }]);
                    return;
                }
                
                ui.alert(Granite.I18n.get("Error"), message, "error");
            }
        });
    }

    // Auspost custom workflow for ourpost and global projects - unpublish action
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
        		ui.notify(null, Granite.I18n.get("Request for page deletion has been forwarded for an approval workflow"));

        	   },
        	   type: 'POST'
        	});

    };

    function CQ_UI_siteadmin_quickDelete(paths){

            $.ajax({
                	   url: '/bin/auspost-workflow-status',
                	   async: false,
                	   data: {
                		   "_charset_":"UTF-8",
                	        "pagePath":paths,
                	        },
                	   error: function() {
                	     alert('Error, Please contact the admin !!!');
                	   },
                	   success: function(data) {
                        if(data.workflowStatus === false){
                        var admin = this;
                        var model= "";

                        if (paths.toString().includes('ourpost') == true){
                        model = "/etc/workflow/models/auspost-ourpost-activate-workflow/jcr:content/model";
                        }else{
                        model = "/etc/workflow/models/auspost-global-activate-workflow/jcr:content/model";
                        }

                         auspostWorkflowTrigger_delete(paths, "", model);
                        }else{
                        alert('This page is already in a worklflow. Please note you can not proceed until the assigned workflow is approved.');
                        setTimeout(location.reload(), 5000);
                        }

                	   },
                	   type: 'POST'
                	   });


    }

    function createAlert() {
            				var alertDiv = $("<div/>", {
            					"class": "coral-Alert coral-Alert--error"
            				}).append($("<i/>", {
            					"class": "coral-Alert-typeIcon coral-Icon coral-Icon--sizeS coral-Icon--alert"
            				})).append($("<strong/>", {
            					"class": "coral-Alert-title"
            				}).text(" Error ")).append($("<div/>", {
            					"class": "coral-Alert-message"
            				}).text(Granite.I18n.get("Request has been forwarded for approval")));

            				var alert = new CUI.Alert({
            					element: alertDiv
            				});

            				return alert;
            			}

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq.wcm.delete",
        handler: function(name, el, config, collection, selections) {
            var message = $(document.createElement("div"));
            
            var intro = $(document.createElement("p")).appendTo(message);
            if (selections.length === 1) {
                intro.text(Granite.I18n.get("You are going to delete the following item:"));
            } else {
                intro.text(Granite.I18n.get("You are going to delete the following {0} items:", selections.length));
            }
            
            var list = [];
            var maxCount = Math.min(selections.length, 12);
            for (var i = 0, ln = maxCount; i < ln; i++) {
                list.push("<b>" + $(selections[i]).find(".foundation-collection-item-title").html() + "</b>");
            }
            if (selections.length > maxCount) {
                list.push("&#8230;"); // &#8230; is ellipsis
            }
            
            $(document.createElement("p")).html(list.join("<br>")).appendTo(message);
            
            ui.prompt(deleteText, message.html(), "notice", [{
                text: cancelText
            }, {
                text: deleteText,
                warning: true,
                handler: function() {
                    var paths = selections.map(function(v) {
                        return $(v).data("foundationCollectionItemId");
                    });
                    
                    CQ_UI_siteadmin_quickDelete(paths);
                }
            }]);
        }
    });
})(window, document, Granite.$, Granite);
