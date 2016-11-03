<%--
  ==============================================================================
  Error page footer script.
  ==============================================================================
--%>
<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8"
	import="java.util.ResourceBundle, 
			java.util.Locale, 
			java.util.Calendar,
			org.strut.amway.core.util.PageUtils,
			org.strut.amway.core.util.CategoryConstants"%>

<cq:setContentBundle />
<%
    Locale pageLocale = currentPage.getLanguage(false);
    ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

    String year = ((Integer) Calendar.getInstance().get(Calendar.YEAR)).toString();
    boolean isSubCategoryPage = PageUtils.isSubCategoryPage(currentPage);

    String mostPopularLink = currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL).getPath() + resourceBundle.getString("mostPopularName");
	String mostPopularCss = "";
	Page mostPopularPage = pageManager.getPage(mostPopularLink);
	if(mostPopularPage == null){
	    mostPopularLink = "#";
	    mostPopularCss = "hide";
	}else{
	    mostPopularLink = mostPopularLink + ".html";
	}
%>
<div id="fixed-footer" class="hidden-print">
	<!-- End Content Footer -->
	<!-- Footer -->
	<div id="inner-footer" class="hidden-xs">
		<div class="container">
			<div class="text-center">
				<small class="inner-footer-menu"><fmt:message key="lbCopyright"> <fmt:param value="<%=year%>" /> </fmt:message></small>
				<small class="point-space">|</small> 
				<small class="inner-footer-menu"><fmt:message key="lbRss" /></small>
				<br class="visible-xs"> 
				<small class="point-space hidden-xs">|</small>
				<small class="inner-footer-menu"><cq:include path="<%= currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL).getPath() + "/jcr:content/privacy-security" %>" resourceType="corporate/amway-today/components/privacy-security"/></small>
				<small class="point-space">|</small> 
                <small class="inner-footer-menu"><a href="<%= currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL).getPath() %>/toolbar/site-map.html"><fmt:message key="lbSiteMap" /></a></small>
				<small class="point-space">|</small>
				<small class="inner-footer-menu"><a href="<%= currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL).getPath() %>/toolbar/contact-us.html"><fmt:message key="lbContact" /></a></small>
			</div>
		</div>
	</div>
	<!-- End Footer -->
</div>
