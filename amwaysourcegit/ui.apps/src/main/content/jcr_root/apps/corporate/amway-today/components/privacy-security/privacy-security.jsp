<%@page session="false" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:setContentBundle source="auto" />
<cq:includeClientLib js="apps.strut-amway.privacy"/>
<%
    String urlPrivacy = properties.get("link-address", "");
    String urlTermOfUse = properties.get("link-address-term-of-use", "");
%>
<small class="inner-footer-menu"><a target="_blank" href="<%=urlPrivacy%>"><fmt:message key="lbPrivacyAndSecurity"/></a></small>
<small class="point-space">|</small>
<small class="inner-footer-menu"><a target="_blank" href="<%=urlTermOfUse%>"><fmt:message key="lbTermOfUse"/></a></small>
