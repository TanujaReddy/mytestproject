<%--
  ==============================================================================
  Error Page Header script.

  Draws the top header and shows an example of how to include a cacheable
  navigation by using a script include. see page/topnav.jsp for more details.
  Note that the topnav script is included as .html so that it is flushed on the
  dispatcher on invalidation requests.
  ==============================================================================
--%>
<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8"
		import="com.day.text.Text,
		java.util.ResourceBundle,
		java.util.Locale,
		org.strut.amway.core.model.Account,
		org.strut.amway.core.util.AuthenticationConstants,
		org.strut.amway.core.util.LinkTransformerUtils,
		org.strut.amway.core.util.CategoryConstants" %>
<cq:setContentBundle />
<%
    Locale pageLocale = currentPage.getLanguage(false);
	ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

	String linkNoImage = resourceBundle.getString("linkNoImage");
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

<!-- Top Header -->
<div class="header hidden-xs hidden-print">
    <div class="container">
        <div class="row">
            <div class="mt-10 pull-left"> <a id="logo" href="<%= currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL).getAbsoluteParent(CategoryConstants.ABS_LEVEL).getPath() %>.html" class="sprites">AmwayOn</a> </div>
            <!-- Menu header -->
            <div class="menu-header">
                <div  class="btn-group pull-right" id="header-family-sites">
				<cq:include path="family-sites" resourceType="foundation/components/iparsys"/>
                </div>

                <div  class="btn-group pull-right" id="header-language">
                    <cq:include path="languages" resourceType="foundation/components/iparsys"/>
                </div>

                <div id="header-profile" class="btn-group pull-right">
                    <cq:include path="user-profile" resourceType="foundation/components/iparsys"/>
                </div>
                
                <div id="header-most-popular" class="btn-group pull-right <%= mostPopularCss %>"> <a href="<%= mostPopularLink%>" class="btn btn-header-group-1 btn-header break-word"><span class="hidden-sm"><fmt:message key="lbViewMostPopularText"/></span><span class="visible-sm"><fmt:message key="mostPopularText"/></span></a> </div>
                
            </div>
        </div>
    </div>
</div>
<!-- End Top Header -->
