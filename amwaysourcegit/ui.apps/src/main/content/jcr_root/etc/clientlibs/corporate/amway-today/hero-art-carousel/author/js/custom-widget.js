// It would probably be better to use the json composite control from http://adobe-consulting-services.github.io/acs-aem-commons/features/widgets.html

// In sync with HeroArticleController
const FIELD_SEPARATOR = '*--*';

var HArtWidget = {};

HArtWidget.CustomWidget = CQ.Ext.extend(CQ.form.CompositeField, {

    /**
     * @private
     * @type CQ.Ext.form.TextField
     */
    hiddenField: null,

    /**
     * @private
     * @type CQ.form.PathField
     */
    articlePath: null,

    /**
     * @private
     * @type CQ.form.PathField
     */
    ImagePath: null,

    /**
     * @private
     * @type CQ.Ext.form.TextField
     */
    formPanel: null,

    constructor: function (config) {
        config = config || {};
        var defaults = {
            "border": true,
            //"layout": "table",
            //"columns": 2
        };
        //config = CQ.Util.applyDefaults(config, defaults);
        config = CQ.Util.merge(config, defaults);
        HArtWidget.CustomWidget.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function () {
        HArtWidget.CustomWidget.superclass.initComponent.call(this);

        //Hidden Field
        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });

        this.add(this.hiddenField);

        // Article path

        this.add(new CQ.Ext.form.Label({
            cls: "customwidget-label",
            text: "Article path"
        }));

        this.articlePath = new CQ.form.PathField({
            cls: "customwidget-txtbox",
            validatePath: true,
            listeners: {
                change: {
                    scope: this,
                    fn: this.updateHidden
                },
                dialogclose: {
                    scope: this,
                    fn: this.updateHidden
                },
            }
        });
        this.add(this.articlePath);

        //Image path

        this.add(new CQ.Ext.form.Label({
            cls: "customwidget-label",
            text: "Image path"
        }));


        this.add(this.ImagePath = new ACS.CQ.form.DDPathField({
            cls: "customwidget-txtbox",
            rootPath: "/content/dam"
        }));
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function (value) {
        var parts = value.split(FIELD_SEPARATOR);

        this.articlePath.setValue(parts[0]);
        this.ImagePath.setValue(parts[1]);
        this.hiddenField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function () {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function () {
        var value =
            this.articlePath.getValue() + FIELD_SEPARATOR +
            this.ImagePath.getValue();
        return value;
    },

    updateHidden: function () {
        this.hiddenField.setValue(this.getValue());
    }

});

// register xtype
CQ.Ext.reg("herocomposite", HArtWidget.CustomWidget);

//------------------------------------------------------------------------------
