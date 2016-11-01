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

  Workflow summary for Geometrixx pages

--%><%@ page 
  session="false"
  import="
  com.day.cq.commons.TidyJSONWriter,
  javax.jcr.Node
  " 
%><%@include file="/libs/foundation/global.jsp"%><%

final TidyJSONWriter w = new TidyJSONWriter(response.getWriter());

// Workflow console already uses page title, use subtitle
// as description if we have it
String desc = "";
String icon = "";
final Node n = currentPage.getContentResource().adaptTo(Node.class);
if(n != null && n.hasProperty("subtitle")) {
  desc = n.getProperty("subtitle").getString();
  icon = request.getContextPath() + n.getParent().getPath() + ".thumb.48.48.png";
}

w.object();
w.key("description").value(desc);
w.key("icon").value(icon);
w.endObject();
%>