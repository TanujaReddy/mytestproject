<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%>
<%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0"%>
<ui:includeClientLib categories="cq.gui.siteadmin.deletepageviaworkflow"/>
<%@page session="false" import="java.net.URLEncoder,
                                    java.security.Principal,
                                    java.text.SimpleDateFormat,
                                    java.util.Calendar,
                                    java.util.Collection,
                                    java.util.LinkedHashSet,
                                    java.util.ResourceBundle,
                                    java.util.Set,
                                    javax.jcr.RepositoryException,
                                    javax.jcr.security.AccessControlManager,
                                    javax.jcr.security.Privilege,
                                    javax.jcr.Session,
                                    org.apache.jackrabbit.api.JackrabbitSession,
                                    org.apache.jackrabbit.api.security.principal.PrincipalIterator,
                                    org.apache.jackrabbit.api.security.user.Authorizable,
                                    org.apache.sling.api.resource.Resource,
                                    org.apache.sling.api.resource.ResourceUtil,
                                    org.apache.sling.api.resource.ValueMap,
                                    com.adobe.cq.wcm.launches.utils.LaunchUtils,
                                    com.adobe.granite.ui.components.AttrBuilder,
                                    com.adobe.granite.ui.components.Config,
                                    com.adobe.granite.security.user.UserPropertiesManager,
                                    com.adobe.granite.security.user.util.AuthorizableUtil,
                                    com.adobe.granite.workflow.status.WorkflowStatus,
                                    com.adobe.cq.contentinsight.ProviderSettingsManager,
                                    com.day.cq.commons.date.RelativeTimeFormat,
                                    com.day.cq.security.util.CqActions,
                                    com.day.cq.wcm.api.Page,
                                    com.day.cq.wcm.api.components.Component,
                                    com.day.cq.wcm.msm.api.LiveRelationshipManager,
                                    com.day.cq.wcm.msm.api.BlueprintManager" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    ResourceBundle resourceBundle = slingRequest.getResourceBundle(slingRequest.getLocale());

    AccessControlManager acm = null;
    Session session = resourceResolver.adaptTo(Session.class);
    Authorizable authorizable = resourceResolver.adaptTo(Authorizable.class);

    try {
        acm = session.getAccessControlManager();
    } catch (RepositoryException e) {
        log.error("Unable to get access manager", e);
    }

    Page targetPage = null;

    // get page object from suffix
    String pagePath = slingRequest.getRequestPathInfo().getSuffix();
    if( pagePath != null ) {
        Resource pageResource = slingRequest.getResourceResolver().resolve(pagePath);
        if( pageResource != null ) {
            targetPage = pageResource.adaptTo(Page.class);
        }
    }

    if( targetPage == null ) return;

    // page properties
    ValueMap targetPageProperties = targetPage.getProperties();

    Calendar modifiedDateRaw = targetPage.getLastModified();
    String modifiedDate = modifiedDateRaw == null ?
            i18n.get("never") :
            toRelativeTime(modifiedDateRaw, resourceBundle);
    String modifiedBy = xssAPI.filterHTML(AuthorizableUtil.getFormattedName(resourceResolver, targetPage.getLastModifiedBy()));

    Calendar publishedDateRaw = targetPageProperties.get("cq:lastReplicated", Calendar.class);
    String publishedDate = publishedDateRaw == null ?
            i18n.get("never") :
            toRelativeTime(publishedDateRaw, resourceBundle);
    String publishedBy = xssAPI.filterHTML(AuthorizableUtil.getFormattedName(resourceResolver, targetPageProperties.get("cq:lastReplicatedBy", "")));

    String lastReplicationAction = targetPageProperties.get("cq:lastReplicationAction", String.class);
    Calendar lastReplicationDateRaw = targetPageProperties.get("cq:lastRolledout", Calendar.class);
    String rolledOutDate = lastReplicationDateRaw == null ?
            i18n.get("never") :
            toRelativeTime(lastReplicationDateRaw, resourceBundle);
    boolean isDeactivated = "Deactivate".equals(lastReplicationAction);
    String publishStatus = "";
    if(isDeactivated){
        publishStatus = xssAPI.filterHTML(i18n.get("Page has been deactivated"));
    }else if(publishedDateRaw == null){
        publishStatus = xssAPI.filterHTML(i18n.get("Page is not published"));
    }else{
        publishStatus = xssAPI.filterHTML(i18n.get("Page has been published"));
    }

    boolean canModify = false;
    try {
        // Get the set of principals for authorizable
        Set<Principal> principals = new LinkedHashSet<Principal>();
        Principal principal = authorizable.getPrincipal();
        principals.add(principal);

        for (PrincipalIterator it = ((JackrabbitSession) session).getPrincipalManager().getGroupMembership(principal); it.hasNext();) {
            principals.add(it.nextPrincipal());
        }

        // Test the modify permission from allowed actions
        CqActions cqActions = new CqActions(session);
        Collection<String> allowedActions = cqActions.getAllowedActions(targetPage.getPath(), principals);
        canModify = allowedActions.contains("modify");
    } catch (RepositoryException e) {
        log.error("Unable to retrieve allowed user actions", e);
    }
    
    LiveRelationshipManager relationshipManager = sling.getService(LiveRelationshipManager.class);
    BlueprintManager bpm = resourceResolver.adaptTo(BlueprintManager.class);
    // condition copied to /cq/modules/wcm/content/jcr_root/libs/wcm/msm/components/touch-ui/renderconditions/isblueprint
    // please keep in sync
    boolean isBlueprint = false;
    if (relationshipManager != null) {
        isBlueprint =  bpm != null
                && bpm.getContainingBlueprint(targetPage.getPath()) != null
                && relationshipManager.isSource(targetPage.adaptTo(Resource.class));
    }

    ProviderSettingsManager providerSettingsManager = sling.getService(ProviderSettingsManager.class);
    boolean hasAnalytics = false;
    if(providerSettingsManager != null) {
        hasAnalytics = providerSettingsManager.hasActiveProviders(targetPage.adaptTo(Resource.class));
    }

    // dom attributes
    Config cfg = new Config(resource);
    AttrBuilder divAttrs = new AttrBuilder(request, xssAPI);
    divAttrs.add("id", cfg.get("id", String.class));
    divAttrs.addOther("path", resource.getPath());
    divAttrs.addOthers(cfg.getProperties(), "id");

    String propertyConfig = cfg.get("propertyURL", "/libs/wcm/core/content/sites/properties.html");
    String propertyURL = request.getContextPath() + propertyConfig + "?" + cfg.get("propertyURLParam", "item") + "=" + URLEncoder.encode(targetPage.getPath(), "utf-8");

    String publishLabel = i18n.get("Request publication approval");
    String unpublishLabel = i18n.get("Request unpublication  approval");
    String adminViewLabel = i18n.get("View in Admin");
    String propertiesLabel = i18n.get("Open Properties");
    String analyticsLabel = i18n.get("Open Analytics & Recommendations");
    String startWorkflowLabel = i18n.get("Start Workflow");
    String lockPageLabel = i18n.get("Lock Page");
    String unlockPageLabel = i18n.get("Unlock Page");
    String rolloutPageLabel = i18n.get("Rollout Page");
    String deleteLabel = i18n.get("Request deletion  approval");
    String promoteLabel = i18n.get("Promote Launch");
    String helpLabel = i18n.get("Help");

    boolean hasPermission = hasPermission(acm, targetPage, "crx:replicate");
    if (!hasPermission) {
        publishLabel = xssAPI.filterHTML(i18n.get("Request publication approval"));
        unpublishLabel = xssAPI.filterHTML(i18n.get("Request unpublication  approval"));
    }
    
    boolean hasLockUnlockPermission = hasPermission(acm, targetPage, "jcr:lockManagement");
    
    Component component = resourceResolver.getResource(targetPage.getContentResource().getResourceType()).adaptTo(Component.class);
    Resource dialog = resourceResolver.getResource("/mnt/override" + component.getPath() + "/cq:dialog");
    if (dialog == null) {
        dialog = resourceResolver.getResource("/mnt/override" + component.getPath() + "/dialog");
    }
    String contentPath = targetPage.getContentResource().getPath();
    String dialogSrc = request.getContextPath() + dialog.getResourceResolver().map(dialog.getPath()) + ".html" + contentPath;

    boolean isLaunchResource = LaunchUtils.isLaunchResourcePath(contentPath);
    AttrBuilder promoteActivatorAttrs = new AttrBuilder(request, xssAPI);
    Resource promoteWizardRes = resourceResolver.getResource(cfg.get("promoteWizardPath", String.class));
    if (promoteWizardRes == null) {
        promoteWizardRes = resourceResolver.getResource("wcm/core/content/sites/promotelaunchwizard");
    }
    promoteActivatorAttrs.add("data-promotewizardurl", promoteWizardRes.getPath() + ".html" + targetPage.getPath());
    
    AttrBuilder adminViewActivatorAttrs = new AttrBuilder(request, xssAPI);
    adminViewActivatorAttrs.add("data-adminurl", cfg.get("adminURL", "/sites.html"));
    if (targetPage.getParent() != null) {
        adminViewActivatorAttrs.add("data-parentpath", targetPage.getParent().getPath());
    }

    AttrBuilder propertiesActivatorAttrs = new AttrBuilder(request, xssAPI);
    propertiesActivatorAttrs.addClass("properties-activator");
    propertiesActivatorAttrs.add("data-path", contentPath);
    propertiesActivatorAttrs.add("data-dialog-src", dialogSrc);

    // Workflow (start and info)
    WorkflowStatus wfState = targetPage.adaptTo(WorkflowStatus.class);
    boolean isInWorkflow = (wfState != null && wfState.isInRunningWorkflow(true));
    AttrBuilder workflowActivatorAttrs = new AttrBuilder(request, xssAPI);
    workflowActivatorAttrs.add("data-path", targetPage.getPath());

%><div <%= divAttrs.build() %>>

    <div class="editor-PageInfoAction pageinfo-unpublishconfirm">
        <h2 class="editor-PageInfoAction-header coral-Heading coral-Heading--2"> <%= unpublishLabel %> </h2>

        <div class="editor-PageInfoAction-content">
            <span> <%= xssAPI.filterHTML(i18n.get("You are going to unpublish:")) %><br>
                <%= xssAPI.filterHTML(targetPageProperties.get("jcr:title", "")) %> </span>
            <div class="editor-PageInfoAction-buttons">
                <button class="coral-Button editor-PageInfoAction-buttons-button unpublishconfirm-cancel" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Cancel")) %>"> <%= xssAPI.filterHTML(i18n.get("Cancel")) %> </button>
                <button class="coral-Button editor-PageInfoAction-buttons-button coral-Button--primary unpublishconfirm-confirm primary cq-siteadmin-admin-actions-quickunpublish-activator" data-path="<%= targetPage.getPath() %>" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Confirm")) %>" data-edit=true> <%= xssAPI.filterHTML(i18n.get("Confirm")) %> </button>
            </div>
        </div>
    </div>

    <div class="pageinfo-statusandactions">
        <div class="endor-List">
            <div class="coral-MinimalButton endor-List-item editor-PageInfo-text">
                <%= i18n.get("Modified {0} by {1}", "0 is relative date, such as 'x minutes ago', 1 is user name", modifiedDate,  modifiedBy) %><br/>
                <% if (!isLaunchResource) { %>
                    <% if(isDeactivated) { %>
                       <%= i18n.get("{0} by {1}", "0 replaced with 'Page has been deactivated', 2 replaced with user name", publishStatus, publishedBy) %>
                    <% } else if(publishedDateRaw != null) { %>
                        <%= i18n.get("{0} {1} by {2}", "0 replaced with 'Page has been published', 2 replaced with time '5 minutes ago', 3 replaced with user name", publishStatus, publishedDate, publishedBy) %>
                    <% } else { %>
                        <%= publishStatus %>
                    <% } %>
                <% } %>
            </div>
            <% if(canModify) { %>
            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(propertiesLabel) %>" class="coral-MinimalButton endor-List-item endor-List-item--interactive js-editor-PageInfo-closePopover pageinfo-editproperties properties-activator editor-PageInfo-action" <%= propertiesActivatorAttrs.build() %>><%= xssAPI.filterHTML(propertiesLabel) %></button>
            <% } %>
            <% if(isBlueprint) { %>
            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(rolloutPageLabel) %>" id="rolloutBtn" class="coral-MinimalButton endor-List-item endor-List-item--interactive js-editor-PageInfo-closePopover editor-PageInfo-action"><%= xssAPI.filterHTML(rolloutPageLabel) %></button>
            <% } %>
            <% if(!isInWorkflow) {

            } %>
            <% if (!isLaunchResource) { %>
            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(publishLabel) %>" class="coral-MinimalButton endor-List-item endor-List-item--interactive js-editor-PageInfo-closePopover cq-siteadmin-admin-actions-quickpublish-activator editor-PageInfo-action" data-path="<%= targetPage.getPath() %>" data-edit="true"><%= xssAPI.filterHTML(publishLabel) %></button>
            <% } else { %>
            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(promoteLabel) %>" class="coral-MinimalButton endor-List-item endor-List-item--interactive js-editor-PageInfo-closePopover promotelaunch-activator editor-PageInfo-action" <%= promoteActivatorAttrs.build() %>><%= xssAPI.filterHTML(promoteLabel) %></button>
            <% } %>
            <% if(!targetPage.isLocked() && hasLockUnlockPermission) { %>
            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(lockPageLabel) %>" class="coral-MinimalButton endor-List-item endor-List-item--interactive js-editor-PageInfo-closePopover cq-author-lock-page editor-PageInfo-action"><%= xssAPI.filterHTML(lockPageLabel) %></button>
            <% } %>
            <% if(targetPage.isLocked() && hasLockUnlockPermission) { %>
            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(unlockPageLabel) %>" class="coral-MinimalButton endor-List-item endor-List-item--interactive js-editor-PageInfo-closePopover cq-author-unlock-page editor-PageInfo-action"><%= xssAPI.filterHTML(unlockPageLabel) %></button>
            <% } %>
            <% if(hasAnalytics) { %>
            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(analyticsLabel) %>" class="coral-MinimalButton endor-List-item endor-List-item--interactive js-editor-PageInfo-closePopover open-contentinsight editor-PageInfo-action"><%= xssAPI.filterHTML(analyticsLabel) %></button>
            <% } %>
            <% if (!isLaunchResource) { %>
            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(unpublishLabel) %>" class="coral-MinimalButton endor-List-item endor-List-item--interactive unpublish-confirmator editor-PageInfo-action" data-path="<%= targetPage.getPath() %>" data-edit="true"><%= xssAPI.filterHTML(unpublishLabel) %></button>
            <% } %>
            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(adminViewLabel) %>" class="coral-MinimalButton endor-List-item endor-List-item--interactive js-editor-PageInfo-closePopover pageinfo-adminview editor-PageInfo-action" <%= adminViewActivatorAttrs.build() %>><%= xssAPI.filterHTML(adminViewLabel) %></button>

            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(deleteLabel) %>" class="coral-MinimalButton endor-List-item endor-List-item--interactive js-editor-PageInfo-closePopover editor-PageInfo-action" data-path="<%= targetPage.getPath() %>" onclick="CQ_UI_siteadmin_quickDelete();"><%= xssAPI.filterHTML(deleteLabel) %></button>

            <button type="button" title="<%= xssAPI.encodeForHTMLAttr(helpLabel) %>" class="coral-MinimalButton endor-List-item endor-List-item--interactive js-editor-PageInfo-closePopover launch-tour editor-PageInfo-action"><%= xssAPI.filterHTML(helpLabel) %></button>
        </div>
    </div>

</div><%!

    private boolean hasPermission(AccessControlManager acm, Page page, String privilege) {
        try {
            if (acm != null) {
                Privilege p = acm.privilegeFromName(privilege);
                return acm.hasPrivileges(page.getPath(), new Privilege[]{p});
            }
        } catch (RepositoryException e) {
            // ignore
        }
        return false;
    }

    private String toRelativeTime(Calendar date, ResourceBundle rb) {
        String dateText = null;
        try {
            RelativeTimeFormat tf = new RelativeTimeFormat(RelativeTimeFormat.SHORT, rb);
            dateText = tf.format(date.getTimeInMillis(), true);
        } catch (IllegalArgumentException e) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateText = sdf.format(date.getTime());
        }
        return dateText;
    }
%>