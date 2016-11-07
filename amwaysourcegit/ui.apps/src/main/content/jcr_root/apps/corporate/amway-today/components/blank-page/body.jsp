<%@page session="false"%><%--
  ==============================================================================

  Default body script.

  Draws the HTML body with the page content.

  ==============================================================================

--%><%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<cq:setContentBundle />
<body class="no-touch">
    <cq:include script="header.jsp"/>
	<div class="container">
        <div class="row">
    		<cq:include script="content.jsp"/>
        </div>
    </div>

	<cq:include script="footer.jsp"/>
	<!-- Month Nav -->
    <div class="month-nav btn-group-vertical hidden-xs"></div>
    <!-- End Month Nav -->
</body>