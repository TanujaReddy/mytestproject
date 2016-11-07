<%@ page import="java.util.ResourceBundle, 
	    		com.day.cq.i18n.I18n, 
				java.util.Locale,
				com.day.cq.wcm.foundation.Placeholder" %>
<%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Text component

  Draws text.

--%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

<%
Locale pageLocale = currentPage.getLanguage(false);
//If above bool is set to true. CQ looks in to page path rather than jcr:language property.
ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
//String abc = currentPage.getContentResource("jcr:title");
String abc1 = resourceBundle.getString("target");

%><%@include file="/libs/foundation/global.jsp"%>
<cq:text property="text" escapeXml="true"
        placeholder="<%= Placeholder.getDefaultPlaceholder(slingRequest, component, null)%>"/>
