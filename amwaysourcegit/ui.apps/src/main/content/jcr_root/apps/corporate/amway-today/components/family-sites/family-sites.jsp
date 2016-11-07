<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@ page contentType="text/html; charset=utf-8" 
 	import="org.strut.amway.core.controller.FamilySitesController,
		 java.util.Map,
		 java.util.HashMap,
		 com.day.cq.i18n.I18n"%>
<cq:setContentBundle source="auto"/>

<div class="btn-group">
	<a href="#"
    	class="btn-header btn-footer btn btn-header-group-2 dropdown-toggle text-center"
		data-toggle="dropdown"><fmt:message key="familySitesComponentName" /></a>

	<ul class="btn-option-header btn-family-sites dropdown-menu">
		<%
		  FamilySitesController controller = new FamilySitesController();
		  Map<String, String> familySites = controller.getContent(currentNode);
		  for (String key : familySites.keySet()) {
		      String value = familySites.get(key);
		%>
		    	<li class="text-center">
                    <a href="<%= value%>" target="_blank"><%= key%></a>
        		</li>
		<%
		  }
		  final I18n i18n = new I18n(slingRequest.getResourceBundle(currentPage.getLanguage(false)));
		%>
		<input id="familySiteLinkInput" type="hidden" value="<%=i18n.get("familySiteLinkInput") %>"/>
		<input id="urlInputDescription" type="hidden" value="<%=i18n.get("urlInputDescription") %>"/>
	</ul>
</div>
