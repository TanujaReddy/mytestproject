var com = com || {};
com.onigroup = com.onigroup || {};
com.onigroup.gsa = com.onigroup.gsa || {};
com.onigroup.util = com.onigroup.util || {};
com.onigroup.gsa.constants = com.onigroup.gsa.constants || {};
com.onigroup.gsa.settings = com.onigroup.gsa.settings || {};

//false: not use wrapper; true: use search wrapper
com.onigroup.gsa.settings.enable_wrapper = false;
//com.onigroup.gsa.settings.gsa_jquery_noconflict='onigroupJQuery';


//collection to use
//com.onigroup.gsa.settings.collection = 'default_collection'; //Test Collection, switch back to startrack
//frontend name
//com.onigroup.gsa.settings.frontend = 'StarTrack';

//html element to show search content
com.onigroup.gsa.settings.gsa_container_id = 'search_container';

//protocal for gsa search service/wrapper (http or https)
com.onigroup.gsa.settings.gsa_protocal = location.protocol.replace(":","");

//host for gsa search service/wrapper(ip or domain name)
//com.onigroup.gsa.settings.gsa_host = "122.100.3.57";

//port for gsa search service/wrapper(if empty, then 80 will be used)
//com.onigroup.gsa.settings.gsa_port = "80";

// css class for header search box, autocomplete will be added based on this
com.onigroup.gsa.settings.banner_search_css = "banner-search";
// css class for header search button
com.onigroup.gsa.settings.banner_search_btn_css = "banner-search-btn";

// the path of search page
//com.onigroup.gsa.settings.banner_search_destination = "/content/startrack_corp/SearchResults.html";

com.onigroup.gsa.settings.banner_search_enter_keydown = true;

/* dynamic nav filter section begin */
//filter name: metatag name
com.onigroup.gsa.settings.gsa_filter_name='gsaentity_startrack';

//filter link css, default is gsa_filters. Each link must have 'filter' attribute which contains filter's value
com.onigroup.gsa.settings.gsa_filter_search_css = 'gsa_filters';

//if it is set and the filter value is equal to this, then no filter will be used.
com.onigroup.gsa.settings.gsa_filter_all_value='All';

com.onigroup.gsa.settings.gsa_filter_select_id = "gsa_filters_select";
/* dynamic nav filter section end */

//html element container(div) to show cluster
//com.onigroup.gsa.settings.gsa_cluster_container_id = 'related';
//html element to show search term for cluster.
com.onigroup.gsa.settings.gsa_cluster_query_id = 'gsa_cluster_query';
//html element to show cluster item.
com.onigroup.gsa.settings.gsa_cluster_item_css = 'gsa_cluster_item';

//html forms used to do search. If multi, use ',' to separate them
com.onigroup.gsa.settings.gsa_search_forms = 'suggestion_form';

//default search form which will contain all parameters. default value is suggestion_form
com.onigroup.gsa.settings.gsa_search_defalut_form = 'suggestion_form';

//html forms used to do search. If multi, use ',' to separate them
com.onigroup.gsa.settings.gsa_suggest_fields_css = 'q';

//max_matches=10 (defalut 10)
com.onigroup.gsa.settings.gsa_suggest_max_matches = 10;

//os or rich (default is os)
com.onigroup.gsa.settings.gsa_suggest_format = 'os';

//html css for page navigation links, if not set, gsa_page_nav will be used
com.onigroup.gsa.settings.gsa_page_nav_link_css = "gsa_page_nav";
com.onigroup.gsa.settings.gsa_page_size = 10;
//css class for general links in search page
com.onigroup.gsa.settings.gsa_update_general_link_css = "gsa_update_link";
