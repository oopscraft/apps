"use strict";
var duice;
(function (duice) {
    let vue;
    (function (vue) {
        vue.data = {};
        duice.initializeComponent = function (container, $context) {
            console.log('duice.initializeComponent', container, $context);
            [duice.ListComponentFactory, duice.MapComponentFactory]
                .forEach(function (factoryType) {
                duice.getComponentFactories().forEach(function (componentDefinition) {
                    var elements = container.querySelectorAll(componentDefinition.getSelector() + ':not([data-duice-id])');
                    for (var i = 0, size = elements.length; i < size; i++) {
                        let element = elements[i];
                        console.debug('invokeComponent', element, $context);
                        let data = new Object();
                        for (var name in duice.vue.data) {
                            if (duice.vue.data.hasOwnProperty(name)) {
                                data[name] = duice.vue.data[name];
                            }
                        }
                        for (var name in $context) {
                            if ($context.hasOwnProperty(name)) {
                                data[name] = $context[name];
                            }
                        }
                        console.debug('==> data', data);
                        new Vue({
                            el: element,
                            data: function () {
                                return data;
                            }
                        });
                    }
                });
            });
        };
        Vue.component('duice-input', {
            template: '<input/>',
            replace: false,
            props: {
                type: String,
                bindMap: Object,
                bindName: String,
                mask: String
            },
            mounted: function () {
                this.$el.setAttribute('type', this.type);
                let input = null;
                switch (this.type) {
                    case 'text':
                        input = new duice.InputText(this.$el);
                        if (this.mask) {
                            input.setMask(this.mask);
                        }
                        break;
                    case 'number':
                        input = new duice.InputNumber(this.$el);
                        if (this.mask) {
                            input.setMask(this.mask);
                        }
                        break;
                    case 'checkbox':
                        input = new duice.InputCheckbox(this.$el);
                        break;
                    case 'radio':
                        input = new duice.InputRadio(this.$el);
                        break;
                    case 'date':
                    case 'datetime-local':
                        input = new duice.InputDate(this.$el);
                        if (this.mask) {
                            input.setMask(this.mask);
                        }
                        break;
                    default:
                        input = new duice.InputGeneric(this.$el);
                }
                input.bind(this.bindMap, this.bindName);
            }
        });
        Vue.component('duice-table', {
            template: '<table><slot></slot></table>',
            props: {
                bindList: Object,
                bindItem: String
            },
            mounted: function () {
                let table = new duice.Table(this.$el);
                table.bind(this.bindList, this.bindItem);
            }
        });
    })(vue = duice.vue || (duice.vue = {}));
})(duice || (duice = {}));
