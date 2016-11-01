<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8"
		import="com.day.text.Text,
		java.util.ResourceBundle,
		java.util.Locale,
		org.strut.amway.core.model.Account,
		org.strut.amway.core.util.AuthenticationConstants,
		org.strut.amway.core.util.LinkTransformerUtils" %>
<cq:setContentBundle source="auto"/>
<div class="btn-group btn-login-sites"> 
	<a href="#" class="btn-header btn btn-header-group-1 dropdown-toggle text-center" data-toggle="dropdown"> <span class="sprites icon-profile"></span></a>
	<ul class="btn-option-header login-form dropdown-menu" data-value="<%= LinkTransformerUtils.transformPath(slingRequest.getResourceResolver(), currentPage.getPath()) %>">
		<%
		final HttpSession httpSession = slingRequest.getSession();
		Object loginedUser = httpSession.getAttribute(AuthenticationConstants.LOGINED_USER);
		Locale pageLocale = currentPage.getLanguage(false);
		ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
		if( loginedUser == null) {
		%>
		    <form class="login" autocomplete="off">
		        <li>
		            <input class="ibo-number-input text-center" type="text" name="username" placeholder="<%= resourceBundle.getString("placeholderIBONumber") %>" />
		        </li>
		        <li>
		            <input class="password-input text-center" type="password" name="password" placeholder="<%= resourceBundle.getString("placeholderPasssword") %>" />
		        </li>
                <li class="btn-login text-center"><input type="submit" value='<fmt:message key="btnLogin"/>' /></li>
		    </form>
		    <li class="login-description"><a href="javascript:void(0)"><fmt:message key="lbLoginDes"/></a></li>
		<%
		} else {
			Account loginedAccount = (Account)loginedUser;
		                 %>
		    <li class="text-center"><a href="javascript:void(0)"><%= loginedAccount.getUserName() %></a></li>
		    <li class="btn-logout text-center"><a href="#"><fmt:message key="btnLogout"/></a></li>
		<%
		    }
		%>
    </ul>
</div>
