
CUI.rte.plugins.CustomJustifyPlugin = new Class({

    JUSTIFY_LEFT_PLUGIN: "customjustifyleft",
    JUSTIFY_CENTER_PLUGIN: "customjustifycenter",
    JUSTIFY_RIGHT_PLUGIN: "customjustifyright",

    PLUGIN_CLASS_MAP: {
    	customjustifyleft: "span-text-align--left",
    	customjustifycenter: "span-text-align--center",
    	customjustifyright: "span-text-align--right"
	},

    toString: "CustomJustifyPlugin",

    extend: CUI.rte.plugins.Plugin,

    /**
     * @private
     */
    customJustifyLeftUI: null,

    /**
     * @private
     */
    customJustifyCenterUI: null,

    /**
     * @private
     */
    customJustifyRightUI: null,


    getFeatures: function() {
        return [ this.JUSTIFY_LEFT_PLUGIN, this.JUSTIFY_CENTER_PLUGIN, this.JUSTIFY_RIGHT_PLUGIN ];
    },

    initializeUI: function(tbGenerator) {
        var plg = CUI.rte.plugins;
        var ui = CUI.rte.ui;
        if (this.isFeatureEnabled(this.JUSTIFY_LEFT_PLUGIN)) {
            this.customJustifyLeftUI = tbGenerator.createElement(this.JUSTIFY_LEFT_PLUGIN, this, true,
                    this.getTooltip(this.JUSTIFY_LEFT_PLUGIN));
            tbGenerator.addElement("format", plg.Plugin.SORT_FORMAT, this.customJustifyLeftUI, 100);
        }
        if (this.isFeatureEnabled(this.JUSTIFY_CENTER_PLUGIN)) {
            this.customJustifyCenterUI = tbGenerator.createElement(this.JUSTIFY_CENTER_PLUGIN, this, true,
                    this.getTooltip(this.JUSTIFY_CENTER_PLUGIN));
            tbGenerator.addElement("format", plg.Plugin.SORT_FORMAT, this.customJustifyCenterUI, 110);
        }
        if (this.isFeatureEnabled(this.JUSTIFY_RIGHT_PLUGIN)) {
            this.customJustifyRightUI = tbGenerator.createElement(this.JUSTIFY_RIGHT_PLUGIN, this, true,
                    this.getTooltip(this.JUSTIFY_RIGHT_PLUGIN));
            tbGenerator.addElement("format", plg.Plugin.SORT_FORMAT, this.customJustifyRightUI, 120);
        }
    },

    /**
     * Checks all plugins and deactivate them if they are activated (except the plugin id passed as a parameter).
     * @private
     */
     deactivatePlugins: function(pluginID) {
        var selectionDef = this.editorKernel.currentAnalyzedSelection;

        //iterate through plugins
        for (var plugin in this.PLUGIN_CLASS_MAP) {
            if (plugin != pluginID) {
                //checking if plugin is activated
                if (this.queryState(selectionDef, plugin)) {
                    this.editorKernel.relayCmd(plugin, { "class": this.PLUGIN_CLASS_MAP[plugin]});
                }
            }
        }
    },

    execute: function(pluginID) {

        //getting corresponding class for plugin in order to apply it to span tag
		var cssClass = this.PLUGIN_CLASS_MAP[pluginID];
        if (cssClass != null) {
            this.deactivatePlugins(pluginID);
            this.editorKernel.relayCmd(pluginID, { "class": cssClass});

        }
    },

    updateState: function(selDef) {

        if (this.customJustifyLeftUI != null) {
            var hasCustomJustifyLeft = this.queryState(selDef, this.JUSTIFY_LEFT_PLUGIN);
            this.customJustifyLeftUI.setSelected(hasCustomJustifyLeft);
        }
        if (this.customJustifyCenterUI != null) {
            var hasCustomJustifyCenter = this.queryState(selDef,this.JUSTIFY_CENTER_PLUGIN);
            this.customJustifyCenterUI.setSelected(hasCustomJustifyCenter);
        }
        if (this.customJustifyRightUI != null) {
            var hasCustomJustifyRight = this.queryState(selDef, this.JUSTIFY_RIGHT_PLUGIN);
            this.customJustifyRightUI.setSelected(hasCustomJustifyRight);
        }
    },

    notifyPluginConfig: function(pluginConfig) {
        pluginConfig = pluginConfig || { };
        var defaults = {
            "tooltips": {
                "customjustifyleft": {
                    "title": CUI.rte.Utils.i18n("plugins.justify.leftTitle"),
                    "text": CUI.rte.Utils.i18n("plugins.justify.leftText")
                },
                "customjustifycenter": {
                    "title": CUI.rte.Utils.i18n("plugins.justify.centerTitle"),
                    "text": CUI.rte.Utils.i18n("plugins.justify.centerText")
                },
                "customjustifyright": {
                    "title": CUI.rte.Utils.i18n("plugins.justify.rightTitle"),
                    "text": CUI.rte.Utils.i18n("plugins.justify.rightText")
                }
            }
        };
        CUI.rte.Utils.applyDefaults(pluginConfig, defaults);
        this.config = pluginConfig;
    },

    queryState: function(selectionDef, pluginID) {
        var com = CUI.rte.Common;

        //getting html tag that plugin applies
        var tagName = new CUI.rte.commands.DefaultFormatting()._getTagNameForCommand(pluginID);
        if (!tagName) {
            return undefined;
        }

        //checking if selection has tag and css class that plugin applies
        var context = selectionDef.editContext;
        var selection = selectionDef.selection;
        return (com.getTagInPath(context, selection.startNode, tagName, { "class": this.PLUGIN_CLASS_MAP[pluginID]}) != null);
    }

});


// register plugin
CUI.rte.plugins.PluginRegistry.register("customjustify", CUI.rte.plugins.CustomJustifyPlugin);
