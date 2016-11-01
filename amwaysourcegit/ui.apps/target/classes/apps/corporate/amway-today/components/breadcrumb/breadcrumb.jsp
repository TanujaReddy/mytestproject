<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8"
    import="org.strut.amway.core.controller.BreadcrumbController,
    org.strut.amway.core.model.Breadcrumb,
	org.strut.amway.core.util.PageUtils,
    java.util.List,
	java.util.Locale,
	java.util.ResourceBundle" %>

<cq:setContentBundle />
<%
	Locale pageLocale = currentPage.getLanguage(false);
	ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

	BreadcrumbController breadcrumbController = new BreadcrumbController();
	List<Breadcrumb> breadcrumbs = breadcrumbController.createBreadcrumb(resourceBundle, currentPage, currentStyle);
%>
<div class="container-breadcrumb hidden-print">
    <ol class="mod-breadcrumb">
<%
   for(Breadcrumb breadcrumb : breadcrumbs){
%>
        <li><a class="btn btn-breadcrumb" title="<%= breadcrumb.getTitle()%>" href="<%= xssAPI.getValidHref(breadcrumb.getPath()) %>" ><%= xssAPI.encodeForHTML(breadcrumb.getTitle())%></a></li>
<%
    }
%>
<%
	if( PageUtils.isCategoryPage(currentPage) || PageUtils.isSubCategoryPage(currentPage)) {
%>
        <li class="pull-right"><a id="btn-grid-view" href="#" class="btn btn-view hidden-xs" data-view="grid"><span class="sprites icon-grid"></span></a></li>
        <li class="pull-right"><a id="btn-list-view" href="#" class="btn btn-view hidden-xs" data-view="list"><span class="sprites icon-list"></span></a></li>
<%
	}
%>
    </ol>
</div>
