<%@page import="static org.apache.commons.lang3.StringUtils.*" %>
<%@page import="org.strut.amway.core.util.AnalyticsUtils" %>
<%@page import="java.util.Locale" %>

<%@include file="/libs/foundation/global.jsp"%>
<%--
  ==============================================================================
  Default analytics script
  ==============================================================================
--%>

<%
    Locale locale = currentPage.getLanguage(false);

    String section = lowerCase(defaultString((String) pageContext.getAttribute(AnalyticsUtils.SECTION, PageContext.REQUEST_SCOPE)), locale);
    String category = lowerCase(defaultString((String) pageContext.getAttribute(AnalyticsUtils.CATEGORY, PageContext.REQUEST_SCOPE)), locale);
    String subCategory = lowerCase(defaultString((String) pageContext.getAttribute(AnalyticsUtils.SUBCATEGORY, PageContext.REQUEST_SCOPE)), locale);
    String detail = lowerCase(defaultString((String) pageContext.getAttribute(AnalyticsUtils.DETAIL, PageContext.REQUEST_SCOPE)), locale);
%>

<!-- Adobe Analytics -->
<script type="text/javascript">
    function buildDataLayerPage() {
        return {
            section: '<%= section %>',
            category: '<%= category %>',
            subCategory: '<%= subCategory %>',
            detail: '<%= detail %>'
        };
    }

    if (window.isPublishInstance == 'true') {
//        console.log('send pageview to analytics for publish instance');

        window.dataLayer = {
            site: buildDataLayerSite(),
            page: buildDataLayerPage()
        };

        _satellite.pageBottom();
    } else {
        console.log('do not send pageview to analytics for author instance');
    }
</script>
<!-- Adobe Analytics End -->
