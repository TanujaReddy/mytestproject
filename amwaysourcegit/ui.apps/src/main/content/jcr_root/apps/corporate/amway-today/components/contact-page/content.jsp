<%@page import="org.strut.amway.core.util.AnalyticsUtils" %>
<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp" %>

<%--
  Contact Page component.

  This is a component to render contact page.
--%>

<%
    // Analytics parameters
    pageContext.setAttribute(AnalyticsUtils.SECTION, AnalyticsUtils.SECTION_CONTACT, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.CATEGORY, "", PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.SUBCATEGORY, "", PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.DETAIL, "", PageContext.REQUEST_SCOPE);
%>
<script type="text/javascript">
    $(window).load(function() {
        deleteBlankTop();
    });
</script>
<cq:include path="breadcrumb" resourceType="corporate/amway-today/components/breadcrumb"/>
<div class="content-blog">
	<cq:include path="contact-page" resourceType="foundation/components/parsys"/>
</div>