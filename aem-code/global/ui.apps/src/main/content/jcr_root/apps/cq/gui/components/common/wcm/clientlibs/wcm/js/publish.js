/*
 ADOBE CONFIDENTIAL

 Copyright 2014 Adobe Systems Incorporated
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Adobe Systems Incorporated and its suppliers,
 if any.  The intellectual and technical concepts contained
 herein are proprietary to Adobe Systems Incorporated and its
 suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Adobe Systems Incorporated.

 This file has been overlayed to facilitate the Auspost custom workflow integration

 */
(function(window, document, $, URITemplate) {

    var ui = $(window).adaptTo("foundation-ui");
    var replicateURL = Granite.HTTP.externalize("/bin/replicate");
    var editMode, later, params, paths, activator, contentPath;

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq.wcm.publish",
        handler: function(name, el, config, collection, selections) {
            ui.wait();
            var paths = selections.map(function(v) {
                return $(v).data("foundationCollectionItemId");
            });
            
            if (!paths.length) return;
            CQ_UI_siteadmin_quickPublish(paths);
           }
    });

        CQ_UI_siteadmin_quickPublish = function(paths) {
        // Map the workflow based on the content path

        $.ajax({
            	   url: '/bin/auspost-workflow-status',
            	   async: false,
            	   data: {
            		   "_charset_":"UTF-8",
            	        "pagePath":paths,
            	        },
            	   error: function() {
            	     alert('Error occurred in servlet !!!');
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
                     auspostWorkflowTrigger_publish(paths, "", model);
                    }else{
                    alert('This page is already in a worklflow. Please note you can not proceed until the assigned workflow is approved.');
                    setTimeout(location.reload(), 5000);
                    }

            	   },
            	   type: 'POST'
            	   });


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

        		   ui.notify(null, Granite.I18n.get("Request for page publication has been forwarded for an approval workflow"));
        		   setTimeout(location.reload(), 5000);


        	   },
        	   type: 'POST'
        	});


    };

    /**
     * On content load, display a notification in case we have been redirect from
     * the publish or unpublish page wizard.
     */
    $(document).on("foundation-contentloaded", ".cq-siteadmin-admin-childpages", function (e) {
        var message = CUI.util.state.getSessionItem("cq-page-published-message");
        if (message) {
            CUI.util.state.removeSessionItem("cq-page-published-message");
            ui.notify(null, Granite.I18n.getVar(message));
        }
    });
})(window, document, Granite.$, Granite.URITemplate);
