<%@page session="false"%><%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  External component

  Shows the contents of another web site, with all links rewritten.

--%><%@ page import="com.day.cq.wcm.foundation.External, com.day.cq.wcm.foundation.Placeholder" %><%
%><%@include file="/libs/foundation/global.jsp"%><%

    External external = new External(resource, currentPage, "spool", "external", "CFC__target");

    // draw placeholder for UI mode touch
    boolean isAuthoringUIModeTouch = Placeholder.isAuthoringUIModeTouch(slingRequest);
    if (isAuthoringUIModeTouch && !external.hasContent()) {
        %><%= Placeholder.getDefaultPlaceholder(slingRequest, component, "") %><%
    }

    String value = request.getParameter("CFC__target");
    if (value != null) {
        external.setTarget(value);
    }

    external.draw(slingRequest, slingResponse);
%>
