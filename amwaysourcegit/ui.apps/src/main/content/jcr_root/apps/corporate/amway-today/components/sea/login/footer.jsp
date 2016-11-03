<%--
  ==============================================================================
  Default footer script.
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
    boolean isHomePage = PageUtils.isHomePage(currentPage);

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
	<!-- Content Footer -->
	<div class="content-footer hidden-xs">
		<div class="container">
			<div class="row">
				
			</div>
		</div>
	</div>
	<!-- End Content Footer -->
	<!-- Footer -->
	
	<!-- End Footer -->
</div>
