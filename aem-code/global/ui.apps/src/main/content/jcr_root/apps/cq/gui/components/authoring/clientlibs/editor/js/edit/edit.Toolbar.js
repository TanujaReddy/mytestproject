/*
 *
 * This is an overlay for (/libs/cq/gui/components/authoring/clientlibs/editor/js/edit/edit.Toolbar.js)
 * To fix Edit toolbar Clipboard Copy/Pate bug, fixes from AEM ServicePack1, comments are provided in the line where fixes are done
 * TODO: Verify and Cleanup relevant fixes from AEM ServicePack1
 */
;(function ($, ns, channel, window, undefined) {

    /**
     *
     * default actions for the editables
     */
    var defaultActions = {
        // actions
        'EDIT': {
            icon: 'coral-Icon--edit',
            text: Granite.I18n.get('Edit'),
            handler: function (editable, param, target) {
                ns.edit.actions.doInPlaceEdit(editable);
                // Start - Toolbar Copy/Paste: fix from AEM ServicePack1
                if(editable.config.editConfig.inplaceEditingConfig.editorType === "hybrid") {
                    return false;
                }
                // End - Toolbar Copy/Paste: fix from AEM ServicePack1
            },
            condition: function (editable) {
                var canModify = ns.page.info.permissions && ns.page.info.permissions.modify; // same as for Configure
                return canModify && ns.edit.actions.canInPlaceEdit(editable);
            },
            isNonMulti: true // means it could be executed when only one editable is selected
        },
        'CONFIGURE': {
            icon: 'coral-Icon--wrench',
            text: Granite.I18n.get('Configure'),
            handler: function (editable, param, target) {
                ns.edit.actions.doConfigure(editable);
                // Start - Toolbar Copy/Paste: fix from AEM ServicePack1

                // do not close toolbar
                //                return false;

                // End - Toolbar Copy/Paste: fix from AEM ServicePack1
            },
            condition: function (editable) {
                var canModify = ns.page.info.permissions && ns.page.info.permissions.modify;
                return !!editable.config.dialog && canModify;
            },
            isNonMulti: true // means it could be executed when only one editable is selected
        },
        'COPY': {
            icon: 'coral-Icon--copy',
            text: Granite.I18n.get('Copy'),
            shortcut: 'ctrl+c',
            handler: function (editable, param, target) {
                var editables = ns.selection.getAllSelected();
                ns.edit.actions.doCopyToClipboard(editables);
            }
        },
        'MOVE': {
            icon: 'coral-Icon--cut',
            text: Granite.I18n.get('Cut'),
            shortcut: 'ctrl+x',
            handler: function (editable, param, target) {
                var editables = ns.selection.getAllSelected();
                ns.edit.actions.doCutToClipboard(editables);
            },
            condition: function (editable) {
                return ns.page.info.permissions && ns.page.info.permissions.modify;
            }
        },
        'DELETE': {
            icon: 'coral-Icon--delete',
            text: Granite.I18n.get('Delete'),
            shortcut: 'ctrl+del',
            handler: function (editable, param, target) {
                var editables = ns.selection.getAllSelected();
                ns.edit.actions.doDeleteConfirm(editables);
            },
            condition: function (editable) {
                return ns.page.info.permissions && ns.page.info.permissions.modify;
            }
        },
        'INSERT': {
            icon: 'coral-Icon--paste',
            text: Granite.I18n.get('Paste'),
            shortcut: 'ctrl+v',
            handler: function (editableBefore, param, target) {
                ns.edit.actions.doPasteFromClipboard(editableBefore);
            },
            condition: function (editable) {
                var canModify = ns.page.info.permissions && ns.page.info.permissions.modify;
                return !ns.clipboard.isEmpty() && canModify;
            },
            isNonMulti: true
        },
        'INSERT_COMPONENT': {
            icon: 'coral-Icon--add',
            text: Granite.I18n.get('Insert Component'),
            handler: function (editableBefore, param, target) {
                ns.edit.actions.openInsertDialog(editableBefore);
            },
            // Start - Toolbar Copy/Paste: fix from AEM ServicePack1
            condition: function (editable) {
                var parent = ns.store.getParent(editable);
                var allowedComponents = parent && ns.page.calculateAllowedComponents(ns.page.design, parent);
                var canModify = ns.page.info.permissions && ns.page.info.permissions.modify;
                var hasInsertAction = editable.config.editConfig.actions.indexOf('INSERT') > -1;
                var hasNotEditAction = editable.config.editConfig.actions.indexOf('EDIT')  < 0 &&
                    editable.config.editConfig.actions.indexOf('CONFIGURE') < 0;

                return canModify && hasNotEditAction && hasInsertAction && !!allowedComponents.length;
            },

            // End - Toolbar Copy/Paste: fix from AEM ServicePack1
            isNonMulti: true
        },
        'PARENT': {
            icon: 'coral-Icon--selectContainer',
            text: Granite.I18n.get('Parent'),
            handler: function (editable, param, target) {
                target.addClass('is-active');
                ns.edit.actions.doSelectParent(editable, target);
                // do not close toolbar
                return false;
            },
            isNonMulti: true
        },
        'GROUP': {
            icon: 'coral-Icon--group',
            text: Granite.I18n.get('Group'),
            shortcut: function (editable, keymap) {
                if (keymap.shift) {
                    this.getButton('GROUP').addClass('is-active');

                    var self = this;
                    channel.one("keyup", function () { // when released
                        self.getButton('GROUP').removeClass('is-active');
                    });
                }
                // do not close toolbar
                return false;
            },
            handler: function (editable, param, target) {
                if (!ns.selection.isGroupSelection()) {
                    target.addClass('is-active');
                    ns.selection.buttonPressed = true;
                } else {
                    target.removeClass('is-active');
                    ns.selection.buttonPressed = false;
                }
                // do not close toolbar
                return false;
            },
            render: function (dom) {
                return dom.toggleClass('is-active', ns.selection.isGroupSelection());
            }
        }
    };

    /**
     * Toolbar to be used in editer
     *
     * @constructor
     */
    ns.edit.Toolbar = function () {
        ns.edit.Toolbar.super_.constructor.apply(this, arguments);
    };

    /**
     * @readonly
     * @type Object default actions for the editables
     */
    ns.edit.Toolbar.defaultActions = defaultActions;

    ns.util.inherits(ns.edit.Toolbar, ns.ui.Toolbar);

    ns.edit.Toolbar.prototype.init = function () {
        // remember custom actions
        this._customActions = [];

        // set the default actions
        return ns.edit.Toolbar.super_.init.call(this, {
            actions: defaultActions
        });
    };

    ns.edit.Toolbar.prototype.destroy = function () {
        ns.edit.Toolbar.super_.destroy.apply(this, arguments);
    };

    ns.edit.Toolbar.prototype.registerAction = function (name, action) {
        this._customActions.push(name);

        ns.edit.Toolbar.super_.registerAction.apply(this, arguments);
    };

    ns.edit.Toolbar.prototype.appendButton = function (editable, name, action) {
        if (ns.selection.isGroupSelection() && action.isNonMulti) {
            return;
        }

        ns.edit.Toolbar.super_.appendButton.apply(this, arguments);
    };

    ns.edit.Toolbar.prototype.render = function (editable) {
        var actionName, actionHandler, i;

        // remove previous editable content
        this.dom.empty();

        var availableActions = editable.config.editConfig.actions;

        if (ns.selection.isGroupSelection()) {

            availableActions = (ns.selection.getAllSelected()

                // get the selected editables' available set of actions
                .map(function (editable) {
                    return editable.config.editConfig.actions
                })

                // intersection of all available set of actions
                .reduce(function (a,b){
                    return $(a).filter(b)
                }));
        }

        for (i=0; i < availableActions.length; i++) {
            actionName = availableActions[i];
            actionHandler = this.config.actions[actionName] || availableActions[i];

            this.appendButton(editable, actionHandler.name || actionName, actionHandler);
        }

        // consider also the custom actions
        for (i=0; i < this._customActions.length; i++) {
            actionName = this._customActions[i];
            actionHandler = this.config.actions[actionName];

            this.appendButton(editable, actionName, actionHandler);
        }

        return this;
    };

    ns.edit.Toolbar.prototype.handleEvent = function (event) {
        if (this.isDisabled) {
            return false;
        }

        var target = $(event.currentTarget),
            path = target.data('path'),
            action = target.data('action'),
            param = target.data('param'),
            editable = ns.store.find(path)[0],
            actionObj = this.config.actions[action] || $.grep(editable.config.editConfig.actions, function (item) {
                return item && item.name === action;
            })[0],
            ret;

        event.stopPropagation();

        try {
            ret = actionObj.handler.call(editable, editable, param, target);

            // close toolbar if not explicitly returning "false"
            if (ret !== false) {
                this.close();
            }
        } catch (e) {
            // eg, if the action custom code contains ExtJS code
            channel.trigger($.Event('error', {
                message: "An error has occured during execution of the selected action: " + e.message,
                exception: e
            }));
        }
    };

        ns.edit.Toolbar.prototype.checkActionCondition = function(actionName, editable) {
        var actionObj = this.config.actions[actionName] || $.grep(editable.config.editConfig.actions, function (item) {
            return item && item.name === action;
        })[0];

        return actionObj
                && actionObj.condition
                && actionObj.condition(editable)
    };



}(jQuery, Granite.author, jQuery(document), this));
