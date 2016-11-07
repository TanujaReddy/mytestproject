//depends on jQuery and xxx_search_config.js
//define namespace
var com = com || {};
com.onigroup = com.onigroup || {};
com.onigroup.gsa = com.onigroup.gsa || {};
com.onigroup.gsa.ga = com.onigroup.gsa.ga || {};

//store the previous url
com.onigroup.gsa.ga.lastURL = '';

com.onigroup.gsa.ga.eventToSend = null;//{p1: null, p2: null, p3: null, p4: null, p5: null, p6:null};

(function($){

com.onigroup.gsa.ga.init = function(context){
	com.onigroup.gsa.ga.context = context;
	if(com.onigroup.gsa.ga_account != null && com.onigroup.gsa.ga_account != ''){
		(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
		  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
		  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
		  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
		  
		com.onigroup.gsa.ga.doGA('create');
		com.onigroup.gsa.ga.doGA('send', 'pageview');
		com.onigroup.gsa.ga.lastURL = "/" + window.location.search;
		com.onigroup.gsa.ga.navigationElementId = com.onigroup.gsa.navigationElementId;
		
		// set up dimensions for all events
		//GSA_FLAG = "GSA" the identification of this event which comes from GSA
		com.onigroup.gsa.ga.doGA('set', 'dimension1', 'GSA');
		
		//bind event for links
		context.registerPostAction(com.onigroup.gsa.ga.trackLink);
		
		context.registerPostAction(com.onigroup.gsa.ga.handleURLChange);
		context.registerPostAction(com.onigroup.gsa.ga.sendPostResultEvent);
		
	}
};

com.onigroup.gsa.ga.doGA = function(method, p1, p2, p3, p4, p5, p6){
	switch ( method ) {
		case 'create':
			com.onigroup.gsa.ga.doGACreate();
			break;
		case 'set':
			com.onigroup.gsa.ga.doGASet(p1, p2);
			break;
		case 'send':
			com.onigroup.gsa.ga.doGASend(p1,p2,p3,p4,p5, p6);
			break;
		default:
			break;
	}
};

com.onigroup.gsa.ga.doGACreate = function(){
	//Todo... to support multi accounts. the first one is the default one
	var accountArray = com.onigroup.gsa.ga_account.split(',');
	com.onigroup.gsa.ga.otherTrackers = new Array();
	for(var i = 0; i < accountArray.length; i++){
		var tracker = "";
		if(i == 0){
			ga('create', accountArray[i], 'auto');
		}else{
			tracker = "tracker" + i;
			ga('create', accountArray[i], 'auto',tracker);
			com.onigroup.gsa.ga.otherTrackers.push(tracker);
		}
		
	}
	
};

//to support multi accounts
com.onigroup.gsa.ga.doGASet = function(key, value){ 
	//get dimension index
	var dimensionIndexes = com.onigroup.gsa.ga_dimension_start_index.split(',');
	var startIndex = parseInt(dimensionIndexes[0]); 
	var newKey = com.onigroup.gsa.ga._resetDimensionIndex(key,startIndex);
	ga('set', newKey, value);
	for(var i = 0; i < com.onigroup.gsa.ga.otherTrackers.length; i++){
		startIndex = parseInt(dimensionIndexes[i+1]); 
		newKey = com.onigroup.gsa.ga._resetDimensionIndex(key,startIndex);
		var tracker = com.onigroup.gsa.ga.otherTrackers[i];
		ga(tracker + '.set', newKey, value);
	}//end for
};

com.onigroup.gsa.ga._resetDimensionIndex = function(dimensionKey, startIndex){
	if(dimensionKey.indexOf('dimension') == 0){
		var currentIndex = parseInt(dimensionKey.substr('dimension'.length));
		// check for constants which stay the same for all properties
		if (com.onigroup.gsa.ga_dimension_constants && com.onigroup.gsa.ga_dimension_constants != "") {
			var constants = com.onigroup.gsa.ga_dimension_constants.split(',');
			if ($.inArray(currentIndex + "", constants) >= 0) {
				return dimensionKey;
			}
		}
		
		return 'dimension' + (startIndex + currentIndex - 1);
	}else{
		return dimensionKey;
	}
	
};


//'send', 'event', 'category', 'action', 'label', value
//to support multi accounts
com.onigroup.gsa.ga.doGASend = function(event, category,action,label,value, options){
//	// try set dl parameters
//	ga('set', 'location', location.href);
//	for(var i = 0; i < com.onigroup.gsa.ga.otherTrackers.length; i++){
//		var tracker = com.onigroup.gsa.ga.otherTrackers[i];
//		ga(tracker + '.set', 'location', location.href);
//	}//end for

	if (value instanceof Object) {
		options = value;
	}
	
	var newCategory = category;
	var dimensionIndexes = com.onigroup.gsa.ga_dimension_start_index.split(',');
	
	if(category != null && event != 'pageview'){
		newCategory = com.onigroup.gsa.ga_category_prefix + newCategory;
	}
	if( event != 'pageview' || (category != '' && category != null) || (event == 'pageview' && com.onigroup.gsa.ga_nopageview_for_default == false)){
        var startIndex = parseInt(dimensionIndexes[0]); 
		
		var newOptions = {};
		if (options) {
			$.each(options, function(name, value) {
				if (name.indexOf("dimension") == 0) {
					var newKey = com.onigroup.gsa.ga._resetDimensionIndex(name, startIndex);
					newOptions[newKey] = value;
				} else {
					newOptions[name] = value;
				}
			});
		}
		
		if (value instanceof Object) {
			ga('send', event, newCategory,action,label, newOptions);
		} else {
			ga('send', event, newCategory,action,label,value, newOptions);
		}
		
	}
	
	for(var i = 0; i < com.onigroup.gsa.ga.otherTrackers.length; i++){
		var startIndex = parseInt(dimensionIndexes[i+1]); 
		
		var newOptions = {};
		if (options) {
			$.each(options, function(name, value) {
				if (name.indexOf("dimension") == 0) {
					var newKey = com.onigroup.gsa.ga._resetDimensionIndex(name, startIndex);
					newOptions[newKey] = value;
				} else {
					newOptions[name] = value;
				}
			});
		}
		
		
		var tracker = com.onigroup.gsa.ga.otherTrackers[i];
		if (value instanceof Object) {
			ga(tracker + '.send', event, newCategory,action,label, newOptions);
		} else {
			ga(tracker + '.send', event, newCategory,action,label,value, newOptions);
		}
		
	}//end for
};

com.onigroup.gsa.ga.handleURLChange = function(){
	var url = location.pathname + location.search;
	if(com.onigroup.util.isSameStr(url,com.onigroup.gsa.ga.lastURL) == false){
		com.onigroup.gsa.ga.doGASend('pageview',url);
	}
	com.onigroup.gsa.ga.lastURL = url;
};

com.onigroup.gsa.ga.getLastFilter = function() {
	
	var searchParam = com.onigroup.gsa.ga.context.getLastSearchParam();
	
	var filter = searchParam.getFilter();
	if (filter == null || filter.length == 0) {
		return "All";
	} else {
		return filter.split(":")[1];
	}
};

com.onigroup.gsa.ga.sendPostResultEvent = function() {
	
	// if no result, send unsuccessful result event
	var searchParam = com.onigroup.gsa.ga.context.getLastSearchParam();
	var query = searchParam.getQuery();
	
	var etc = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_NAME_ORIGINAL_PARAMS, location.search);
	var as_sitesearch = "";
	if (etc && etc != "") {
		etc = "?" + com.onigroup.util.htmlDecode(etc);
		as_sitesearch = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_AS_SITESEARCH, etc);
	}
	
	if (com.onigroup.gsa.ga.is_zero_result(query)) {
		var site = searchParam.getCollection();
		if(site == null || site == ''){
			site = com.onigroup.gsa.settings.collection;
		}
		var filter = searchParam.getFilter();
		var gsa_filter = com.onigroup.gsa.ga.getLastFilter();
		// add as_sitesearch to dimension19
		var options = {nonInteraction: true, 'dimension6': gsa_filter};
		if (as_sitesearch && as_sitesearch != "") {
			options['dimension19'] = as_sitesearch;
		}
		com.onigroup.gsa.ga.doGA('send', 'event','No Results', 'Unsuccessful Search', query, options);
	} 
	
	if (com.onigroup.gsa.ga.isSuccessfulSearch(query)) {
		
		if (com.onigroup.gsa.ga.eventToSend != null && com.onigroup.gsa.ga.eventToSend.p1 != null) {
			var event = com.onigroup.gsa.ga.eventToSend;
			
			// add as_sitesearch to dimension19
			if (as_sitesearch && as_sitesearch != "") {
				if (event.p5 && event.p5 instanceof Object) {
					event.p5['dimension19'] = as_sitesearch;
				} else if (event.p6 && event.p6 instanceof Object){
					event.p6['dimension19'] = as_sitesearch;
				}
			}
			
			com.onigroup.gsa.ga.doGA('send', event.p1, event.p2, event.p3, event.p4, event.p5, event.p6);
			
			com.onigroup.gsa.ga.eventToSend = null;
		}
	}
	
};

com.onigroup.gsa.ga.triggerInitialSearch = function(searchParam, fromSuggest) {
	//tracking load action

	var query = searchParam.getQuery();

	var page = searchParam.getPage();
	if (!page || page.length == 0) {
		page = 1;
	}
	
	var filter = com.onigroup.gsa.ga.getLastFilter();
	if (fromSuggest) {
		filter = filter + " - Suggested";
	}
	
	com.onigroup.gsa.ga.eventToSend = {
		    p1: "event", 
		    p2: "Results",
		    p3: "Successful Search",
		    p4: query,
		    p5: {nonInteraction: true, 'dimension6': filter, 'dimension5': page}
	};

};

com.onigroup.gsa.ga.trackLink = function(){
	$("a", $("#" + com.onigroup.gsa.settings.gsa_container_id)).each(function(){
		$(this).click(function(){
			var cdata = $(this).attr("cdata");
			var ctype = $(this).attr("ctype");
			var rank = $(this).attr("rank");
			var src_id = $(this).attr("src_id");
			if (!ctype) {
				// handle filters/dynamic nav
				if ($(this).hasClass(com.onigroup.gsa.settings.gsa_filter_search_css)) {
					ctype = 'filter';
					cdata = $(this).attr('v');
				} else {
					ctype = "OTHER";
				}
			    
			// unknown link type
			}
			
			if (ctype == "nav.page") {
				cdata = $(this).text();
			}
			
			var url = $(this).attr("href");
			var searchParam = com.onigroup.gsa.ga.context.getLastSearchParam();
			var query = searchParam.getQuery();
			var site = searchParam.getCollection();
			if(site == null || site == ''){
				site = com.onigroup.gsa.settings.collection;
			}
			var start = null;
			var page = searchParam.getPage();
			if( page != null && page != ''){
				start = (parseInt(page) - 1) * com.onigroup.gsa.settings.gsa_page_size;
			}
			// Calls Analytics Event Tracking code
			com.onigroup.gsa.ga.cl_analytics_clk(url, query, ctype, cdata, rank, start, site, src_id);
		});//click
	});//each

};


com.onigroup.gsa.ga.is_zero_result = function(query){
		if(query == '' || query == 'undefined'){
			//query is empty, then there is no search
			return false;
		}else{
			var navigationDiv = document.getElementById(com.onigroup.gsa.ga.navigationElementId);
			if(navigationDiv == null || navigationDiv == 'undefined'){
				return true;
			}else{
				return false;
			}
		}
  	};
  	
com.onigroup.gsa.ga.isSuccessfulSearch = function(query){
	if(query == '' || query == 'undefined'){
		//query is empty, then there is no search
		return false;
	}else{
		var navigationDiv = document.getElementById(com.onigroup.gsa.ga.navigationElementId);
		if(navigationDiv == null || navigationDiv == 'undefined'){
			return false;
		}else{
			return true;
		}
	}
};

/**
 * Function that logs the click to be hit with Google Analytics
 * 
 * Uses all the same parameters as search reporting click logger.
 * 
 * @return {Boolean} true if we think we logged the click.
 */
com.onigroup.gsa.ga.cl_analytics_clk = function(url, query, ctype, cdata, rank, start, site, src_id) {
	ctype = ctype.toString();
	var pageSize = com.onigroup.gsa.settings.gsa_page_size;
	if(start == '' || start == null){
		start = 0;
	}else{			
		start = parseInt(start);
	}
	var totalRank = 0;
	if(rank != null && rank != ''){
		totalRank = start + parseInt(rank);
	}
	
	//custom dimension3- Rank of Selected Results
	var totalRankStr = "";
	if(totalRank){
		totalRankStr = totalRank;
	}
	
	if(totalRank < 10 && totalRank > 0){ //if it 
		totalRankStr = "0" + totalRank;
	}
	
	var page = start / pageSize + 1;
	
	var lastFilter = com.onigroup.gsa.ga.getLastFilter();
	//Sample
	//ga('send', 'event', 'category', 'action');
	//ga('send', 'event', 'category', 'action', 'label');
	//ga('send', 'event', 'category', 'action', 'label', value);  // value is a number.
    
    switch (ctype) {
	    case "nav.next":
	    	com.onigroup.gsa.ga.eventToSend = {
			    p1: "event", 
			    p2: "Results",
			    p3: 'Next Results Page Click',
			    p4: query,
			    p5: {'dimension6': lastFilter, 'dimension5': page}
		    };

	        break;
	    case "nav.page":
	    	com.onigroup.gsa.ga.eventToSend = {
			    p1: "event", 
			    p2: "Results",
			    p3: 'Specific Page Number Click',
			    p4: query,
			    p5: {'dimension6': lastFilter, 'dimension5': cdata}
		    };
	    	
	        break;
	    case "nav.prev":
	    	com.onigroup.gsa.ga.eventToSend = {
			    p1: "event", 
			    p2: "Results",
			    p3: 'Previous Results Page Click',
			    p4: query,
			    p5: {'dimension6': lastFilter, 'dimension5': page}
		    };
	    	
	        break;
	        
	    case "spell":
	    	com.onigroup.gsa.ga.eventToSend = {
			    p1: "event", 
			    p2: "Results",
			    p3: "Successful Search",
			    p4: query,
			    p5: {nonInteraction: true, 'dimension6': lastFilter + " - Spelling", 'dimension5': 1}
		    };
	        break;
	        
	    case "synonym":
	    	com.onigroup.gsa.ga.eventToSend = {
			    p1: "event", 
			    p2: "Results",
			    p3: "Successful Search",
			    p4: query,
			    p5: {nonInteraction: true, 'dimension6': lastFilter + " - Synonym", 'dimension5': 1}
		    };
	        break;
	        
	    case "filter":
	    	//set filter
	    	com.onigroup.gsa.ga.eventToSend = {
			    p1: "event", 
			    p2: "Results",
			    p3: "Filter Click",
			    p4: query,
			    p5: {'dimension6': cdata, 'dimension5': page}
		    };
	    	
	    	break;
	    case "sitesearch":
	    	
	    	com.onigroup.gsa.ga.eventToSend = {
			    p1: "event", 
			    p2: "Results",
			    p3: "More results from... Link Click",
			    p4: query,
			    p5: {'dimension6': lastFilter + " - More", 'dimension5': 1}
		    };
	        break;
	    
	    case "showomitted":
	    	com.onigroup.gsa.ga.eventToSend = {
			    p1: "event", 
			    p2: "Results",
			    p3: "Include Omitted Result... Link Click",
			    p4: query,
			    p5: {'dimension6': lastFilter + " - Include Omitted Result ", 'dimension5': 1}
		    };
	        break;
	    case "c":
	    	//set result type:
	    	if(cdata != null && cdata.length > 0){
	    		com.onigroup.gsa.ga.doGA('send', 'event','Results Clicks', 'Page Result Click', query, totalRank, {'dimension3': totalRankStr, 'dimension5': page, 'dimension4': url, 'dimension7': cdata});
	    	} else {
	    		com.onigroup.gsa.ga.doGA('send', 'event','Results Clicks', 'Page Result Click', query, totalRank, {'dimension3': totalRankStr, 'dimension5': page, 'dimension4': url});
	    	}
	    	// GSA_Rank GSA_Page only to 'Page Result Click' event - UOM-76
	    	//Custom dimention 5 - Page of Selected Results
	    	
	        break;
	    case "keymatch":
	    	if(cdata != null && cdata.length > 0){
	    		com.onigroup.gsa.ga.doGA('send', 'event','Results Clicks', 'Keymatch Result Click', query, {'dimension4': url, 'dimension5': page, 'dimension7': cdata});
	    	} else {
	    		com.onigroup.gsa.ga.doGA('send', 'event','Results Clicks', 'Keymatch Result Click', query, {'dimension4': url, 'dimension5': page});
	    	}
	    	
	        break;
	        
	    default:
	    	com.onigroup.gsa.ga.eventToSend = {
			    p1: "event", 
			    p2: "Results",
			    p3: "Unknown Result Type",
			    p4: query
		    };

	        break;
    }
    return new Boolean(true);
};


})((com.onigroup.gsa.settings.gsa_jquery_noconflict && com.onigroup.gsa.settings.gsa_jquery_noconflict != "") ? eval(com.onigroup.gsa.settings.gsa_jquery_noconflict) : window.$);
