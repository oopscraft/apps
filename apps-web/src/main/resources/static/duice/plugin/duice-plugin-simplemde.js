"use strict";
var duice;
(function (duice) {
    let plugin;
    (function (plugin) {
        class SimplemdeFactory extends duice.MapComponentFactory {
            getSelector() {
                return `div[is="${duice.getAlias()}-plugin-simplemde"]`;
            }
            getComponent(element) {
                let config = null;
                if (element.dataset.duiceConfig) {
                    config = JSON.parse(element.dataset.duiceConfig.replace(/\'/g, '"'));
                }
                console.log(element);
                let simplemde = new Simplemde(element, config);
                let bind = element.dataset.duiceBind.split(',');
                simplemde.bind(this.getContextProperty(bind[0]), bind[1]);
                return simplemde;
            }
        }
        plugin.SimplemdeFactory = SimplemdeFactory;
        class Simplemde extends duice.MapComponent {
            constructor(div, config) {
                super(div);
                this.div = div;
                this.div.classList.add('duice-plugin-simplemde');
                this.textarea = document.createElement('textarea');
                this.div.appendChild(this.textarea);
                this.config = {
                    element: this.textarea,
                    autoDownloadFontAwesome: false,
                    previewRender: function (plainText, preview) {
                        preview.innerHTML = marked(plainText);
                        preview.querySelectorAll('[class^=language-]').forEach(function (pre) {
                            console.debug(pre);
                            pre.classList.add('line-numbers');
                        });
                        Prism.highlightAll();
                        return preview.innerHTML;
                    },
                    tabSize: 4,
                    renderingConfig: {
                        insertTexts: {
                            horizontalRule: ["", "\n\n-----\n\n"],
                            image: ["![](http://", ")"],
                            link: ["[", "](http://)"],
                            table: ["", "\n\n| Column 1 | Column 2 | Column 3 |\n| -------- | -------- | -------- |\n| Text     | Text      | Text     |\n\n"],
                        },
                    }
                };
                if (config) {
                    for (let property in config) {
                        this.config[property] = config[property];
                    }
                }
                this.simpleMDE = new SimpleMDE(this.config);
                let _this = this;
                this.simpleMDE.codemirror.on("blur", function () {
                    console.debug(_this.simpleMDE.value());
                    _this.setChanged();
                    _this.notifyObservers(_this);
                });
            }
            update(map, obj) {
                let value = map.get(this.getName());
                if (!value) {
                    value = '';
                }
                if (value !== this.simpleMDE.value()) {
                    this.simpleMDE.value(value);
                    let codemirror = this.simpleMDE.codemirror;
                    setTimeout(function () {
                        codemirror.refresh();
                    }.bind(codemirror), 0);
                }
                this.setDisable(map.isDisable(this.getName()));
                this.setReadonly(map.isReadonly(this.getName()));
            }
            getValue() {
                return this.simpleMDE.value();
            }
            setDisable(disable) {
                this.simpleMDE.codemirror.options.readOnly = disable;
            }
            setReadonly(readonly) {
                this.simpleMDE.codemirror.options.readOnly = readonly;
            }
        }
        plugin.Simplemde = Simplemde;
        duice.addComponentFactory(new SimplemdeFactory());
    })(plugin = duice.plugin || (duice.plugin = {}));
})(duice || (duice = {}));
