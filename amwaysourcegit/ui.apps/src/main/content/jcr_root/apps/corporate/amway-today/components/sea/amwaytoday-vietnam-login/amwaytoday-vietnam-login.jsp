<%@include file="/libs/foundation/global.jsp"%>


<%@page contentType="text/html; charset=utf-8"
		import="org.strut.amway.core.util.ArticleLabelUtils,com.day.text.Text,
		java.util.ResourceBundle,
		java.util.Locale,
		org.strut.amway.core.util.LinkTransformerUtils" %>

<%		
    boolean isLoggedIn = ArticleLabelUtils.isPersonalizationCookiePresent(slingRequest);
//out.println("isLoggedIn"+isLoggedIn);
	String country =  properties.get("country",(String) null);
	String errorMessage =  properties.get("errormessagelogin",(String) null);
%>
<style>
   .amwaytoday-login .btn-group+.btn-group{margin-left:0;}
</style>
<cq:setContentBundle source="auto"/>
<div style="display:none;" class="btn-group login-grp"> 
	<a href="#" class="btn-header btn btn-header-group-1 dropdown-toggle text-center" data-toggle="dropdown"><fmt:message key="aboLogin"/></a>
	<ul class="btn-option-header dropdown-menu" data-value="<%= LinkTransformerUtils.transformPath(slingRequest.getResourceResolver(), currentPage.getPath()) %>">

		    <form class="sea-login" autocomplete="off">
		        <li>
		            <input class="ibo-number-input" type="text" name="sea-ato-username" placeholder="ABO No" />
		        </li>
		        <li>
		            <input class="password-input" type="password" name="sea-ato-password" placeholder="Password" />
		        </li>
                <li class="btn-login text-center"><input id="sea-login-btn" type="button" value='<fmt:message key="btnLogin"/>' /></li>
		    </form>
    </ul>
</div>
<div style="display:none;" class="btn-group logout-grp"> 
	<a href="#" id="sea-logout-btn" class="btn-header btn btn-header-group-1 text-center"><fmt:message key="btnLogout"/></a>
</div>


<script type="text/javascript">
   setParamsLogin('<%=country%>','<%=errorMessage%>');
    $(document).ready(function(){

		if(getCookieByName('dfx-auth-token') != "")
            $('.logout-grp').show();
        else
			$('.login-grp').show();

    });
</script>