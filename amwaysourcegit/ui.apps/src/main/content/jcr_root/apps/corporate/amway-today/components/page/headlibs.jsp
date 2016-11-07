<%@page session="false"%><%--
  ==============================================================================

  Includes the scripts and css to be included in the head tag

  ==============================================================================

--%><%@include file="/libs/foundation/global.jsp" %><%
%><%@ page import="com.day.cq.commons.Doctype,
				   com.day.cq.i18n.I18n,
				   org.apache.commons.lang3.StringEscapeUtils,
                   org.apache.commons.lang3.StringUtils,
                   org.apache.sling.settings.SlingSettingsService,
                   org.strut.amway.core.util.AEMUtils,
                   java.text.SimpleDateFormat,org.strut.amway.core.util.CategoryConstants,
                   java.util.Date,
                   java.util.Locale,
                   java.util.ResourceBundle
                   " %>
<cq:setContentBundle />
<%
    Locale pageLocale = currentPage.getLanguage(false);
	ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
	I18n i18n = new I18n(resourceBundle);
%><%
String xs = Doctype.isXHTML(request) ? "/" : "";

if(!properties.get("cq:lastModified", "").equals("")) {
	SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss z");
	String date = sdf.format(properties.get("cq:lastModified", Date.class) );
    %><meta http-equiv="Last-Modified" content="<%= StringEscapeUtils.escapeHtml4(date) %>"<%=xs%>><%
}

if(!properties.get("cq:lastModifiedBy", "").equals("")) {
    %><meta name="author" content="<%= StringEscapeUtils.escapeHtml4(properties.get("authorName", "")) %>"<%=xs%>><%
}

if(!properties.get("jcr:title", "").equals("")) {
    %><meta name="title" content="<%= StringEscapeUtils.escapeHtml4(properties.get("jcr:title", "")) %>"<%=xs%>><%
}

if(!properties.get("subtitle", "").equals("")) {
    %><meta name="subtitle" content="<%= StringEscapeUtils.escapeHtml4(properties.get("subtitle", "")) %>"<%=xs%>><%
}

if(properties.get("cq:tags", new String[0]).length > 0) {
    %><meta name="tags" content="<%= StringEscapeUtils.escapeHtml4( StringUtils.join(properties.get("cq:tags", new String[0]), ",") ) %>"<%=xs%>><%
}
%>

<%
    boolean applyThaiTheme = false;
  Page rootPage = currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL);
	ValueMap props = rootPage.getProperties();

if(props.get("thaitheme") != null && ((String) props.get("thaitheme")).equalsIgnoreCase("true")){
	applyThaiTheme = true;
}
 %>
<%if(applyThaiTheme){%>
<cq:includeClientLib categories="apps.strut-amway.th.all"/>
<%}else{%>
<cq:includeClientLib categories="apps.strut-amway.all"/>
<%}%>

<link rel="icon" href="/etc/designs/corporate/amway-today/images/favicon.ico">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<%
%><cq:include script="/libs/cq/cloudserviceconfigs/components/servicelibs/servicelibs.jsp"/>
<%  currentDesign.writeCssIncludes(pageContext); %>
<script type="text/javascript">
    var monthNames = [ 	'<%=resourceBundle.getString("januaryMonth")%>',
                     	'<%=resourceBundle.getString("februaryMonth")%>',
                     	'<%=resourceBundle.getString("marchMonth")%>',
                     	'<%=resourceBundle.getString("aprilMonth")%>',
                     	'<%=resourceBundle.getString("mayMonth")%>',
                     	'<%=resourceBundle.getString("juneMonth")%>',
                     	'<%=resourceBundle.getString("julyMonth")%>',
                     	'<%=resourceBundle.getString("augustMonth")%>',
                     	'<%=resourceBundle.getString("septemberMonth")%>',
                     	'<%=resourceBundle.getString("octoberMonth")%>',
                     	'<%=resourceBundle.getString("novemberMonth")%>',
                     	'<%=resourceBundle.getString("decemberMonth")%>'];
    var monthShortNames = ['<%=resourceBundle.getString("janMonth")%>',
                     	'<%=resourceBundle.getString("febMonth")%>',
                     	'<%=resourceBundle.getString("marMonth")%>',
                     	'<%=resourceBundle.getString("aprMonth")%>',
                     	'<%=resourceBundle.getString("mayMonth")%>',
                     	'<%=resourceBundle.getString("junMonth")%>',
                     	'<%=resourceBundle.getString("julMonth")%>',
                     	'<%=resourceBundle.getString("augMonth")%>',
                     	'<%=resourceBundle.getString("sepMonth")%>',
                     	'<%=resourceBundle.getString("octMonth")%>',
                     	'<%=resourceBundle.getString("novMonth")%>',
                     	'<%=resourceBundle.getString("decMonth")%>'];
    var serverError = '<%=resourceBundle.getString("serverError")%>';
    var serverTimeout = '<%=resourceBundle.getString("serverTimeout")%>';
</script>

<!-- Adobe Analytics -->
<%
    String analyticsScript;
	SlingSettingsService slingSettingsService = sling.getService(SlingSettingsService.class);
	if (AEMUtils.isProduction(slingSettingsService)) {
        analyticsScript = "//assets.adobedtm.com/82aecb95e0b4e7a4cd5816c4c5d4fb1902bdbcf5/satelliteLib-bfd09861396ce11e7b987f3f1a04f5510352be8f.js";
	} else {
        analyticsScript = "//assets.adobedtm.com/82aecb95e0b4e7a4cd5816c4c5d4fb1902bdbcf5/satelliteLib-bfd09861396ce11e7b987f3f1a04f5510352be8f-staging.js";
    }
%>
<script type="text/javascript" src="<%= analyticsScript %>"></script>