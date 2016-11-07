<%@include file="/apps/global/components/shared-datasource/global.jsp"%>
 <!--  This file has been made to populate the clientlibs in the page properties in touch ui -->
<%@page session="false"
	import="javax.jcr.*,
		org.apache.sling.api.resource.*,
		com.day.cq.search.Query,
		com.day.cq.search.PredicateGroup,
		com.day.cq.search.QueryBuilder,
		com.day.cq.search.result.Hit,
		com.day.cq.search.result.SearchResult,
		org.apache.commons.collections.Transformer,
        com.adobe.granite.ui.components.ds.DataSource,
        com.adobe.granite.ui.components.Config,
        com.adobe.granite.ui.components.ds.ValueMapResource,
        com.adobe.granite.ui.components.ComponentHelper,
        java.util.HashMap,
        org.apache.sling.api.wrappers.ValueMapDecorator,
        com.adobe.granite.ui.components.ds.SimpleDataSource,
        org.apache.commons.collections.iterators.TransformIterator,
        java.util.Map,
        java.util.LinkedHashMap"%>
        
 
<%
final ComponentHelper cmp = new ComponentHelper(pageContext);
final Map<String, String> icons = new LinkedHashMap<String, String>();
Config cfg = cmp.getConfig();

final QueryBuilder builder = sling.getService(QueryBuilder.class);
Map<String, String> map = new HashMap<String, String>();
map.put("path", cfg.get("folderPath", String.class));
map.put("type", "dam:Asset");
final ResourceResolver resolver = slingRequest.getResourceResolver();
try {
	Query query = builder.createQuery(PredicateGroup.create(map), resolver.adaptTo(Session.class));
	SearchResult result = query.getResult();
	for (Hit hit : result.getHits()) {
		String path = hit.getPath();
		Node node = resolver.getResource(path).adaptTo(Node.class);
		icons.put(path,node.getName());
	}

	}catch (RuntimeException e) {
		throw new RuntimeException("Exception",e);
	}
	
    DataSource ds = new SimpleDataSource(new TransformIterator(icons.keySet().iterator(), new Transformer() {
        public Object transform(Object o) {
            String iconName = (String) o;
            ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
            vm.put("value", iconName);
            vm.put("text", icons.get(iconName));
 
            return new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm);
        }
    }));
 
    request.setAttribute(DataSource.class.getName(), ds);
%>