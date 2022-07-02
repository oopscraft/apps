/// <reference path="../duice.ts" />
declare var CKEDITOR:any;

namespace duice {

    export namespace plugin {

        /**
         * duice.plugin.CkeditorFactory
         */
        export class CkeditorFactory extends duice.MapComponentFactory {
            getSelector(): string {
                return `div[is="${getAlias()}-plugin-ckeditor"]`;
            }
            getComponent(element:HTMLDivElement):Ckeditor {
                let config = null;
                if(element.dataset.duiceConfig){
                    config = JSON.parse(element.dataset.duiceConfig.replace(/\'/g, '"'));
                }
                let ckEditor = new Ckeditor(element, config);
                let bind = element.dataset.duiceBind.split(',');
                ckEditor.bind(this.getContextProperty(bind[0]), bind[1]);
                return ckEditor;
            }
        }

        /**
         * duice.plugin.Ckeditor
         */
        export class Ckeditor extends duice.MapComponent {
            div:HTMLDivElement;
            config:object;
            textarea:HTMLTextAreaElement;
            ckeditor:any;
            constructor(div:HTMLDivElement, config:any){
                super(div);
                this.div = div;
                this.div.classList.add('duice-plugin-ckeditor');
                this.config = config;
                this.textarea = document.createElement('textarea');
                this.div.appendChild(this.textarea);
                this.ckeditor = CKEDITOR.replace(this.textarea, this.config);
                let _this = this;
                this.ckeditor.on('blur', function(event:any){
                    if(_this.map.get(_this.getName()) !== _this.getValue()){
                        _this.setChanged();
                        _this.notifyObservers(_this);
                    }
                });
            }
            update(map:duice.Map, obj:object){
                let value = map.get(this.getName());

                // check value is empty
                if(!value){
                    value = '';
                }
                
                // sets value
                if(value !== this.ckeditor.getData()){
                    this.ckeditor.setData(value);
                }
            }
            getValue():any {
                return this.ckeditor.getData();
            }
        }

        // Adds component definition
        addComponentFactory(new CkeditorFactory());
    }

}
