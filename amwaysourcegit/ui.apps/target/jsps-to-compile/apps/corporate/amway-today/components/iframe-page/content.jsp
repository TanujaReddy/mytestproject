<%@page import="org.strut.amway.core.util.AnalyticsUtils" %>
<%@page import="org.strut.amway.core.util.CategoryUtils" %>
<%@page session="false" %>
<%@include file="/libs/foundation/global.jsp"%>

<%--
  Iframe SubCategory Page component.

  This is a component to render Iframe page.
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
    
</div>
 <cq:include path="iframe-content-text-center" resourceType="foundation/components/parsys"/>