<%-- Most Popular Page component. --%>

<%@page session="false" %>
<%@page import="org.strut.amway.core.util.AnalyticsUtils" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
    // Analytics parameters
    pageContext.setAttribute(AnalyticsUtils.SECTION, AnalyticsUtils.SECTION_MOST_POPULAR, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.CATEGORY, "", PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.SUBCATEGORY, "", PageContext.REQUEST_SCOPE);
    pageContext.setAttribute(AnalyticsUtils.DETAIL, "", PageContext.REQUEST_SCOPE);
%>
<script type="text/javascript">
    $(window).load(function() {
        deleteBlankTop();
    });
</script>
<div class="content">    
    <div id="list-content">
        <c:set var="articleQueryUrl" value="amway.most.popular.json" scope="request"/>
        <c:set var="includingPage" value="most-popular-page" scope="request"/>
		<cq:include path="article-list" resourceType="corporate/amway-today/components/article-list"/>
    </div>
    <div id="article-list-info" class="row-loading text-center"><img id="loading-img" src="<%= currentDesign.getPath() + "/images/loading.gif"%>"></div>
</div>