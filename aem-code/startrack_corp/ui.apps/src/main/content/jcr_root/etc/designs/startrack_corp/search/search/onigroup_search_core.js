//version Core GSA Client API

/**
 * Define the namespace
 */
var com = com || {};
com.onigroup = com.onigroup || {};
com.onigroup.gsa = com.onigroup.gsa || {};
com.onigroup.util = com.onigroup.util || {};
com.onigroup.gsa.constants = com.onigroup.gsa.constants || {};
com.onigroup.gsa.settings = com.onigroup.gsa.settings || {};
/**
 * inheritance
 */

Function.prototype.inheritsFrom = function( parentClassOrObject ){ 
	if ( parentClassOrObject.constructor == Function ) 
	{ 
		//Normal Inheritance 
		this.prototype = new parentClassOrObject;
		this.prototype.constructor = this;
		this.prototype.parent = parentClassOrObject.prototype;
	} 
	else 
	{ 
		//Pure Virtual Inheritance 
		this.prototype = parentClassOrObject;
		this.prototype.constructor = this;
		this.prototype.parent = parentClassOrObject;
	} 
	return this;
} 


/**
 * constants for Search Wrapper
 */
com.onigroup.gsa.constants.P_NAME_QUERY = "query";
com.onigroup.gsa.constants.P_NAME_COLLECTION = "collection";
com.onigroup.gsa.constants.P_NAME_FRONTEND = "frontend";
com.onigroup.gsa.constants.P_NAME_FILTER = "filter";
com.onigroup.gsa.constants.P_NAME_RES_FROMAT = "res_format"; //for search (xml/json)
com.onigroup.gsa.constants.P_NAME_LIMIT = "limit";
com.onigroup.gsa.constants.P_NAME_FORMAT = "format"; // for suggest (rich/os)
com.onigroup.gsa.constants.P_NAME_CALLBACK = "callback";
com.onigroup.gsa.constants.P_NAME_PAGE = "page";
com.onigroup.gsa.constants.P_NAME_DYNAMIC_NAVS = "dnavs";
com.onigroup.gsa.constants.P_NAME_ORIGINAL_PARAMS = "etc";

/**
 * GSA parameters
 */
com.onigroup.gsa.constants.P_GSA_NAME_SITE = "site";
com.onigroup.gsa.constants.P_GSA_NAME_QUERY = "q";
com.onigroup.gsa.constants.P_GSA_NAME_STYLESHEET = "proxystylesheet";
com.onigroup.gsa.constants.P_GSA_NAME_CLIENT = "client";
com.onigroup.gsa.constants.P_GSA_NAME_NUM = "num";
com.onigroup.gsa.constants.P_GSA_NAME_START = "start";
com.onigroup.gsa.constants.P_GSA_NAME_EMMAIN = "emmain";
com.onigroup.gsa.constants.P_GSA_NAME_EMSINGLERRES = "emsingleres";
com.onigroup.gsa.constants.P_GSA_NAME_DYNAMIC_NAVS = "dnavs";
com.onigroup.gsa.constants.P_GSA_NAME_REQUIRED_FIELDS = "requiredfields";
com.onigroup.gsa.constants.P_GSA_NAME_FILTER = "filter";
com.onigroup.gsa.constants.P_GSA_NAME_AS_SITESEARCH = "as_sitesearch";


(function($) {
	

/**
 * Parameter Class
 */
com.onigroup.gsa.SearchParamBase = function(){
	this._query = null;
	this._collection = null;
	this._frontend = null;
	this._page = null;
	this._start = 0;
	this._requiredFields = null;
	this._gsaFilter = null;
	this._origURL = null;
	this._addAllParams = true;
	this._asSiteSearch = null;
	
	this.setQuery = function(q){
		this._query = com.onigroup.util.htmlEncode(com.onigroup.util.htmlDecode(q));
	};
	
	this.getQuery = function(){
		return com.onigroup.util.null2String(this._query);
	};
	
	this.setCollection = function(c){
		this._collection = c;
	};
	this.getCollection = function(){
		return com.onigroup.util.null2String(this._collection);
	};
	this.setFrontend = function(f){
		this._frontend = f;
	};
	this.getFrontend = function(){
		return com.onigroup.util.null2String(this._frontend);
	};
	
	this.setPage = function(p){
		this._page = p;
	};
	this.getPage = function(){
		return this._page;
	};
	this.setDNAVS = function(dnavs) {
		this._dnavs = dnavs;
	};
	this.getDNAVS = function() {
		return this._dnavs;
	};
	
	this.setStart = function(p){
		this._start = p;
	};
	this.getStart = function(){
		return this._start;
	};
	
	this.getRequiredFields = function() {
		return this._requiredFields;
	};
	this.setRequiredFields = function(required) {
		this._requiredFields = required;
	};
	
	this.setGSAFilter = function(filter) {
		this._gsaFilter = filter;
	};
	this.getGSAFilter = function() {
		return this._gsaFilter;
	};
	
	this.setAsSiteSearch = function(as_sitesearch) {
		this._asSiteSearch = as_sitesearch;
	};
	this.getAsSiteSearch = function() {
		return this._asSiteSearch;
	};
	
	this.setOrigURL = function(url){
		var temp = url;
		if(url.indexOf('?') >= 0){
			temp = url.substr(url.indexOf('?') + 1);
		}
		if (temp.charAt(0) == '&') {
			temp = temp.substr(1);
		}
		this._origURL = temp;
	};
	this.getOrigURL = function(){
		return this._origURL;
	};
	
	this._toURL = function(){
		if(com.onigroup.gsa.settings.enable_wrapper){
			return this._toURLWrap();
		}else{
			return this._toURLGSA();
		}
	};
	
	this.setAddAllParams = function(addAllParams) {
		this._addAllParams = addAllParams;
	};
	
	this._toURLWrap = function(){
		var result = "";
		
		result = com.onigroup.gsa.constants.P_NAME_QUERY + "=" + this.getQuery();
		if(this.getCollection() != ""){
			result += "&" + com.onigroup.gsa.constants.P_NAME_COLLECTION + "=" + this.getCollection(); 
		}
		if(this.getFrontend() != ""){
			result += "&" + com.onigroup.gsa.constants.P_NAME_FRONTEND + "=" + this.getFrontend(); 
		}
		if(this.getPage() != null && parseInt(this.getPage()) > 0){
			result += "&" + com.onigroup.gsa.constants.P_NAME_PAGE + "=" + this.getPage(); 
		}
		if (this.getDNAVS() != null && this.getDNAVS().length > 0) {
			result += "&" + com.onigroup.gsa.constants.P_NAME_DYNAMIC_NAVS + "=" + this.getDNAVS();
		}
		if (this._addAllParams && this._origURL && this._origURL.length > 0) {
			result += "&" + com.onigroup.gsa.constants.P_NAME_ORIGINAL_PARAMS + "=" + com.onigroup.util.htmlEncode(this._origURL);
		}
		
		return result;
	};
	
	this._toURLGSA = function(){
		var result = "";
		
		if(this.getOrigURL() != null && this.getOrigURL() != ''){
			var origURL = this.getOrigURL();
			
			//check if frontend and collection are set
			var collection = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_SITE, origURL);
			var frontend = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_STYLESHEET, origURL);
			var gsaFilter = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_FILTER, origURL);
			var as_sitesearch = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_AS_SITESEARCH, origURL);
			
			if(collection == null || collection == ''){
				origURL += "&" + com.onigroup.gsa.constants.P_GSA_NAME_SITE + "=" + this.getCollection(); 
			}
			if(frontend == null || frontend == ''){
				origURL += "&" + com.onigroup.gsa.constants.P_GSA_NAME_STYLESHEET + "=" + this.getFrontend(); 
				origURL += "&" + com.onigroup.gsa.constants.P_GSA_NAME_CLIENT + "=" + this.getFrontend(); 
			}
			if ((gsaFilter == null || gsaFilter == '' ) && this.getGSAFilter() != null && this.getGSAFilter().length > 0) {
				origURL += "&" + com.onigroup.gsa.constants.P_GSA_NAME_FILTER + "=" + this.getGSAFilter();
			}
			if ((as_sitesearch == null || as_sitesearch == '') && this.getAsSiteSearch() != null && this.getAsSiteSearch().length > 0) {
				origURL += "&" + com.onigroup.gsa.constants.P_GSA_NAME_AS_SITESEARCH + "=" + this.getAsSiteSearch();
			}
			
			return origURL;		
        }
		
		result = com.onigroup.gsa.constants.P_GSA_NAME_QUERY + "=" + this.getQuery();
		if(this.getCollection() != ""){
			result += "&" + com.onigroup.gsa.constants.P_GSA_NAME_SITE + "=" + this.getCollection(); 
		}
		if(this.getFrontend() != ""){
			result += "&" + com.onigroup.gsa.constants.P_GSA_NAME_STYLESHEET + "=" + this.getFrontend(); 
			result += "&" + com.onigroup.gsa.constants.P_GSA_NAME_CLIENT + "=" + this.getFrontend(); 
		}
		/*
		var temp = com.onigroup.gsa.GSAManager.getProxy();
		if(temp != null && temp != ''){
			result += "&" + com.onigroup.gsa.constants.P_GSA_NAME_EMMAIN + "=" + temp; 
		}
		
		temp = com.onigroup.gsa.GSAManager.getGSAResourcePath();
		if(temp != null && temp != ''){
			result += "&" + com.onigroup.gsa.constants.P_GSA_NAME_EMSINGLERRES + "=" + temp; 
		}
		*/
		
		if(this.getStart() != null && this.getStart() != 0){
			result += "&" + com.onigroup.gsa.constants.P_GSA_NAME_START + "=" + this.getStart(); 
		}
		if (this.getDNAVS() != null && this.getDNAVS().length > 0) {
			result += "&" + com.onigroup.gsa.constants.P_GSA_NAME_DYNAMIC_NAVS + "=" + this.getDNAVS();
		}
		if (this.getRequiredFields() != null && this.getRequiredFields().length > 0) {
			result += "&" + com.onigroup.gsa.constants.P_GSA_NAME_REQUIRED_FIELDS + "=" + this.getRequiredFields();
		}
		if (this.getGSAFilter() != null && this.getGSAFilter().length > 0) {
			result += "&" + com.onigroup.gsa.constants.P_GSA_NAME_FILTER + "=" + this.getGSAFilter();
		}
		if (this.getAsSiteSearch() != null && this.getAsSiteSearch().length > 0) {
			result += "&" + com.onigroup.gsa.constants.P_GSA_NAME_AS_SITESEARCH + "=" + this.getAsSiteSearch();
		}
		return result;
	};
	return this;
}

//Parameter class for simple search
com.onigroup.gsa.SearchParam = function(){
	this._filter = null;
	
	return this;
};

com.onigroup.gsa.SearchParam.inheritsFrom(com.onigroup.gsa.SearchParamBase);
com.onigroup.gsa.SearchParam.prototype.setFilter = function(f){
	this._filter = f;
	if(!com.onigroup.gsa.settings.enable_wrapper) {
		this.setRequiredFields(f);
	}
};
com.onigroup.gsa.SearchParam.prototype.getFilter = function(){
	return com.onigroup.util.null2String(this._filter);	
};

com.onigroup.util.decodeHex = function(input) {
	return input.replace(/\\x([0-9A-Fa-f]{2})/g, function() {
        return String.fromCharCode(parseInt(arguments[1], 16));
    });
};

com.onigroup.gsa.SearchParam.prototype.toURL = function(){
	var result = this._toURL();
	if(this.getFilter() != null){
		if(com.onigroup.gsa.settings.enable_wrapper) {
			result += "&" + com.onigroup.gsa.constants.P_NAME_FILTER + "=" + this.getFilter();
		}
		
	}
	return result;
}
com.onigroup.gsa.SearchParam.prototype.init = function(urlStr){
	if(com.onigroup.gsa.settings.enable_wrapper){
		return this._initWrapMode(urlStr);
	}else{
		return this._initGSAMode(urlStr);
	}
};

com.onigroup.gsa.SearchParam.prototype._initWrapMode = function(urlStr){
	//To be enhanced in the future... the properties are gone when read the object again, so store the object to string and initialize back
	var query = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_NAME_QUERY, urlStr);
	var collection = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_NAME_COLLECTION, urlStr);
	var page = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_NAME_PAGE, urlStr);
	var frontend = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_NAME_FRONTEND, urlStr);
	var filter = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_NAME_FILTER, urlStr);
	this.setQuery(query);
	this.setCollection(collection);
	this.setPage(page);
	this.setFrontend(frontend);
	this.setFilter(filter);
	return this;
};

com.onigroup.gsa.SearchParam.prototype._initGSAMode = function(urlStr){
	//To be enhanced in the future... the properties are gone when read the object again, so store the object to string and initialize back
	var query = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_QUERY, urlStr);
	var collection = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_SITE, urlStr);
	var frontend = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_STYLESHEET, urlStr);
	
	var requiredfields = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_REQUIRED_FIELDS, urlStr);
	var gsaFilter = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_FILTER, urlStr);
	var as_sitesearch = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_AS_SITESEARCH, urlStr);
	// add except for no filter search
	//com.onigroup.gsa.constants.P_GSA_NAME_REQUIRED_FIELDS = "requiredfields";
	//com.onigroup.gsa.constants.P_GSA_NAME_FILTER = "filter";
	//com.onigroup.gsa.constants.P_GSA_NAME_AS_SITESEARCH = "as_sitesearch";
	
	this.setQuery(query);
	this.setCollection(collection);
	this.setFrontend(frontend);
	this.setFilter(requiredfields);
	this.setGSAFilter(gsaFilter);
	this.setAsSiteSearch(as_sitesearch);
	
	return this;
};

//Parameter class for suggest
com.onigroup.gsa.SuggestParam = function(){
	return this;
};
com.onigroup.gsa.SuggestParam.inheritsFrom(com.onigroup.gsa.SearchParamBase);

com.onigroup.gsa.SuggestParam.prototype.getMaxMatches = function(){
	if(com.onigroup.gsa.settings.gsa_suggest_max_matches != null){
		return com.onigroup.gsa.settings.gsa_suggest_max_matches;
	}else{
		return 10;
	}
};

com.onigroup.gsa.SuggestParam.prototype.getFormat = function(){
	if(com.onigroup.gsa.settings.gsa_suggest_format != null){
		return com.onigroup.gsa.settings.gsa_suggest_format;
	}else{
		return 'os';
	}
};

com.onigroup.gsa.SuggestParam.prototype.toURL = function(){
	//suggest?callback={callback}&query={query}&collection={collection}&frontend={frontend}&limit={limit}&format={format}
	var result = this._toURL();
	result += "&" + com.onigroup.gsa.constants.P_NAME_LIMIT + "=" + this.getMaxMatches() + "&format=" + this.getFormat();
	return result;
}

//cluster?callback={callback}&query={query}&collection={collection}&frontend={frontend}"

//Parameter class for Cluster
com.onigroup.gsa.ClusterParam = function(){
	return this;
};
com.onigroup.gsa.ClusterParam.inheritsFrom(com.onigroup.gsa.SearchParamBase);

/**
 * parameter object for ajax call
 */
com.onigroup.gsa.AjaxParam = function(){
	this._url = null;
	this._dataType = null;
	this._type = null;
	this._data = null;
	
	this.getURL = function(){
		return com.onigroup.util.null2String(this._url);
	};
	this.setURL = function(u){
		this._url = u;
	};
	
	this.getDataType = function(){
		return com.onigroup.util.null2String(this._dataType);
	};
	this.setDataType = function(dType){
		this._dataType = dType;
	};
	this.getType = function(){
		return com.onigroup.util.null2String(this._type);
	};
	this.setType = function(t){
		this._type = t;
	};
	this.getData = function(){
		return this._data;
	};
	this.setData = function(d){
		this._data = d;
	};
	return this;
};

/**
 * common functions
 */
com.onigroup.util.constants = com.onigroup.util.constants || {};
com.onigroup.util.constants.AJAX_DATA_TYPE_JSON = 'json';
com.onigroup.util.constants.AJAX_DATA_TYPE_JSONP = 'jsonp';
com.onigroup.util.constants.AJAX_DATA_TYPE_HTML = 'html';

com.onigroup.util.null2String = function(input){
	if(input == null){
		return '';
	}else{
		return input.trim();
	}
};

com.onigroup.util.getParameterByNameInURL = function(name, url) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)");
    results = regex.exec(url);
    try{
        // avoid decode %25xx like %2520
    	return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " ").replace(/%25/g, "%2525"));
    }catch(e){
    	console.log("getParameterByNameInURL error: start.")
    	console.log(e);
    	console.log(results);
    	console.log("getParameterByNameInURL error: end.")
    	return "";
    }
};


com.onigroup.util.initHistory = function(){
	//var state = History.getState(),
	//$log = $('#log');

	// Log Initial State
	//History.log('initial:', state.data, state.title, state.url);

	// Bind to State Change
	History.Adapter.bind(window,'statechange',function(){ // Note: We are using statechange instead of popstate
		// Log the State
		var state = History.getState(); // Note: We are using History.getState() instead of event.state
		//History.log('statechange:', state.data, state.title, state.url);
		if(com.onigroup.gsa.GSAManager.isSavingHistory() == false){
			var paramObj = com.onigroup.util.buildSearchParamFromURL(state.url);
			paramObj.setOrigURL(state.url);
			com.onigroup.gsa.GSAManager.doSimpleSearch(paramObj);
		}
	});
};

com.onigroup.util.preventDefault = function (evt){
	if(evt != null){
		if(evt.preventDefault){
			evt.preventDefault();
		}else{
			evt.returnValue = false;
		}
	}
}

/**
 * get the parameter value in the current location 
 */

com.onigroup.util.getParameterByName = function(name) {
    return com.onigroup.util.getParameterByNameInURL(name, location.search);
};

com.onigroup.util.compitableIE8 = function(){
	if (typeof String.prototype.trim !== 'function') {
		String.prototype.trim = function() {
			return this.replace(/^\s+|\s+$/g, '');
		}
	}
	

	if (!Object.keys) {
		Object.keys = function(obj) {
			var keys = [];

			for ( var i in obj) {
				if (obj.hasOwnProperty(i)) {
					keys.push(i);
				}
			}

			return keys;
		};
	}
};

com.onigroup.util.htmlEncode = function(value){
	if(encodeURIComponent){
		return encodeURIComponent(value);
	}else if(encodeURI){
		return encodeURI(value);
	}else{
		return escape(value);
	}
};

com.onigroup.util.htmlDecode = function(value){
	if(decodeURIComponent){
		return decodeURIComponent(value);
	}else if(decodeURI){
		return decodeURI(value);
	}else{
		return unescape(value);
	}
};

com.onigroup.util.endWith = function(inputStr, suffix){
	return inputStr.indexOf(suffix, inputStr.length - suffix.length) !== -1;
};

/**
 * from init search URL. the search page loading... (for example, it is 'query', not 'q' which is for gsa direct search)
 */
com.onigroup.util.buildSearchParamFromURL = function(){
	//ING
	if(com.onigroup.gsa.settings.enable_wrapper){
		return com.onigroup.util.buildSearchParamFromURLWrapper();
	}else{
		var result = new com.onigroup.gsa.SearchParam();
		result.setOrigURL(location.search);
		return result;
	}
};

/**
 * from init search URL. the search page loading... (for example, it is 'query', not 'q' which is for gsa direct search)
 */
com.onigroup.util.buildSearchParamFromURLWrapper = function(){
	var result = new com.onigroup.gsa.SearchParam();
	result.setQuery(com.onigroup.util.getParameterByName(com.onigroup.gsa.constants.P_NAME_QUERY));
	result.setCollection(com.onigroup.util.getParameterByName(com.onigroup.gsa.constants.P_NAME_COLLECTION));
	result.setFrontend(com.onigroup.util.getParameterByName(com.onigroup.gsa.constants.P_NAME_FRONTEND));
	result.setFilter(com.onigroup.util.getParameterByName(com.onigroup.gsa.constants.P_NAME_FILTER));
	result.setDNAVS(com.onigroup.util.getParameterByName(com.onigroup.gsa.constants.P_NAME_DYNAMIC_NAVS));
	return result;
};

com.onigroup.util.buildSearchParamFromGSAURL = function(url){
//	com.onigroup.gsa.constants.P_GSA_NAME_SITE = "site";
//	com.onigroup.gsa.constants.P_GSA_NAME_QUERY = "q";
//	com.onigroup.gsa.constants.P_GSA_NAME_STYLESHEET = "proxystylesheet";
//	com.onigroup.gsa.constants.P_GSA_NAME_CLIENT = "client";
//  support dynamic nav  
	var result = new com.onigroup.gsa.SearchParam();
	result.setQuery(com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_QUERY,url));
	result.setCollection(com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_SITE,url));
	result.setFrontend(com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_STYLESHEET,url));
	//result.setFilter(com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_NAME_FILTER,url));
	var start = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_START,url);
	if(start != null && start != ''){
		result.setStart(parseInt(start));
	};
	//get page
	var num = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_NUM,url)
	//set default page size
	if(num == null || num == ''){
		num = com.onigroup.gsa.settings.gsa_page_size;
	}
	if(start != null && start != ''){
		result.setPage(parseInt(start) / parseInt(num) + 1) ;
	};
	// dynamic navigation parameter dnav which is used for dynamic navigation status
	var dnavs = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_DYNAMIC_NAVS,url)
	if(dnavs != null && dnavs != '') {
		result.setDNAVS(dnavs);
	}
	
	var requiredFields = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_REQUIRED_FIELDS,url)
	if(requiredFields != null && requiredFields != '') {
		result.setRequiredFields(requiredFields);
	}
	
	var gsaFilter = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_FILTER,url)
	if(gsaFilter != null && gsaFilter != '') {
		result.setGSAFilter(gsaFilter);
	}
	
	var asSiteSearch = com.onigroup.util.getParameterByNameInURL(com.onigroup.gsa.constants.P_GSA_NAME_AS_SITESEARCH,url)
	if(asSiteSearch != null && asSiteSearch != '') {
		result.setAsSiteSearch(asSiteSearch);
	}
	// 
	return result;
};

com.onigroup.util.logger = function(){
	var LOG_LEVEL_ERROR = 2;
	var LOG_LEVEL_INFO = 1;
	var LOG_LEVEL_DEBUG = 0;
	
	this._log = function(msg, level){
		console.log(msg);
	};
	this.error = function(msg){
		this.log(msg, LOG_LEVEL_ERROR);
	};
	this.info = function(msg){
		this.log(msg, LOG_LEVEL_INFO);
	};
	this.debug = function(msg){
		this.log(msg, LOG_LEVEL_DEBUG);
	};
	return this;
};


(function(){
	/**
	 * Configuration - static parameter
	 */
	var GSA_PROTOCAL = ""; //default
	var GSA_HOST = ""; //default
	var GSA_PORT = ""; //default
	var GSA_PROXY = "";
	var GSA_SERVICE = "SearchService.svc"; //no change, constant
	
	var GSA_DEFAULT_FRONTEND = '';
	var GSA_DEFAULT_COLLECTION = '';
	
	/**
	 * store hashcode in url for history
	 */
	var GSA_HASH = "gsa_hash";
	
	var WRAP_SERVICE_SEARCH = "simple"; 
	var WRAP_SERVICE_SUGGEST = "suggest";
	var WRAP_SERVICE_CLUSTER = "cluster";
	
	var GSA_SERVICE_SEARCH = "search"; 
	var GSA_SERVICE_SUGGEST = "suggest";
	var GSA_SERVICE_CLUSTER = "cluster";
	
	//Filter search
	var FILTER_SEARCH_LINK_CSS = "gsa_filters";
	var FILTER_SEARCH_LINK_ACTIVE_CSS = "active";
	var FILTER_NAME = "gsa_filter";
	var FILTER_NAME_FIELD = "NM";
	var FILTER_VALUE_FIELD = "V";
	var FILTER_ALL_VALUE = '';
	var HIDE_ZERO_FILTER = false;
	var FILTER_SELECT_ID = "gsa_filters_select";
	
	var SERVICE_BASE_URL = "";
	
	var SERVICE_SEARCH_URL = "";
	var SERVICE_SUGGEST_URL = "";
	var SERVICE_CLUSTER_URL = "";
	
	//Collection search
	var COLLECT_SEARCH_LINK_CSS = "gsa_collections";
	var COLLECT_SEARCH_LINK_ACTIVE_CSS = "active";
	
	//Message box
	var GSA_MSG_DIV_ID = "gsa_onigroup_msg_div";
	var GSA_PAGE_CONTAINER = "";
	var GSA_PAGE_NAV_LINK_CSS = "gsa_page_nav"; //default
	var GSA_UPDATE_SEARCH_LINK_CLASS = "gsa_update_search_link"; // default
	var GSA_UPDATE_DNAV_LINK_CSS = "gsa_update_dnav_link";// default
	var GSA_UPDATE_GENERAL_LINK_CSS = "gsa_update_link";
	var GSA_SEARCH_FORMS = null;
	var GSA_SEARCH_DEFAULT_FORM = null;
	var GSA_CUSTOM_POST_ACTION = new Array();
	var GSA_SUGGEST_FIELDS_CSS = null;
	
	//cluster
	var GSA_CLUSTER_CONTAINER_ID = null;
	var GSA_CLUSTER_ITEM_CSS = null;
	var GSA_CLUSTER_QUERY_ID = null;
	
	//dynamic navigation
	var GSA_DYN_NAV_DIV_ID = null;
	var GSA_DYN_NAV_FRONTEND = null;
	
	var GSA_DYN_NAV_BY_SITE = false;
	
	com.onigroup.util.compitableIE8();
	
	var Base64 = {_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/\r\n/g,"\n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}};
	
	function querySuggest(request, response){
		com.onigroup.gsa.GSAManager.querySuggest(request, response, com.onigroup.gsa.GSAManager);
	}
	
	
	
	/**
	 * Client core class for Search service
	 * @returns {GSAManager}
	 */
	function GSAManager(){
		var currentContainerID = "";
		var initialized = false;
		var collectionSearchInHeader = false;
		var lastSearchParamString = null;
		var savingHistory = false;
		
		var dataCache = new Array();
		var usingCachedDataDyn = false;
		var filterSearchInHeader = false;
		//Cache keys
		var CACHE_KEY_DYN_DATA = "cache_dyn_data";
		
		
		var _logger = new com.onigroup.util.logger();
		/**
		 * this method must be called before doing search request
		 * Version 0.7
		 */
		this.initGSAService = function(){
			if(initialized){
				return;
			}
			
			// change protocal and domain to the one in use
			if (com.onigroup.gsa.settings.gsa_use_current_baseurl) {
				GSA_PROTOCAL = location.protocol.indexOf(":") > 0 ? location.protocol.replace(":", "") : location.protocol;
				GSA_HOST = location.host;
				if (com.onigroup.gsa.settings.gsa_use_path) {
					GSA_HOST += com.onigroup.gsa.settings.gsa_use_path;
				}
			} else {
				GSA_PROTOCAL = com.onigroup.gsa.settings.gsa_protocal;
				GSA_HOST = com.onigroup.gsa.settings.gsa_host;
				GSA_PORT = com.onigroup.gsa.settings.gsa_port;
			}
			
			GSA_PROXY = com.onigroup.gsa.settings.gsa_proxy;
			GSA_RESOURCE_PATH = com.onigroup.gsa.settings.gsa_resource_path;
			
			GSA_CLUSTER_CONTAINER_ID = com.onigroup.gsa.settings.gsa_cluster_container_id;
			GSA_CLUSTER_ITEM_CSS = com.onigroup.gsa.settings.gsa_cluster_item_css;
			GSA_CLUSTER_QUERY_ID = com.onigroup.gsa.settings.gsa_cluster_query_id;
			
			GSA_DYN_NAV_DIV_ID = com.onigroup.gsa.settings.gsa_dyn_nav_id;
			GSA_DYN_NAV_FRONTEND = com.onigroup.gsa.settings.frontend_no_filter;
			
			if(com.onigroup.gsa.settings.gsa_dyn_nav_type == 'site'){
				GSA_DYN_NAV_BY_SITE = true;
			}
			
			
			if(GSA_HOST != "" && GSA_HOST != undefined){ //not in same site with the current page
				SERVICE_BASE_URL = GSA_PROTOCAL + "://" + GSA_HOST;
				if(GSA_PORT != ''){
					SERVICE_BASE_URL += ":" + GSA_PORT;
				}
			}
			if(GSA_PROXY != "" && GSA_PROXY != undefined){
				SERVICE_BASE_URL += GSA_PROXY;
			}
			
			if(com.onigroup.gsa.settings.enable_wrapper){
				SERVICE_BASE_URL += "/" + GSA_SERVICE + "/";
				SERVICE_SEARCH_URL = SERVICE_BASE_URL + WRAP_SERVICE_SEARCH + "?";
				SERVICE_SUGGEST_URL = SERVICE_BASE_URL + WRAP_SERVICE_SUGGEST + "?";
				SERVICE_CLUSTER_URL = SERVICE_BASE_URL + WRAP_SERVICE_CLUSTER + "?";
				
			}else{
				SERVICE_BASE_URL += "/";
				SERVICE_SEARCH_URL = SERVICE_BASE_URL + GSA_SERVICE_SEARCH + "?";
				SERVICE_SUGGEST_URL = SERVICE_BASE_URL + GSA_SERVICE_SUGGEST + "?";
				SERVICE_CLUSTER_URL = SERVICE_BASE_URL + GSA_SERVICE_CLUSTER + "?";
				
				//GSA_DEFAULT_FRONTEND
				GSA_DEFAULT_FRONTEND = com.onigroup.gsa.settings.frontend;
				
				//GSA_DEFAULT_COLLECTION
				GSA_DEFAULT_COLLECTION = com.onigroup.gsa.settings.collection;
			}
			
			//set GSA_PAGE_CONTAINER
			GSA_PAGE_CONTAINER = com.onigroup.gsa.settings.gsa_container_id;
			
			if(com.onigroup.gsa.settings.gsa_page_nav_link_css != null && com.onigroup.gsa.settings.gsa_page_nav_link_css != ''){
				GSA_PAGE_NAV_LINK_CSS = com.onigroup.gsa.settings.gsa_page_nav_link_css;
			}
			if (com.onigroup.gsa.settings.gsa_update_dnav_link_css != null && com.onigroup.gsa.settings.gsa_update_dnav_link_css != '') {
				GSA_UPDATE_DNAV_LINK_CSS = com.onigroup.gsa.settings.gsa_update_dnav_link_css;
			}
			if (com.onigroup.gsa.settings.gsa_update_general_link_css != null && com.onigroup.gsa.settings.gsa_update_general_link_css != '') {
				GSA_UPDATE_GENERAL_LINK_CSS = com.onigroup.gsa.settings.gsa_update_general_link_css;
			}
			
			if(com.onigroup.gsa.settings.gsa_update_search_link_css != null && com.onigroup.gsa.settings.gsa_update_search_link_css != ''){
				GSA_UPDATE_SEARCH_LINK_CLASS = com.onigroup.gsa.settings.gsa_update_search_link_css;
			}
			
			if(com.onigroup.gsa.settings.gsa_collection_search_css != null && com.onigroup.gsa.settings.gsa_collection_search_css != ''){
				COLLECT_SEARCH_LINK_CSS = com.onigroup.gsa.settings.gsa_collection_search_css;
			}
			
			if(com.onigroup.gsa.settings.gsa_collection_search_active_css != null && com.onigroup.gsa.settings.gsa_collection_search_active_css != ''){
				COLLECT_SEARCH_LINK_ACTIVE_CSS = com.onigroup.gsa.settings.gsa_collection_search_active_css;
			}
			
			//filter parameters
			if(com.onigroup.gsa.settings.gsa_filter_search_css != null && com.onigroup.gsa.settings.gsa_filter_search_css != ''){
				FILTER_SEARCH_LINK_CSS = com.onigroup.gsa.settings.gsa_filter_search_css;
			}
			
			if(com.onigroup.gsa.settings.gsa_filter_search_active_css != null && com.onigroup.gsa.settings.gsa_filter_search_active_css != ''){
				FILTER_SEARCH_LINK_ACTIVE_CSS = com.onigroup.gsa.settings.gsa_filter_search_active_css;
			}
			
			if(com.onigroup.gsa.settings.gsa_filter_name != null && com.onigroup.gsa.settings.gsa_filter_name != ''){
				FILTER_NAME = com.onigroup.gsa.settings.gsa_filter_name;
			}
			if(com.onigroup.gsa.settings.hide_zero_filter != null && com.onigroup.gsa.settings.hide_zero_filter != ''){
				HIDE_ZERO_FILTER = com.onigroup.gsa.settings.hide_zero_filter;
			}
			
			if(com.onigroup.gsa.settings.gsa_filter_all_value != null && com.onigroup.gsa.settings.gsa_filter_all_value != ''){
				FILTER_ALL_VALUE = com.onigroup.gsa.settings.gsa_filter_all_value;
			}
			if(com.onigroup.gsa.settings.gsa_filter_select_id != null && com.onigroup.gsa.settings.gsa_filter_select_id != ''){
				FILTER_SELECT_ID = com.onigroup.gsa.settings.gsa_filter_select_id;
			}
	 
			//set fields which support suggest
			if(com.onigroup.gsa.settings.gsa_suggest_fields_css != null && com.onigroup.gsa.settings.gsa_suggest_fields_css != ''){
				GSA_SUGGEST_FIELDS_CSS = com.onigroup.gsa.settings.gsa_suggest_fields_css.split(",");
			}
			//set search forms
			if(com.onigroup.gsa.settings.gsa_search_forms != null && com.onigroup.gsa.settings.gsa_search_forms != ''){
				GSA_SEARCH_FORMS = com.onigroup.gsa.settings.gsa_search_forms.split(",");
			}
			
			//set default form
			GSA_SEARCH_DEFAULT_FORM = com.onigroup.gsa.settings.gsa_search_defalut_form;
			if(GSA_SEARCH_DEFAULT_FORM == null || GSA_SEARCH_DEFAULT_FORM == ''){
				GSA_SEARCH_DEFAULT_FORM = 'suggestion_form';
			}
			currentContainerID = GSA_PAGE_CONTAINER; //default
			
			if($("." + COLLECT_SEARCH_LINK_CSS).length > 0){
				collectionSearchInHeader = true;
				this._updateCollectionsSearchLink(this);
			}
			if($("." + FILTER_SEARCH_LINK_CSS).length > 0){
				filterSearchInHeader = true;
				this._updateFiltersSearchLink(this);
			}
			
			initialized = true;
		};
		
		this._setCache = function(key, value){
			dataCache[key] = value;
		};
		
		this._getCache = function(key){
			try{
				return dataCache[key];
			}catch(e){
				return null;
			}
		};
		
		this.cacheDynNavData = function(context, data){
			context._setCache(CACHE_KEY_DYN_DATA, data);
		};
		
		this.getCachedDynNavData = function(context){
			return context._getCache(CACHE_KEY_DYN_DATA);
		};
		
		this._updateFiltersSelect = function(context) {
			var lastParamObj =  context.getLastSearchParam();
			var lastFilter = lastParamObj.getFilter();
			//set the current one, if it is not empty, clear all
			if( lastFilter != null && lastFilter.length > 0){
				$("." + FILTER_SEARCH_LINK_CSS).each(function(){
					
					var filterName = $(this).attr(FILTER_NAME_FIELD);
					if(filterName == null || filterName.length == 0){
						filterName = FILTER_NAME;
					}
					var currentFilter = filterName.replace(".", "%252E") + ":" + $(this).attr(FILTER_VALUE_FIELD); // filter
					if(currentFilter == lastFilter){
						// add to parent element li <li class="active"...
						$(this).prop('selected', true);
					}//end if
				});//end each
			}//end if
			
			var that = this;
			$("#" + FILTER_SELECT_ID).change(function() {
				var selectedOption = $(this).find("option:selected");
				
				var filter = selectedOption.attr(FILTER_VALUE_FIELD); // filter
				var filterName = selectedOption.attr(FILTER_NAME_FIELD);
				if(filterName == null || filterName.length == 0){
					filterName = FILTER_NAME;
				}
				var paramObj =  context.getLastSearchParam();
				if(FILTER_ALL_VALUE == filter){
					paramObj.setFilter(null);
				}else{
					paramObj.setFilter(filterName.replace(".", "%252E") + ":" + com.onigroup.util.htmlEncode(filter));
				}
				
				paramObj.setPage(1);
				
				context.doSearch(paramObj);
			}); 
			
	 
		};
		
		this._updateFiltersSearchLink = function(context){
			//not in header, then need to update UI after loading
			if(filterSearchInHeader == false){
				var lastParamObj =  context.getLastSearchParam();
				var lastFilter = lastParamObj.getFilter();
				//set the current one, if it is not empty, clear all
				if( lastFilter != null && lastFilter.length > 0){
					$("." + FILTER_SEARCH_LINK_CSS).each(function(){
						$(this).parent().removeClass(FILTER_SEARCH_LINK_ACTIVE_CSS);
						var filterName = $(this).attr(FILTER_NAME_FIELD);
						if(filterName == null || filterName.length == 0){
							filterName = FILTER_NAME;
						}
						var currentFilter = filterName + ":" + $(this).attr(FILTER_VALUE_FIELD); // filter
						if(currentFilter == lastFilter){
							// add to parent element li <li class="active"...
							$(this).parent().addClass(FILTER_SEARCH_LINK_ACTIVE_CSS);
						}//end if
					});//end each
				}//end if
			}//end if
			
			var that = this;
			$("." + FILTER_SEARCH_LINK_CSS).each(function(){
				$(this).click(function(event){
					//remove all active css 
					$("." + FILTER_SEARCH_LINK_CSS).each(function(){
						$(this).parent().removeClass(FILTER_SEARCH_LINK_ACTIVE_CSS);
					});
					$(this).parent().addClass(FILTER_SEARCH_LINK_ACTIVE_CSS);
					com.onigroup.util.preventDefault(event);
					var filter = $(this).attr(FILTER_VALUE_FIELD); // filter
					var filterName = $(this).attr(FILTER_NAME_FIELD);
					if(filterName == null || filterName.length == 0){
						filterName = FILTER_NAME;
					}
					var paramObj =  context.getLastSearchParam();
					if(FILTER_ALL_VALUE == filter){
						paramObj.setFilter(null);
					}else{
						paramObj.setFilter(filterName.replace(".", "%252E") + ":" + com.onigroup.util.htmlEncode(filter));
					}
					
					paramObj.setPage(1);
					
					//that._keepAsSitesearchAndGSAFilter(paramObj);
					
					context.doSearch(paramObj);
					usingCachedDataDyn = true;
					return false;
				});
			}); //end each
		};
		
		this.getProxy = function(){
			return GSA_PROXY;
		};
		
		this.getGSAResourcePath = function(){
			return GSA_RESOURCE_PATH;
		};
		

		this._updateCollectionsSearchLink = function(context, paramObj){
			$("." + COLLECT_SEARCH_LINK_CSS).each(function(){
				$(this).click(function(event){
					//remove all active css 
					$("." + COLLECT_SEARCH_LINK_CSS).each(function(){
						$(this).removeClass(COLLECT_SEARCH_LINK_ACTIVE_CSS);
					});
					$(this).addClass(COLLECT_SEARCH_LINK_ACTIVE_CSS);
					com.onigroup.util.preventDefault(event);
					var site = $(this).attr("site"); // collection
					var paramObj =  context.getLastSearchParam();
					paramObj.setCollection(site);
					context.doSearch(paramObj);
					
					return false;
				});
			});
			
			//change css for active
			if(paramObj != null){
				var currentSite = paramObj.getCollection();
				$("." + COLLECT_SEARCH_LINK_CSS).each(function(){
					var siteInLink = $(this).attr("site"); // collection
					if(siteInLink == null || siteInLink == undefined){
						siteInLink = '';
					}
					$(this).removeClass(COLLECT_SEARCH_LINK_ACTIVE_CSS);
					if(siteInLink == currentSite || (siteInLink == '' && currentSite == GSA_DEFAULT_COLLECTION)){
						$(this).addClass(COLLECT_SEARCH_LINK_ACTIVE_CSS);
					}
				});
			}
		};
		
		this._updateAfterShowContent = function(context, paramObj){
			//always show top
			window.scrollTo(0,0);
			
			if(context == null){
				context = com.onigroup.gsa.GSAManager;
			}
			//update form
			context._updateFormSubmit(context);
			
			//update page nav links
			context._updatePageNavLink(context);
			
			//update classic dynamic navigation links
			//context._updateDynamicNavLink(context);
			
			//update dynamic nav links
			//context._updateDynNavLinks(context);
			
			//update other links
			context._updateGeneralLink(context);
			
			//call customized post actions
			context._callRegisteredPostActions(paramObj);
			
			//init suggest
			context._initSuggest(context);
			
			//get cluster TODO: /cluster cross domain not working for post
			if (GSA_CLUSTER_CONTAINER_ID != null) {
				context.getCluster();
			}
			
			//init collection search
			/*if(collectionSearchInHeader == false){
				context._updateCollectionsSearchLink(context, paramObj);
			}*/
			
			context._updateFiltersSelect(context);
		
			//get dynamic data
			//context._queryDynNavData(context);
			
		};
		
		this.getDefaultQueryParam = function(){
			var paramObj =  com.onigroup.util.buildSearchParamFromGSAURL( "?" + $('#' +  GSA_SEARCH_DEFAULT_FORM).serialize());
			return paramObj;
		};

		this._updateFormSubmit = function(context){
			if(GSA_SEARCH_FORMS != null && GSA_SEARCH_FORMS.length > 0){
				for(var i = 0; i < GSA_SEARCH_FORMS.length; i++){
					$("#" + GSA_SEARCH_FORMS[i]).submit(function( event ) {
						com.onigroup.util.preventDefault(event);
						var paramObj =  com.onigroup.util.buildSearchParamFromGSAURL( "?" + $(this).serialize());
						paramObj.setPage(1);
						
						context.doSearch(paramObj);
					});
				}
			}
			
		};
		
		//use GSA default xslt page navigation link
		this._updatePageNavLink = function(context){
			//GSA_PAGE_NAV_LINK_CSS
			$("." + GSA_PAGE_NAV_LINK_CSS).each(function(){
				$(this).click(function(event){
					com.onigroup.util.preventDefault(event);
					var url = $(this).attr("href");
					var paramObj =  com.onigroup.util.buildSearchParamFromGSAURL(url);
					var page = paramObj.getPage();
					var lastParam = context.getLastSearchParam();
					lastParam.setPage(page);
					lastParam.setStart(paramObj.getStart());
					lastParam.setOrigURL(url);
					lastParam.setDNAVS(paramObj.getDNAVS());
					context.doSearch(lastParam);
					return false;
				});
			});
		};
		
		// classic dynamic navigation and other links
		this._updateDynamicNavLink = function(context){

			$("." + GSA_UPDATE_DNAV_LINK_CSS).each(function(){
				$(this).click(function(event){
					com.onigroup.util.preventDefault(event);
					var url = $(this).attr("href");
					var paramObj =  com.onigroup.util.buildSearchParamFromGSAURL(url);
					
					paramObj.setOrigURL(url);
					context.doSearch(paramObj);
					return false;
				});
			});
		};
		
		this._updateGeneralLink = function(context){

			$("." + GSA_UPDATE_GENERAL_LINK_CSS).each(function(){
				$(this).click(function(event){
					com.onigroup.util.preventDefault(event);
					var url = $(this).attr("href");
					var paramObj =  com.onigroup.util.buildSearchParamFromGSAURL(url);
					
					paramObj.setOrigURL(url);
					paramObj.setAddAllParams(true);
					context.doSearch(paramObj);
					
					return false;
				});
			});
		};
		
		this._updateSearchLink = function(context, linkItem, paramObj){
			$(linkItem).click(function(event){
				com.onigroup.util.preventDefault(event);
				var url = $(linkItem).attr("href");
				if(paramObj == null){
					paramObj =  com.onigroup.util.buildSearchParamFromGSAURL(url);
				}
				paramObj.setOrigURL(url);
				context.doSearch(paramObj);
				return false;
			});
		};
		
		
		this.searchOnLoad = function(){
			
			this.clearMsg();
			
			if(this.getContainer().length == 0){
				return;
			}
			
			//build search parameter object and do search
			var paramObj = com.onigroup.util.buildSearchParamFromURL();
			this.doSearch(paramObj);
			
		};
		
		this._handleAjaxError = function(jqXHR, textStatus, errorThrown, context){
			console.log("ajax call error:" + errorThrown);
		};
		
		this.showErrorMsg = function(msg, type){
			_logger.error(msg);
			//use jQuery dialog
//			if($('#' +  GSA_MSG_DIV_ID).length == 0){
//				$(body).append('<div id="' + GSA_MSG_DIV_ID + '" style="display:none" title="Message"/>');
//			}
//			$('#' +  GSA_MSG_DIV_ID).text(msg);
//			$('#' +  GSA_MSG_DIV_ID).dialog({
//				modal: true,
//				dialogClass: "no-close",
//				buttons: [
//				    {
//				      text: "OK",
//				      click: function() {
//				        $( this ).dialog( "close" );
//				      }
//				    }
//				  ]
//			});
		};
		
		this.clearMsg = function(){
			//$('#' +  GSA_MSG_DIV_ID).dialog("close");
		};
		
		this.getContainer =  function(){
			return $("#" + currentContainerID);
		};
		
		this.showContent = function(container, data){
			container.html(data);
		}
		
		this._showContentOnly = function(data,context, parameterObj){
			try{
				var container = context.getContainer();
				if(container.length > 0){
					context.showContent(container, data);
				}
				
			}catch(e){
				//even there is any html error, let the program run.
				//console.log(e);
			}
		};
		this._showContent = function(data,context, parameterObj){
			try{
				context._showContentOnly(data,context, parameterObj);
				
				context._updateAfterShowContent(context,parameterObj);
			}catch(e){
				//even there is any html error, let the program run.
				//console.log(e);
			}
		};
		
		this._showCluster = function(data, context, parameterObj){
			if(parameterObj == null){
				parameterObj = context.getLastSearchParam();
			}
			//
			parameterObj.setPage(null);
			
			var container = $("#" + GSA_CLUSTER_CONTAINER_ID);
			//@@@@Step 1: show cluster data
			if(data.clusters && data.clusters[0] && data.clusters[0].clusters){
				//1.1: set title
				var title =  parameterObj.getQuery();
				$("#" +  GSA_CLUSTER_QUERY_ID).html(title);
				
				//1.2 fill items
				var clusters = data.clusters[0].clusters;
				var maxLength = $("." + GSA_CLUSTER_ITEM_CSS).length;
				var i = 0;
				for(i = 0; i < clusters.length && i < maxLength; i++){
					var text = clusters[i].label;
					//sample <li><a href="#" class="gsa_update_search_link">tax <b>rate calculator</b></a>
					$($("." + GSA_CLUSTER_ITEM_CSS)[i]).html(text);
				}
				
				//1.3 hide extra items
				for(i; i < maxLength; i++){
					$($("." + GSA_CLUSTER_ITEM_CSS)[i]).parent().hide();
				}
				container.show();
			}else{
				container.hide();
			}
			
			//@@@@Step 2: bind search event for related search
			$("." + GSA_UPDATE_SEARCH_LINK_CLASS).each(function(){
				var searchTerm = $(this).text();
				searchTerm = com.onigroup.util.htmlEncode(searchTerm);
				$(this).attr('href', "?" + com.onigroup.gsa.constants.P_GSA_NAME_QUERY + '=' + searchTerm);
				context._updateSearchLink(context, this, null);
			});
			
		}
		
		
		/**
		 * do search and show result in the html element which id is containerId.
		 * if containerId is null, then use GSA_PAGE_CONTAINER 
		 */
		this.doSearch = function(paramObj, containerId){
			// disable gsa filter
			paramObj.setGSAFilter('0');
			/*if (paramObj.getRequiredFields() 
					&& (paramObj.getRequiredFields().indexOf(":") > 0 || 
						paramObj.getRequiredFields().indexOf(":") > 0) ) {
				paramObj.setGSAFilter('0');
			} */
			
			if(containerId == null || containerId == ''){
				containerId = GSA_PAGE_CONTAINER;
			}
			currentContainerID = containerId;
			var url = paramObj.toURL();
			if(url.indexOf("?") >= 0){
				lastSearchParamString = url;
			}else{
				lastSearchParamString = "?" + url;
			}
			
			this._ajaxCallSearch(paramObj); 
			
		};
		this.getLastSearchParam = function(){
			var lastSearchParam = new com.onigroup.gsa.SearchParam();
			lastSearchParam = lastSearchParam.init(lastSearchParamString);
			return lastSearchParam;
		};
		this._doSearchWithSuggest = function(context,q){
			var paramObj = context.getDefaultQueryParam();
			paramObj.setQuery(q);
			context.doSearch(paramObj);
		};
		
		this._queryDynNavData = function(context){
			if($("#" + GSA_DYN_NAV_DIV_ID).length == 0){
				return;
			}
			
			var paramObj = context.getLastSearchParam();
			paramObj.setPage(1);
			paramObj.setFilter(null);
			
			// add as_sitesearch filter
			//this._keepAsSitesearchAndGSAFilter(paramObj);
			
			if(GSA_DYN_NAV_BY_SITE){
				$("." + FILTER_SEARCH_LINK_CSS).each(function(){
					var site = $(this).attr('site');
					paramObj.setFrontend(com.onigroup.gsa.settings.gsa_collection_num_frontend);
					paramObj.setCollection(site);
					context.doSimpleSearch(paramObj, context._showDynNavDataBySite);
				});
				
			}else{
				if(GSA_DYN_NAV_FRONTEND == null || GSA_DYN_NAV_FRONTEND == ''){
					context.showErrorMsg('No frontend for Dynamic Navigation data query, please check you configuration.');
					return;
				}
				paramObj.setFrontend(GSA_DYN_NAV_FRONTEND);
				if(usingCachedDataDyn){
					context._showDynNavData(context.getCachedDynNavData(context), context);
				}else{
					this.doSimpleSearch(paramObj, context._showDynNavData);
				}
				
			}
			//reset to false
			usingCachedDataDyn = false;
			
		};
		
		this._showDynNavDataBySite = function(data, context, parameterObj){
			//@@@@Step 1: get the parameters
			if(parameterObj == null){
				parameterObj = context.getLastSearchParam();
			}
			
			//@@@@Step 2: parse the json data from the backend
			if(data){
				$("." + FILTER_SEARCH_LINK_CSS).each(function(){
					var obj = $.parseJSON(data);
					if(obj.count == ''){
						obj.count = 0;
					}
					var site = obj.site;
					var nodeSite =  $(this).attr('site');
					if(site ==  nodeSite){
						var text = $(this).text();
						if(com.onigroup.gsa.settings.gsa_filter_show_count){
							text = text + " (" + obj.count + ")";
						}
						$(this).html(text);
					}
				});
			}
			//@@@@Step 3: show the collections
			$("#" + GSA_DYN_NAV_DIV_ID).show();
		};
		
		/**
		 * 
		 * "NM": "Country",
    		"PV": [
			      {
			        "V": "Australia",
			        "L": "",
			        "H": "",
			        "C": "117"
			      },
			      {
			        "V": "Canada",
			        "L": "",
			        "H": "",
			        "C": "117"
			      }
			      ]
		 */
		this._showDynNavData = function(data, context, parameterObj){
			//@@@@Step 1: get the parameters
			if(parameterObj == null){
				parameterObj = context.getLastSearchParam();
			}
			
			//@@@@Step 2: parse the json data from the backend
			if(data){
				//cached the data
				context.cacheDynNavData(context, data);
				$("." + FILTER_SEARCH_LINK_CSS).each(function(){
					var NM = $(this).attr(FILTER_NAME_FIELD);
					if(NM == null || NM.length == 0){
						NM = FILTER_NAME;
					}
					var V =  $(this).attr(FILTER_VALUE_FIELD);
					//read dynamic data and show them in the div
					var dataObj = $.parseJSON(data);
					var foundInResult = false;
					for(var i = 0; i < dataObj.length; i++){
						var oneEntity = dataObj[i];
						var entityNM = oneEntity[FILTER_NAME_FIELD];
						if(entityNM == NM){
							var pvArray = oneEntity["PV"];
							//C/H/L/V
							for(var j = 0; j < pvArray.length; j++){
								var onePV = pvArray[j];
								if(onePV[FILTER_VALUE_FIELD] == V){
									var count = onePV["C"];
									var text = $(this).text();
									if(com.onigroup.gsa.settings.gsa_filter_show_count){
										text = text + " (" + count + ")";
									}
									
									$(this).html(text);
									if(count.length > 1 || parseInt(count) > 0){
										//do nothing
									}else{
										if(HIDE_ZERO_FILTER){
											$(this).hide();
										}
									}
									foundInResult = true;
									break;
								}
							}//end for
						}//end if
						if(foundInResult){
							break;
						}
					}//end for
					if(foundInResult == false && HIDE_ZERO_FILTER){
						$(this).hide();
					}
				});//end each
			}//end if
			
			//@@@@Step 3: show the collections
			$("#" + GSA_DYN_NAV_DIV_ID).show();
		};
		
		
		this._updateDynNavLinks = function(context){
			//TODO..
			$("." + FILTER_SEARCH_LINK_CSS).each(function(){
				//$(this)
				var paramObj =  context.getLastSearchParam();
				$(this).removeClass(COLLECT_SEARCH_LINK_ACTIVE_CSS);
				if(GSA_DYN_NAV_BY_SITE){
					var site = $(this).attr("site"); // collection
					var currentSite = paramObj.getCollection();
					if(currentSite == site){
						$(this).addClass(COLLECT_SEARCH_LINK_ACTIVE_CSS);
					}
				}
				$(this).click(function(event){
					com.onigroup.util.preventDefault(event);
					if(GSA_DYN_NAV_BY_SITE){
						paramObj.setCollection(site);
						context.doSearch(paramObj);
					}else{//by metatag..
						//TODO..
					}
					return false;
				});
			});
		};
		
		
		this.querySuggest = function(request, response,context) {   
			//@@@@Step 1: get the url for suggest
			var paramObj =  com.onigroup.util.buildSearchParamFromGSAURL( "?" + $('#' +  GSA_SEARCH_DEFAULT_FORM).serialize());
			
			var suggestParam = new com.onigroup.gsa.SuggestParam();
			suggestParam.setQuery(request.term);
			suggestParam.setCollection(paramObj.getCollection());
			suggestParam.setFrontend(paramObj.getFrontend());
			
			//@@@@Step 2: build callback method
			var callback = function (data) {    
				  
				  var suggestions = [];
				  $.each(data, function(index, item) {
					  if ($.isArray(item)) {
						  $.each(item, function(index, suggest) {
							  if (suggest != null && suggest != '') {
								  suggestions.push(suggest);
							  }
						  });
					  }
				  });
				  response($.map(suggestions, 
						         function (item) { 
					  				var searchTerm = $.trim(request.term);
					  				searchTerm = searchTerm.replace(/[\<\>]/g, "");
					  				item = com.onigroup.util.decodeHex(item);
									var label = "<b>" + searchTerm + "</b>" + (searchTerm.length < item.length ? item.substr(searchTerm.length) : "");
									return {   label: label, value: item  };
								 })); 
			};
			
			//@@@@Step 3: do ajax call to get suggest
			context._ajaxCall(GSA_SERVICE_SUGGEST,suggestParam, callback);
		};
		
		this.querySuggestWithCallback = function(request, callback, context) {   
			//@@@@Step 1: get the url for suggest
			var paramObj =  com.onigroup.util.buildSearchParamFromGSAURL( "?" + $('#' +  GSA_SEARCH_DEFAULT_FORM).serialize());
			
			var suggestParam = new com.onigroup.gsa.SuggestParam();
			suggestParam.setQuery(request.term);
			suggestParam.setCollection(paramObj.getCollection());
			suggestParam.setFrontend(paramObj.getFrontend());
			
//			
			
			//@@@@Step 3: do ajax call to get suggest
			context._ajaxCall(GSA_SERVICE_SUGGEST,suggestParam, callback);
		};
		
		//@paramObj if it is null, then ust the param for last search. 
		this.getCluster = function(paramObj){
			//check if GSA_CLUSTER_CONTAINER_ID exist
			if($("#" + GSA_CLUSTER_CONTAINER_ID).length == 0){
				return;
			}
			var clusterParam = null;
			if(paramObj == null){
				clusterParam = this.getLastSearchParam();
			}else{
				clusterParam = paramObj;
			}
			this._ajaxCall(GSA_SERVICE_CLUSTER,clusterParam, this._showCluster);
		};
		//specially modfied for startrack
		this._initSuggest = function(context){
			
			if(GSA_SUGGEST_FIELDS_CSS != null){
				for(var i = 0; i < GSA_SUGGEST_FIELDS_CSS.length;i++){
					$("." + GSA_SUGGEST_FIELDS_CSS[i]).each(function(){
						var autoComplete = $(this).autocomplete({  
							  appendTo: $(this).parent().parent(), 
							  delay:300,        
							  source: function (request, response) { querySuggest(request, response);},        
							  messages: {noResults: '',results: function() {}},
							  minLength: 1,
							  select:function(event, ui){
								  context._doSearchWithSuggest(context,ui.item.value);
								  //add more function here in the future
							  }
						});
				
						if (autoComplete.data("ui-autocomplete")) {
							autoComplete.data("ui-autocomplete")._renderMenu = function( ul, items ) {
								var that = this;
								$(ul).addClass("search-ac-results");
								$(ul).addClass("inline-results");
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
								$(ul).addClass("inline-results");
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
						
				         //end autocomplete
					});//end each
				}//end for
			}//end if
		};//end _initSuggest
		
		/**
		 * do search without caring about history
		 */
		this.doSimpleSearch = function(paramObj,successCallback,errorCallback){
			this._ajaxCall(GSA_SERVICE_SEARCH, paramObj, successCallback,errorCallback);
		};
		
		this._ajaxCallSearch = function(paramObj,successCallback,errorCallback){
			var url = this._ajaxCall(GSA_SERVICE_SEARCH, paramObj, successCallback,errorCallback);
			this.saveHistoryForSearch(url);
		};
		this._getServiceBaseURL = function(service){
			var serviceBaseURL = null;
			if(service == GSA_SERVICE_SEARCH){
				serviceBaseURL = SERVICE_SEARCH_URL;
			}else if(service == GSA_SERVICE_SUGGEST){
				serviceBaseURL = SERVICE_SUGGEST_URL;
			}else{
				serviceBaseURL = SERVICE_CLUSTER_URL;
			}
			return serviceBaseURL;
		};
		
		this._ajaxCall = function(service, parameterObj, successCallback, errorCallback){
			var context = this;
			if(successCallback == undefined || successCallback == null){
				successCallback = this._showContent;
			}
			if(errorCallback == undefined || successCallback == null){
				errorCallback = this._handleAjaxError;
			}
			var ajaxParam = null;
			
			
			if(com.onigroup.gsa.settings.enable_wrapper){
				ajaxParam = this._ajaxCallWrap(service, parameterObj);
			}else{
				ajaxParam = this._ajaxCallGSA(service, parameterObj);
			}
			
			//to avoid jquery to add timestamp for script tag
			$.ajaxPrefilter('script', function(options) { options.cache = true; });
			
			//do ajax call
			var url = ajaxParam.getURL();
			var dataType = ajaxParam.getDataType();
			var requestType = ajaxParam.getType();
			var queryData = ajaxParam.getData();
			
			// check wrapper proxy usage
			if (com.onigroup.gsa.settings.wrapper_proxy && com.onigroup.gsa.settings.wrapper_proxy != "") {
				url = com.onigroup.gsa.settings.wrapper_proxy + com.onigroup.util.htmlEncode(url);
			}
			$.ajax({
				url:  url,
				dataType: dataType,
				type: requestType,
				data : queryData,
				success: function(data) {
					var outputData = null;
					if(dataType == com.onigroup.util.constants.AJAX_DATA_TYPE_JSONP && data.data != undefined){
						outputData = data.data;
					}else{
						outputData = data;
					}
					successCallback(outputData, context, parameterObj, dataType);
				},
				error:function(jqXHR, textStatus, errorThrown){
					errorCallback(jqXHR, textStatus, errorThrown, context, parameterObj);
				}
	  		});
			
			return ajaxParam.getURL();
		};
		
		this._ajaxCallWrap =  function(service,parameterObj){
			var result = new com.onigroup.gsa.AjaxParam();
			var dataType = com.onigroup.util.constants.AJAX_DATA_TYPE_JSONP;
			var requestType = 'GET';
			var serviceBaseURL = this._getServiceBaseURL(service);
			var url = serviceBaseURL + parameterObj.toURL();
			var queryData = {};
			
			result.setURL(url);
			result.setData(queryData);
			result.setDataType(dataType);
			result.setType(requestType);
			
			return result;
		}
		
		/**
		 * TODO
		 */
		this._ajaxCallGSA = function(service,parameterObj){
			var result = new com.onigroup.gsa.AjaxParam();
			//check default value
			if(parameterObj.getCollection() == ''){
				parameterObj.setCollection(GSA_DEFAULT_COLLECTION);
			}
			if(parameterObj.getFrontend() == ''){
				parameterObj.setFrontend(GSA_DEFAULT_FRONTEND);
			}
			
			var serviceBaseURL = this._getServiceBaseURL(service);
			var dataType = com.onigroup.util.constants.AJAX_DATA_TYPE_JSON;
			var requestType = 'GET';
			var url = '';
			var queryData = {};
			if(service == GSA_SERVICE_SEARCH){
				dataType = com.onigroup.util.constants.AJAX_DATA_TYPE_HTML;
				url = serviceBaseURL + parameterObj.toURL();
			}else if(service == GSA_SERVICE_CLUSTER){
				dataType = com.onigroup.util.constants.AJAX_DATA_TYPE_JSON;
				requestType = 'POST';
				url = serviceBaseURL + parameterObj.toURL();
				//queryData = parameterObj;
			}else if(service == GSA_SERVICE_SUGGEST){ //ING
				dataType = com.onigroup.util.constants.AJAX_DATA_TYPE_JSONP;
				url = serviceBaseURL + parameterObj.toURL();
			}
			
			result.setURL(url);
			result.setData(queryData);
			result.setDataType(dataType);
			result.setType(requestType);
			
			return result;
		};
		
		/**
		 * this actions will be triggered when search result is loaded
		 */
		this.registerPostAction = function (func){
			if($.inArray(func, GSA_CUSTOM_POST_ACTION) < 0){
				GSA_CUSTOM_POST_ACTION.push(func);
			}
		};
		
		this._callRegisteredPostActions = function(paramObj){
			for(var i = 0; i < GSA_CUSTOM_POST_ACTION.length; i++){
				GSA_CUSTOM_POST_ACTION[i](paramObj);
			}
		};
		
		this.saveHistoryForSearch = function(searchURL){
			var tempArray = searchURL.split('?');
			savingHistory = true;
			History.pushState(null, null, '?' + tempArray[1]);
			savingHistory = false;
		};
		
		this.isSavingHistory = function(){
			return savingHistory;
		};
		
		return this;
	}
	
	com.onigroup.gsa.GSAManager = new GSAManager();
})();

$(document).ready(function(){
	com.onigroup.util.initHistory();
	com.onigroup.gsa.GSAManager.initGSAService();
	com.onigroup.gsa.GSAManager.searchOnLoad();
});


})( (com.onigroup.gsa.settings.gsa_jquery_noconflict && com.onigroup.gsa.settings.gsa_jquery_noconflict != "") ? eval(com.onigroup.gsa.settings.gsa_jquery_noconflict) : window.$);





