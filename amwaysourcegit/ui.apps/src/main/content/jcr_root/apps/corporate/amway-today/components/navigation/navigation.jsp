<%@page session="false"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page contentType="text/html; charset=utf-8"
		import="org.strut.amway.core.controller.NavigationController,
				com.day.cq.wcm.foundation.Navigation,
				java.util.ResourceBundle,
				com.day.cq.i18n.I18n,
				java.util.Locale,
				org.strut.amway.core.util.PageUtils"%>

<cq:setContentBundle />

<%
    	Locale pageLocale = currentPage.getLanguage(false);
        //If above bool is set to true. CQ looks in to page path rather than jcr:language property.
		ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
		I18n i18n = new I18n(resourceBundle);
%>

<ul class="sidebar-menu number-section">
	<li>
		<ul class="sub-menu" style="display: block;">
			<li title="amwaycallcenter" class="no-sub null amwaycallcenter">
			<a href="tel://+027258000" style="color: #fff;"> 
				Amway Call Center <br>
				<strong>0-2725-8000</strong>		
			</a>
			</li>
		</ul>
	</li>
</ul>


<ul id="main-menu" class="sidebar-menu">

<%
		NavigationController navigationController = new NavigationController(currentPage, currentStyle, slingRequest);
		for (Navigation.Element e: navigationController.create()) {
             switch (e.getType()) {
                case NODE_OPEN:
					%><ul class="sub-menu"><%
				    break;

                case ITEM_BEGIN:
					String categoryType = String.valueOf(e.getPage().getProperties().get("categoryType"));
		            String categoryTitle = String.valueOf(e.getTitle());

                 	String categoryPath = "javascript:void(0)";
		            String categoryCSSClass = "";
		            String span = "";
		            String target ="";
					
		            String template = e.getPage().getProperties().get("cq:template","");
		            String location = e.getPage().getProperties().get("redirectTarget", "");
		            String[] openinNewWindow = e.getPage().getProperties().get("openinnewwindow", new String[0]);
		         
		          //  out.println(template + categoryType);
                	String[] bits = template.split("/");
                	String templatePage = bits[bits.length-1];
		            if(templatePage!=null && templatePage !="") {
		        
		           if(e.hasChildren()) {
		        	   	if(templatePage.equals("sub-category")) {
		        	   		categoryCSSClass = e.isOnTrail()? "no-sub active-sub " + categoryType : "no-sub " + categoryType;
		        	   	 categoryPath = String.valueOf(e.getPath()) + ".html";
		        	   	} else {
		            	categoryCSSClass = e.isOnTrail()? "has-sub has-sub-active active " + categoryType : "has-sub " + categoryType;
		            	span = e.isOnTrail()? "<span class=\"explander\">-</span>" : "<span class=\"explander\">+</span>";
		        	   	}
		            	
		            } 
		            else {
		                categoryCSSClass = e.isOnTrail()? "no-sub active-sub " + categoryType : "no-sub " + categoryType;
		                
		                if(PageUtils.isMostPopularPage(e.getPage())){
		                    categoryTitle = resourceBundle.getString("mostPopularText");
		                    categoryPath = String.valueOf(e.getPath()) + ".html";
		                } else if(categoryType.equals("null") || categoryType.equals("")) {
		                //	categoryCSSClass = "redirect";
		                	if (templatePage.equals("redirect") && location != null && location != "" ) {
		                		if (openinNewWindow != null && openinNewWindow.length > 0 && openinNewWindow[0].equals("newWindow")) {
		                		categoryPath = location;
		                		target = "_blank";
		                		} else {
		                			categoryPath = location;
		                		}
		                	} else if (templatePage.equals("redirect") && location == null && location == "" ) {
		                	//	categoryCSSClass = "redirect";
		                		categoryPath = String.valueOf(e.getPath()) + ".html";
		                	} else {
		                		categoryPath = String.valueOf(e.getPath()) + ".html";
		                	}
		                }
		            }
		            }
					 %><li title="<%=categoryTitle%>" class="<%=categoryCSSClass%>"><a href="<%=categoryPath%>" target='<%=target%>'><%=categoryTitle%></a><%=span%><%
                    break;

                 case ITEM_END:
                     %></li><%
                     break;

                 case NODE_CLOSE:
                     %> 
                     </ul><%
                     break;
             }
         }
 %>
 </ul>
 <script type="text/javascript">
var html = document.querySelector('.sidebar-menu.number-section .amwaycallcenter');
if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)){
	html.innerHTML =  '<a href="tel://+027258000" style="color: #fff;">' +
                       'Amway Call Center <br>' +
                       '<strong>0-2725-8000</strong>' +
                     '</a>';
} else {
			html.innerHTML = '<a href="javascript:void(0)" style="color: #fff;cursor: default;">'
					+ 'Amway Call Center <br>'
					+ '<strong>0-2725-8000</strong>'
					+ '</a>';
		}
	</script>