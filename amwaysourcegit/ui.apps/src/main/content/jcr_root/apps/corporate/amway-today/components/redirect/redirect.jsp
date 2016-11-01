<%@page session="false"%><%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Default redirect component.

  Sends a redirect to the location specified in "redirectTarget" if the WCM is
  disabled. Otherwise calls the super script.

  ==============================================================================

--%><%@ page import="org.apache.commons.lang3.ArrayUtils,
                     com.day.cq.wcm.api.WCMMode,
                     com.day.cq.wcm.foundation.ELEvaluator, com.day.cq.wcm.api.components.IncludeOptions" %><%
%><%@include file="/libs/foundation/global.jsp" %><%

%><%@page session="false"%><%

    
    String location = properties.get("redirectTarget", "");
    location = ELEvaluator.evaluate(location, slingRequest, pageContext);  
    String[] openinNewWindow = properties.get("openinnewwindow", new String[0]);
  
      %>
  		<%	if(location!=null && location!=""){
  			if (openinNewWindow != null && openinNewWindow.length > 0 && openinNewWindow[0].equals("newWindow")) {
        %>
                  
                   <a href="<%=location %>" target='_blank'><%=location %></a>
                    <%} else { %>
                     <a href="<%=location %>"><%=location %></a>
                    <%}}%>

