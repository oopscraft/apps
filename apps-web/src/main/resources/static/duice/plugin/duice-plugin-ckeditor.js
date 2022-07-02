"use strict";
var duice;
(function (duice) {
    let plugin;
    (function (plugin) {
        class CkeditorFactory extends duice.MapComponentFactory {
            getSelector() {
                return `div[is="${duice.getAlias()}-plugin-ckeditor"]`;
            }
            getComponent(element) {
                let config = null;
                if (element.dataset.duiceConfig) {
                    config = JSON.parse(element.dataset.duiceConfig.replace(/\'/g, '"'));
                }
                let ckEditor = new Ckeditor(element, config);
                let bind = element.dataset.duiceBind.split(',');
                ckEditor.bind(this.getContextProperty(bind[0]), bind[1]);
                return ckEditor;
            }
        }
        plugin.CkeditorFactory = CkeditorFactory;
        class Ckeditor extends duice.MapComponent {
            constructor(div, config) {
                super(div);
                this.div = div;
                this.div.classList.add('duice-plugin-ckeditor');
                this.config = config;
                this.textarea = document.createElement('textarea');
                this.div.appendChild(this.textarea);
                this.ckeditor = CKEDITOR.replace(this.textarea, this.config);
                let _this = this;
                this.ckeditor.on('blur', function (event) {
                    if (_this.map.get(_this.getName()) !== _this.getValue()) {
                        _this.setChanged();
                        _this.notifyObservers(_this);
                    }
                });
            }
            update(map, obj) {
                let value = map.get(this.getName());
                if (!value) {
                    value = '';
                }
                if (value !== this.ckeditor.getData()) {
                    this.ckeditor.setData(value);
                }
            }
            getValue() {
                return this.ckeditor.getData();
            }
        }
        plugin.Ckeditor = Ckeditor;
        duice.addComponentFactory(new CkeditorFactory());
    })(plugin = duice.plugin || (duice.plugin = {}));
})(duice || (duice = {}));
