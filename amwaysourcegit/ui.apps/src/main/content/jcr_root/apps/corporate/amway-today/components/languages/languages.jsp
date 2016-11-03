<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.strut.amway.core.controller.LanguagesController,
org.strut.amway.core.model.Language,java.util.Iterator"%>
<%@ page import="org.strut.amway.core.util.CategoryConstants" %>
<cq:setContentBundle/>

<%
    LanguagesController languageController = new LanguagesController();
%>
<div  class="btn-group"> <a href="#" class="btn-header btn-footer btn btn-header-group-2 dropdown-toggle text-center" data-toggle="dropdown"><fmt:message key="languagesTitle" /></a>
	<ul class="btn-option-header dropdown-menu btn-language-sites">
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
	   	    <li class="text-center"><a href="<%=curPage.getPath()%>.html"><%=title%></a></li>
	   <%
  	       }
	   %>
	</ul>
</div>