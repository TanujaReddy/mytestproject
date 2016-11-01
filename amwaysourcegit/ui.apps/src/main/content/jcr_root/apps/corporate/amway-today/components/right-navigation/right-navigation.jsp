<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8" 
		import="java.util.ResourceBundle, 
				com.day.cq.i18n.I18n, 
				java.util.Locale, 
				java.util.Calendar,
				java.util.Map,
		 		java.util.HashMap,
				org.strut.amway.core.util.PageUtils,
				org.strut.amway.core.model.Account,
				org.strut.amway.core.util.AuthenticationConstants,
				org.strut.amway.core.controller.ExternalLinkController,
				org.strut.amway.core.controller.LanguagesController,
				org.strut.amway.core.controller.FamilySitesController,
				org.strut.amway.core.model.Language,java.util.Iterator,
				org.strut.amway.core.util.LinkTransformerUtils,
				org.strut.amway.core.util.CategoryConstants,
				 org.strut.amway.core.util.ExternalLinkConstants" %>
<cq:setContentBundle />

<%
	Locale pageLocale = currentPage.getLanguage(false);
	ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
    I18n i18n = new I18n(resourceBundle);
    
    String year = ((Integer) Calendar.getInstance().get(Calendar.YEAR)).toString();
	boolean isSubCategoryPage = PageUtils.isSubCategoryPage(currentPage);
	
    LanguagesController languageController = new LanguagesController(); 

%>
<script type="text/javascript">
	
	$(function(){
		/* show or hide multi-language */
		if($(".btn-language-sites").length > 0){
			$("#multi-language").show();
		}else{
			$("#multi-language").hide();
		}
		
		/* hide or show family site*/
		if($(".btn-family-sites").length > 0){
			
			$("#family-site").show();
		}else{
			$("#family-site").hide();
		}
		
		/* hide or show login-site */
		if($(".btn-login-sites").length > 0){
			
			$("#login-site").show();
		}else{
			$("#login-site").hide();
		}
	});

	
</script>

<!-- Right Navigation -->
<!--div class="title-nav text-uppercase"><fmt:message key="lbSetting"/></div-->
    <ul id="setting-menu" class="sidebar-menu">
        <li id="login-site" class="has-sub main-blogging"><a href="javascript:void(0)"><fmt:message key="lbProfile"/></a><span class="explander">+</span>
            <ul class="btn-option-header login-form sub-menu" data-value="<%= LinkTransformerUtils.transformPath(slingRequest.getResourceResolver(), currentPage.getPath()) %>">
                <%
                    final HttpSession httpSession = slingRequest.getSession();
                    Object loginedUser = httpSession.getAttribute(AuthenticationConstants.LOGINED_USER);
                    if( loginedUser == null) {
                %>
                <form class="login">
                    <li>
                        <label class="w85 pull-left"><fmt:message key="placeholderIBONumber"/></label>
                        <input class="ibo-number-input" name="username" type="text" placeholder="<%= resourceBundle.getString("placeholderTypeHere") %>" />
                        <span class="required">*</span> 
                    </li>
                    <li>
                        <label class="w85 pull-left"><fmt:message key="placeholderPasssword"/></label>
                        <input class="password-input" name="password" type="password" placeholder="<%= resourceBundle.getString("placeholderTypeHere") %>" />
                        <span class="required">*</span> 
                    </li>
                </form>
                <li class="btn-login text-center"><a href="#"><fmt:message key="btnLogin"/></a></li>
                <li class="login-description text-center hidden"><a href="javascript:void(0)"></a></li>
                <%
                	} else {
                    	Account loginedAccount = (Account)loginedUser;
                %>
                <form class="login">
                    <li>
                        <label class="w85 pull-left">IBO number</label>
                        <input class="ibo-number-input" name="username" type="text" value="<%= loginedAccount.getUserName() %>" readonly  />
                    </li>
                    <li>
                        <label class="w85 pull-left">Passsword</label>
                        <input class="password-input" name="password" type="password" value="******" readonly  />
                    </li>
                </form>
                <li class="btn-logout text-center"><a href="#"><fmt:message key="btnLogout"/></a></li>
                <%
                    }
                %>
            </ul>
        </li>
        <li id="multi-language" class="has-sub main-blogging"><a href="javascript:void(0)"><fmt:message key="btnLanguages"/></a><span class="explander">+</span>
             <ul class="sub-menu">

                    <%

                Page rootPage = currentPage.getAbsoluteParent(CategoryConstants.LANGUAGE_LEVEL);
                String sitePath = rootPage.getPath();
        
                Iterator<Page> pageIterator = rootPage.listChildren();
                while (pageIterator.hasNext()) {
        
                Page curPage = pageIterator.next();
                ValueMap props = curPage.getProperties();
                String title = "";
                if(props.get("languagelabel") != null)
                {
                    title = (String) props.get("languagelabel");
                }
        
                    else
                    {
                        title = curPage.getTitle();
                    }


	  			 %>
						<li><a href="<%=curPage.getPath()%>.html"><%=title%></a></li>

				   <%
				    }
				   %>
              </ul>
        </li>
        <li id="family-site" class="has-sub main-blogging"><a href="javascript:void(0)"><fmt:message key="btnFamilySites"/></a><span class="explander">+</span>
            <ul class="sub-menu">
               <%
                  FamilySitesController controller = new FamilySitesController();
				  Map<String, String> familySites = controller.getContent(currentNode);
				  for (String key : familySites.keySet()) {
                  String value = familySites.get(key);


				%>
				    	<li>
		                    <a href="<%= value%>" target="_blank"><%= key%></a>
		        		</li>
				<%
                        }
				%>
            </ul>
        </li>        
        <!--li><a <% if(isSubCategoryPage){ %> href="<%= currentPage.getPath() %>.feedarticles.html" <%} else {%> href="javascript:void(0)"<%} %>><fmt:message key="lbRss"/></a></li-->
        <%
			String[] externalLinks = new String[2];
        	externalLinks[0] = ExternalLinkConstants.DEFAULT_LINK;
        	externalLinks[1] = ExternalLinkConstants.DEFAULT_LINK;
			String resourcePath = currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL).getPath()+ ExternalLinkConstants.NODE_PATH;

			Resource pageResource = resourceResolver.getResource(resourcePath);
        	if (pageResource != null) {
            ValueMap props = pageResource.adaptTo(ValueMap.class);
            if (props != null) {
                externalLinks[0] = props.get(ExternalLinkConstants.PRIVACY_SECURITY_PROPERTIY, 
                        ExternalLinkConstants.DEFAULT_LINK);
                externalLinks[1] = props.get(ExternalLinkConstants.TERM_OF_USE_PROPERTIY, 
                        ExternalLinkConstants.DEFAULT_LINK);
            }
            }



        %>
        <li><a href="<%=externalLinks[0]%>"><fmt:message key="lbPrivacyAndSecurity"/></a></li>
        <li><a href="<%=externalLinks[1]%>"><fmt:message key="lbTermOfUse"/></a></li>
        <li><a href="<%= currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL).getPath() %>/toolbar/site-map.html"><fmt:message key="lbSiteMap" /></a></li>
        <li><a href="<%= currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL).getPath() %>/toolbar/contact-us.html"><fmt:message key="lbContact" /></a></li>
        <!--li><a href="javascript:void(0)"><fmt:message key="lbCopyright"><fmt:param value="<%= year %>"/></fmt:message></a></li-->        
    </ul>
<!-- End Right Navigation --> 