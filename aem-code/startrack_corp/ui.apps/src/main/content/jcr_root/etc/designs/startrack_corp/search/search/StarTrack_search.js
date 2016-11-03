

(function($, context){
	
	
	//context.registerPostAction(function(){});
	// handle breadcrumbs
	context.registerPostAction(function() {
			$(".keymatch_breadcrumbs").each(function(){
				var breadcrumb = $(this);
				
				var path = breadcrumb.attr('path');
				
				var parser = document.createElement('a');
				parser.href = path;

				var pathArray = parser.pathname.split("/");
				var htmlContent = "";
				
				for(var i = 0, j = 0; i < pathArray.length && j < 2; i++){
					if(pathArray[i] == ''){
						continue;
					}
					if (j != 0) {
						htmlContent += "&nbsp;&gt;&nbsp;";
					} 
					htmlContent += "<a>" + pathArray[i] + "</a>";
					j++;
				}
				
				breadcrumb.html(htmlContent);
			});
	});
	
	
})((com.onigroup.gsa.settings.gsa_jquery_noconflict && com.onigroup.gsa.settings.gsa_jquery_noconflict != "") ? eval(com.onigroup.gsa.settings.gsa_jquery_noconflict) : window.$, com.onigroup.gsa.GSAManager);

