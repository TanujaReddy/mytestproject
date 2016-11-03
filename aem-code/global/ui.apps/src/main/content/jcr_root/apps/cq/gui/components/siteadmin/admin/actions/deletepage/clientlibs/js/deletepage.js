/* Auspost custom workflow for ourpost and global projects - delete action */

function auspostWorkflowTrigger_delete(paths, startComment, model) {
    var admin = this;
    var startComment = startComment;
   // var ui = $(window).adaptTo("foundation-ui");
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
    		//ui.notify(null, Granite.I18n.get("Request for page deletion has been forwarded for an approval workflow"));
            alert("Request for page deletion has been forwarded for an approval workflow");
            setTimeout(location.reload(), 5000);
    	   },
    	   type: 'POST'
    	});

};

function CQ_UI_siteadmin_quickDelete(){
var gPath = Granite.HTTP.getPath();
                                var lastDot = gPath.lastIndexOf(".");
                                var extSuffixe = gPath.substr(lastDot);
                                var suffixeSlash = extSuffixe.indexOf('/');

                                contentPath = extSuffixe.substr(suffixeSlash);
                                if (suffixeSlash == -1) {
                                contentPath = '/content';
                                }
$.ajax({
                	   url: '/bin/auspost-workflow-status',
                	   async: false,
                	   data: {
                		   "_charset_":"UTF-8",
                	        "pagePath":contentPath,
                	        },
                	   error: function() {
                	     alert('Error, Please contact the admin !!!');
                	   },
                	   success: function(data) {
                        if(data.workflowStatus === false){


                                var admin = this;
                                var model= "";

                                if (contentPath.toString().includes('ourpost') == true){
                                model = "/etc/workflow/models/auspost-ourpost-activate-workflow/jcr:content/model";
                                }else{
                                model = "/etc/workflow/models/auspost-global-activate-workflow/jcr:content/model";
                                }

                                auspostWorkflowTrigger_delete(contentPath, "auspost", model);
                        }else{
                        alert('This page is already in a worklflow. Please note you can not proceed until the assigned workflow is approved.');
                        setTimeout(location.reload(), 5000);
                        }

                	   },
                	   type: 'POST'
                	   });

}

