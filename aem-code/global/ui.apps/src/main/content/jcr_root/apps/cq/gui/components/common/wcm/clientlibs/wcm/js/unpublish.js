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
(function(window, document, $) {
    var ui = $(window).adaptTo("foundation-ui");
    var replicateURL = Granite.HTTP.externalize("/bin/replicate");
     var editMode, later, params, paths, activator, contentPath;


     CQ_UI_siteadmin_quickUnpublish = function(paths) {
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
            auspostWorkflowTrigger_unpublish(paths, "", model);
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
           setTimeout(location.reload(), 5000);
    	   },
    	   type: 'POST'
    	});

};


    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq.wcm.unpublish",
        handler: function(name, el, config, collection, selections) {
            ui.wait();
            
            var paths = selections.map(function(v) {
                return $(v).data("foundationCollectionItemId");
            });
            
            if (!paths.length) return;
            CQ_UI_siteadmin_quickUnpublish(paths);

        }
    });
}(window, document, Granite.$));
