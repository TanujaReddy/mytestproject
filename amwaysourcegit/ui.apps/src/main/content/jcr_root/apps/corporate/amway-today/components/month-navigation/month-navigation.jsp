<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8" 
        import="java.util.ResourceBundle,
                com.day.cq.i18n.I18n, java.util.Locale,
                org.strut.amway.core.util.LinkTransformerUtils" %>

<cq:setContentBundle />
<%
	Locale pageLocale = currentPage.getLanguage(false);
	//If above bool is set to true. CQ looks in to page path rather than jcr:language property.
	ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
	I18n i18n = new I18n(resourceBundle);
	String serverTimeout = resourceBundle.getString("serverTimeout");
	String serverError = resourceBundle.getString("serverError");
%>
<script type="text/javascript">
	$(document).ready(function(e) {
        var now = new Date();
        var currentDate = encodeURIComponent(getISO8601LocalDate(now));
        var resourcePath = "<%= LinkTransformerUtils.transformPath(slingRequest.getResourceResolver(), currentPage.getPath()) %>";
        var monthListURL = resourcePath.concat(".amway.month.navigation.json?clientTime="+currentDate);
		loadMonth(monthListURL, "div.month-nav");
    });
</script>