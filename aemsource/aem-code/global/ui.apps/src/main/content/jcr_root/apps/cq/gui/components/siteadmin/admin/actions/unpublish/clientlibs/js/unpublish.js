/* Auspost custom workflow for ourpost and global projects - unpublish action- siteadmin */


(function(document, $) {
    
    var editMode, later, params, paths, activator, contentPath;

    var DOCUMENT_REFERRER_KEY = "document.referrer";
    
    CQ_UI_siteadmin_quickUnpublish = function(childPaths, edit, hasPermission) {

        $.ajax({
                            	   url: '/bin/auspost-workflow-status',
                            	   async: false,
                            	   data: {
                            		   "_charset_":"UTF-8",
                            	        "pagePath":childPaths,
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
                                                     var admin = this;
                                                     var model= "";

                                                     if (contentPath.toString().includes('ourpost') == true){
                                                     model = "/etc/workflow/models/auspost-ourpost-activate-workflow/jcr:content/model";
                                                     }else{
                                                     model = "/etc/workflow/models/auspost-global-activate-workflow/jcr:content/model";
                                                     }

                                                     auspostWorkflowTrigger_unpublish(contentPath, "", model);
                                    }else{
                                    alert('This page is already in a worklflow. Please note you can not proceed until the assigned workflow is approved.');
                                    setTimeout(location.reload(), 5000);
                                    }

                            	   },
                            	   type: 'POST'
                            	   });

    };


    // Auspost custom workflow for ourpost and global projects - unpublish action
    function auspostWorkflowTrigger_unpublish(paths, startComment, model) {
        var admin = this;
        var startComment = startComment;
        var ui = $(window).adaptTo("foundation-ui");
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
        	   ui.notify(null, Granite.I18n.get("Request for page unpublication has been forwarded for an approval workflow"));

        	   },
        	   type: 'POST'
        	});

    };

    function deactivateCallback(xhr, status){
        var $notificationSelector = $(".endor-Page-content.endor-Panel.foundation-content"),
            $foundationContent = $(".foundation-content"),
            notificationSlider = (Granite.author && Granite.author.notifications) ||
                new Granite.UI.NotificationSlider($notificationSelector);
        var notificationClass = Granite.author !== undefined ? "admin-notification" : "notification-alert--absolute admin-notification";

        if (status === "success") {
            if($foundationContent.length > 0){
                var contentApi = $foundationContent.adaptTo("foundation-content");
                contentApi.refresh();
            }else{
                $('.pageinfo .popover').show();
            }
            notificationSlider.notify({
                content: Granite.I18n.get("The page has been unpublished"),
                type: "info",
                closable: false
            });
        }else{
            //error handling
            notificationSlider.notify({
                content: Granite.I18n.get("Failed to unpublish the selected page(s)."),
                type: "error",
                closable: false,
                className: notificationClass
            });
        }
    }

    
    $(document).off("foundation-contentloaded.cards_unpublish").on("foundation-contentloaded.cards_unpublish", function(e) {
        
        if($(".cq-siteadmin-admin-actions-unpublish-activator").length > 0){
            $(document).off("tap.unpublish click.unpublish").fipo("tap.unpublish", "click.unpublish", ".cq-siteadmin-admin-actions-unpublishnow-activator, .cq-siteadmin-admin-actions-unpublishlater-activator", function(e) {
                CUI.util.state.setSessionItem(DOCUMENT_REFERRER_KEY, location.href);
                activator = $(this);
                later = $(this).data("later") || false;

                var selectedItems = $(".foundation-collection").find(".foundation-selections-item");
                var childPaths = [];
                for (var i = 0; i < selectedItems.length; i++) {
                    var $item = $(selectedItems[i]);
                    var path = $item.data("path");
                    childPaths.push(encodeURI(path));
                }
                CQ_UI_siteadmin_quickUnpublish(childPaths, false, true);
            });
        }
        
        if($(".cq-siteadmin-admin-actions-quickunpublish-activator").length > 0){
            $(document).off("tap.quickunpublish click.quickunpublish").fipo("tap.quickunpublish", "click.quickunpublish", ".cq-siteadmin-admin-actions-quickunpublish-activator", function(e) {
                CUI.util.state.setSessionItem(DOCUMENT_REFERRER_KEY, location.href);
                activator = $(this);
                var childPaths = [];
                childPaths.push(encodeURI(activator.data("path")));
                var isEditor = activator.data("edit") || false;
                CQ_UI_siteadmin_quickUnpublish(childPaths, isEditor, true);

            });
        }
    });
    
}(document, Granite.$));



