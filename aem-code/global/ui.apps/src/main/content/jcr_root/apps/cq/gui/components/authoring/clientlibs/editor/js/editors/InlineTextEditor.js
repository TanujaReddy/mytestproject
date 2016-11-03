/*
 *  This file has been overlayed in order to fix DE6550-RTE heading and body copy-when copy pasting text in inline editing in chrome browser the page scrolls to the top.
 *  This file has been taken from Service pack 1 of aem-service-pkg-wrapper-6.1.SP1
 */
;
(function ($, ns, channel, window, undefined) {

    var ui = { };

    var env = undefined, scrollViewTopBeforePaste, editorHeightBeforePaste, contentWrapperHeightBeforePaste;

    function saveEnvironment(dom) {
        var $dom = $(dom);
        var $cq = $dom.find("cq").remove();
        env = {
            $root: $dom,
            $cq: $cq
        };
    }

    function restoreEnvironment() {
        if (env) {
            env.$root.append(env.$cq);
            env = undefined;
        }
    }

    function finish(editable) {
        ui[editable.path].remove();
        delete ui[editable.path];
        channel.off("editor-frame-mode-changed.ipe", onEditModeChange);
        restoreEnvironment();
    }

    function onEditModeChange(event) {
        var data = event.data;
        var editable = data.editable;
        var editor = data.editor;
        editor.finishInlineEdit(editable, editor.rte.getContent(), true);
    }

    var sel = CUI.rte.Selection;

    function getBookmark(rte) {
        var context = rte.editorKernel.getEditContext();
        return sel.createSelectionBookmark(context);
    }

    function selectBookmark(rte, bookmark) {
        var context = rte.editorKernel.getEditContext();
        sel.selectBookmark(context, bookmark);
    }

    function installFullScreenAdapter() {
        // executed in CUI.RichText scope
        var com = CUI.rte.Common;
        this.editorKernel.execCmd("setFullScreenAdapter", new FullScreenAdapter(this));
        if (com.ua.isWebKit) {
            this.editorKernel.addPluginListener("paste", saveScrollStateBeforePaste, this, null, false, 900);
            this.editorKernel.addPluginListener("commandexecuted", handleScrollOnPaste, this, null, false);
        }
    }

    function saveScrollStateBeforePaste(e) {
        var com = CUI.rte.Common;
        if (e.type == "paste" && com.ua.isWebKit) {
            // save the current scrollTop of contentScrollView
            scrollViewTopBeforePaste = $("#ContentScrollView").scrollTop();
            contentWrapperHeightBeforePaste = $("#ContentWrapper").height();
            // save the current height of editor, later we can subtract this from new height of editor
            // to get height of pasted content. We can then add this pasted content's height to the above
            // saved scroll Top of contentScrollView to get the new scroll top for contentScrollView
            editorHeightBeforePaste = e.editContext.root.offsetHeight;
        }
    }

    function handleScrollOnPaste(e) {
        var com = CUI.rte.Common, newScrollTop, heightIncrease;
        if (e.cmd == "paste" && com.ua.isWebKit) {
            // calculate increase in editor height
            heightIncrease = e.editContext.root.offsetHeight - editorHeightBeforePaste;
            newScrollTop = scrollViewTopBeforePaste + heightIncrease;
            // Actual paste might not have taken place yet. So set content wrapper's height so that
            // setting scroll top of contentscrollview would be effective
            $("#ContentWrapper").height(contentWrapperHeightBeforePaste + heightIncrease);
            $("#ContentScrollView").scrollTop(newScrollTop);
        }
    }

    function calcEditorHeight($toolbar) {
        var vpHeight = window.innerHeight;
        return vpHeight - $toolbar.outerHeight();
    }

    function handleConfig(config, type, componentType) {
        var com = CUI.rte.Common;
        if (type === "uiSettings") {
            if (config.hasOwnProperty("fullscreen")) {
                var fs = config.fullscreen;
                if (fs.hasOwnProperty("toolbar")) {
                    var fsTb = fs.toolbar;
                    if (com.ua.isTouch) {
                        var i = com.arrayIndex(fsTb, "fullscreen#finish");
                        if (i >= 0) {
                            fsTb[i] = "control#close";
                        }
                        fsTb.push("control#save");
                    }
                }
            }
            if (componentType && componentType == "table" && config.hasOwnProperty("tableEditOptions")) {
                var tableSettings = config.tableEditOptions;
                if (tableSettings.hasOwnProperty("toolbar")) {
                    var tableTb = tableSettings.toolbar;
                    var fullscreenFinishIndex = com.arrayIndex(tableTb, "fullscreen#finish");
                    if (!com.ua.isTouch && fullscreenFinishIndex < 0) {
                        tableTb.push("fullscreen#finish");
                    }
                }
            }
        }
        return config;
    }

    var FullScreenAdapter = new Class({

        baseRTE: undefined,

        fullScreenRTE: undefined,

        touchScrollLimiter: undefined,

        extend: CUI.rte.commands.FullScreenAdapter,

        $container: undefined,

        $editor: undefined,

        $sourceEditor: undefined,

        adjustWrapper: function ($currentToolbar) {
            var $wrapper = this.$container.find("div.coral-RichText-editorWrapper");
            var $ui = this.$container.find("div.coral-RichText-ui");
            if (!$currentToolbar) {
                var ek = this.fullScreenRTE.editorKernel;
                $currentToolbar = CUI.rte.UIUtils.getToolbar($ui, ek.getToolbar().tbType);
            }
            if (!$currentToolbar || !this.isFullScreen()) {
                return;
            }
            var tbHeight = $currentToolbar.outerHeight();
            $wrapper.offset({
                top: tbHeight,
                left: 0
            });
            $wrapper.outerHeight(calcEditorHeight($currentToolbar));
        },

        _handleEscape: function() {
            this.finish();
            return true;
        },

        construct: function(baseRTE) {
            this.baseRTE = baseRTE;
        },

        start: function(adapterConfig) {
            var com = CUI.rte.Common;
            var isTouch = com.ua.isTouch;
            var bkm = getBookmark(this.baseRTE);
            this.$container = ns.editor.fullscreenController.start(undefined, adapterConfig);
            var $ui = $("<div/>");
            $ui.addClass("coral-RichText-ui");
            // $ui.css("height", "80px");
            var $wrapper = $("<div/>");
            $wrapper.addClass("coral-RichText-editorWrapper");
            this.$editor = $("<div/>");
            this.$editor.addClass("coral-RichText-editor");
            this.$sourceEditor = $("<textarea/>");
            this.$sourceEditor.addClass("coral-RichText-sourceEditor");
            this.$sourceEditor.hide();
            this.$container.append($ui);
            this.$container.append($wrapper);
            $wrapper.append(this.$editor);
            $wrapper.append(this.$sourceEditor);
            var self = this;
            this.$sourceEditor.fipo("tap.rte-" + this.id, "click.rte-" + this.id,
                function(e) {
                    e.stopPropagation();
                });
            // need to prevent both mousedown and click events from bubbling up to
            // avoid focus loss on desktop browsers
            this.$container.fipo("touchstart.rteOOA", "mousedown.rteOOA click.rteOOA",
                function(e) {
                    var target = e.target;
                    var $target = $(target);
                    if ((target === self.$container[0]) || (target === $wrapper[0])
                        || $target.hasClass("coral-RichText-toolbar")) {
                        e.preventDefault();
                        e.stopPropagation();
                    }
                });
            this.fullScreenRTE = new CUI.RichText({
                "element": this.$editor,
                "initialContent": this.baseRTE.getContent(),
                "preventCaretInitialize": true,
                "$ui": $ui,
                "isFullScreen": true,
                "autoConfig": true,
                "fullScreenAdapter": this,
                "componentType": this.baseRTE.getComponentType(),
                "listeners": {
                    "beforeEscape": CUI.rte.Utils.scope(this._handleEscape, this),
                    // handler that ensure fullscreen mode gets hidden if RTE is left
                    // in fullscreen mode (iOS only)
                    "beforeFinish": function() {
                        self._leaveFromFullScreenMode();
                    },
                    "beforeCancel": function() {
                        self._leaveFromFullScreenMode(true);
                    }
                }
            });
            var undoConfig = this.baseRTE.getUndoConfig();
            this.baseRTE.suspend();
            this.fullScreenRTE.start(CUI.rte.Utils.copyObject(this.baseRTE.originalConfig));

            //Changes are done for Issue :- CQ-38308

            var ek = this.fullScreenRTE.editorKernel;
            if(com.ua.isGecko || com.ua.isIE ) {
                ek.initializeCaret(true);
            }
            this.fullScreenRTE.setUndoConfig(undoConfig);
            if (isTouch) {
                // ensure "main focus" is transferred from iframe to our window - otherwise
                // keyboard input is ignored on iOS devices
                window.focus();
            }


            this.fullScreenRTE.focus();
            var context = ek.getEditContext();
            // replace the dom objects in bookmark by their counterparts in the fullscreen editor
            this._convertBookmark(bkm, this.baseRTE.editorKernel.getEditContext(), context);
            selectBookmark(this.fullScreenRTE, bkm);
            // position toolbar on the top of the screen (works for desktop only, need
            // to simulate this for touch)
            var $tb = CUI.rte.UIUtils.getToolbar($ui, "fullscreen");
            this.adjustWrapper($tb);
            if (isTouch) {
                this.touchScrollLimiter = new CUI.rte.ui.cui.TouchScrollLimiter();
                this.touchScrollLimiter.attach(this.$container, $wrapper, this.$editor);
            }
            if (!isTouch) {
                $(window).on("resize.rteFSResize", function(e) {
                    //adjust wrapper according to active toolbar
                    self.adjustWrapper(CUI.rte.UIUtils.getToolbar($ui, ek.getToolbar().tbType));
                });
            }
            context.setState("CUI.touchScrollLimiter", this.touchScrollLimiter);
            return ek;
        },

        /**
         * Modifies the dom element references present in the bookmark created from old context
         * to point to corresponding elements in new context.
         * @param bkm the bookmark to convert
         * @param fromContext the old edit context
         * @param toContext the new edit context
         * @private
         */
        _convertBookmark: function(bkm, fromContext, toContext) {
            var indexPath;
            var com = CUI.rte.Common;
            if (bkm.insertObject) {
                indexPath = com.createIndexPath(fromContext, bkm.insertObject);
                bkm.insertObject = com.getElementByIndexPath(toContext.root, indexPath);
            }
            if (bkm.object) {
                indexPath = com.createIndexPath(fromContext, bkm.object);
                bkm.object = com.getElementByIndexPath(toContext.root, indexPath);
            }
            if (bkm.cells) {
                var cellsCopy = [];
                for (var c=0; c < bkm.cells.length; c++) {
                    indexPath = com.createIndexPath(fromContext, bkm.cells[c]);
                    cellsCopy.push(com.getElementByIndexPath(toContext.root, indexPath));
                }
                bkm.cells = cellsCopy;
            }
        },

        /**
         * Get content from source editor and push it into RTE.
         * @private
         */
        pushValue: function(){
            var v = this.$sourceEditor.val();
            if (!this.sourceEditMode || this.togglingSourceEdit) {
                this.fullScreenRTE.editorKernel.setUnprocessedHtml(v);
            }
        },

        /**
         * Get content from RTE and push it into source editor.
         * @private
         */
        syncValue: function() {
            if (!this.sourceEditMode || this.togglingSourceEdit) {
                var html = this.fullScreenRTE.editorKernel.getProcessedHtml();
                this.$sourceEditor.val(html);
            }
        },

        toggleSourceEdit: function(sourceEditMode){
            this.togglingSourceEdit = true;
            if (sourceEditMode === undefined){
                sourceEditMode = !this.sourceEditMode;
            }
            sourceEditMode = sourceEditMode === true;
            var isChanged = sourceEditMode !== this.sourceEditMode;
            this.sourceEditMode = sourceEditMode;
            var ek = this.fullScreenRTE.editorKernel;
            if (!isChanged) {
                return;
            }
            if (this.sourceEditMode) {
                ek.disableFocusHandling();
                ek.notifyBlur();
                ek.disableToolbar([ "sourceedit" ]);
                this.syncValue();
                this.$editor.hide();
                this.$sourceEditor.show();
                this.$sourceEditor.focus();
                ek.firePluginEvent("sourceedit", {
                    "enabled": true
                }, false);
            } else {
                ek.enableFocusHandling();
                if (this.initialized && !this.disabled){
                    ek.enableToolbar();
                }
                this.$editor.show();
                this.$sourceEditor.hide();
                this.pushValue();
                ek.focus();
                ek.firePluginEvent("sourceedit", {
                    "enabled": false
                }, false);
            }
            this.togglingSourceEdit = false;
        },

        _dropFullScreenMode: function() {
            if (this.touchScrollLimiter) {
                this.touchScrollLimiter.detach();
                this.touchScrollLimiter = undefined;
            }
            if (!CUI.rte.Common.ua.isTouch) {
                $(window).off("resize.rteFSResize");
            }
            var $container = ns.editor.fullscreenController.getContainer();
            $container.off(".rteOOA");
            ns.editor.fullscreenController.finish();
        },

        /**
         * Finishes editing from full screen mode (iOS only) - the current content of the
         * full screen editor is transferred to the base RTE, which then is regularily gets
         * "finished".
         *
         * @private
         */
        _leaveFromFullScreenMode: function(isCancelled) {
            var content = this.fullScreenRTE.getContent();
            var isTouch = CUI.rte.Common.ua.isTouch;
            if (isTouch) {
                // need to remove table toolbar on touch devices explicitly, since it is removed on
                // fullscreen-finish command which is never called on touch devices
                this.fullScreenRTE.editorKernel.removeBackgroundToolbar("tableEditOptions");
            }
            this.fullScreenRTE.suspend();
            this.fullScreenRTE = undefined;
            this._dropFullScreenMode();
            if (!isCancelled) {
                this.baseRTE.reactivate(content);
            }
            this.baseRTE.finish(isCancelled);
            this.baseRTE = undefined;
            if (isTouch) {
                // ensure "main focus" is transferred from top window object back to the
                // iframe - otherwise keyboard input is ignored on iOS devices
                $("#ContentFrame")[0].contentWindow.focus();
            }
        },

        finish: function() {
            var bkm = getBookmark(this.fullScreenRTE);
            var content = this.fullScreenRTE.getContent();
            var undoConfig = this.fullScreenRTE.getUndoConfig();
            this.fullScreenRTE.suspend();
            this._dropFullScreenMode();
            this.baseRTE.reactivate(content);
            // replace the dom objects in bookmark by their counterparts in the inline editor
            this._convertBookmark(bkm, this.fullScreenRTE.editorKernel.getEditContext(),
                this.baseRTE.editorKernel.getEditContext());
            this.fullScreenRTE = undefined;
            this.baseRTE.setUndoConfig(undoConfig);
            if (CUI.rte.Common.ua.isTouch) {
                // ensure "main focus" is transferred from top window object back to the
                // iframe - otherwise keyboard input is ignored on iOS devices
                $("#ContentFrame")[0].contentWindow.focus();
            }
            this.baseRTE.focus();
            selectBookmark(this.baseRTE, bkm);
            return this.baseRTE.editorKernel;
        },

        isFullScreen: function() {
            return ns.editor.fullscreenController.isActive();
        }

    });

    ns.editor.InlineTextEditor = function(componentType) {
        var self = this;
        this.componentType = componentType;
        // TODO: still here for compatibility reasons, but it's better
        // to launch the editor via startImageEditor() directly
        channel.on("inline-edit-start", function(e) {
            var editable = e.editable;
            self.startInlineEdit(editable);
        });
    };

    ns.editor.InlineTextEditor.prototype.setUp = function (editable, targetId) {
        this.startInlineEdit(editable, targetId);
    };

    ns.editor.InlineTextEditor.prototype.tearDown = function (editable, targetId) {
        this.finishInlineEdit(editable, targetId);
    };

    function onContentRead (self, data, editable, targetId) {
        var content = $.parseJSON(data);
        if (targetId && targetId.substring(0,2) == "./") {
            targetId = targetId.substring(2);
        }
        var property = targetId || "text";
        var initialContent = content[property] || "";
        var $uiContainer = $("#InlineEditingUI");
        var configCallBack = null;
        ui[editable.path] = $("<div class='coral-RichText-ui'></div>");
        $uiContainer.show();
        $uiContainer.append(ui[editable.path]);
        var ipeConfig = editable.config.ipeConfig || { };
        var $editable = $(editable.dom);
        // handle editElementQuery like in classic UI
        var subElementQuery = ipeConfig["editElementQuery"] || "div:first";
        //handle multiple text instances
        subElementQuery = targetId ? "div." + targetId : subElementQuery;
        if (subElementQuery != ".") {
            var $subEditable = $editable.find(ipeConfig["editElementQuery"]);
            if ($subEditable.length) {
                $editable = $subEditable;
            }
        }
        $editable.data("config", JSON.stringify(ipeConfig));
        // TODO replace with object once Coral UI is updated
        // $editable.data("config", ipeConfig);
        self.notifyInitialHistoryContent(editable.path, initialContent);
        if (self.componentType && self.componentType == 'table') {
            initialContent = content["tableData"] || "";
            var defaults = {
                    "useColPercentage": false,
                "rtePlugins": {
                    "table": {
                        "features": "*",
                        "defaultValues": {
                            "width": "100%"
                        },
                        "editMode": CUI.rte.plugins.TablePlugin.EDITMODE_TABLE
                    }
                }
            };
            configCallBack = function(config) {
                return Granite.Util.applyDefaults(config, defaults);
            };
        }
        if (CUI.rte.Common.ua.isTouch) {
            self.rte = new ns.editor.TouchRTE({
                "element": $editable,
                "initialContent": initialContent,
                "$ui": ui[editable.path],
                "useMask": true,
                "autoConfig": true,
                "listeners": {
                    "onStarted": installFullScreenAdapter
                }
            });
        } else {
            self.rte = new CUI.RichText({
                "element": $editable,
                "initialContent": initialContent,
                "$ui": ui[editable.path],
                "autoConfig": true,
                "componentType": self.componentType,
                "listeners": {
                    "onStarted": installFullScreenAdapter
                }
            });
        }
        saveEnvironment(editable.dom);
        CUI.rte.Utils.setI18nProvider(new CUI.rte.UiTouchI18nProvider());
        CUI.rte.ConfigUtils.loadConfigAndStartEditing(self.rte, $editable, configCallBack);
    }

    /**
     * Starts inline/inplace editing of the Editable.
     */
    ns.editor.InlineTextEditor.prototype.startInlineEdit = function(editable, targetId) {
        var self = this;
        var synchronousRead = CUI.rte.Common.ua.isTouch;
        channel.trigger("cq-hide-overlays");
        channel.on("editor-frame-mode-changed.ipe", {
            "editor": this,
            "editable": editable
        }, onEditModeChange);
        if (!targetId) {
            targetId = editable.config.ipeConfig.textPropertyName;
        }
        editable.dom.on("editing-finished editing-cancelled", function (e, content) {
            editable.dom.off("editing-finished editing-cancelled");
            var isFinished = (e.type === "editing-finished");
            if (isFinished) {
                self.finishInlineEdit(editable, content, null, targetId);
            } else {
                self.cancelInlineEdit(editable);
            }
        });

        ns.history.Manager.setBlocked(true);

        ns.persistence.readParagraphContent(editable, synchronousRead)
            .done(function (data) {
                onContentRead(self, data, editable, targetId);
            })
            .fail(function (data) {
                ns.persistence.updateParagraph(editable, {"./sling:resourceType": editable.type}).done(function () {
                    ns.persistence.readParagraphContent(editable, synchronousRead).done(function (data) {
                        onContentRead(self, data, editable, targetId);
                    });
                });
            });

        if (CUI.rte.Common.ua.isTouch) {
            self.rte.focus();
        }
    };

    ns.editor.InlineTextEditor.prototype.finishInlineEdit =
        function(editable, changedContent, preventModeChange, targetId) {
            var self = this;
            var persistedPropertyName = self.componentType && self.componentType == 'table' ? "tableData" : (targetId || "text");
            finish(editable);
            var updateObject = {};
            updateObject[persistedPropertyName] = changedContent;
            updateObject["textIsRich"] = "true";
            ns.edit.actions.doUpdate(editable, updateObject).always(function(e) {
                editable.refresh().done(function () {
                    ns.overlayManager.recreate(editable);
                    ns.selection.select(editable);
                });
                channel.trigger($.Event('inline-edit-finish', {
                    editable: editable,
                    changedContent: changedContent
                }));
                if (!preventModeChange) {
                    channel.trigger("cq-show-overlays");
                }
                self.addHistoryStep(editable, changedContent); // Add the undo/redo history step
            });

            $("#InlineEditingUI").hide();
            ns.history.Manager.setBlocked(false);
        };

    /**
     * Notifies the history handling about the initial content.
     * @param {String} path The path of the content
     * @param {String} initialContent The initial content
     */
    ns.editor.InlineTextEditor.prototype.notifyInitialHistoryContent = function(path, initialContent) {
        var historyEnabled = ns.history.Manager.isEnabled(),
            self = this;

        if (historyEnabled) {
            self.historyPath = path;
            self.historyInitialContent = initialContent;
        }
    };

    /**
     * Sets up the undo/redo history step for the text editing update.
     * @param {Granite.author.Editable} editable The editable that is being edited
     * @param {String} persistedContent The content that was persisted following inline editing
     */
    ns.editor.InlineTextEditor.prototype.addHistoryStep = function(editable, persistedContent) {
        var self = this,
            updateProperty = self.componentType && self.componentType == 'table' ? "tableData" : "text", // TODO - configurable property
            originalData = {},
            changedData = {};

        if (editable) {
            originalData[updateProperty] = self.historyInitialContent;
            originalData['textIsRich'] = true;
            changedData[updateProperty] = persistedContent;
            changedData['textIsRich'] = true;

            if (originalData[updateProperty] !== changedData[updateProperty]) {
                ns.history.util.Utils.addUpdateParagraphStep(self.historyPath, editable.type, originalData, changedData);
            }
        }
    };

    ns.editor.InlineTextEditor.prototype.cancelInlineEdit = function(editable, preventModeChange) {
        finish(editable);
        editable.refresh();
        channel.trigger($.Event('inline-edit-cancel', {
            editable: editable
        }));
        if (!preventModeChange) {
            channel.trigger("cq-show-overlays");
        }
    };

    // register the editor to the editor registry
    ns.editor.register('text', new ns.editor.InlineTextEditor('text'));
    // register the editor for table to the editor registry
    ns.editor.register('table', new ns.editor.InlineTextEditor('table'));

    // register config handlers for RTE
    var tk = CUI.rte.ui.ToolkitRegistry.get();
    tk.addToolkitData(CUI.rte.ui.ToolkitDefs.CONFIG_ADAPTER, handleConfig);

}(jQuery, Granite.author, jQuery(document), this));
