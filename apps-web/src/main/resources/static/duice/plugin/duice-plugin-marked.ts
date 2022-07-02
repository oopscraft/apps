/// <reference path="../duice.ts" />
declare var Prism:any;
declare var marked:any;

namespace duice {

    export namespace plugin {

        /**
         * duice.integrate.MarkedFactory
         */
        export class MarkedFactory extends duice.MapComponentFactory {
            getSelector(): string {
                return `div[is="${getAlias()}-plugin-marked"]`;
            }
            getComponent(element:HTMLDivElement):Marked {
                let marked = new Marked(element);
                let bind = element.dataset.duiceBind.split(',');
                marked.bind(this.getContextProperty(bind[0]), bind[1]);
                return marked;
            }
        }

        /**
         * duice.plugin.Ckeditor
         */
        export class Marked extends duice.MapComponent {
            div:HTMLDivElement;
            value:string;
            constructor(div:HTMLDivElement){
                super(div);
                this.div = div;
            }
            update(map:duice.Map, obj:object){
                this.value = map.get(this.getName());
                this.div.innerHTML = marked(duice.defaultIfEmpty(this.value,''));
                this.div.querySelectorAll('[class^=language-]').forEach(function(pre:Element){
                    pre.classList.add('line-numbers');
                });
                // highlight
                Prism.highlightAll();
            }
            getValue():any {
                return this.value;
            }
        }

        // Adds component definition
        addComponentFactory(new MarkedFactory());
    }

}
