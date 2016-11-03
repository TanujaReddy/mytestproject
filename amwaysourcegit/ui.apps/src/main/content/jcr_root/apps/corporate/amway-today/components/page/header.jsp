<%--
  ==============================================================================
  Header script.

  Draws the top header and shows an example of how to include a cacheable
  navigation by using a script include. see page/topnav.jsp for more details.
  Note that the topnav script is included as .html so that it is flushed on the
  dispatcher on invalidation requests.
  ==============================================================================
--%>
<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8"
		import="org.apache.sling.settings.SlingSettingsService,
		org.strut.amway.core.util.AEMUtils,
		org.strut.amway.core.util.CategoryConstants,
		org.strut.amway.core.util.LinkTransformerUtils,
		java.util.Locale,
		java.util.ResourceBundle" %>
<%@page import="org.apache.commons.lang3.StringUtils" %>
<%@page import="org.strut.amway.core.util.AnalyticsUtils" %>
<%@ page import="org.strut.amway.core.util.CategoryConstants" %>
<%
  response.setHeader("Dispatcher", "no-cache");
%>
<%

		Page rootPage = currentPage.getAbsoluteParent(CategoryConstants.LANGUAGE_LEVEL + 1);

        ValueMap props = rootPage.getProperties();
        String hidePopularButton = "false";
        if(props.get("hideMostPopular") != null)
        {
			hidePopularButton = (String) props.get("hideMostPopular");
        }
%>

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

    String language = currentPage.getLanguage(false).getLanguage();
    String aemPath = StringUtils.lowerCase(currentPage.getAbsoluteParent(CategoryConstants.LANGUAGE_LEVEL).getPath());
    SlingSettingsService slingSettingsService = sling.getService(SlingSettingsService.class);
%>

<!-- Adobe Analytics -->
<script type="text/javascript">
    // Define global var
    var dataLayer;

    function buildDataLayerSite() {
        return {
            isProduction: '<%= AEMUtils.isProduction(slingSettingsService) %>',
            type: 'responsive',
            language: '<%= language %>',
            aemPath: '<%= aemPath %>',
            region: '<%= AnalyticsUtils.getSiteRegion(currentPage) %>',
            subGroup: '<%= AnalyticsUtils.getSiteSubGroup(currentPage) %>',
            subRegion: '<%= AnalyticsUtils.getSiteSubRegion(currentPage) %>',
            country: '<%= AnalyticsUtils.getSiteCountry(currentPage) %>'
        };
    }
</script>
<!-- Adobe Analytics End -->

<!-- Top Header -->
<div class="header hidden-xs hidden-print">
    <div class="container">
        
        <div class="row">
            <div class="mt-10 pull-left"> <a id="logo" href="<%= currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL).getPath() %>.html" class="sprites">AmwayOn</a> </div>
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
                <%if( !hidePopularButton.equalsIgnoreCase("true")){%>
                <div id="header-most-popular" class="btn-group pull-right <%= mostPopularCss %>"> <a href="<%= mostPopularLink%>" class="btn btn-header-group-1 btn-header break-word"><span class="hidden-sm"><fmt:message key="lbViewMostPopularText"/></span><span class="visible-sm"><fmt:message key="mostPopularText"/></span></a> </div>
                <%}%>
                <div id="header-search" class="search-group pull-right">
                    <form action="<%= currentPage.getPath() %>.search.html" class="header-search has-suggest" role="search" method="GET" autocomplete="off">
                        <div class="input-group">
							<input resource-path="<%= LinkTransformerUtils.transformPath(slingRequest.getResourceResolver(), currentPage.getPath()) %>" link-no-image="<%=currentDesign.getPath()%><%= linkNoImage %>" type="text" class="form-search form-control" placeholder="<%=resourceBundle.getString("placeholderSearch") %>" name="text" value="<c:out value="${param['text']}"/>" />
							<input type="hidden" name="_charset_" value="UTF-8" />
                            <img id="search-icon-loading" src="<%= currentDesign.getPath() + "/images/loading.gif"%>">
                            <div class="input-group-btn">
                                <button class="btn btn-search" type="submit"><span class="sprites icon-search"></span></button>

                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- End Top Header -->
