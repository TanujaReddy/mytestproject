<%@page import="org.strut.amway.core.util.AnalyticsUtils" %>
<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:include path="navigation" resourceType="corporate/amway-today/components/month-navigation"/>

<%--

  Home Page component.

  This is a component to render home page.

--%>

<%
    // Analytics parameters
    pageContext.setAttribute(AnalyticsUtils.SECTION, AnalyticsUtils.SECTION_HOME, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.CATEGORY, "", PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.SUBCATEGORY, "", PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.DETAIL, "", PageContext.REQUEST_SCOPE);
%>

<div class="content">
    <cq:include path="hero-article" resourceType="foundation/components/parsys"/>
    <div id="list-content">
        <c:set var="articleQueryUrl" value="amway.article.grid.json" scope="request"/>
        <c:set var="includingPage" value="home-page" scope="request"/>
		<cq:include path="article-list" resourceType="corporate/amway-today/components/article-list"/>
	</div>
	<div id="article-list-info" class="row-loading text-center"><img id="loading-img" src="<%= currentDesign.getPath() + "/images/loading.gif"%>"></div>
</div>