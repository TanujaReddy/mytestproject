
<%--
  ==============================================================================
  Default header mobile script.
  ==============================================================================
--%>
<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8"
		import="com.day.text.Text, 
		java.util.ResourceBundle, 
		com.day.cq.i18n.I18n, 
		java.util.Locale,
		org.strut.amway.core.util.CategoryConstants"%>
<cq:setContentBundle />
<%
	Locale pageLocale = currentPage.getLanguage(false);
	ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
	I18n i18n = new I18n(resourceBundle);

    String[] selectors = slingRequest.getRequestPathInfo().getSelectors();

%>

<!-- Mobile header -->
<div id="header-mobile" class="container fixed-position visible-xs hidden-print">
    <div class="row"> 
        <!--Top header-->
        <div class="top-header text-center">
            <div id="toggle-sidebar-left" class="sprites pull-left"></div>
            <div class="mobile-logo"> <a id="logo" href="<%= currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL).getPath() %>.html" class="sprites">AmwayOn</a> </div>
            <div id="toggle-sidebar-right" class="sprites pull-right"></div>
        </div>

        <!--Search box-->
        <div class="mobile-search">
            <form class="header-search" role="search" action="<%= currentPage.getPath() %>.search.html" method="GET" autocomplete="off">
                <div class="input-group">
                    <input type="text" class="form-search form-control" placeholder="<%= resourceBundle.getString("placeholderSearch") %>" name="text" value="<c:out value="${param['text']}"/>"  />
                    <input type="hidden" name="_charset_" value="UTF-8" />
                    <div class="input-group-btn">
                        <button class="btn btn-search" type="submit"><span class="sprites icon-search"></span></button>
                    </div>
                </div>
            </form>
        </div>
        
        <!--Month filter-->
    <%
    if (selectors.length == 0) {
    %>
        <div class="mobile-month-filter month-nav"></div>
    <%
    }
    %>
    </div>
</div>