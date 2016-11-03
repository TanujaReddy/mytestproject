"use strict";

var Utils = require("./utils.jsx").default;
var Config = require("./config.js");

(function ($, Granite) {
    var isLock = false;

    function getMapofValues(str) {
        var res = {};
        if (str && str !== null && str !== "") {
            var arr = str.split(',');
            for (var i in arr) {
                var value = arr[i];
                res[value] = value;
            }
        }
        return res;
    }

    function setupTagControl(sectionName, tagName, hiddenName, label, isValue) {
        // creates and fills level 3 tags
        var inpt3El = document.querySelector(".page-article [data-name='" + sectionName + "']");
        if (inpt3El !== null) {
            inpt3El.setAttribute('data-name', '');
            var hiddenEl = document.querySelector(".page-article " + hiddenName);
            if (hiddenEl !== null) {
                hiddenEl.parentNode.classList.add('hidden');
            }

            var values = document.querySelectorAll("[name='" + tagName + "']")
            var valuesStr = '';
            for (var i = 0; i < values.length; i++) {
                var el = values[i];
                valuesStr += (i != values.length - 1) ? el.value + ',' : el.value;
                el.parentNode.removeChild(el);
            }

            var map3 = getMapofValues(valuesStr);
            Utils.ajaxGet(Config.level3TagsUrl, function (data) {
                if (data && data.result === "ok") {
                    var content = '<label>' + label + '</label><select multiple><options>';
                    for (var i in data.data) {
                        var x = data.data[i];
                        if (map3[x.value] !== undefined) {
                            content += '<option selected value="' + (isValue? x.value: x.name) + '">' + x.name + '</option>';
                        } else {
                            content += '<option value="' + (isValue? x.value : x.name) + '">' + x.name + '</option>';
                        }
                    }
                    content += '</options></select>';
                    inpt3El.innerHTML = content;
                }
            }, function () {
                console.log("Tags: unable to get level3 tags");
            });

            var $form = $(inpt3El).parents('form');
            $form.on('submit', function (event) {
                var sel3Val = $(inpt3El).find('select').val();
                //inpt3El.querySelector('select').value;
                for (var i in sel3Val) {
                    var val = sel3Val[i];
                    var inp = document.createElement("INPUT");
                    inp.setAttribute('type', 'hidden');
                    inp.setAttribute('name', tagName);
                    inp.value = val;
                    $form[0].appendChild(inp);
                    console.log("value: " + inp.value);
                }
            });
        }
    }

    var transformTags = function () {
        isLock = true;

        // creates and fills level 1 tags
        var inpt1Var = document.querySelector(".page-article #level1TagsHidden");
        var inpt1El = document.querySelector(".page-article [data-name='./level1TagsSection']");
        if (inpt1Var !== null && inpt1El !== null) {
            inpt1El.setAttribute('data-name', '');

            var map1 = getMapofValues(inpt1Var.value);
            Utils.ajaxGet(Config.level1TagsUrl, function (data) {
                if (data && data.result === "ok") {
                    var content = '<label>Page type</label><select><options><option value="">Select</option>';
                    for (var i in data.data) {
                        var x = data.data[i];
                        if (map1[x.value] !== undefined) {
                            content += '<option selected value="' + x.value + '">' + x.name + '</option>';
                        } else {
                            content += '<option value="' + x.value + '">' + x.name + '</option>';
                        }
                    }
                    content += '</options></select>';
                    inpt1El.innerHTML = content;
                }
            }, function () {
                console.log("Tags: unable to get level1 tags");
            });

            var $form = $(inpt1El).parents('form');
            $form.on('submit', function (event) {
                event.preventDefault();

                var sel1Val = $(inpt1El).find('select').val();
                $(inpt1Var).val(sel1Val);
            });

        }

        // creates and fills level 2 tags
        var inpt2Var = document.querySelector(".page-article #level2TagsHidden");
        var inpt2El = document.querySelector(".page-article [data-name='./level2TagsSection']");
        if (inpt2Var !== null && inpt2El !== null) {
            inpt2El.setAttribute('data-name', '');

            var map2 = getMapofValues(inpt2Var.value);
            Utils.ajaxGet(Config.level2TagsUrl, function (data) {
                if (data && data.result === "ok") {
                    var content = '<label>Insight tags</label><select multiple><options>';
                    for (var i in data.data) {
                        var x = data.data[i];
                        if (map2[x.value] !== undefined) {
                            content += '<option selected value="' + x.value + '">' + x.name + '</option>';
                        } else {
                            content += '<option value="' + x.value + '">' + x.name + '</option>';
                        }
                    }
                    content += '</options></select>';
                    inpt2El.innerHTML = content;
                }
            }, function () {
                console.log("Tags: unable to get level2 tags");
            });

            var $form = $(inpt2El).parents('form');
            $form.on('submit', function (event) {
                var sel2Val = $(inpt2El).find('select').val();
                $(inpt2Var).val(sel2Val);
            });
        }

        setupTagControl('./level3TagsSection', './level3tags', '.hidden1', 'Faq &#47; how to tags', true);
        setupTagControl('./faqtagsSection', './faqtags', '.hidden1', 'FAQ', true);
        setupTagControl('./htgtagsSection', './htgtags', '.hidden2', 'How to', true);
        setupTagControl('./tracktracetagsSection', './cq:tracktracetags', '.hidden1', 'Faq &#47; how to tags', true);
        setupTagControl('./srywemisstagsSection', './cq:srywemisstags', '.hidden2', 'Faq &#47; how to tags', true);
        setupTagControl('./pickupbookingtagsSection', './cq:pickupbookingtags', '.hidden3', 'Faq &#47; how to tags', true);
        setupTagControl('./transittagsSection', './cq:transittags', '.hidden4', 'Faq &#47; how to tags', true);

        isLock = false;
    }

    document.addEventListener("DOMNodeInserted", function (ev) {
        var frm = document.querySelector('.coral-Form');
        if (frm !== null && !isLock) {
            transformTags();
        }
    }, false);

    transformTags();
}(jQuery, Granite));