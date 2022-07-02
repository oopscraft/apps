/// <reference path="../duice.ts" />
declare var SimpleMDE:any;
declare var Prism:any;
declare var marked:any;

namespace duice {

    export namespace plugin {

        /**
         * duice.integrate.SimplemdeFactory
         */
        export class SimplemdeFactory extends duice.MapComponentFactory {
            getSelector(): string {
                return `div[is="${getAlias()}-plugin-simplemde"]`;
            }
            getComponent(element:HTMLDivElement):Simplemde {
                let config = null;
                if(element.dataset.duiceConfig){
                    config = JSON.parse(element.dataset.duiceConfig.replace(/\'/g, '"'));
                }
                console.log(element);
                let simplemde = new Simplemde(element, config);
                let bind = element.dataset.duiceBind.split(',');
                simplemde.bind(this.getContextProperty(bind[0]), bind[1]);
                return simplemde;
            }
        }

        /**
         * duice.plugin.Ckeditor
         */
        export class Simplemde extends duice.MapComponent {
            div:HTMLDivElement;
            config:any;
            textarea:HTMLTextAreaElement;
            simpleMDE:any;
            constructor(div:HTMLDivElement, config:any){
                super(div);
                this.div = div;
                this.div.classList.add('duice-plugin-simplemde');
                this.textarea = document.createElement('textarea');
                this.div.appendChild(this.textarea);

                // setting default config
                this.config = {
                    element: this.textarea,
                    autoDownloadFontAwesome: false,
                	// previewRender: function(plainText:string):string {
                	// 	return marked(plainText); // Returns HTML from a custom parser
                	// },
                	previewRender: function(plainText:string, preview:any):string { // Async method
                        preview.innerHTML = marked(plainText);
                        preview.querySelectorAll('[class^=language-]').forEach(function(pre:HTMLElement){
                            console.debug(pre);
                            pre.classList.add('line-numbers');
                        });
                        // highlight
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
                }

                // in case of custm config is exists.
                if(config){
                    for(let property in config){
                        this.config[property] = config[property];
                    }
                }

                // creates simpleMDE
                this.simpleMDE = new SimpleMDE(this.config);
                let _this = this;
                this.simpleMDE.codemirror.on("blur", function(){
                    console.debug(_this.simpleMDE.value());
                    _this.setChanged();
                    _this.notifyObservers(_this);
                });
            }
            update(map:duice.Map, obj:object){
                let value = map.get(this.getName());

                // check value is empty
                if(!value){
                    value = '';
                }

                // checks value is changed
                if(value !== this.simpleMDE.value()){
                    // sets value
                    this.simpleMDE.value(value);
                    // Fixes CodeMirror bug (#344) - refresh not working after value changed.
                    let codemirror = this.simpleMDE.codemirror;
                    setTimeout(function() {
                        codemirror.refresh();
                    }.bind(codemirror), 0);
                }

                // handles readonly and disable
                this.setDisable(map.isDisable(this.getName()));
                this.setReadonly(map.isReadonly(this.getName()));
            }
            getValue():any {
                return this.simpleMDE.value();
            }
            setDisable(disable:boolean):void {
                this.simpleMDE.codemirror.options.readOnly = disable;
            }
            setReadonly(readonly:boolean):void {
                this.simpleMDE.codemirror.options.readOnly = readonly;
            }
        }

        // Adds component definition
        addComponentFactory(new SimplemdeFactory());

    }

}
