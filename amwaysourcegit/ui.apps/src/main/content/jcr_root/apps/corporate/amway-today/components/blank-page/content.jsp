<%--

  Blank Page component.

  This is a component to render home page.

--%>
<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp" %>

<%String pageTitle = properties.get("jcr:title", "no title");


%>
<div class="content" style="margin: 200px 0">
    <cq:include path="error-code" resourceType="corporate/amway-today/components/error-code"/>
</div>