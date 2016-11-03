<%--

  Poll Survey component

  Draws text.

--%>

<%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
<%@ page import="java.util.ResourceBundle, 
	    		com.day.cq.i18n.I18n, 
				java.util.Locale,
				com.day.cq.wcm.foundation.Placeholder,
                 org.strut.amway.core.util.AEMUtils,
                 org.apache.sling.settings.SlingSettingsService" %>

<%
Locale pageLocale = currentPage.getLanguage(false);
//If above bool is set to true. CQ looks in to page path rather than jcr:language property.
ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
//String abc = currentPage.getContentResource("jcr:title");

String target = properties.get("target", null);
%><%@include file="/libs/foundation/global.jsp"%>

 <%
    if (target != null) {
  %>

<%=  target %>
<%} %>
<cq:text property="text" escapeXml="true"
        placeholder="<%= Placeholder.getDefaultPlaceholder(slingRequest, component, null)%>"/>
