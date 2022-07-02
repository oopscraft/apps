"use strict";
var duice;
(function (duice) {
    let plugin;
    (function (plugin) {
        class MarkedFactory extends duice.MapComponentFactory {
            getSelector() {
                return `div[is="${duice.getAlias()}-plugin-marked"]`;
            }
            getComponent(element) {
                let marked = new Marked(element);
                let bind = element.dataset.duiceBind.split(',');
                marked.bind(this.getContextProperty(bind[0]), bind[1]);
                return marked;
            }
        }
        plugin.MarkedFactory = MarkedFactory;
        class Marked extends duice.MapComponent {
            constructor(div) {
                super(div);
                this.div = div;
            }
            update(map, obj) {
                this.value = map.get(this.getName());
                this.div.innerHTML = marked(duice.defaultIfEmpty(this.value, ''));
                this.div.querySelectorAll('[class^=language-]').forEach(function (pre) {
                    pre.classList.add('line-numbers');
                });
                Prism.highlightAll();
            }
            getValue() {
                return this.value;
            }
        }
        plugin.Marked = Marked;
        duice.addComponentFactory(new MarkedFactory());
    })(plugin = duice.plugin || (duice.plugin = {}));
})(duice || (duice = {}));
