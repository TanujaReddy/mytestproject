<%@page import="org.strut.amway.core.controller.SiteMapController"%>
<%@page import="org.strut.amway.core.model.CategoryModel"%>
<%@page import="org.strut.amway.core.model.SubCategory"%>
<%@page import="org.strut.amway.core.util.AnalyticsUtils"%>
<%@page import="java.util.List" %>
<%@page session="false"%>

<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.strut-amway.sitemap" />

<%
	// Analytics parameters
	pageContext.setAttribute(AnalyticsUtils.SECTION, AnalyticsUtils.SECTION_SITEMAP, PageContext.REQUEST_SCOPE);
	pageContext.setAttribute(AnalyticsUtils.CATEGORY, "", PageContext.REQUEST_SCOPE);
	pageContext.setAttribute(AnalyticsUtils.SUBCATEGORY, "", PageContext.REQUEST_SCOPE);
	pageContext.setAttribute(AnalyticsUtils.DETAIL, "", PageContext.REQUEST_SCOPE);

	//
	SiteMapController siteMapController = new SiteMapController(
			currentPage, currentStyle, slingRequest);
	List<CategoryModel> categoryModels = siteMapController
			.getCategories();
%>
<div class="site-map-list">
	<%
		int size = categoryModels.size();
		if (size > 1) {
			int block = 3; //amount of Catelogry in a line;
			for (int row = 0; row <= size - 2; row += block) {
	%>
	<div class="row">
		<%
			int breakPoint = row + block;
					if (breakPoint > size) {
						breakPoint = size;
					}
					for (int index = row; index < breakPoint; index++) {
						CategoryModel categoryModel = categoryModels.get(index);
		%>
		<div class="col-md-4">
			<div class="title"><a href="<%=categoryModel.getUrl()%>"><%=categoryModel.getTitle()%></a></div>
			<ul>
				<%
					List<SubCategory> subCatelogs = categoryModel.getSubCategories();
								for (SubCategory subCatelog : subCatelogs) {
				%>
				<li><a href="<%=subCatelog.getUrl() %>"><%=subCatelog.getTitle()%></a></li>
				<%
					}
				%>
			</ul>
		</div>
		<%
			}
		%>
	</div>
	<%
			}
		} else {
			CategoryModel categoryModel = categoryModels.get(0);
	%>
	<div class="col-md-4">
		<div class="title"><a href="<%=categoryModel.getUrl()%>"><%=categoryModel.getTitle()%></a></div>
		<ul>
			<%
				List<SubCategory> subCatelogs = categoryModel.getSubCategories();
					for (SubCategory subCatelog : subCatelogs) {
			%>
			<li><a href="<%=subCatelog.getUrl() %>"><%=subCatelog.getTitle()%></a></li>
			<%
				}
			%>
		</ul>
	</div>
	<%
		}
	%>
</div>