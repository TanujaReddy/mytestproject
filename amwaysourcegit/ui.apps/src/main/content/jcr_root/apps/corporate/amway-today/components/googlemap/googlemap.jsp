<%@page session="false" pageEncoding="utf-8" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
String address = properties.get("address", "821 Pacific Hwy,Chatswood,NSW,2067").replaceAll(" ","+");
%>
<iframe width="<%= properties.get("width", "700")%>" height="<%=properties.get("height", "385")%>" 
    frameborder="0" scrolling="no" 
    marginheight="0" marginwidth="0" 
    src="http://maps.google.com.au/maps?f=q&amp;source=s_q&amp;hl=en&amp;geocode=&amp;q=<%= address %>&amp;aq=&amp;vpsrc=0&amp;ie=UTF8&amp;hq=&amp;hnear=<%= address %>&amp;z=14&amp;output=embed">
</iframe>
<br />
<small>
<a href="http://maps.google.com.au/maps?q=<%= address %>">View Larger Map</a>
</small>
