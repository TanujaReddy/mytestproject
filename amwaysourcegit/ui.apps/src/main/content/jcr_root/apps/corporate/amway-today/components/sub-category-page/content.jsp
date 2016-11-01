<%@page import="org.strut.amway.core.util.AnalyticsUtils" %>
<%@page import="org.strut.amway.core.util.CategoryUtils" %>
<%@page session="false" %>
<%@include file="/libs/foundation/global.jsp"%>

<%--
  Sub-Category Page component.

  This is a component to render sub-category page.
--%>

<%
    // Analytics parameters
    pageContext.setAttribute(AnalyticsUtils.SECTION, AnalyticsUtils.SECTION_ARTICLES, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.CATEGORY, CategoryUtils.getCategoryNameIfPossible(currentPage), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.SUBCATEGORY, CategoryUtils.getSubCategoryNameIfPossible(currentPage), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.DETAIL, "", PageContext.REQUEST_SCOPE);
%>
<cq:include path="navigation" resourceType="corporate/amway-today/components/month-navigation"/>
<div class="content">
    <!--Breadcrumb Desktop-->
    <cq:include path="breadcrumb" resourceType="corporate/amway-today/components/breadcrumb"/>
    <!--End Breadcrumb Desktop-->
    <div id="list-content">
        <c:set var="articleQueryUrl" value="amway.article.grid.json" scope="request"/>
        <c:set var="includingPage" value="sub-category-page" scope="request"/>
		<cq:include path="article-list" resourceType="corporate/amway-today/components/article-list"/>
    </div>
    <div id="article-list-info" class="row-loading text-center"><img id="loading-img" src="<%= currentDesign.getPath() + "/images/loading.gif"%>"></div>
</div>