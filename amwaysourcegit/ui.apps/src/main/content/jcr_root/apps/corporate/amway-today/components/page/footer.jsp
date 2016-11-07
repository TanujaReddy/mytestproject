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
				<div class="main-content-footer">
					<div class="btn-group pull-right">
			        	<cq:include path="family-sites-footer" resourceType="foundation/components/iparsys"/>
			        </div>
			        <div class="btn-group pull-right" id="footer-language">
			        	<cq:include path="languages-footer" resourceType="foundation/components/iparsys"/>
			        </div>
                     <%if( !hidePopularButton.equalsIgnoreCase("true")){%>
					<div class="btn-group pull-right <%= mostPopularCss %>">
                        <a href="<%= mostPopularLink%>" class="btn btn-footer-group-1 btn-footer most-popular break-word"><fmt:message key="lbViewMostPopularText" /></a>
					</div>
                    <%}%>
					<div class="search-footer-group pull-right">
						<form action="<%=currentPage.getPath()%>.search.html" class="header-search" role="search" method="GET" autocomplete="off">
							<div class="input-group">
								<input type="text" class="form-search form-control" placeholder="<%= resourceBundle.getString("placeholderSearch") %>" name="text" value="<c:out value="${param['text']}"/>" />
								<input type="hidden" name="_charset_" value="UTF-8" />
								<div class="input-group-btn">
									<button class="btn btn-search" type="submit">
										<span class="sprites icon-search"></span>
									</button>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- End Content Footer -->
	<!-- Footer -->
	<div id="inner-footer" class="hidden-xs">
		<div class="container">
			<div class="text-center">
				<small class="inner-footer-menu"><fmt:message key="lbCopyright"> <fmt:param value="<%=year%>" /> </fmt:message></small>
<!-- 				<small class="point-space">|</small>  -->
<!-- 				<small class="inner-footer-menu"><%if (isHomePage) {%> <a href="<%=currentPage.getPath()%>.feedarticles.html"> <%}%><fmt:message key="lbRss" /></a></small> -->
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
