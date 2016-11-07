<%--

  Error Code component.

  display error code and instructions

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<% 
	String description = properties.get("description", "Enter some description"); // default is empty array
	String errorcode = properties.get("errorcode", "000");
	String title = properties.get("title", "Enter some title");
		%>
        <div class="error-code">
            <div class="row">
                <div class="code col-md-4 text-center">
                    <%= errorcode %>
                </div>
                
                <div class="description col-md-8">
                    <div class="title">
                    	<%= title %>
                    </div>
                    <div class="content">
                    	<%= description %>
                    </div>
                </div>
            </div>
        </div>
