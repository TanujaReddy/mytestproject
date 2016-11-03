/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2013 Adobe Systems Incorporated
*  All Rights Reserved.
*
* NOTICE:  All information contained herein is, and remains
* the property of Adobe Systems Incorporated and its suppliers,
* if any.  The intellectual and technical concepts contained
* herein are proprietary to Adobe Systems Incorporated and its
* suppliers and are protected by trade secret or copyright law.
* Dissemination of this information or reproduction of this material
* is strictly forbidden unless prior written permission is obtained
* from Adobe Systems Incorporated.
**************************************************************************/

(function($) {

    var ICONS = {

        // Popover triggers
        "#format": "coral-Icon coral-Icon--text",
        "#justify": "coral-Icon coral-Icon--textLeft",
        "#lists": "coral-Icon coral-Icon--textBulleted",
        "#styles": "coral-Icon coral-Icon--textStyle",
        "#paraformat": "coral-Icon coral-Icon--textParagraph",

        // Commands
        "format#bold": "coral-Icon coral-Icon--textBold",
        "format#italic": "coral-Icon coral-Icon--textItalic",
        "format#underline": "coral-Icon coral-Icon--textUnderline",
        "customjustify#customjustifycenter": "coral-Icon coral-Icon--textCenter",
        "customjustify#customjustifyleft": "coral-Icon coral-Icon--textLeft",
        "customjustify#customjustifyright": "coral-Icon coral-Icon--textRight",
        "subsuperscript#subscript": "coral-Icon coral-Icon--textSubscript",
        "subsuperscript#superscript": "coral-Icon coral-Icon--textSuperscript",
        "edit#cut": "coral-Icon coral-Icon--cut",
        "edit#copy": "coral-Icon coral-Icon--copy",
        "edit#paste-default": "coral-Icon coral-Icon--paste",
        "edit#paste-plaintext": "coral-Icon coral-Icon--pasteText",
        "edit#paste-wordhtml": "coral-Icon coral-Icon--pasteHTML",
        "justify#justifyleft": "coral-Icon coral-Icon--textLeft",
        "justify#justifycenter": "coral-Icon coral-Icon--textCenter",
        "justify#justifyright": "coral-Icon coral-Icon--textRight",
        "lists#unordered": "coral-Icon coral-Icon--textBulleted",
        "lists#ordered": "coral-Icon coral-Icon--textNumbered",
        "lists#outdent": "coral-Icon coral-Icon--textIndentDecrease",
        "lists#indent": "coral-Icon coral-Icon--textIndentIncrease",
        "links#modifylink": "coral-Icon coral-Icon--link",
        "links#unlink": "coral-Icon coral-Icon--linkOff",
        "links#anchor": "coral-Icon coral-Icon--anchor",
        "table#table": "coral-Icon coral-Icon--table",
        "table#insertcolumn-before": "coral-Icon coral-Icon--tableColumnAddLeft",
        "table#insertcolumn-after": "coral-Icon coral-Icon--tableColumnAddRight",
        "table#removecolumn": "coral-Icon coral-Icon--tableColumnRemoveCenter",
        "table#insertrow-before": "coral-Icon coral-Icon--tableRowAddTop",
        "table#insertrow-after": "coral-Icon coral-Icon--tableRowAddBottom",
        "table#removerow": "coral-Icon coral-Icon--tableRowRemoveCenter",
        "table#mergecells-right": "coral-Icon coral-Icon--tableRowMerge",
        "table#mergecells-down": "coral-Icon coral-Icon--tableColumnMerge",
        "table#mergecells": "coral-Icon coral-Icon--tableMergeCells",
        "table#splitcell-horizontal": "coral-Icon coral-Icon--tableRowSplit",
        "table#splitcell-vertical": "coral-Icon coral-Icon--tableColumnSplit",
        "table#modifytableandcell": "coral-Icon coral-Icon--edit",
        "table#selectrow": "coral-Icon coral-Icon--tableSelectRow",
        "table#selectcolumn": "coral-Icon coral-Icon--tableSelectColumn",
        "table#ensureparagraph" : "coral-Icon coral-Icon--textParagraph",
        "table#removetable": "coral-Icon coral-Icon--delete",
        "table#exitTableEditing": "coral-Icon coral-Icon--close",
        "spellcheck#checktext": "coral-Icon coral-Icon--spellcheck",
        "undo#undo": "coral-Icon coral-Icon--undo",
        "undo#redo": "coral-Icon coral-Icon--redo",
        "findreplace#find": "coral-Icon coral-Icon--search",
        "findreplace#replace": "coral-Icon coral-Icon--findAndReplace",
        "misctools#specialchars": "coral-Icon coral-Icon--star",
        "misctools#sourceedit": "coral-Icon coral-Icon--fileCode",
        "fullscreen#toggle": "coral-Icon coral-Icon--fullScreen",
        "fullscreen#start": "coral-Icon coral-Icon--fullScreen",
        "fullscreen#finish": "coral-Icon coral-Icon--fullScreenExit",
        "control#close": "coral-Icon coral-Icon--close",
        "control#save": "coral-Icon coral-Icon--check"
    };

    var CLASSES = {
        "#format": "coral-RichText--multiSelect",
        "#justify": "coral-RichText--singleSelect",
        "fullscreen#toggle": "coral-RichText--modechanger",
        "fullscreen#start": "coral-RichText--modechanger",
        "fullscreen#finish": "coral-RichText--modechanger",
        "control#close": "coral-RichText--modechanger",
        "control#save": "coral-RichText--modechanger",
        "links#modifylink": "coral-RichText--trigger",
        "links#anchor": "coral-RichText--trigger",
        "findreplace#find": "coral-RichText--trigger",
        "findreplace#replace": "coral-RichText--trigger",
        "misctools#specialchars": "coral-RichText--trigger"
    };

    CUI.rte.ui.cui.CuiToolbarBuilder = new Class({

        toString: "CuiToolbarBuilder",

        extend: CUI.rte.ui.ToolbarBuilder,

        $editable: null,

        uiSettings: undefined,

        additionalClasses: {},

        // Helpers -------------------------------------------------------------------------

        _getUISettings: function(options) {

        	//This was added to fix the inline RTE edit.
        	if (options && options.uiSettings && options.uiSettings.cui) {
                this.uiSettings = options.uiSettings.cui;
            }

            if (this.uiSettings) {
                return this.uiSettings;
            }
            if (options && options.uiSettings && options.uiSettings.cui) {
                this.uiSettings = options.uiSettings.cui;
            } else {
                this.uiSettings = $.extend(true, {}, CUI.rte.ui.cui.DEFAULT_UI_SETTINGS);
            }
            var tk = CUI.rte.ui.ToolkitRegistry.get("cui");
            var adapter = tk.getToolkitData(CUI.rte.ui.ToolkitDefs.CONFIG_ADAPTER);
            if (typeof adapter === "function") {
                this.uiSettings = adapter(this.uiSettings, "uiSettings", options.componentType);
            }
            return this.uiSettings;
        },

        _registerIcons: function(iconDefs) {
            if (!iconDefs) {
                return;
            }
            CUI.rte.Common.removeJcrData(iconDefs);
            for (var node in iconDefs) {
                if (iconDefs.hasOwnProperty(node)) {
                    var icon = iconDefs[node];
                    if (icon.command && icon.icon) {
                        this.registerIcon(icon.command, icon.icon);
                    }
                }
            }
        },

        registerIcon: function(commandRef, iconClass) {
            ICONS[commandRef] = iconClass;
        },

        _getIconForCommand: function(commandRef) {
            if (ICONS.hasOwnProperty(commandRef)) {
                return ICONS[commandRef];
            }
            return undefined;
        },

        _registerAllAdditionalClasses: function(clsDefs) {
            var com = CUI.rte.Common;
            if (!clsDefs) {
                return;
            }
            com.removeJcrData(clsDefs);
            for (var node in clsDefs) {
                if (clsDefs.hasOwnProperty(node)) {
                    var clsDef = clsDefs[node];
                    if (clsDef.command && !com.isNull(clsDef.classes)) {
                        this.registerAdditionalClasses(clsDef.command, clsDef.classes);
                    }
                }
            }
        },

        /**
         * @param {String} commandRef The command refence (#trigger for popup triggers;
         *        plugin#command for active RTE buttons
         * @param {String} cssClasses Additional CSS classes; space separated
         */
        registerAdditionalClasses: function(commandRef, cssClasses) {
            CLASSES[commandRef] = cssClasses;
        },

        _getClassesForCommand: function(commandRef) {
            if (CLASSES.hasOwnProperty(commandRef)) {
                return CLASSES[commandRef];
            }
            if (this.additionalClasses && this.additionalClasses.hasOwnProperty(commandRef)) {
                return this.additionalClasses[commandRef];
            }
            return undefined;
        },

        _buildToolbar: function($editable, elements, options) {

            function getItem(id) {
                var itemCnt = items.length;
                for (var i = 0; i < itemCnt; i++) {
                    if (window.x) console.log(items[i].ref);
                    if (items[i].ref === id) {
                        return items[i];
                    }
                }
                return null;
            }

            var self = this;
            this.additionalClasses = options.additionalClasses;
            function createPopoverItem(poItemDef) {
                if (poItemDef !== "-") {
                    // popover item
                    var poItem = getItem(poItemDef);
                    if (poItem) {
                        var cmd = poItem.ref;
                        addClasses = self._getClassesForCommand(cmd);
                        addClasses = (addClasses ? " " + addClasses : "");
                        poItem.icon = poItem.icon || self._getIconForCommand(cmd);
                        poItem.addClasses = addClasses;
                        poItems.push(popoverItemTpl(poItem));
                    }
                } else {
                    // popover separator
                    poItems.push(separatorTpl());
                }
            }

            function createDynamicPopover(itemDef) {
                var defs = itemDef.split(":");
                if (defs.length >= 2) {
                    var plugin = options.editorKernel.getPlugin(defs[0]);
                    var propName = defs[1];
                    var prop = plugin[propName];
                    if (plugin && prop) {
                        if (typeof prop === "function") {
                            prop = prop.call(plugin);
                        }
                        if (prop) {
                            var template = CUI.rte.Templates[defs[2]];
                            poItems.push(template(prop));
                        }
                    }
                }
            }

            function featureEnabled(pluginId, feature) {
                var plugin = options.editorKernel.getPlugin(pluginId);
                var isEnabled;
                if (feature) {
                    isEnabled = plugin.isFeatureEnabled(feature);
                } else {
                    isEnabled = plugin.isAnyFeatureEnabled();
                }
                return isEnabled;
            }

            function popoverAvailable(popoverDef) {
                if (!popoverDefs) {
                    return false;
                }
                var id = popoverDef.substring(1);
                if (popoverDefs.hasOwnProperty(id)) {
                    var popoverItems = popoverDefs[id].items;
                    if (popoverItems)
                        if ((typeof popoverItems === "string") &&
                                (popoverItems.indexOf(":") > 0)) {
                            var def = popoverItems.split(":");
                            if (featureEnabled(def[0])) {
                                return true;
                            }
                        } else {
                            for (var i = 0; i < popoverItems.length; i++) {
                            var ref = popoverItems[i].split("#");
                            if (featureEnabled(ref[0], ref[1])) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }

            var com = CUI.rte.Common;
            var uiSettings = this._getUISettings(options);
            var addClasses;

            var items = [ ];
            for (var e = 0; e < elements.length; e++) {
                elements[e].addToToolbar(items);
            }
            // reorder according to settings
            com.removeJcrData(uiSettings);
            var toolbars = [ ];
            var toolbarTpl = CUI.rte.Templates["toolbar"];
            var itemTpl = CUI.rte.Templates["toolbar-item"];
            var triggerTpl = CUI.rte.Templates["popover-trigger"];
            var popoverTpl = CUI.rte.Templates["popover"];
            var popoverItemTpl = CUI.rte.Templates["popover-item"];
            var separatorTpl = CUI.rte.Templates["separator"];
            var isPrevSeparator = false;
            for (var tbId in uiSettings) {
                if (uiSettings.hasOwnProperty(tbId)) {
                    var toolbar = uiSettings[tbId];
                    var tbItems = [ ];
                    var popovers = [ ];
                    var itemDefs = toolbar.toolbar;
                    if (!itemDefs) {
                        continue;
                    }
                    var popoverDefs = toolbar.popovers;

                    // toolbar
                    var itemCnt = itemDefs.length;
                    for (var i = 0; i < itemCnt; i++) {
                        var itemToAdd = itemDefs[i];
                        if (itemToAdd && itemToAdd.length) {
                            if (itemToAdd.charAt(0) === "#") {
                                if (popoverAvailable(itemToAdd)) {
                                    // popover trigger
                                    addClasses = this._getClassesForCommand(itemToAdd);
                                    addClasses = (addClasses ? " " + addClasses : "");
                                    tbItems.push(triggerTpl({
                                        "ref": itemToAdd,
                                        "icon": this._getIconForCommand(itemToAdd),
                                        "addClasses": addClasses
                                    }));
                                    isPrevSeparator = false;
                                }
                            } else if (itemToAdd === "-") {
                                // separator - only, if the previously added item is not
                                // a separator as well
                                if (!isPrevSeparator) {
                                    tbItems.push(separatorTpl());
                                    isPrevSeparator = true;
                                }
                            } else {
                                // regular item
                                var element = getItem(itemToAdd);
                                if (element &&
                                        featureEnabled(element.plugin, element.command)) {
                                    addClasses = this._getClassesForCommand(itemToAdd);
                                    addClasses = (addClasses ? " " + addClasses : "");
                                    element.icon = element.icon ||
                                            this._getIconForCommand(element.ref);
                                    element.addClasses = addClasses;
                                    tbItems.push(itemTpl(element));
                                    isPrevSeparator = false;
                                }
                            }
                        }
                    }
                    // popovers
                    if (popoverDefs) {
                        com.removeJcrData(popoverDefs);
                        for (var p in popoverDefs) {
                            if (popoverDefs.hasOwnProperty(p)) {
                                var poItems = [ ];
                                var popoverToProcess = popoverDefs[p];
                                var poItemDefs = popoverToProcess.items;
                                if (CUI.rte.Utils.isString(poItemDefs)) {
                                    createDynamicPopover(poItemDefs);
                                } else {
                                    var poItemCnt = poItemDefs.length;
                                    for (var pi = 0; pi < poItemCnt; pi++) {
                                        createPopoverItem(poItemDefs[pi]);
                                    }
                                }
                                popovers.push(popoverTpl({
                                    "ref": popoverToProcess.ref,
                                    "popoverItems": poItems
                                }));
                            }
                        }
                    }

                    // add representation
                    toolbars.push({
                        "id": tbId,
                        "toolbar": toolbarTpl({
                            "toolbarItems": tbItems
                        }),
                        "popovers": popovers
                    });
                }
            }

            var $toolbar = $(CUI.rte.Templates["tb-container"]({
                "toolbars": toolbars
            }));
            var $ui = CUI.rte.UIUtils.createOrGetUIContainer($editable);
            $ui.append($toolbar)
        },

        // Toolbar management --------------------------------------------------------------

        /**
         * Notify all elements to use the passed toolbar.
         * @param {CUI.rte.ui.Toolbar} toolbar The toolbar to use
         * @param {Boolean} skipHandlers true to skip attaching handlers to buttons
         */
        notifyToolbar: function(toolbar, skipHandlers) {
            var groupCnt = this.groups.length;
            for (var groupIndex = 0; groupIndex < groupCnt; groupIndex++) {
                var groupElements = this.groups[groupIndex].elements;
                var elCnt = groupElements.length;
                for (var elIndex = 0; elIndex < elCnt; elIndex++) {
                    var element = groupElements[elIndex].def;
                    var toolbarDef = element.createToolbarDef();
                    if (toolbarDef != null) {
                        element.notifyToolbar(toolbar, skipHandlers);
                    }
                }
            }
        },

        /**
         * Create the abstracted toolbar.
         * @return {CUI.rte.ui.Toolbar} The toolbar
         * @ignore
         */
        createToolbar: function(options) {
            var com = CUI.rte.Common;
            var toolbarItems = [ ];
            var elements = [ ];
            var elementMap = { };
            var groupCnt = this.groups.length;

            // create data model
            var hasMembers = false;
            for (var groupIndex = 0; groupIndex < groupCnt; groupIndex++) {
                var groupElements = this.groups[groupIndex].elements;
                var elCnt = groupElements.length;
                for (var elIndex = 0; elIndex < elCnt; elIndex++) {
                    var element = groupElements[elIndex].def;
                    if ((elIndex == 0) && hasMembers) {
                        toolbarItems.push("-");
                        hasMembers = false;
                    }
                    var toolbarDef = element.createToolbarDef();
                    if (toolbarDef != null) {
                        if (!CUI.rte.Utils.isArray(toolbarDef)) {
                            toolbarDef = [ toolbarDef ];
                        }
                        var itemCnt = toolbarDef.length;
                        for (var i = 0; i < itemCnt; i++) {
                            var def = toolbarDef[i];
                            toolbarItems.push(def);
                            elementMap[def.id] = def;
                        }
                        elements.push(element);
                        hasMembers = true;
                    }
                }
            }

            // register additional/override existing icons, if available
            var uiSettings = this._getUISettings(options);
            if (uiSettings && uiSettings.hasOwnProperty("icons")) {
                this._registerIcons(uiSettings["icons"]);
                delete uiSettings["icons"];
            }
            if (uiSettings && uiSettings.hasOwnProperty("additionalClasses")) {
                this._registerAllAdditionalClasses(uiSettings["additionalClasses"]);
                delete uiSettings["additionalClasses"];
            }

            // attach model to UI/create UI from model
            var $editable = options.$editable;
            var $toolbar = CUI.rte.UIUtils.getToolbar($editable, options.tbType);
            var elementCnt = elements.length;
            if (!$toolbar) {
                // create new toolbar if none is present yet
                this._buildToolbar($editable, elements, options);
            }

            // use existing/newly created toolbar
            var toolbar = new CUI.rte.ui.cui.ToolbarImpl(elementMap, $editable,
                    options.tbType, options.isFullScreen);
            for (var e = 0; e < elementCnt; e++) {
                elements[e].notifyToolbar(toolbar);
            }
            toolbar.createPopoverTriggerToElementMapping();

            // add marker class for touch/desktop usage
            var $ui = CUI.rte.UIUtils.createOrGetUIContainer($editable);
            if (com.ua.isTouch && !$ui.hasClass("is-touch")) {
                $ui.addClass("is-touch");
            }
            if (!com.ua.isTouch && !$ui.hasClass("is-desktop")) {
                $ui.addClass("is-desktop");
            }

            return toolbar;
        },


        // Creating elements -------------------------------------------------------------------

        createElement: function(id, plugin, toggle, tooltip, css, cmdDef) {
            return new CUI.rte.ui.cui.ElementImpl(id, plugin, toggle, tooltip, css,
                    cmdDef);
        },

        createParaFormatter: function(id, plugin, tooltip, formats) {
            return new CUI.rte.ui.cui.ParaFormatterImpl(id, plugin, false, tooltip, false,
                    undefined, formats);
        },

        createStyleSelector: function(id, plugin, tooltip, styles) {
            return new CUI.rte.ui.cui.StyleSelectorImpl(id, plugin, false, tooltip, false,
                    undefined, styles);
        }

    });

    CUI.rte.ui.cui.DEFAULT_UI_SETTINGS = {
        "inline": {
            // TODO adjust to final decision of default inline toolbar settings
            "toolbar": [
                "#format",
                "-",
                "#justify",
                "-",
                "#lists",
                "-",
                "links#modifylink",
                "links#unlink",
                "-",
                "fullscreen#start",
                "-",
                "control#close",
                "control#save"
            ],
            "popovers": {
                "format": {
                    "ref": "format",
                    "items": [
                        "format#bold",
                        "format#italic",
                        "format#underline"
                    ]
                },
                "justify": {
                    "ref": "justify",
                    "items": [
                        "justify#justifyleft",
                        "justify#justifycenter",
                        "justify#justifyright"
                    ]
                },
                "lists": {
                    "ref": "lists",
                    "items": [
                        "lists#unordered",
                        "lists#ordered",
                        "lists#outdent",
                        "lists#indent"
                    ]
                },
                "styles": {
                    "ref": "styles",
                    "items": "styles:getStyles:styles-pulldown"
                },
                "paraformat": {
                    "ref": "paraformat",
                    "items": "paraformat:getFormats:paraformat-pulldown"
                }
            }
        },
        "fullscreen": {
            "toolbar": [
                "format#bold",
                "format#italic",
                "format#underline",
                "customjustify#customjustifycenter",
                "customjustify#customjustifyleft",
                "customjustify#customjustifyright",
                "subsuperscript#subscript",
                "subsuperscript#superscript",
                "-",
                "edit#cut",
                "edit#copy",
                "edit#paste-default",
                "edit#paste-plaintext",
                "edit#paste-wordhtml",
                "-",
                "links#modifylink",
                "links#unlink",
                "links#anchor",
                "-",
                "findreplace#find",
                "findreplace#replace",
                "-",
                "undo#undo",
                "undo#redo",
                "-",
                "justify#justifyleft",
                "justify#justifycenter",
                "justify#justifyright",
                "-",
                "lists#unordered",
                "lists#ordered",
                "lists#outdent",
                "lists#indent",
                "-",
                "table#table",
                "spellcheck#checktext",
                "misctools#specialchars",
                "misctools#sourceedit",
                "-",
                // TODO Source code (?)
                "#styles",
                "#paraformat",
                "-",
                "fullscreen#finish"
            ],
            "popovers": {
                "styles": {
                    "ref": "styles",
                    "items": "styles:getStyles:styles-pulldown"
                },
                "paraformat": {
                    "ref": "paraformat",
                    "items": "paraformat:getFormats:paraformat-pulldown"
                }
            }
        },
        "tableEditOptions": {
            "toolbar": [
                "table#insertcolumn-before",
                "table#insertcolumn-after",
                "table#removecolumn",
                "-",
                "table#insertrow-before",
                "table#insertrow-after",
                "table#removerow",
                "-",
                "table#mergecells-right",
                "table#mergecells-down",
                "table#mergecells",
                "table#splitcell-horizontal",
                "table#splitcell-vertical",
                "-",
                "table#selectrow",
                "table#selectcolumn",
                "-",
                "table#ensureparagraph",
                "-",
                "table#modifytableandcell",
                "table#removetable",
                "-",
                "undo#undo",
                "undo#redo",
                "-",
                "table#exitTableEditing",
                "-"
            ]
        }
    };

})(window.jQuery);
