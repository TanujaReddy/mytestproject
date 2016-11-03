/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2012 Adobe Systems Incorporated
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

/**
 * @class CUI.rte.commands.DefaultFormatting
 * @extends CUI.rte.commands.Command
 * @private
 */
CUI.rte.commands.DefaultFormatting = new Class({

    toString: "DefaultFormatting",

    extend: CUI.rte.commands.Command,

    /**
     * @private
     */
    containsTag: function(list, tagName) {
        var com = CUI.rte.Common;
        for (var key in list) {
            var dom = list[key];
            if (com.isTag(dom, tagName)) {
                return true;
            }
        }
        return false;
    },

    /**
     * @private
     */
    _getTagNameForCommand: function(cmd) {
        var cmdLC = cmd.toLowerCase();
        var tagName = null;
        switch (cmdLC) {
            case "bold":
                tagName = "b";
                break;
            case "italic":
                tagName = "i";
                break;
            case "underline":
                tagName = "u";
                break;
            case "customjustifyleft":
            case "customjustifycenter":
            case "customjustifyright":
                tagName = "span";
                break;
            case "subscript":
                tagName = "sub";
                break;
            case "superscript":
                tagName = "sup";
                break;
        }
        return tagName;
    },

    _getParamsFromExecDef: function(execDef) {
        var cmdLC = execDef.command.toLowerCase();
        var isStyle = (cmdLC === "style");
        var tagName, attributes;
        if (isStyle) {
            tagName = execDef.value.tag;
            attributes = execDef.value.attributes;
        } else {
            tagName = this._getTagNameForCommand(cmdLC);
            attributes = execDef.value;
        }
        return {
            "tag": tagName,
            "attributes": attributes,
            "isStyle": isStyle
        };
    },


    /**
     * @private
     */
    isStrucStart: function(context, node, offset) {
        var parentNode = node.parentNode;
        if (!parentNode) {
            return false;
        }
        if (node !== parentNode.firstChild) {
            return false;
        }
        if (node.nodeType === 3) {
            return (offset === 0);
        }
        if (CUI.rte.Common.isOneCharacterNode(node)) {
            return (offset === undefined) || (offset === null);
        }
        return true;
    },

    /**
     * @private
     */
    isStrucEnd: function(context, node, offset) {
        var com = CUI.rte.Common;
        var parentNode = node.parentNode;
        if (!parentNode) {
            return false;
        }
        if (node !== parentNode.lastChild) {
            return false;
        }
        if (node.nodeType === 3) {
            return (offset === com.getNodeCharacterCnt(node));
        }
        if (com.isOneCharacterNode(node)) {
            return (offset === 0);
        }
        return true;
    },

    /**
     * @private
     */
    _clean: function(dom, stopDom) {
        var dpr = CUI.rte.DomProcessor;
        // ensure that the provided DOM element gets processed as well, even if it is
        // the same as the "stop" element
        var isTraversed = false;
        while (!isTraversed) {
            var parent = dom.parentNode;
            if ((dom.nodeType !== 1) || (dom.childNodes.length !== 0) ||
                    (dpr.getTagType(dom) !== dpr.STRUCTURE)) {
                break;
            }
            parent.removeChild(dom);
            isTraversed = (dom === stopDom);
            dom = parent;
        }
    },

    /**
     * @private
     */
    cleanLeft: function(dom) {
        var deepChild = dom;
        while (deepChild.firstChild) {
            deepChild = deepChild.lastChild;
        }
        this._clean(deepChild, dom);
    },

    /**
     * @private
     */
    cleanRight: function(dom) {
        var deepChild = dom;
        while (deepChild.firstChild) {
            deepChild = deepChild.firstChild;
        }
        this._clean(deepChild, dom);
    },

    /**
     * @private
     */
    split: function(splitParent, dom, offset) {
        CUI.rte.DomProcessor.splitToParent(splitParent, dom, offset);
    },

    /**
     * @private
     */
    clean: function(domLeft, domRight) {
        if (domLeft) {
            this.cleanLeft(domLeft);
        }
        if (domRight) {
            this.cleanRight(domRight);
        }
    },

    /**
     * @private
     */
    setCaretTo: function(execDef) {
        var com = CUI.rte.Common;
        var sel = CUI.rte.Selection;
        var dpr = CUI.rte.DomProcessor;

        function removePlaceholderIfPresent() {
            if (placeholderNode) {
                startNode = placeholderNode.parentNode;
                startOffset = com.getChildIndex(placeholderNode);
                placeholderNode.parentNode.removeChild(placeholderNode);
            }
        }

        var def = this._getParamsFromExecDef(execDef);
        var tag = def.tag;
        var attribs = def.attributes;
        if (tag) {
            var context = execDef.editContext;
            var selection = execDef.selection;
            var startNode = selection.startNode;
            var startOffset = selection.startOffset;
            var isPlaceholder = dpr.isZeroSizePlaceholder(startNode);
            var placeholderNode;
            if (isPlaceholder) {
                placeholderNode = (startNode.nodeType === 3 ? startNode.parentNode :
                        startNode);
            }
            var existing = com.getTagInPath(context, startNode, tag, attribs);
            if (!existing) {
                // switch on style
                if (placeholderNode) {
                    startNode = placeholderNode.parentNode;
                    startOffset = com.getChildIndex(placeholderNode);
                }
                var el = dpr.createNode(context, tag, attribs);
                com.setAttribute(el, com.TEMP_EL_ATTRIB, com.TEMP_EL_REMOVE_ON_SERIALIZE
                        + ":emptyOnly");
                dpr.insertElement(context, el, startNode, startOffset);
                sel.selectEmptyNode(context, el);
                if (placeholderNode) {
                    placeholderNode.parentNode.removeChild(placeholderNode);
                }
            } else {
                // switch off style
                var path = [ ];
                var dom = startNode;
                var ref;
                while (dom && (dom !== existing)) {
                    if ((dom.nodeType === 1) && !dpr.isZeroSizePlaceholder(dom)) {
                        path.push(dom);
                    }
                    dom = com.getParentNode(context, dom);
                }
                var pathCnt = path.length;
                var parentNode;
                if (pathCnt === 0) {
                    // switching off current style
                    parentNode = com.getParentNode(context, startNode);
                    if (!isPlaceholder &&
                            this.isStrucStart(context, startNode, startOffset)) {
                        sel.selectBeforeNode(context, parentNode);
                    } else if (!isPlaceholder &&
                            this.isStrucEnd(context, startNode, startOffset)) {
                        sel.selectAfterNode(context, parentNode);
                    } else {
                        removePlaceholderIfPresent();
                        if (com.isCharacterNode(startNode)) {
                            // split structure at caret
                            this.split(parentNode, startNode, startOffset);
                            this.clean(parentNode, parentNode.nextSibling);
                            // select behind split point (an empty element will be created
                            // automatically)
                            sel.selectAfterNode(context, parentNode);
                        } else {
                            this.split(existing, startNode, startOffset);
                            ref = existing.nextSibling;
                            var tempSpan = dpr.createTempSpan(context, true, false, true);
                            tempSpan.appendChild(context.createTextNode(
                                    dpr.ZERO_WIDTH_NBSP));
                            existing.parentNode.insertBefore(tempSpan, ref);
                            this.clean(existing, ref);
                            sel.selectEmptyNode(context, tempSpan);
                        }
                    }
                } else {
                    // switching off a style that's somewhere up in the hierarchy
                    var duplicatedNode;
                    // create delta style hierarchy
                    for (var p = pathCnt - 1; p >= 0; p--) {
                        var cloned = path[p].cloneNode(false);
                        if (!parentNode) {
                            duplicatedNode = cloned;
                        } else {
                            parentNode.appendChild(cloned);
                        }
                        parentNode = cloned;
                    }
                    removePlaceholderIfPresent();
                    // split hierarchy and add delta style hierarchy
                    this.split(existing, startNode, startOffset);
                    ref = existing.nextSibling;
                    existing.parentNode.insertBefore(duplicatedNode, ref);
                    this.clean(existing, ref);
                    // select the deepest (= last cloned) element of the delta styles
                    sel.selectEmptyNode(context, cloned);
                }
            }
            return {
                "preventBookmarkRestore": true
            };
        }
        return undefined;
    },

    isCommand: function(cmdStr) {
        var cmdLC = cmdStr.toLowerCase();
        return (cmdLC == "bold") || (cmdLC == "italic") || (cmdLC == "underline")
 				|| (cmdLC == "customjustifyleft") || (cmdLC == "customjustifycenter") || (cmdLC == "customjustifyright")
                || (cmdLC == "subscript") || (cmdLC == "superscript") || (cmdLC == "style");
    },

    getProcessingOptions: function() {
        var cmd = CUI.rte.commands.Command;
        return cmd.PO_SELECTION | cmd.PO_BOOKMARK | cmd.PO_NODELIST;
    },

    execute: function(execDef) {
        var com = CUI.rte.Common;
        var nodeList = execDef.nodeList;
        var selection = execDef.selection;
        var context = execDef.editContext;
        var def = this._getParamsFromExecDef(execDef);
        if (def.isStyle && def.attributes.hasOwnProperty("class")) {
            // the "style" command may handle objects differently if a styleable object
            // is the only selection
            var selectedDom = CUI.rte.Selection.getSelectedDom(context, selection);
            var styleableObjects = CUI.rte.plugins.StylesPlugin.STYLEABLE_OBJECTS;
            var styleName = def.attributes["class"];
            if (selectedDom && com.isTag(selectedDom, styleableObjects)) {
                if (com.hasCSS(selectedDom, styleName)) {
                    com.removeClass(selectedDom, styleName);
                } else {
                    com.addClass(selectedDom, styleName);
                }
                return undefined;
            }
        }
        if (!CUI.rte.Selection.isSelection(selection)) {
            // execDef.editContext.doc.execCommand(execDef.command, false, null);
            return this.setCaretTo(execDef);
        }
        // see queryState()
        var tags = com.getTagInPath(context, selection.startNode, def.tag, def.attributes);
        var isActive = (tags != null);
        if (!isActive) {
            nodeList.surround(execDef.editContext, def.tag, def.attributes);
        } else {
            nodeList.removeNodesByTag(execDef.editContext, def.tag, def.attributes, true);
        }
        return undefined;
    },

    queryState: function(selectionDef, cmd) {
        var com = CUI.rte.Common;
        var tagName = this._getTagNameForCommand(cmd);
        if (!tagName) {
            return undefined;
        }
        var context = selectionDef.editContext;
        var selection = selectionDef.selection;
        return (com.getTagInPath(context, selection.startNode, tagName) != null);
    }

});

// register command
CUI.rte.commands.CommandRegistry.register("_defaultfmt",
        CUI.rte.commands.DefaultFormatting);
