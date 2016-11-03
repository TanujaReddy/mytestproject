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
--%><%
%><%@include file="/libs/granite/ui/global.jsp"
%><%@page session="false"
          import="java.util.HashMap,
                  java.util.Iterator,
                  java.util.Map,
                  org.apache.commons.lang.StringUtils,
                  org.apache.sling.commons.json.JSONObject,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag"%><%--###
ColorPicker
===========

.. granite:servercomponent:: /apps/global/components/foundation/customcolorpicker
   :supertype: /apps/global/components/foundation/customfield

   Colorpicker is a component that allows the user to pick predefined colors.
   The colors can be defined by configuring the sub-nodes with colors configuration.

   It extends :granite:servercomponent:`Field </apps/global/components/foundation/customfield>` component.

   It has the following content structure:

   .. gnd:gnd::

      [granite:FormColorPicker]

      /**
       * The id attribute.
       */
      - id (String)

      /**
       * The class attribute. This is used to indicate the semantic relationship of the component similar to ``rel`` attribute.
       */
      - rel (String)

      /**
       * The class attribute.
       */
      - class (String)

      /**
       * The title attribute.
       */
      - title (String) i18n

      /**
       * The name that identifies the field when submitting the form.
       */
      - name (String)
      
      /**
       * The value of the field.
       */
      - value (StringEL)

      /**
       * Indicates if the field is in disabled state.
       */
      - disabled (Boolean)

      /**
       * Displays the classic mode with palette shades.
       */
      - classicPaletteType (Boolean)

      /**
       * Displays the freestyle mode without palette shades.
       */
      - freestylePaletteType (Boolean)

      /**
       * Displays edit mode for selected color.
       */
      - editType (Boolean)
###--%><%

    Config cfg = cmp.getConfig();
    ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());

    Resource mappingsRes = null;
    Resource rt = resourceResolver.getResource(resource.getResourceType());
    if (rt != null) {
        mappingsRes = rt.getChild("mappings");
    }

    Map<String, String> colors = new HashMap<String, String>();

    if (mappingsRes != null) {
        Iterator<Resource> itMappings = mappingsRes.listChildren();

        while (itMappings.hasNext()) {
            Config mappingCfg = new Config(itMappings.next());

            String name = mappingCfg.get("name", String.class);
            String value = mappingCfg.get("value", String.class);

            if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
                colors.put(i18n.getVar(name), value);
            }
       }
    }

    Map<String, Boolean> pickerModes = new HashMap<String, Boolean>();
    pickerModes.put("classicPalette", cfg.get("classicPaletteType", false));
    pickerModes.put("freestylePalette", cfg.get("freestylePaletteType", true));
    pickerModes.put("edit", cfg.get("editType", false));

    // TODO use JSONStringer instead of JSONObject
    JSONObject colorpickerJson = new JSONObject();
    colorpickerJson.put("colors", colors);
    colorpickerJson.put("pickerModes", pickerModes);


    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();

    attrs.add("id", cfg.get("id", String.class));
    attrs.addRel(cfg.get("rel", String.class));
    attrs.addClass(cfg.get("class", String.class));
    attrs.add("title", i18n.getVar(cfg.get("title", String.class)));

    attrs.addClass("coral-ColorPicker");
    attrs.add("data-init", "customColorPicker");
    attrs.add("data-name", cfg.get("name", String.class));
    attrs.add("data-disabled", cfg.get("disabled", false));
    attrs.add("data-config", colorpickerJson.toString());
    attrs.add("value", vm.get("value", String.class));

    attrs.addOthers(cfg.getProperties(), "id", "class", "rel", "title", "name", "disabled", "value", "classicPaletteType", "freestylePaletteType", "editType", "fieldLabel", "fieldDescription", "renderReadOnly", "ignoreData");

%><span <%= attrs.build() %>><button class="coral-ColorPicker-button coral-MinimalButton" type="button"></button></span>