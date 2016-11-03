<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8"
                    import="com.day.cq.commons.Doctype,
            		com.day.cq.wcm.api.WCMMode,
                    com.day.cq.wcm.foundation.ELEvaluator,
                    java.util.ResourceBundle, 
					com.day.cq.i18n.I18n, 
					java.util.Locale" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<cq:defineObjects/>
<cq:setContentBundle /><%

    // read the redirect target from the 'page properties' and perform the
    // redirect if WCM is disabled.
    String location = properties.get("redirectTarget", "");
    // resolve variables in path
    location = ELEvaluator.evaluate(location, slingRequest, pageContext);
    boolean wcmModeIsDisabled = WCMMode.fromRequest(request) == WCMMode.DISABLED;
    boolean wcmModeIsPreview = WCMMode.fromRequest(request) == WCMMode.PREVIEW;
    if ( (location.length() > 0) && ((wcmModeIsDisabled) || (wcmModeIsPreview)) ) {
        // check for recursion
        if (currentPage != null && !location.equals(currentPage.getPath()) && location.length() > 0) {
            // check for absolute path
            final int protocolIndex = location.indexOf(":/");
            final int queryIndex = location.indexOf('?');
            final String redirectPath;
            if ( protocolIndex > -1 && (queryIndex == -1 || queryIndex > protocolIndex) ) {
                redirectPath = location;
            } else {
                redirectPath = slingRequest.getResourceResolver().map(request, location) + ".html";
            }
            response.sendRedirect(redirectPath);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return;
    }
    // set doctype
    if (currentDesign != null) {
        currentDesign.getDoctype(currentStyle).toRequest(request);
    }

%><%= Doctype.fromRequest(request).getDeclaration() %>
<html <%= wcmModeIsPreview ? "class=\"preview\"" : ""%>>
<cq:include script="head.portlet.jsp"/>
<body class="no-touch">
<cq:include script="header.jsp"/>
<cq:include path="navigation" resourceType="corporate/amway-today/components/month-navigation"/>
<div id="content" class="container">
    <!-- Overlay -->
    <div class="overlay hidden-print"></div>
    <div class="row">
        <div id="navigation" class="hidden-print">
            <cq:include path="navigation" resourceType="corporate/amway-today/components/navigation"/>
        </div>
        <div id="main-content" class="search-container">
            <cq:include script="headermobile.jsp"/>

            <div id="list-content" class="content">
                <c:set var="articleQueryUrl" value="amway.search.universal.json" scope="request"/>
                <c:set var="includingPage" value="search-result-page" scope="request"/>
                <div class="search-result-jukebox">
                    <div class="no-search-result-notification text-center"><fmt:message key="lbNoResult"/></div>
                    <div class="search-result-notification"><h1><fmt:message key="searchResult"/></h1></div>
                    <div class="search-result-filter">

                    </div>
                </div>
                <cq:include path="search-article-list-result" resourceType="corporate/amway-today/components/article-list"/>
            </div>
            <div id="article-list-info" class="row-loading text-center"><img id="loading-img" src="<%= currentDesign.getPath() + "/images/loading.gif"%>"></div>
        </div>
        <div id="settings" class="visible-xs hidden-print">
            <cq:include path="right-navigation" resourceType="corporate/amway-today/components/right-navigation"/>
        </div>
    </div>
</div>
<cq:include script="footer.jsp"/>

</body>
</html>