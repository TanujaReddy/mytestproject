<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="java.util.Locale,
				com.day.cq.tagging.Tag" %>

<%	
	Locale locale = currentPage.getLanguage(false);
	Tag[] tags = currentPage.getTags();
	if(tags != null){
%>
<div class="tag-article">
<%
		for (Tag tag : tags) {
%>
	<a href='<%= xssAPI.getValidHref(currentPage.getPath() + ".search.html?tagId=") + tag.getTagID() %>' class="btn btn-tag" data-value="<%= tag.getTagID()%>"><%= tag.getTitle(locale)%></a>&nbsp	
<%
    	}
	}
%>
</div>