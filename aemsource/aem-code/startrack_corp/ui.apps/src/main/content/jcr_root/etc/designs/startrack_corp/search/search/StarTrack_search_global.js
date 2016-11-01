/**
 * 
 */
(function($, context){
	

$(document).ready(function() {
	
	
	if (com.onigroup.gsa.settings.banner_search_btn_css) {
		$("." + com.onigroup.gsa.settings.banner_search_btn_css).click(function(e) {
			  
		      var url = com.onigroup.gsa.settings.banner_search_destination;
		    
			  url += "?q=" + com.onigroup.util.htmlEncode($("." + com.onigroup.gsa.settings.banner_search_css).val());
			  window.location.href = url;

		});
	}
	
	if (com.onigroup.gsa.settings.banner_search_css) {
		
		if (com.onigroup.gsa.settings.banner_search_enter_keydown) {
			$("." + com.onigroup.gsa.settings.banner_search_css).keydown(function(e) {
				  var code = e.keyCode || e.which;
				  if (code === 13) {
					    var url = com.onigroup.gsa.settings.banner_search_destination;
					    
						url += "?q=" + com.onigroup.util.htmlEncode($(this).val());
						window.location.href = url;
			        }
			});
	    }
		
		$("." + com.onigroup.gsa.settings.banner_search_css).each(function() {
			
			var autoComplete = $(this).autocomplete({  
				  delay:300,        
				  appendTo: $(this).parent(),
				  source: function (request, response) { context.querySuggest(request, response, context);},        
				  messages: {noResults: '',results: function() {}},
				  minLength: 1,
				  select:function(event, ui){
					    var url = com.onigroup.gsa.settings.banner_search_destination;
					    
						url += "?q=" + com.onigroup.util.htmlEncode(ui.item.value);
						window.location.href = url;
				  }
			});
	
			if (autoComplete.data("ui-autocomplete")) {
				autoComplete.data("ui-autocomplete")._renderMenu = function( ul, items ) {
					var that = this;
					$(ul).addClass("search-ac-results");
					$.each( items, function( index, item ) {
						that._renderItemData( ul, item );
					} );
			    };
				autoComplete.data("ui-autocomplete")._renderItem = function (ul, item) {
			         return $("<li></li>")
		           .data("item.autocomplete", item)
		           .append("<a>" + item.label + "</a>")
		           .appendTo(ul);
		      }; 
			} else {
				// jquery ui 1.8.3
				autoComplete.data("autocomplete")._renderMenu = function( ul, items ) {
					var that = this;
					$(ul).addClass("search-ac-results");
					$.each( items, function( index, item ) {
						that._renderItemData( ul, item );
					} );
			    };
			      autoComplete.data("autocomplete")._renderItem = function (ul, item) {
				         return $("<li></li>")
			           .data("item.autocomplete", item)
			           .append("<a>" + item.label + "</a>")
			           .appendTo(ul);
			      };
		      
			}
			
		});
	}
	
});

})((com.onigroup.gsa.settings.gsa_jquery_noconflict && com.onigroup.gsa.settings.gsa_jquery_noconflict != "") ? eval(com.onigroup.gsa.settings.gsa_jquery_noconflict) : window.$, com.onigroup.gsa.GSAManager);
