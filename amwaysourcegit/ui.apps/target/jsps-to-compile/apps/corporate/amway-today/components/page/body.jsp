<%@page session="false" contentType="text/html; charset=utf-8" %>
<%@page import="org.strut.amway.core.util.AEMUtils,
    org.apache.sling.settings.SlingSettingsService" %>
<%--
  ==============================================================================

  Default body script.

  Draws the HTML body with the page content.

  ==============================================================================
--%>
<%@include file="/libs/foundation/global.jsp" %>

<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<cq:defineObjects/>
<cq:setContentBundle />

<%
    // get server flag
    SlingSettingsService slingSettingsService = sling.getService(SlingSettingsService.class);
    boolean isPublishInstance = AEMUtils.isPublish(slingSettingsService);
    pageContext.setAttribute(AEMUtils.IS_PUBLISH_INSTANCE, (Boolean) isPublishInstance, PageContext.REQUEST_SCOPE);
%>
<body class="no-touch">
<script type="text/javascript">
    // global variable to check publish instance
    window.isPublishInstance = '<%= pageContext.findAttribute(AEMUtils.IS_PUBLISH_INSTANCE) %>';
</script>

<div class="global-warning-message">
    <div class="message">
        <p class="text-center"><fmt:message key="browserMessage"/></p>
        <p class="text-center"><fmt:message key="browserList"/></p>
    </div>
    <button type="button" class="close" aria-label="Close" onclick="$(this.parentNode).hide()"><span aria-hidden="true">&times;</span></button>
</div>

<script>
    checkBrowserCompatibility();
</script>


<cq:include script="header.jsp"/>
<div id="content" class="container">
    <!-- Overlay -->
    <div class="overlay hidden-print"></div>
    <div class="row">
        <div id="navigation" class="hidden-print">
            <cq:include path="navigation" resourceType="corporate/amway-today/components/navigation"/>
        </div>
        <div id="main-content">
            <cq:include script="headermobile.jsp"/>
            <cq:include script="content.jsp"/>
        </div>
        <div id="settings" class="visible-xs hidden-print">

				<cq:include path="right-navigation" resourceType="foundation/components/iparsys"/>
        </div>
    </div>
</div>
<cq:include script="footer.jsp"/>
<!-- Month Nav -->
<div class="month-nav btn-group-vertical hidden-xs"></div>
<!-- End Month Nav -->

<cq:include script="analytics.jsp"/>
</body>