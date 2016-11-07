/*
 *
 * This is an overlay for (/libs/cq/gui/components/authoring/clientlibs/editor/js/ui/ui.Toolbar.js)
 * To fix Edit toolbar Clipboard Copy/Pate bug, fixes from AEM ServicePack1, comments are provided in the line where fixes are done
 * TODO: Verify and Cleanup relevant fixes from AEM ServicePack1
 */
;(function ($, ns, channel, window, undefined) {

    var $doc = $(document),
        dom = $doc.find('#EditableToolbar'),
        toolbarHeight = 44;

    /**
     * Toolbar to be used in editor
     *
     * @constructor
     */
    ns.ui.Toolbar = function () {
        // allows to create a new Toolbar
        // to add actions including keyboard access (keyboardController replacement)
        this.dom = dom;

        // allows / block user interaction (clicks, keyboard shortcuts)
        this.isDisabled = false;
    };

    ns.ui.Toolbar.prototype.init = function (config) {
        var self = this;

        this.config = config;
        this._currentButtons = {};

        this._bindEvents();

        return this;
    };

    /**
     * teardown function, remove all events and generated UI
     */
    ns.ui.Toolbar.prototype.destroy = function () {
        this._unbindEvents();

        this.dom.empty();
    };

    /**
     *
     * @private
     */
    ns.ui.Toolbar.prototype._bindEvents = function () {
        // actual clicks on a button
        dom.on('click.toolbar', '.cq-editable-action', this.handleEvent.bind(this));

        // the shortcut handling
        $doc.on('keydown.toolbar', this.handleKeypress.bind(this));

        // block / allow interactions when dialog is opened / closed
        channel.on('dialog-ready.block-interactions', this.disable.bind(this));
        channel.on('dialog-closed.allow-interactions', this.enable.bind(this));
    };

    /**
     *
     * @private
     */
    ns.ui.Toolbar.prototype._unbindEvents = function () {
        dom.off('click.toolbar', '.cq-editable-action');
        $doc.off('keydown.toolbar');

        channel.off('dialog-ready.block-interactions');
        channel.off('dialog-closed.allow-interactions');
    };

    /**
     * adds an action to the global config
     * @param {String} name unique name for the action
     * @param {Object} action configuration object for the toolbar
     * @param {Function} [action.condition] function which returns a Boolean to indicate if the button should be rendered
     * @param {Function} [action.render] custom render function which receives the rendered node before it is attached to the toolbar
     * @param {String} [action.icon] icon name to be shown on the button
     * @param {String} action.text decription for the button
     * @param {String|Function} action.shortcut
     * @param {Function} action.handler function to be called when the button is pressed
     */
    ns.ui.Toolbar.prototype.registerAction = function (name, action) {
        this.config.actions[name] = action;
    };

    /**
     * renders a button in the toolbar
     *
     * @param {Editable} editable
     * @param {String} name unique name for the action
     * @param {Object} action configuration object for the toolbar
     * @param {Function} [action.condition] function which returns a Boolean to indicate if the button should be rendered
     * @param {Function} [action.render] custom render function which receives the rendered node before it is attached to the toolbar
     * @param {String} [action.icon] icon name to be shown on the button
     * @param {String} action.text decription for the button
     * @param {String|Function} action.shortcut
     * @param {Function} action.handler function to be called when the button is pressed
     *
     * @returns {jQuery} the button HTML Element
     */
    ns.ui.Toolbar.prototype.appendButton = function (editable, name, action) {
        var button;

        // stop in case a existing condition is invalid
        if (action.condition && !action.condition(editable)) {
            return;
        }

        // add button only if a handler is existing
        if (action.handler) {
            button = $('<button/>', {
                'class': 'coral-Button coral-Button--quiet cq-editable-action',
                'type': 'button',
                'data-action': name,
                'data-path': editable.path,
                'title': action.text // already i18n'ed
            });

            if (action.icon) {
                button.append($('<i/>', {
                    'class': 'coral-Icon ' + action.icon + ' coral-Icon--sizeS',
                    'title': action.text // already i18n'ed
                }));
            } else {
                button.append($('<span/>', {
                    'class': 'cq-EditableToolbar-text',
                    'text': action.text // already i18n'ed
                }));
            }

            // enable customizable rendering
            if (action.render) {
                action.render(button, editable).appendTo(this.dom);
            } else {
                button.appendTo(this.dom);
            }

            this._currentButtons[name] =  button;
        }

        return button;
    };

    /**
     * handles interactions with editable action triggers
     * expects to have data-action, data-path and optional data-param
     */
    ns.ui.Toolbar.prototype.handleEvent = function (event) {
        if (this.isDisabled) {
            return false;
        }

        var target = $(event.currentTarget),
            path = target.data('path'),
            action = target.data('action'),
            param = target.data('param'),
            editable = ns.store.find(path)[0],
            actionObj = this.config.actions[action],
            ret;

        event.stopPropagation();

        try {
            ret = actionObj.handler.call(this, editable, param, target);

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

    /**
     * handles the keyboard shortcuts
     *
     * @param event
     */
    ns.ui.Toolbar.prototype.handleKeypress = function (event) {
        var self = this,
            key = (event.keyCode >= 48 && event.keyCode <= 57) || (event.keyCode >= 65 && event.keyCode <= 90) ?
                String.fromCharCode(event.keyCode).toLowerCase() :
                null,
            keymap = {
                alt: event.altKey,
                shift: event.shiftKey,
                del: event.which === 8,
                ctrl: event.ctrlKey || event.metaKey
            };

        if (key) {
            keymap[key] = true;
        }

        $.each(this.config.actions, function (name, obj) {
            var i, map, ret = false, valid = true;

            if (obj.shortcut && self._currentEditable) {
                if (typeof obj.shortcut === 'string') {
                    map = obj.shortcut.split('+');

                    for (i=0; i < map.length; i++) {
                        if (!keymap[map[i]]) {
                            valid = false;
                        }
                    }

                    if (valid) {
                    // Start - Toolbar Copy/Paste: fix from AEM ServicePack1
                        if (self._currentEditable.hasAction(name)) {
                            ret = obj.handler.call(self, self._currentEditable);
                        }
                     // End - Toolbar Copy/Paste: fix from AEM ServicePack1
                    }

                } else if ($.isFunction(obj.shortcut)) {
                    ret = obj.shortcut.call(self, self._currentEditable, keymap);
                }

                // close toolbar if not explicitly returning "false"
                if (ret !== false) {
                    self.close();
                }
            }
        });
    };

    /**
     *
     * @param editable
     * @returns {*}
     */
    ns.ui.Toolbar.prototype.render = function (editable) {
        var self = this;

        this._currentButtons = {};

        // remove previous editable content
        this.dom.empty();

        $.each(this.config.actions, function (name, obj) {
            self.appendButton(editable, name, obj);
        });

        return this;
    };

    /**
     *
     * @param editable
     * @returns {*}
     */
    ns.ui.Toolbar.prototype.position = function (editable) {
        this._currentEditable = editable || this._currentEditable;

        // position the toolbar to the very left to let it expand the full width (needed for calc)
        this.dom.css({
            display: 'block',
            top: 0,
            left: 0
        });

        // calculate the position
        this.dom.position({
            'my': 'left bottom+2',
            'at': 'left top',
            'of': this._currentEditable.overlay.dom,
            'collision': 'flip',
            'within': '#ContentScrollView'
        });

        return this;
    };

    /**
     * repositions the toolbar on its current underlying editable (if existing)
     */
    ns.ui.Toolbar.prototype.reposition = function () {
            if (!this._currentEditable) {
            return;
        } else {
            this.position(this._currentEditable);
        }
    };

    /**
     *
     */
    ns.ui.Toolbar.prototype.close = function () {
        this._currentEditable = null;
        if (this.dom) {
            this.dom.hide();
        }
    };

    /**
     *  Disable the toolbar (blocks possible user interaction with it)
     */
    ns.ui.Toolbar.prototype.disable = function () {
        this.isDisabled = true;
    };

    /**
     *  Enable the toolbar (allow user interaction with it)
     */
    ns.ui.Toolbar.prototype.enable = function () {
        this.isDisabled = false;
    };


    ns.ui.Toolbar.prototype.getButton = function (name) {
        return this._currentButtons[name];
    };

    // Start - Toolbar Copy/Paste: fix from AEM ServicePack1
    ns.ui.Toolbar.prototype.checkActionCondition = function(actionName, editable) {
        var actionObj = this.config.actions[actionName] || $.grep(editable.config.editConfig.actions, function (item) {
            return item && item.name === actionName;
        })[0];

        return actionObj
            && actionObj.condition
            && actionObj.condition(editable)
    };
     // End - Toolbar Copy/Paste: fix from AEM ServicePack1

}(jQuery, Granite.author, jQuery(document), this));
