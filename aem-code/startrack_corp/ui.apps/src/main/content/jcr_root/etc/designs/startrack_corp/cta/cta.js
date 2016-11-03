"use strict";

(function ($, Granite) {
    var $document = $(document);
    var _ = window._, CUI = window.CUI, Class = window.Class;

    $document.on("foundation-contentloaded", function(e) {
        if($(".card-image-multifield").length) {
            ACS.TouchUI.CompositeImageMultiField = new Class({
                toString: 'ACS TouchUI Composite Image Multifield',
                extend: ACS.TouchUI.Widget,


                //reads multifield data from server, creates the nested composite multifields and fills them
                addDataInFields: function () {
                    var cmf = this, mNames = [],
                        $fieldSets = $("[" + cmf.DATA_ACS_COMMONS_NESTED + "][class='coral-Form-fieldset']"),
                        $form = $fieldSets.closest("form.foundation-form"),
                        actionUrl = $form.attr("action") + ".json",
                        mValues, $field, name, $multifield;
                    $(".js-coral-Multifield-add").click(function(){
                        $multifield = $(this).parent();
                        setTimeout(function(){
                            addImageMultifield($document);
                        }, 500);

                    });
                    
                    if (_.isEmpty($fieldSets)) {
                        return;
                    }

                    $fieldSets.each(function (i, fieldSet) {
                        if (!cmf.isJsonStore($(fieldSet).data(cmf.ACS_COMMONS_NESTED))) {
                            return;
                        }
                        mNames.push($(fieldSet).data("name"));
                    });

                    mNames = _.uniq(mNames);

                    //Intialize Image Field
                    function addImageMultifield($document){
                        var $uploadElement = $document.find(".cq-dialog .coral-FileUpload:last");
                        var widget = $uploadElement.data("fileUpload");
                        var resourceURL = $uploadElement.parents("form.cq-dialog").attr("action");
                        var imageField = new Granite.FileUploadField(widget, resourceURL);
                        //Empty thumbnail Img
                        $.ajax({
                            url: imageField.resourceURL + ".2.json",
                            cache: false
                        }).done(handler);
         
                        function handler(data){
                             setTimeout(function(){imageField.widget.$element.find(".cq-dd-image").attr("src","file")}, 20);
                        }
                    }
                    //Block to check the multifield has image file upload field
                    function isImage($field) {
                        return !_.isEmpty($field) && $field.prop("type") === "file" && $field.hasClass("coral-FileUpload-input");
                    }
                  //Block to set the image file upload field value
                    function setImageField($field, value) {
                        var $parent = $field.parent().parent();
                        $parent.find(".cq-dd-image").attr("src",value);
                    }
                    //Override setWidgetValue to have check for image field 
                    function setWidgetValue($field, value) {
                        //console.log("records to be set in multifields: ", $field, value);

                      if (_.isEmpty($field)) {
                            return;
                        }
            
                        if (cmf.isSelectOne($field)) {
                            cmf.setSelectOne($field, value);
                        } else if (cmf.isCheckbox($field)) {
                            cmf.setCheckBox($field, value);
                        } else if (isImage($field)) {
                             setImageField($field, value);
                        }else {
                            $field.val(value);
                        }
                    }

                    //creates & fills the nested multifield with data
                    function fillNestedFields($multifield, valueArr) {
                        _.each(valueArr, function (record, index) {
                            $multifield.find(".js-coral-Multifield-add").click();
                            //a setTimeout may be needed
                            _.each(record, function (value, key) {
                                setWidgetValue($($multifield.find("[name='./" + key + "']")[index]), value);
                            });
                        });
                    }

                    function postProcess(data) {
                        _.each(mNames, function (mName) {
                            if (_.isEmpty(mName)) {
                                return;
                            }

                            $fieldSets = $("[data-name='" + mName + "']");

                            //strip ./
                            mName = mName.substring(2);

                            mValues = data[mName];

                            if (_.isString(mValues)) {
                                mValues = [JSON.parse(mValues)];
                            }

                            _.each(mValues, function (record, i) {
                                if (!record) {
                                    return;
                                }

                                if (_.isString(record)) {
                                    record = JSON.parse(record);
                                }

                                _.each(record, function (rValue, rKey) {
                                    $field = $($fieldSets[i]).find("[name='./" + rKey + "']");
                                    if (_.isArray(rValue) && !_.isEmpty(rValue)) {
                                        fillNestedFields($($fieldSets[i]).find("[data-init='multifield']"), rValue);
                                    } else {
                                       setWidgetValue($field, rValue);
                                    }
                                });
                            });
                        });

                        $document.trigger("touchui-composite-multifield-ready", mNames);
                    }

                    $.ajax(actionUrl).done(postProcess);
                },

                fillValue: function($field, record) {
                    var name = $field.attr("name"), value;
            
                   if (!name) {
                        return;
                    }

                    //strip ./
                    if (name.indexOf("./") === 0) {
                        name = name.substring(2);
                    }

                    value = $field.val();

                    if (this.isCheckbox($field)) {
                        value = $field.prop("checked") ? $field.val() : "";
                    }

                    if(!_.isEmpty($field) && $field.prop("type") === "file" && $field.hasClass("coral-FileUpload-input")){
                        var imageValue = $field.parent().parent().find(".cq-dd-image").attr("src");
                        value = this.modifyJcrContent(imageValue);
                    }

                    record[name] = value;

                    //remove the field, so that individual values are not POSTed
                    $field.remove();
                },

                modifyJcrContent: function (url){
                    if(url) {
                        return url.replace(new RegExp("^" + Granite.HTTP.getContextPath()), "")
                            replace(/\/_jcr_content.*|\/jcr:content.*/i, "");
                    }
                },

                //for getting the nested multifield data as js objects
                getRecordFromMultiField: function ($multifield) {
                    var cmf = this, $fieldSets = $multifield.find("[class='coral-Form-fieldset']"),
                        records = [], record, $fields, name;

                    $fieldSets.each(function (i, fieldSet) {
                        $fields = $(fieldSet).find("[name]");

                        record = {};

                        $fields.each(function (j, field) {
                            fillValue($(field), record);
                        });

                        if (!$.isEmptyObject(record)) {
                            records.push(record);
                        }
                    });

                    return records;
                },

                //collect data from widgets in multifield and POST them to CRX as JSON
                collectDataFromFields: function () {
                    var cmf = this, $form = $("form.cq-dialog"),
                        $fieldSets = $("[" + cmf.DATA_ACS_COMMONS_NESTED + "][class='coral-Form-fieldset']"),
                        record, $fields, $field, $fieldSet, name, $nestedMultiField;

                    $fieldSets.each(function (i, fieldSet) {
                        $fieldSet = $(fieldSet);

                        if(!cmf.isJsonStore($(fieldSet).data(cmf.ACS_COMMONS_NESTED))){
                            return;
                        }

                        $fields = $fieldSet.children().children(cmf.CFFW);

                        record = {};

                        $fields.each(function (j, field) {
                            $field = $(field);

                            //may be a nested multifield
                            $nestedMultiField = $field.find("[data-init='multifield']");

                            if ($nestedMultiField.length === 0) {
                                cmf.fillValue($field.find("[name]"), record);
                            } else {
                                name = $nestedMultiField.find("[class='coral-Form-fieldset']").data("name");

                                if (!name) {
                                    return;
                                }

                                //strip ./
                                name = name.substring(2);

                                record[name] = cmf.getRecordFromMultiField($nestedMultiField);
                            }
                        });

                        if ($.isEmptyObject(record)) {
                            return;
                        }

                        //console.log("data to be written: name" + $(fieldSet).data("name") + ", value: " + record);

                        //add the record JSON in a hidden field as string
                        $('<input />').attr('type', 'hidden')
                            .attr('name', $(fieldSet).data("name"))
                            .attr('value', JSON.stringify(record))
                            .appendTo($form);
                    });
                }
            });

            var compositeImageMultiField = new ACS.TouchUI.CompositeImageMultiField();
            $document.off("dialog-ready");
            $document.off("click", ".cq-dialog-submit");
            $document.on("dialog-ready", function(){
                compositeImageMultiField.addDataInFields();
            });
   
            $document.on("click", ".cq-dialog-submit", function(){
                compositeImageMultiField.collectDataFromFields();
            });
        }
        /**
        To adjust the layout container dialog as Tab
        To restrict to have only 1 CTA 
        1. Add first child field by default
        2. Hide the CTA add/remove button
        **/
       if($(".tab-layout-container").length){
            $("div.coral-TabPanel-content").find("null").addClass("coral-FixedColumn coral-FixedColumn-column");
            var multifield = $("div.coral-Multifield");
            if(multifield.find(".coral-Multifield-input").length == 0){
               multifield.find(".js-coral-Multifield-add").click();
            }
            multifield.find(".js-coral-Multifield-add").hide();
            multifield.find(".js-coral-Multifield-remove").hide();
       }
    });
}(jQuery, Granite));
