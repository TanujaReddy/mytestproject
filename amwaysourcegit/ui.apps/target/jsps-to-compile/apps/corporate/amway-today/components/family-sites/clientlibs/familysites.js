var MyClientLib = MyClientLib || {};

MyClientLib.MyMultiPanel = CQ.Ext.extend(CQ.Ext.Panel, {
    panelValue: '',

    constructor: function(config){
        config = config || {};
        MyClientLib.MyMultiPanel.superclass.constructor.call(this, config);
    },

    initComponent: function () {
        MyClientLib.MyMultiPanel.superclass.initComponent.call(this);

        this.panelValue = new CQ.Ext.form.Hidden({
            name: this.name
        });

        this.add(this.panelValue);

        var dialog = this.findParentByType('dialog');

        dialog.on('beforesubmit', function(){
            var value = this.getValue();

            if(value){
                this.panelValue.setValue(value);
            }
        },this);
    },

    getValue: function () {
        var pData = {};

        this.items.each(function(i){
            if(i.xtype == "label" || i.xtype == "hidden" || !i.hasOwnProperty("dName")){
                return;
            }

            pData[i.dName] = i.getValue();
        });

        return $.isEmptyObject(pData) ? "" : JSON.stringify(pData);
    },

    setValue: function (value) {
        this.panelValue.setValue(value);

        var pData = JSON.parse(value);

        this.items.each(function(i){
            if(i.xtype == "label" || i.xtype == "hidden" || !i.hasOwnProperty("dName")){
                return;
            }

            if(!pData[i.dName]){
                return;
            }

            i.setValue(pData[i.dName]);
        });
    },

    validate: function(){
    	var dialog = this.findParentByType('dialog');
    	var multi = dialog.findByType('multifield')[0];
    	var textfields = multi.findByType('textfield');
    	var isError = true;
    	for (var i = 0; i < textfields.length; i++) {
    		
    		var familySiteLinkInput = $("#familySiteLinkInput").val();
    		if (textfields[i].fieldLabel == familySiteLinkInput) {

    			var pattern = new RegExp('(http|https|ftp):\/\/(www\.)?');
    			if (!pattern.test(textfields[i].getValue().trim())) {
    				textfields[i].markInvalid();
    				isError = false;
    			}
    		}
    	}
    	return isError;
    },

    getName: function(){
        return this.name;
    }
});

CQ.Ext.reg("familysitespanel", MyClientLib.MyMultiPanel);