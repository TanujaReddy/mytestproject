/* Auspost custom workflow for ourpost and global projects - publish action- siteadmin */

(function(document, $) {

    var editMode, later, params, paths, activator, contentPath;

    var DOCUMENT_REFERRER_KEY = "document.referrer";

    var notificationSelector = ".endor-Page-content.endor-Panel.foundation-content";

    function activateCallback(xhr, status){
        var notificationSlider = (Granite.author && Granite.author.notifications) ||
                new Granite.UI.NotificationSlider($(notificationSelector));
        var notificationClass = Granite.author !== undefined ? "admin-notification" : "notification-alert--absolute admin-notification";
        if (status === "success") {
            if($(".foundation-content").length > 0){
                var contentApi = $(".foundation-content").adaptTo("foundation-content");
                contentApi.refresh();
            }else{
                $('#pageinfo-popover').popover("hide");
            }
            notificationSlider.notify({
                content: Granite.I18n.get("The page has been published"),
                type: "info",
                closable: false
            });
        }else{
            //error handling
            notificationSlider.notify({
                content: Granite.I18n.get("Failed to publish the selected page(s)."),
                type: "error",
                closable: false,
                className: notificationClass
            });
        }
    }

    function referencesRetrieved(xhr, status) {
        if (status === "success") {
            var json = $.parseJSON(xhr.responseText);
            var assets = json["assets"];
            if (assets.length == 0 && !later) {
                //publish directly
                var replicationUrl = Granite.HTTP.externalize($('.publishwizard-url').data('replication-url'));
                var cmd = encodeURI("Activate");
                var settings = {
                        "type": "POST",
                        "data": {
                            "_charset_": "utf-8",
                            "cmd": cmd,
                            "path": paths
                            },
                            "complete": activateCallback
                };
                $.ajax(replicationUrl, settings);
            } else {
              //go to wizzard
                redirectToPublishWizard();
            }
        }else{
          //error handling
            var notificationSlider = (Granite.author && Granite.author.notifications) ||
                    new Granite.UI.NotificationSlider($(notificationSelector));
            var notificationClass = Granite.author !== undefined ? "admin-notification" : "notification-alert--absolute admin-notification";
            notificationSlider.notify({
                content: Granite.I18n.get("Failed to retrieve references for selected pages."),
                type: "error",
                closable: false,
                className: notificationClass
            });
        }
    }



    CQ_UI_siteadmin_quickPublish = function(refPaths, edit, hasPermission) {


    $.ajax({
                    	   url: '/bin/auspost-workflow-status',
                    	   async: false,
                    	   data: {
                    		   "_charset_":"UTF-8",
                    	        "pagePath":refPaths,
                    	        },
                    	   error: function() {
                    	     alert('Error, Please contact the admin !!!');
                    	   },
                    	   success: function(data) {
                            if(data.workflowStatus === false){
                             var gPath = Granite.HTTP.getPath();
                                     var lastDot = gPath.lastIndexOf(".");
                                     var extSuffixe = gPath.substr(lastDot);
                                     var suffixeSlash = extSuffixe.indexOf('/');

                                     contentPath = extSuffixe.substr(suffixeSlash);
                                     if (suffixeSlash == -1) {
                                         contentPath = '/content';
                                     }
                                     // Map the workflow based on the content path
                                     var admin = this;
                                     var model= "";

                                     if (contentPath.toString().includes('ourpost') == true){
                                     model = "/etc/workflow/models/auspost-ourpost-activate-workflow/jcr:content/model";
                                     }else{
                                     model = "/etc/workflow/models/auspost-global-activate-workflow/jcr:content/model";
                                     }

                                     auspostWorkflowTrigger_publish(contentPath, "", model);
                            }else{
                            alert('This page is already in a worklflow. Please note you can not proceed until the assigned workflow is approved.');
                            setTimeout(location.reload(), 5000);
                            }

                    	   },
                    	   type: 'POST'
                    	   });




    };


    $(document).off("foundation-contentloaded.cards_publish").on("foundation-contentloaded.cards_publish", function(e) {

        if($(".cq-siteadmin-admin-actions-publish-activator").length > 0){
            $(document).off("tap.publish click.publish").fipo("tap.publish", "click.publish", ".cq-siteadmin-admin-actions-publishnow-activator, .cq-siteadmin-admin-actions-publishlater-activator", function(e) {
                CUI.util.state.setSessionItem(DOCUMENT_REFERRER_KEY, location.href);
                activator = $(this);
                later = activator.data("later") || false;

                var selectedItems = $(".foundation-collection").find(".foundation-selections-item");
                var childPaths = [];
                for (var i = 0; i < selectedItems.length; i++) {
                    var $item = $(selectedItems[i]);
                    var path = $item.data("path");
                    childPaths.push(encodeURI(path));
                }

                CQ_UI_siteadmin_quickPublish(childPaths, false, true);

            });
        }

        if($(".cq-siteadmin-admin-actions-quickpublish-activator").length > 0){
            $(document).off("tap.quickpublish click.quickpublish").fipo("tap.quickpublish", "click.quickpublish", ".cq-siteadmin-admin-actions-quickpublish-activator", function(e) {
                CUI.util.state.setSessionItem(DOCUMENT_REFERRER_KEY, location.href);
                activator = $(this);
                var childPaths = [];
                childPaths.push(encodeURI(activator.data("path")));
                var isEditor = activator.data("edit") || false;
                CQ_UI_siteadmin_quickPublish(childPaths, isEditor, true);

            });
        }
    });

    // Auspost custom workflow for ourpost and global projects
    function auspostWorkflowTrigger_publish(paths, startComment, model) {
        var admin = this;
        var startComment = startComment;
        var ui = $(window).adaptTo("foundation-ui");
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
        	   ui.notify(null, Granite.I18n.get("Request for page publication has been forwarded for an approval workflow"));

        	   },
        	   type: 'POST'
        	});


    };

})(document, Granite.$);





