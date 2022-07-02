/* =============================================================================
 * DUICE (Data-oriented UI Component Engine)
 * - Anyone can use it freely.
 * - Modify the source or allow re-creation. However, you must state that you have the original creator.
 * - However, we can not grant patents or licenses for reproductives. (Modifications or reproductions must be shared with the public.)
 * Licence: LGPL(GNU Lesser General Public License version 3)
 * Copyright (C) 2016 chomookun@gmail.com 
 * ============================================================================= */

/**
 * project package
 */
namespace duice {

    let alias = 'duice';
    let componentFactories:ComponentFactory[] = [];

    /**
     * sets alias
     * @param value
     */
    export function setAlias(value:string):void {
        alias = value;
    }

    /**
     * gets alias
     */
    export function getAlias():string {
        return alias;
    }

    /**
     * adds component factory
     * @param componentFactory 
     */
    export function addComponentFactory(componentFactory:ComponentFactory):void {
        componentFactories.push(componentFactory);
    }

    /**
     * returns component factories
     */
    export function getComponentFactories():any {
        return componentFactories;
    }

    /**
     * Initializes component
     * @param container
     * @param $context
     */
    export function initializeComponent(container:any, $context:any) {
        [ListComponentFactory, MapComponentFactory].forEach(function(factoryType){
            componentFactories.forEach(function(componentFactory:ComponentFactory){
                let elements = container.querySelectorAll(componentFactory.getSelector()+`[data-${getAlias()}-bind]:not([data-${getAlias()}-id])`);
                for(let i = 0, size = elements.length; i < size; i ++ ){
                    let element = elements[i];
                    if(componentFactory instanceof factoryType){
                        componentFactory.setContext($context);
                        componentFactory.getComponent(element);
                    }
                }
            });
        });
    }

    /**
     * assert
     * @param expression 
     * @param message 
     */
    export function assert(expression:boolean, message:string) {
        if(!expression){
            throw message;
        }
    }

    /**
     * Check if value is empty
     * @param value
     * @return whether value is empty
     */
    export function isEmpty(value:any){
        return value === undefined
            || value === null
            || value === ''
            || trim(value) === '';
    }
    
    /**
     * Check if value is not empty
     * @param value
     * @return whether value is not empty
     */
    export function isNotEmpty(value:any) {
        return !isEmpty(value);
    }
    
    /**
     * Checks if value is empty and return specified value as default
     * @param value to check
     * @param defaultValue value if value is empty
     */
    export function defaultIfEmpty(value:any, defaultValue:any) {
        if(isEmpty(value) === true) {
            return defaultValue;
        }else{
            return value;
        }
    }

    /**
     * trim string
     * @param value 
     */
    export function trim(value:string):string {
        return (value + "").trim();
    }
    
    /**
     * converts value to left-padded value
     * @param value value
     * @param length to pad
     * @param padChar character
     * @return left-padded value
     */
    export function padLeft(value:string, length:number, padChar:string) {
        for(let i = 0, size = (length-value.length); i < size; i ++ ) {
            value = padChar + value;
        }
        return value;
    }
    
    /**
     * converts value to right-padded value
     * @param value value
     * @param length to pad
     * @param padChar character
     * @return right-padded string
     */
    export function padRight(value:string, length:number, padChar:string) {
        for(let i = 0, size = (length-value.length); i < size; i ++ ) {
            value = value + padChar;
        }
        return value;
    }

    /**
     * Returns current upper window object.
     * @return window object
     */
    function getCurrentWindow():Window {
        if(window.frameElement){
            return window.parent;
        }else{
            return window;
        }
    }
    
    /**
     * Returns current max z-index value.
     * @return max z-index value
     */
    function getCurrentMaxZIndex():number {
        let zIndex,
        z = 0,
        all = document.getElementsByTagName('*');
        for (let i = 0, n = all.length; i < n; i++) {
            zIndex = document.defaultView.getComputedStyle(all[i],null).getPropertyValue("z-index");
            zIndex = parseInt(zIndex, 10);
            z = (zIndex) ? Math.max(z, zIndex) : z;
        }
        return z;
    }

    /**
     * duice.Mask interface
     */
    export interface Mask {
        
        /**
         * Encodes original value as formatted value
         * @param value value
         * @return formatted value
         */
        encode(value:any):any;
        
        /**
         * Decodes formatted value to original value
         * @param value value
         * @return original value
         */
        decode(value:any):any;
    }
    
    /**
     * duice.StringFormat
     * @param string format
     */
    export class StringMask implements Mask {
        pattern:string;
    
        /**
         * Constructor
         * @param pattern
         */
        constructor(pattern?:string){
            this.pattern = pattern;
        }

        /**
         * encode string as format
         * @param value
         */
        encode(value:any):any{
            if(isEmpty(this.pattern)){
                return value;
            }
            let encodedValue = '';
            let patternChars = this.pattern.split('');
            let valueChars = value.split('');
            let valueCharsPosition = 0;
            for(let i = 0, size = patternChars.length; i < size; i ++ ){
                let patternChar = patternChars[i];
                if(patternChar === '#'){
                    encodedValue += defaultIfEmpty(valueChars[valueCharsPosition++], '');
                } else {
                    encodedValue += patternChar;
                }
            }
            return encodedValue;
        }
        
        /**
         * decodes string as format
         * @param value
         */
        decode(value:any):any{
            if(isEmpty(this.pattern)){
                return value;
            }
            let decodedValue = '';
            let patternChars = this.pattern.split('');
            let valueChars = value.split('');
            let valueCharsPosition = 0;
            for(let i = 0, size = patternChars.length; i < size; i ++ ){
                let patternChar = patternChars[i];
                if (patternChar === '#') {
                    decodedValue += defaultIfEmpty(valueChars[valueCharsPosition++], '');
                } else {
                    valueCharsPosition++;
                }
            }
            return decodedValue;
        }
    }
    
    /**
     * duice.NumberFormat
     * @param scale number
     */
    export class NumberMask implements Mask {
        scale:number = 0;
    
       /**
        * Constructor
        * @param scale
        */
        constructor(scale?:number){
            this.scale = scale;
        }
        
        /**
         * Encodes number as format
         * @param number
         */
        encode(number:number):string{
            if(isEmpty(number) || isNaN(Number(number))){
                return '';
            }
            number = Number(number);
            let string = String(number.toFixed(this.scale));
            let reg = /(^[+-]?\d+)(\d{3})/;
            while (reg.test(string)) {
                string = string.replace(reg, '$1' + ',' + '$2');
            }
            return string;
        }
        
        /**
         * Decodes formatted value as original value
         * @param string
         */
        decode(string:string):number{
            if(isEmpty(string)){
                return null;
            }
            if(string.length === 1 && /[+-]/.test(string)){
                string += '0';
            }
            string = string.replace(/,/gi,'');
            if(isNaN(Number(string))){
                throw 'NaN';
            }
            let number = Number(string);
            number = Number(number.toFixed(this.scale));
            return number;
        }
    }
    
    /**
     * duice.DateFormat
     */
    export class DateMask implements Mask {
        pattern:string;
        patternRex = /yyyy|yy|MM|dd|HH|hh|mm|ss/gi;
        
        /**
         * Constructor
         * @param pattern
         */
        constructor(pattern?:string){
            this.pattern = pattern;
        }

        /**
         * Encodes date string
         * @param string
         */
        encode(string: string): string {
            if (isEmpty(string)) {
                return '';
            }
            if (isEmpty(this.pattern)) {
                return new Date(string).toString();
            }
            let date = new Date(string);
            string = this.pattern.replace(this.patternRex, function ($1: any) {
                switch ($1) {
                    case "yyyy":
                        return date.getFullYear();
                    case "yy":
                        return padLeft(String(date.getFullYear() % 1000), 2, '0');
                    case "MM":
                        return padLeft(String(date.getMonth() + 1), 2, '0');
                    case "dd":
                        return padLeft(String(date.getDate()), 2, '0');
                    case "HH":
                        return padLeft(String(date.getHours()), 2, '0');
                    case "hh":
                        return padLeft(String(date.getHours() <= 12 ? date.getHours() : date.getHours() % 12), 2, '0');
                    case "mm":
                        return padLeft(String(date.getMinutes()), 2, '0');
                    case "ss":
                        return padLeft(String(date.getSeconds()), 2, '0');
                    default:
                        return $1;
                }
            });
            return string;
        }
        
        /**
         * Decodes formatted date string to ISO date string.
         * @param string
         */
        decode(string:string):string{
            if(isEmpty(string)){
                return null;
            }
            if(isEmpty(this.pattern)){
                return new Date(string).toISOString();
            }
            let date = new Date(0,0,0,0,0,0);
            let match;
            while ((match = this.patternRex.exec(this.pattern)) != null) {
                let formatString = match[0];
                let formatIndex = match.index;
                let formatLength = formatString.length;
                let matchValue = string.substr(formatIndex, formatLength);
                matchValue = padRight(matchValue, formatLength,'0');
                switch (formatString) {
                    case 'yyyy': {
                        var fullYear = parseInt(matchValue);
                        date.setFullYear(fullYear);
                        break;
                    }
                    case 'yy': {
                        let yyValue = parseInt(matchValue);
                        let yearPrefix = Math.floor(new Date().getFullYear() / 100);
                        let fullYear = yearPrefix * 100 + yyValue;
                        date.setFullYear(fullYear);
                        break;
                    }
                    case 'MM': {
                        let monthValue = parseInt(matchValue);
                        date.setMonth(monthValue - 1);
                        break;
                    }
                    case 'dd': {
                        let dateValue = parseInt(matchValue);
                        date.setDate(dateValue);
                        break;
                    }
                    case 'HH': {
                        let hoursValue = parseInt(matchValue);
                        date.setHours(hoursValue);
                        break;
                    }
                    case 'hh': {
                        let hoursValue = parseInt(matchValue);
                        date.setHours(hoursValue > 12 ? (hoursValue + 12) : hoursValue);
                        break;
                    }
                    case 'mm': {
                        let minutesValue = parseInt(matchValue);
                        date.setMinutes(minutesValue);
                        break;
                    }
                    case 'ss': {
                        let secondsValue = parseInt(matchValue);
                        date.setSeconds(secondsValue);
                        break;
                    }
                }
            }
            return date.toISOString();
        }
    }

   /**
     * duice.Blocker
     */
   export class Blocker {
       element: HTMLElement;
       div: HTMLDivElement;
       opacity: number = 0.2;

       constructor(element: HTMLElement) {
           this.element = element;
           this.div = document.createElement('div');
           this.div.classList.add('duice-blocker');
       }

       setOpacity(opacity: number): void {
           this.opacity = opacity;
       }

       block() {

           // adjusting position
           this.div.style.position = 'fixed';
           this.div.style.zIndex = String(getCurrentMaxZIndex() + 1);
           this.div.style.background = 'rgba(0, 0, 0, ' + this.opacity + ')';
           this.takePosition();

           // adds events
           let _this = this;
           getCurrentWindow().addEventListener('scroll', function () {
               _this.takePosition();
           });

           // append
           this.element.appendChild(this.div);
       }

       unblock() {
           this.element.removeChild(this.div);
       }

       takePosition() {
           // full blocking in case of BODY
           if (this.element.tagName == 'BODY') {
               this.div.style.width = '100%';
               this.div.style.height = '100%';
               this.div.style.top = '0px';
               this.div.style.left = '0px';
           }
           // adjusting to parent element
           else {
               let boundingClientRect = this.element.getBoundingClientRect();
               let width = boundingClientRect.width;
               let height = boundingClientRect.height;
               let left = boundingClientRect.left;
               let top = boundingClientRect.top;
               this.div.style.width = width + "px";
               this.div.style.height = height + "px";
               this.div.style.top = top + 'px';
               this.div.style.left = left + 'px';
           }
       }

       getBlockDiv(): HTMLDivElement {
           return this.div;
       }
   }

    /**
     * Dialog
     */
    export abstract class Dialog {
        // noinspection JSDeprecatedSymbols
        dialog: HTMLDialogElement;
        header: HTMLSpanElement;
        closeButton: HTMLSpanElement;
        contentDiv: HTMLDivElement;
        contentParentNode: Node;
        promise: Promise<any>;
        promiseResolve: Function;
        promiseReject: Function;

        protected constructor(contentDiv: HTMLDivElement) {
            let _this = this;
            this.contentDiv = contentDiv;

            this.dialog = document.createElement('dialog');
            this.dialog.classList.add('duice-dialog');

            // creates header
            this.header = document.createElement('span');
            this.header.classList.add('duice-dialog__header');
            this.dialog.appendChild(this.header);

            // drag
            let currentWindow = getCurrentWindow();
            this.dialog.style.margin = '0px';
            this.header.onmousedown = function (event) {
                let pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
                pos3 = event.clientX;
                pos4 = event.clientY;
                currentWindow.document.onmouseup = function (event) {
                    currentWindow.document.onmousemove = null;
                    currentWindow.document.onmouseup = null;

                };
                currentWindow.document.onmousemove = function (event) {
                    pos1 = pos3 - event.clientX;
                    pos2 = pos4 - event.clientY;
                    pos3 = event.clientX;
                    pos4 = event.clientY;
                    _this.dialog.style.left = (_this.dialog.offsetLeft - pos1) + 'px';
                    _this.dialog.style.top = (_this.dialog.offsetTop - pos2) + 'px';
                };
            };

            // creates close button
            this.closeButton = document.createElement('span');
            this.closeButton.classList.add('duice-dialog__closeButton');
            this.closeButton.addEventListener('click', function (event) {
                _this.reject();
            });
            this.dialog.appendChild(_this.closeButton);

            // on resize event
            currentWindow.addEventListener('resize', function (event) {
                _this.moveToCenterPosition();
            });
        }

        /**
         * Shows modal
         */
        show() {
            // set content parent node
            if (this.contentDiv.parentNode) {
                this.contentParentNode = this.contentDiv.parentNode;
            }

            // adds contents
            this.dialog.appendChild(this.contentDiv);

            // show dialog modal
            getCurrentWindow().document.body.appendChild(this.dialog);
            this.contentDiv.style.display = 'block';
            // @ts-ignore
            this.dialog.showModal();

            // adjusting position
            this.moveToCenterPosition();
        }

        /**
         * Hides modal
         */
        hide() {
            // restore parent node
            if (this.contentParentNode) {
                this.contentParentNode.appendChild(this.contentDiv);
            }

            // closes modal
            // @ts-ignore
            this.dialog.close();
            this.contentDiv.style.display = 'none';
        }

        /**
         * moveToCenterPosition
         */
        moveToCenterPosition() {
            let currentWindow = getCurrentWindow();
            let computedStyle = currentWindow.getComputedStyle(this.dialog);
            let computedWidth = parseInt(computedStyle.getPropertyValue('width').replace(/px/gi, ''));
            let computedHeight = parseInt(computedStyle.getPropertyValue('height').replace(/px/gi, ''));
            this.dialog.style.left = Math.max(0, currentWindow.innerWidth / 2 - computedWidth / 2) + 'px';
            this.dialog.style.top = Math.max(0, currentWindow.innerHeight / 2 - computedHeight / 2) + 'px';
        }

        /**
         * open
         */
        async open() {

            // show modal
            this.show();

            // creates promise
            let _this = this;
            this.promise = new Promise(function (resolve, reject) {
                _this.promiseResolve = resolve;
                _this.promiseReject = reject;
            });
            return this.promise;
        }

        /**
         * confirm
         * @param args
         */
        resolve(...args: any[]) {
            this.hide();
            this.promiseResolve(...args);
        }

        /**
         * close
         * @param args
         */
        reject(...args: any[]) {
            this.hide();
            this.promiseReject(...args);
        }
    }

    /**
     * duice.Alert
     */
    export class Alert extends Dialog {
        message: string;
        iconDiv: HTMLDivElement;
        messageDiv: HTMLDivElement;
        buttonDiv: HTMLDivElement;
        confirmButton: HTMLButtonElement;

        constructor(message: string) {
            let contentDiv = document.createElement('div');
            super(contentDiv);
            this.message = message;
            let _this = this;

            // creates icon div
            this.iconDiv = document.createElement('div');
            this.iconDiv.classList.add('duice-alert__iconDiv');

            // creates message div
            this.messageDiv = document.createElement('div');
            this.messageDiv.classList.add('duice-alert__messageDiv');
            this.messageDiv.innerHTML = this.message;

            // creates button div
            this.buttonDiv = document.createElement('div');
            this.buttonDiv.classList.add('duice-alert__buttonDiv');

            // creates confirm button
            this.confirmButton = document.createElement('button');
            this.confirmButton.classList.add('duice-alert__buttonDiv-button');
            this.confirmButton.classList.add('duice-alert__buttonDiv-button--confirm');
            this.confirmButton.addEventListener('click', function (event) {
                _this.resolve();
            });
            this.buttonDiv.appendChild(this.confirmButton);

            // append parts to bodyDiv
            contentDiv.appendChild(this.iconDiv);
            contentDiv.appendChild(this.messageDiv);
            contentDiv.appendChild(this.buttonDiv);
        }

        open() {
            let promise = super.open();
            this.confirmButton.focus();
            return promise;
        }
    }
    
    /**
     * duice.Confirm
     */
    export class Confirm extends Dialog {
        message:string;
        iconDiv:HTMLDivElement;
        messageDiv:HTMLDivElement;
        buttonDiv:HTMLDivElement;
        cancelButton:HTMLButtonElement;
        confirmButton:HTMLButtonElement;
        constructor(message:string) {
            let contentDiv = document.createElement('div');
            super(contentDiv);
            this.message = message;
            let _this = this;
            
            // creates icon div
            this.iconDiv = document.createElement('div');
            this.iconDiv.classList.add('duice-confirm__iconDiv');

            // creates message div
            this.messageDiv = document.createElement('div');
            this.messageDiv.classList.add('duice-confirm__messageDiv');
            this.messageDiv.innerHTML = this.message;

            // creates button div
            this.buttonDiv = document.createElement('div');
            this.buttonDiv.classList.add('duice-confirm__buttonDiv');
            
            // confirm button
            this.confirmButton = document.createElement('button');
            this.confirmButton.classList.add('duice-confirm__buttonDiv-button');
            this.confirmButton.classList.add('duice-confirm__buttonDiv-button--confirm');
            this.confirmButton.addEventListener('click', function(event){
               _this.resolve(true); 
            });
            this.buttonDiv.appendChild(this.confirmButton);

            // cancel button
            this.cancelButton = document.createElement('button');
            this.cancelButton.classList.add('duice-confirm__buttonDiv-button');
            this.cancelButton.classList.add('duice-confirm__buttonDiv-button--cancel');
            this.cancelButton.addEventListener('click', function(event){
               _this.resolve(false); 
            });
            this.buttonDiv.appendChild(this.cancelButton);
            
            // append parts to bodyDiv
            contentDiv.appendChild(this.iconDiv);
            contentDiv.appendChild(this.messageDiv);
            contentDiv.appendChild(this.buttonDiv);
        }
        open() {
            let promise = super.open();
            this.confirmButton.focus();
            return promise;
        }
    }
    
    /**
     * duice.Prompt
     */
    export class Prompt extends Dialog {
        message:string;
        defaultValue:string;
        iconDiv:HTMLDivElement;
        messageDiv:HTMLDivElement;
        inputDiv:HTMLDivElement;
        input:HTMLInputElement;
        buttonDiv:HTMLDivElement;
        cancelButton:HTMLButtonElement;
        confirmButton:HTMLButtonElement;
        constructor(message:string, defaultValue:string) {
            let contentDiv = document.createElement('div');
            super(contentDiv);
            this.message = message;
            this.defaultValue = defaultValue;
            let _this = this;
            
            // creates icon div
            this.iconDiv = document.createElement('div');
            this.iconDiv.classList.add('duice-prompt__iconDiv');
            
            // creates message div
            this.messageDiv = document.createElement('div');
            this.messageDiv.classList.add('duice-prompt__messageDiv');
            this.messageDiv.innerHTML = this.message;
            
            // creates input div
            this.inputDiv = document.createElement('div');
            this.inputDiv.classList.add('duice-prompt__inputDiv');
            this.input = document.createElement('input');
            this.input.classList.add('duice-prompt__inputDiv-input');
            if(this.defaultValue){
                this.input.value = this.defaultValue;
            }
            this.inputDiv.appendChild(this.input);

            // creates button div
            this.buttonDiv = document.createElement('div');
            this.buttonDiv.classList.add('duice-prompt__buttonDiv');
          
            // confirm button
            this.confirmButton = document.createElement('button');
            this.confirmButton.classList.add('duice-prompt__buttonDiv-button');
            this.confirmButton.classList.add('duice-prompt__buttonDiv-button--confirm');
            this.confirmButton.addEventListener('click', function(event){
               _this.resolve(_this.input.value); 
            });
            this.buttonDiv.appendChild(this.confirmButton);

            // cancel button
            this.cancelButton = document.createElement('button');
            this.cancelButton.classList.add('duice-prompt__buttonDiv-button');
            this.cancelButton.classList.add('duice-prompt__buttonDiv-button--cancel');
            this.cancelButton.addEventListener('click', function(event){
               _this.resolve(null);
            });
            this.buttonDiv.appendChild(this.cancelButton);
            
            // appends parts to bodyDiv
            contentDiv.appendChild(this.iconDiv);
            contentDiv.appendChild(this.messageDiv);
            contentDiv.appendChild(this.inputDiv);
            contentDiv.appendChild(this.buttonDiv);
        }
        open() {
            let promise = super.open();
            this.input.focus();
            return promise;
        }
    }

    /**
     * duice.TabFolderEventListener
     */
    class TabFolderEventListener {
        onBeforeSelectTab:Function;
        onAfterSelectTab:Function;
    }

    /**
     * duice.TabFolder
     */
    export class TabFolder {
        tabs:Array<Tab> = new Array();
        eventListener:TabFolderEventListener = new TabFolderEventListener();
        addTab(tab:Tab):void {
            let _this = this;

            // adds event listener
            const index = Number(this.tabs.length);
            tab.getButton().addEventListener('click', function(event:any){
                _this.selectTab(index);
            });

            // adds tab
            this.tabs.push(tab);
        }
        async selectTab(index:number) {

            // calls onBeforeSelectTab 
            if(this.eventListener.onBeforeSelectTab){
                if(await this.eventListener.onBeforeSelectTab.call(this, this.tabs[index]) === false){
                    throw 'canceled';
                }
            }

            // activates selected tab
            for(let i = 0, size = this.tabs.length; i < size; i ++ ){
                let tab = this.tabs[i];
                if(i === index){
                    tab.setActive(true);
                }else{
                    tab.setActive(false);
                }
            }

            // calls 
            if(this.eventListener.onAfterSelectTab){
                this.eventListener.onAfterSelectTab.call(this, this.tabs[index]);
            }
        }
        onBeforeSelectTab(listener:Function):any {
            this.eventListener.onBeforeSelectTab = listener;
            return this;
        }
        onAfterSelectTab(listener:Function):any {
            this.eventListener.onAfterSelectTab = listener;
            return this;
        }
    }

    /**
     * duice.Tab
     */
    export class Tab {
        button:HTMLElement;
        content:HTMLElement;
        constructor(button:HTMLElement, content:HTMLElement) {
            this.button = button;
            this.content = content;
        }
        getButton():HTMLElement {
            return this.button;
        }
        getContent():HTMLElement {
            return this.content;
        }
        setActive(active:boolean):void {
            if(active === true){
                this.button.style.opacity = 'unset';
                this.content.style.display = null;
            }else{
                this.button.style.opacity = '0.5';
                this.content.style.display = 'none';
            }
        }
    }

    /**
     * duice.Observable
     * Observable abstract class of Observer Pattern
     */
    abstract class Observable {
        observers:Array<Observer> = new Array<Observer>();
        changed:boolean = false;
        notifyEnable:boolean = true;

        /**
         * Adds observer instance
         * @param observer
         */
        addObserver(observer:Observer):void {
            for(let i = 0, size = this.observers.length; i < size; i++){
                if(this.observers[i] === observer){
                    return;
                }
            }
            this.observers.push(observer);
        }
        /**
         * Removes specified observer instance from observer instances
         * @param observer
         */
        removeObserver(observer:Observer):void {
            for(let i = 0, size = this.observers.length; i < size; i++){
                if(this.observers[i] === observer){
                    this.observers.splice(i,1);
                    return;
                }
            }
        }
        /**
         * Notifies changes to observers
         * @param obj object to transfer to observer
         */
        notifyObservers(obj:object):void {
            if(this.notifyEnable && this.hasChanged()){
                this.clearUnavailableObservers();
                for(let i = 0, size = this.observers.length; i < size; i++){
                    if(this.observers[i] !== obj){
                        try {
                            this.observers[i].update(this, obj);
                        }catch(e){
                            console.error(e, this.observers[i]);
                        }
                    }
                }
                this.clearChanged();
            }
        }

        /**
         * Suspends notify
         */
        suspendNotify():void {
            this.notifyEnable = false;
        }

        /**
         * Resumes notify
         */
        resumeNotify():void {
            this.notifyEnable = true;
        }

        /**
         * Sets changed flag 
         */
        setChanged():void {
            this.changed = true;
        }
        /**
         * Returns changed flag
         */
        hasChanged():boolean {
            return this.changed;
        }
        /**
         * Clears changed flag
         */
        clearChanged():void {
            this.changed = false;
        }
        /**
         * Clears unavailable observers to prevent memory leak
         */
        clearUnavailableObservers():void {
            for(let i = this.observers.length - 1; i >= 0; i--){
                try {
                    if(this.observers[i].isAvailable() === false){
                        this.observers.splice(i,1);
                    }
                }catch(e){
                    console.error(e, this.observers[i]);
                }
            }
        }
    }
    
    /**
     * duice.Observer
     * Observer interface of Observer Pattern
     */
    interface Observer {
        isAvailable():boolean;
        update(observable:Observable, obj:object):void;
    }
    
    /**
     * Abstract data object
     * extends from Observable and implements Observer interface.
     */
    export abstract class DataObject extends Observable implements Observer {
        available:boolean = true;
        disable:any = new Object();
        disableAll:boolean = false;
        readonly:any = new Object();
        readonlyAll:boolean = false;
        visible:boolean = true;

        /**
         * clones object
         * @param obj 
         */
        clone(obj:object){
            return JSON.parse(JSON.stringify(obj));
        }

        /**
         * Updates self data object from observable instance 
         * @param observable
         * @param obj
         */
        abstract update(observable:Observable, obj:object):void;
        
        /**
         * Loads data from JSON object 
         * @param args
         */
        abstract fromJson(...args: any[]):void;
        
        /**
         * Converts data into JSON object.
         * @param args
         * @return JSON object
         */
        abstract toJson(...args: any[]):object;

        /**
         * Clears data
         */
        abstract clear():void;

        /**
         * save point
         */
        abstract save():void;

        /**
         * Restores data as original data.
         */
        abstract reset():void;

        /**
         * Checks original data is changed.
         * @return whether original data is changed
         */
        abstract isDirty():boolean;

        /**
         * Returns whether instance is active 
         */
        isAvailable():boolean {
            return true;
        }

        setDisable(name:string, disable:boolean):void {
            this.disable[name] = disable;
            this.setChanged();
            this.notifyObservers(this);
        }

        /**
         * Sets disable all
         * @param disable 
         */
        setDisableAll(disable:boolean):void {
            this.disableAll = disable;
            for(let name in this.disable){
                this.disable[name] = disable;
            }
            this.setChanged();
            this.notifyObservers(this);
        }

        /**
         * Returns if disabled
         */
        isDisable(name:string):boolean {
            if(this.disable.hasOwnProperty(name)){
                return this.disable[name];
            }else{
                return this.disableAll;
            }
        }

        /**
         * Sets read-only
         * @param name 
         */
        setReadonly(name:string, readonly:boolean):void {
            this.readonly[name] = readonly;
            this.setChanged();
            this.notifyObservers(this);
        }

        /**
         * Sets read-only all
         * @param readonly
         */
        setReadonlyAll(readonly:boolean):void {
            this.readonlyAll = readonly;
            for(let name in this.readonly){
                this.readonly[name] = readonly;
            }
            this.setChanged();
            this.notifyObservers(this);
        }

        /**
         * Returns read-only
         * @param name 
         */
        isReadonly(name:string):boolean {
            if(this.readonly.hasOwnProperty(name)){
                return this.readonly[name];
            }else{
                return this.readonlyAll;
            }
        }

        /**
         * Sets visible flag
         * @param visible 
         */
        setVisible(visible:boolean):void {
            this.visible = visible;
            for(let i = 0, size = this.observers.length; i < size; i++){
                try {
                    if(this.observers[i] instanceof Component){
                        let uiComponent = <Component>this.observers[i];
                        uiComponent.setVisible(visible);
                    }
                }catch(e){
                    console.error(e, this.observers[i]);
                }
            }
        }

        /**
         * Returns is visible.
         */
        isVisible():boolean {
            return this.visible;
        }

    }

    /**
     * duice.MapEventListener
     */
    class MapEventListener {
        onBeforeChange:Function;
        onAfterChange:Function;
    }

    /**
     * Map data structure
     * @param JSON object
     */
    export class Map extends DataObject {
        data:any = new Object();                            // internal data object
        originData:string = JSON.stringify(this.data);      // original string JSON data
        eventListener:MapEventListener = new MapEventListener();
    
        /**
         * constructor 
         * @param json
         */
        constructor(json?:any) {
            super();
            this.fromJson(json || {});
        }
        
        /**
         * Updates data from observable instance
         * @param mapComponent
         * @param obj
         */
        update(mapComponent:MapComponent, obj:object):void {
            console.debug('Map.update', mapComponent, obj);
            let name = mapComponent.getName();
            let value = mapComponent.getValue();
            this.set(name, value);
        }
        
        /**
         * Loads data from JSON object.
         * @param json
         */
        fromJson(json:any): void {
            // sets data
            this.data = new Object();
            for(let name in json){
                this.data[name] = json[name];
            }

            // save point
            this.save();
            
            // notify to observers
            this.setChanged();
            this.notifyObservers(this);
        }
        
        /**
         * Convert data to JSON object
         * @return JSON object
         */
        toJson():object {
            let json: any = new Object();
            for(let name in this.data){
                json[name] = this.data[name];
            }
            return json;
        }

        /**
         * Clears data
         */
        clear():void {
            this.data = new Object();
            this.setChanged();
            this.notifyObservers(this);
        }

        /**
         * Save point
         */
        save():void {
            this.originData = JSON.stringify(this.toJson());
        }

        /**
         * Restores instance as original data
         */
        reset():void {
            this.fromJson(JSON.parse(this.originData));
        }
        
        /**
         * Checks original data is changed
         * @return whether original data is changed or not
         */
        isDirty():boolean {
            if(JSON.stringify(this.toJson()) === this.originData){
                return false;
            }else{
                return true;
            }
        }
        
        /**
         * Sets property as input value
         * @param name
         * @param value
         */
        async set(name:string, value:any) {

            // calls beforeChange
            if(this.eventListener.onBeforeChange){
                try {
                    if(await this.eventListener.onBeforeChange.call(this,name,value) === false){
                        throw 'Map.set is canceled';
                    }
                }catch(e){
                    this.setChanged();
                    this.notifyObservers(this);
                    throw e;
                }
            }

            // changes value
            this.data[name] = value;
            this.setChanged();
            this.notifyObservers(this);

            // calls 
            if(this.eventListener.onAfterChange){
                this.eventListener.onAfterChange.call(this,name,value);
            }

            // return true
            return true;
        }
        
        /**
         * Gets specified property value.
         * @param name
         */
        get(name:string):any {
            return this.data[name];
        }

        /**
         * Returns properties names as array.
         * @return array of names
         */
        getNames():string[]{
            let names = new Array();
            for(let name in this.data){
                names.push(name);
            }
            return names;
        }

        /**
         * Sets focus with message
         * @param name 
         */
        setFocus(name:string):void {
            for(let i = 0, size = this.observers.length; i < size; i++){
                let observer = this.observers[i];
                if(observer instanceof MapComponent){
                    let mapUiComponent = <MapComponent>this.observers[i];
                    if(observer.getName() === name){
                        mapUiComponent.setFocus();
                        break;
                    }
                }
            }
        }

        /**
         * Sets listener before change
         * @param listener 
         */
        onBeforeChange(listener:Function):void {
            this.eventListener.onBeforeChange = listener;
        }

        /**
         * Sets listener after change
         * @param listener 
         */
        onAfterChange(listener:Function):void {
            this.eventListener.onAfterChange = listener;
        }

    }

    /**
     * duice.ListEvent
     */
    class ListEventListener {
        onBeforeSelectRow:Function;
        onAfterSelectRow:Function;
        onBeforeMoveRow:Function;
        onAfterMoveRow:Function;
        onBeforeChangeRow:Function;
        onAfterChangeRow:Function;
    }
    
    /**
     * duice.List
     */
    export class List extends DataObject {

        data:Array<duice.Map> = new Array<duice.Map>();
        originData:string = JSON.stringify(this.data);
        index:number = -1;
        eventListener:ListEventListener = new ListEventListener();

        /**
         * constructor
         * @param jsonArray
         */
        constructor(jsonArray?:Array<any>) {
            super();
            this.fromJson(jsonArray || []);
        }

        /**
         * Updates
         * @param observable
         * @param obj 
         */
        update(listComponent:ListComponent, obj:object):void {
            console.debug('List.update', listComponent, obj);
            this.setChanged();
            this.notifyObservers(obj);
        }

        /**
         * Loads data from JSON array
         * @param jsonArray 
         */
        fromJson(jsonArray:Array<any>):void {
            this.clear();
            for(let i = 0; i < jsonArray.length; i ++ ) {
                let map = new duice.Map(jsonArray[i]);
                map.disable = this.clone(this.disable);
                map.disableAll = this.disableAll;
                map.readonly = this.clone(this.readonly);
                map.readonlyAll = this.readonlyAll;
                map.onBeforeChange(this.eventListener.onBeforeChangeRow);
                map.onAfterChange(this.eventListener.onAfterChangeRow);
                map.addObserver(this);
                this.data.push(map);
            }
            this.save();
            this.setIndex(-1);
        }

        /**
         * toJson
         */
        toJson():Array<object> {
            let jsonArray = new Array();
            for(let i = 0; i < this.data.length; i ++){
                jsonArray.push(this.data[i].toJson());
            }
            return jsonArray;
        }

        /**
         * Clears data
         */
        clear():void {
            for(let i = 0, size = this.data.length; i < size; i ++ ){
                this.data[i].removeObserver(this);
            }
            this.data = new Array<duice.Map>();
            this.setIndex(-1);
        }

        /**
         * Save point
         */
        save():void {
            this.originData = JSON.stringify(this.toJson());
        }

        /**
         * Resets data from original data.
         */
        reset():void {
            this.fromJson(JSON.parse(this.originData));
        }

        /**
         * Returns if changed
         */
        isDirty():boolean {
            if(JSON.stringify(this.toJson()) === this.originData){
                return false;
            }else{
                return true;
            }
        }

        /**
         * Sets only row index
         * @param index 
         */
        setIndex(index:number):void {
            this.index = index;
            this.setChanged();
            this.notifyObservers(this);
        }

        /**
         * Returns row index.
         */
        getIndex():number {
            return this.index;
        }

        /**
         * Returns row count
         */
        getRowCount():number {
            return this.data.length;
        }

        /**
         * Return row specified index
         * @param index 
         */
        getRow(index:number):Map {
            return this.data[index];
        }


        /**
         * Sets index.
         * @param index 
         */
        async selectRow(index:number) {

            let selectedRow = this.getRow(index);

            // calls beforeChangeIndex 
            if(this.eventListener.onBeforeSelectRow){
                if(await this.eventListener.onBeforeSelectRow.call(this, selectedRow) === false){
                    throw 'canceled';
                }
            }

            // changes index
            this.setIndex(index);

            // calls 
            if(this.eventListener.onAfterSelectRow){
                this.eventListener.onAfterSelectRow.call(this, selectedRow);
            }

            // returns true
            return true;
        }

        /**
         * moveRow
         * @param fromIndex 
         * @param toIndex 
         */
        async moveRow(fromIndex:number, toIndex:number) {

            let sourceMap = this.getRow(fromIndex);
            let targetMap = this.getRow(toIndex);
            
            // calls beforeChangeIndex 
            if(this.eventListener.onBeforeMoveRow){
                if(await this.eventListener.onBeforeMoveRow.call(this, sourceMap, targetMap) === false){
                    throw 'canceled';
                }
            }

            // moves row
            this.index = fromIndex;
            this.data.splice(toIndex, 0, this.data.splice(fromIndex, 1)[0]);
            this.setIndex(toIndex);

            // calls 
            if(this.eventListener.onAfterMoveRow){
                await this.eventListener.onAfterMoveRow.call(this, sourceMap, targetMap);
            }
        }

        /**
         * Adds row
         * @param map 
         */
        addRow(map:Map):void {
            map.disableAll = this.disableAll;
            map.disable = this.clone(this.disable);
            map.readonlyAll = this.readonlyAll;
            map.readonly = this.clone(this.readonly);
            map.onBeforeChange(this.eventListener.onBeforeChangeRow);
            map.onAfterChange(this.eventListener.onAfterChangeRow);
            map.addObserver(this);
            this.data.push(map);
            this.setIndex(this.getRowCount() - 1);
        }

        /**
         * Inserts row
         * @param index 
         * @param map 
         */
        insertRow(index:number, map:Map):void {
            if(0 <= index && index < this.data.length) {
                map.disableAll = this.disableAll;
                map.disable = this.clone(this.disable);
                map.readonlyAll = this.readonlyAll;
                map.readonly = this.clone(this.readonly);
                map.onBeforeChange(this.eventListener.onBeforeChangeRow);
                map.onAfterChange(this.eventListener.onAfterChangeRow);
                map.addObserver(this);
                this.data.splice(index, 0, map);
                this.setIndex(index);
            }
        }

        /**
         * Removes row by specified index
         * @param index 
         */
        removeRow(index:number):void {
            if(0 <= index && index < this.data.length) {
                this.data.splice(index, 1);
                this.setIndex(Math.min(this.index, this.data.length -1));
            }
        }

        /**
         * indexOf
         * @param handler 
         */
        indexOf(handler:Function){
            for(let i = 0, size = this.data.length; i < size; i ++){
                if(handler.call(this, this.data[i]) === true){
                    return i;
                }
            }
            return -1;
        }
        
        /**
         * contains
         * @param handler 
         */
        contains(handler:Function){
            if(this.indexOf(handler) > -1){
                return true;
            }else{
                return false;
            }
        }
        
        /**
         * forEach
         * @param handler 
         */
        forEach(handler:Function){
            for(let i = 0, size = this.data.length; i < size; i ++){
                if(handler.call(this, this.data[i], i) === false){
                    break;
                }
            }
        }

        /**
         * Sets diabled
         * @param disable 
         */
        setDisable(name:string, disable:boolean):void{
            this.data.forEach(function(map){
                map.setDisable(name, disable);
            });
            super.setDisable(name, disable);
        }

        /**
         * Sets disable all
         * @param disable 
         */
        setDisableAll(disable:boolean):void {
            this.data.forEach(function(map){
                map.setDisableAll(disable);
            });
            super.setDisableAll(disable);
        }

        /**
         * Sets readonly flag
         * @param name 
         * @param readonly 
         */
        setReadonly(name:string,readonly:boolean):void {
            this.data.forEach(function(map){
                map.setReadonly(name,readonly);
            });
            super.setReadonly(name,readonly);
        }

        /**
         * Sets readonly all
         * @param readonly 
         */
        setReadonlyAll(readonly:boolean):void {
            this.data.forEach(function(map){
                map.setReadonlyAll(readonly);
            });
            super.setReadonlyAll(readonly);
        }

        /**
         * onBeforeSelectRow
         * @param listener
         */
        onBeforeSelectRow(listener:Function):void {
            this.eventListener.onBeforeSelectRow = listener;
        }

        /**
         * onAfterSelectRow
         * @param listener onAfterSelectRow event listener
         */
        onAfterSelectRow(listener:Function):void {
            this.eventListener.onAfterSelectRow = listener;
        }

        /**
         * onBeforeMoveRow
         * @param listener onBeforeMoveRow event listener
         */
        onBeforeMoveRow(listener:Function):void {
            this.eventListener.onBeforeMoveRow = listener;
        }

        /**
         * onAfterMoveRow
         * @param listener 
         */
        onAfterMoveRow(listener:Function):void {
            this.eventListener.onAfterMoveRow = listener;
        }

        /**
         * onBeforeChangeRow
         * @param listener 
         */
        onBeforeChangeRow(listener:Function):void {
            this.eventListener.onBeforeChangeRow = listener;
            this.data.forEach(function(map){
                map.onBeforeChange(listener);
            })
        }

        /**
         * onAfterChangeRow
         * @param listener 
         */
        onAfterChangeRow(listener:Function):void {
            this.eventListener.onAfterChangeRow = listener;
            this.data.forEach(function(map){
                map.onAfterChange(listener);
            })
        }
    }

    /**
     * duice.ComponentFactory
     */
    abstract class ComponentFactory {
        context:any;
        setContext(context:any){
            this.context = context;
        }
        getContext():any {
            return this.context;
        }
        getContextProperty(name:string) {
            if(this.context[name]){
                return this.context[name];
            }
            if((<any>window).hasOwnProperty(name)){
                return (<any>window)[name];
            }
            try {
                return eval.call(this.context, name);
            }catch(e){
                return null;
            }
        }
        abstract getSelector():string;
        abstract getComponent(element:HTMLElement):Component;
    }

    /**
     * duice.Component
     */
    abstract class Component extends Observable implements Observer {

        element:HTMLElement;
        
        /**
         * constructor
         * @param element
         */
        constructor(element:HTMLElement){
            super();
            this.element = element;
            this.element.dataset[`${getAlias()}Id`] = this.generateUuid();
        }

        /**
         * Generates random UUID value
         * @return  UUID string
         */
        generateUuid():string {
            let dt = new Date().getTime();
            let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                let r = (dt + Math.random()*16)%16 | 0;
                dt = Math.floor(dt/16);
                return (c=='x' ? r :(r&0x3|0x8)).toString(16);
            });
            return uuid;
        }

        /**
         * Adds class
         */
        addClass(element:HTMLElement, className:string):void {
            element.classList.add(className);
        }
        abstract bind(...args: any[]):void;
        abstract update(dataObject:duice.DataObject, obj:object):void;
        isAvailable():boolean {
            
            // contains method not support(IE)
            if(!Node.prototype.contains) {
                Node.prototype.contains = function(el){
                    while (el = el.parentNode) {
                        if (el === this) return true;
                    }
                    return false;
                }
            }
            
            // checks contains element
            if(document.contains(this.element)){
                return true;
            }else{
                return false;
            }
        }

        /**
         * Executes custom expression in HTML element and returns.
         * @param element
         * @param $context
         * @return converted HTML element
         */
        executeExpression(element:HTMLElement, $context:any):any {
            let string = element.outerHTML;
            let regExp = new RegExp(`\\[@${getAlias()}\\[([\\s\\S]*?)\\]\\]`,'mgi');
            string = string.replace(regExp,function(match, command){
                try {
                    command = command.replace('&amp;', '&');
                    command = command.replace('&lt;', '<');
                    command = command.replace('&gt;', '>');
                    let result = eval(command);
                    return result;
                }catch(e){
                    console.error(e,command);
                    throw e;
                }
            });

            try {
                let template = document.createElement('template');
                template.innerHTML = string;
                return template.content.firstChild;
            }catch(e){
                this.removeChildNodes(element);
                element.innerHTML = string;
                return element;
            }
        }

        /**
         * Removes child elements from HTML element.
         * @param element
         */
        removeChildNodes(element:HTMLElement):void {
            // Remove element nodes and prevent memory leaks
            let node, nodes = element.childNodes, i = 0;
            while (node = nodes[i++]) {
                if (node.nodeType === 1 ) {
                    element.removeChild(node);
                }
            }

            // Remove any remaining nodes
            while (element.firstChild) {
                element.removeChild(element.firstChild);
            }

            // If this is a select, ensure that it displays empty
            if(element instanceof HTMLSelectElement){
                (<HTMLSelectElement>element).options.length = 0;
            }
        }

        /**
         * Sets element visible
         * @param visible 
         */
        setVisible(visible:boolean){
            this.element.style.display = (visible ? '' : 'none');
        }

        /**
         * Sets element focus
         */
        setFocus(){
            if(this.element.focus){
                this.element.focus();
            }
        }

        /**
         * Sets element position to be centered
         * @param element
         */
        setPositionCentered(element:HTMLElement):void {
            let win = getCurrentWindow();
            let computedStyle = win.getComputedStyle(element);
            let computedWidth = parseInt(computedStyle.getPropertyValue('width').replace(/px/gi, ''));
            let computedHeight = parseInt(computedStyle.getPropertyValue('height').replace(/px/gi, ''));
            let computedLeft = Math.max(0,win.innerWidth/2 - computedWidth/2) + win.scrollX;
            let computedTop = Math.max(0,win.innerHeight/2 - computedHeight/2) + win.scrollY;
            computedTop = computedTop - 100;
            computedTop = Math.max(10,computedTop);
            element.style.left = computedLeft + 'px';
            element.style.top = computedTop + 'px';
        }


        /**
         * Returns position info of specified element
         * @param element
         */
        getElementPosition(element:any) {
            let pos:any = ('absolute relative').indexOf(getComputedStyle(element).position) == -1;
            let rect1:any = {top: element.offsetTop * pos, left: element.offsetLeft * pos};
            let rect2:any = element.offsetParent ? this.getElementPosition(element.offsetParent) : {top:0,left:0};
            return {
                top: rect1.top + rect2.top,
                left: rect1.left + rect2.left,
                width: element.offsetWidth,
                height: element.offsetHeight
            };
        }
    }

    /**
     * duice.MapComponentFactory
     */
    export abstract class MapComponentFactory extends ComponentFactory { }

    /**
     * duice.MapComponent
     */
    export abstract class MapComponent extends Component {
        map:duice.Map;
        name:string;
        bind(map:Map, name:string, ...args:any[]):void {
            assert(map instanceof Map, 'duice bind error: ' + this.element.outerHTML);
            this.map = map;
            this.name = name;
            this.map.addObserver(this);
            this.addObserver(this.map);
            this.update(this.map, this.map);
        }
        getMap():duice.Map {
            return this.map;
        }
        getName():string {
            return this.name;
        }
        abstract update(map:duice.Map, obj:object):void;
        abstract getValue():any;
    }

    /**
     * duice.ListComponentFactory
     */
    export abstract class ListComponentFactory extends ComponentFactory { }

    /**
     * duice.ListComponent
     */
    export abstract class ListComponent extends Component {
        list:List;
        item:string;
        bind(list:List, item:string):void {
            assert(list instanceof List, 'duice bind error: ' + this.element.outerHTML);
            this.list = list;
            this.item = item;
            this.list.addObserver(this);
            this.addObserver(this.list);
            this.update(this.list, this.list);
        }
        getList():duice.List {
            return this.list;
        }
        getItem():string {
            return this.item;
        }
        abstract update(list:duice.List, obj:object):void;
    }
   
    /**
     * duice.ScriptletFactory
     */
    export class ScriptletFactory extends MapComponentFactory {
        getSelector(): string {
            return `*[is="${getAlias()}-scriptlet"]`;
        }
        getComponent(element:HTMLElement):Scriptlet {
            let scriptlet = new Scriptlet(element);
            let context:any;
            if(this.getContext() !== window) {
                context = this.getContext();
            }else{
                context = {};
            }
            if(element.dataset[`${getAlias()}Bind`]) {
                let bind = element.dataset[`${getAlias()}Bind`].split(',');
                let _this = this;
                bind.forEach(function(name){
                    context[name] = _this.getContextProperty(name); 
                });
            }
            scriptlet.bind(context);
            return scriptlet;
        }
    }
    
    /**
     * duice.Scriptlet
     */
    export class Scriptlet extends MapComponent {
        script:string;
        context:any;
        constructor(element:HTMLElement){
            super(element);
            this.script = element.dataset[`${getAlias()}Script`];
        }
        bind(context:any):void {
            this.context = context;
            for(let name in this.context){
                let obj = this.context[name];
                if(typeof obj === 'object' && obj instanceof duice.DataObject){
                    obj.addObserver(this);
                    this.addObserver(obj);
                    this.update(obj, obj);
                }
            }
        }
        update(dataObject:duice.DataObject, obj:object) {
            if(this.script){
                try {
                    let func = Function('$context', '"use strict";' + this.script + '');
                    let result = func.call(this.element, this.context);
                    return result;
                }catch(e){
                    console.error(this.script);
                    throw e;
                }
            }
        }
        getValue():string {
            return null;
        }
    }

    /**
     * duice.SpanFactory
     */
    export class SpanFactory extends MapComponentFactory {
        getSelector(): string {
            return `span[is="${getAlias()}-span"]`;
        }
        getComponent(element:HTMLSpanElement):Span {
            let span = new Span(element);
            
            // sets format
            if(element.dataset[`${getAlias()}Mask`]){
                let maskArray:Array<string> = element.dataset[`${getAlias()}Mask`].split(',');
                let maskType = maskArray[0];
                let mask;
                switch(maskType){
                case 'string':
                    mask = new StringMask(maskArray[1]);
                    break;
                case 'number':
                    mask = new NumberMask(parseInt(maskArray[1]));
                    break;
                case 'date':
                    mask = new DateMask(maskArray[1]);
                    break;
                default:
                    throw 'format type[' + maskType + '] is invalid';
                }
                span.setMask(mask);
            }
            
            // binds
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            span.bind(this.getContextProperty(bind[0]), bind[1]);
            return span;
        }
    }
    
    /**
     * duice.Span
     */
    export class Span extends MapComponent {
        span:HTMLSpanElement;
        mask:Mask;
        constructor(span:HTMLSpanElement){
            super(span);
            this.span = span;
            this.addClass(this.span, 'duice-span');
        }
        setMask(mask:Mask){
            this.mask = mask;
        }
        update(map:Map, obj:object):void {
            this.removeChildNodes(this.span);
            let value = map.get(this.name);
            value = defaultIfEmpty(value,'');
            if(this.mask){
                value = this.mask.encode(value);
            }
            this.span.appendChild(document.createTextNode(value));
        }
        getValue():string {
            let value = this.span.innerHTML;
            value = defaultIfEmpty(value, null);
            if(this.mask){
                value = this.mask.decode(value);
            }
            return value;
        }
    }

    /**
     * duice.DivFactory
     */
    export class DivFactory extends MapComponentFactory {
        getSelector(): string {
            return `div[is="${getAlias()}-div"]`;
        }
        getComponent(element:HTMLDivElement):Div {
            let div = new Div(element);

            // binds
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            div.bind(this.getContextProperty(bind[0]), bind[1]);
            return div;
        }
    }

    /**
     * duice.Div
     */
    export class Div extends MapComponent {
        div:HTMLDivElement;
        constructor(div:HTMLDivElement){
            super(div);
            this.div = div;
            this.addClass(this.div, 'duice-div');
        }
        update(map:Map, obj:object):void {
            this.removeChildNodes(this.div);
            let value = map.get(this.name);
            value = defaultIfEmpty(value,'');
            this.div.innerHTML = value;
        }
        getValue():string {
            let value = this.div.innerHTML;
            return value;
        }
    }

    /**
     * duice.InputFactory
     */
    export class InputFactory extends MapComponentFactory {
        getSelector(): string {
            return `input[is="${getAlias()}-input"]`;         
        }
        getComponent(element:HTMLInputElement):Input {
            let input = null;
            let type = element.getAttribute('type');
            let mask = element.dataset[`${getAlias()}Mask`];
            switch(type){
                case 'text':
                    input = new InputText(element);
                    if(mask){
                        input.setMask(new StringMask(mask));
                    }
                    break;
                case 'number':
                    input = new InputNumber(element);
                    if(mask){
                        input.setMask(new NumberMask(parseInt(mask)));
                    }
                    break;
                case 'checkbox':
                    input = new InputCheckbox(element);
                    break;
                case 'radio':
                    input = new InputRadio(element);
                    break;
                case 'date':
                case 'datetime-local':
                    input = new InputDate(element);
                    if(mask){
                        input.setMask(new DateMask(mask));
                    }
                    break;
               default:
                    input = new InputGeneric(element);
            }
            
            // bind
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            input.bind(this.getContextProperty(bind[0]), bind[1]);
            return input;
        }
    }
    
    /**
     * duice.Input
     */
    export abstract class Input extends MapComponent {
        input:HTMLInputElement;
        constructor(input:HTMLInputElement){
            super(input);
            this.input = input;
            let _this = this;
            this.input.addEventListener('keypress', function(event:any){
                let inputChars = String.fromCharCode(event.keyCode);
                let newValue = this.value.substr(0,this.selectionStart) + inputChars + this.value.substr(this.selectionEnd);
                if(_this.checkValue(newValue) === false){
                    event.preventDefault();
                }
            }, true);
            this.input.addEventListener('paste', function(event:any){
                let inputChars = event.clipboardData.getData('text/plain');
                let newValue = this.value.substr(0,this.selectionStart) + inputChars + this.value.substr(this.selectionEnd);
                if(_this.checkValue(newValue) === false){
                    event.preventDefault();
                }
            }, true);
            this.input.addEventListener('change', function(event){
                _this.setChanged();
                _this.notifyObservers(this);
            },true);

            // turn off autocomplete
            _this.input.setAttribute('autocomplete','off');
        }
        abstract update(map:duice.Map, obj:object):void;
        abstract getValue():any;
        checkValue(value:string):boolean {
            return true;
        }
        setDisable(disable:boolean):void {
            if(disable){
                this.input.setAttribute('disabled','true');                    
            }else{
                this.input.removeAttribute('disabled');
            }
        }
        setReadonly(readonly:boolean):void {
            if(readonly === true){
                this.input.setAttribute('readonly', 'readonly');
            }else{
                this.input.removeAttribute('readonly');
            }
        }
    }
    
    /**
     * duice.InputGeneric
     */
    export class InputGeneric extends Input {
        constructor(input:HTMLInputElement){
            super(input);
            this.addClass(this.input, 'duice-input-generic');
        }
        update(map:duice.Map, obj:object):void {
            let value = map.get(this.getName());
            this.input.value = defaultIfEmpty(value, '');
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue():any {
            let value:any = this.input.value;
            if(isEmpty(value)){
                return null;
            }else{
                if(isNaN(value)){
                    return String(value);
                }else{
                    return Number(value);
                }
            }
        }
    }
    
    /**
     * duice.InputText
     */
    export class InputText extends Input {
        mask:StringMask;
        constructor(input:HTMLInputElement){
            super(input);
            this.addClass(this.input,'duice-input-text');
        }
        setMask(mask:StringMask){
            this.mask = mask;
        }
        update(map:duice.Map, obj:object):void {
            let value = map.get(this.getName());
            value = defaultIfEmpty(value, '');
            if(this.mask){
                value = this.mask.encode(value);
            }
            this.input.value = value;
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue():string {
            let value = this.input.value;
            value = defaultIfEmpty(value, null);
            if(this.mask){
                value = this.mask.decode(value);
            }
            return value;
        }
        checkValue(value:string):boolean {

            // test pattern
            let pattern = this.input.getAttribute('pattern');
            if(pattern){
                let regExp = new RegExp(pattern);
                if(!regExp.test(value)){
                    return false;
                }
            }

            // checks format
            if(this.mask){
                try {
                    this.mask.decode(value);
                }catch(e){
                    return false;
                }
            }
            return true;
        }
    }
    
    /**
     * duice.InputNumber
     */
    export class InputNumber extends Input {
        mask:NumberMask;
        constructor(input:HTMLInputElement){
            super(input);
            this.addClass(this.input, 'duice-input-number');
            this.input.removeAttribute('type');

            // default mask
            this.mask = new NumberMask(0);
        }
        setMask(mask:NumberMask){
            this.mask = mask;
        }
        update(map:duice.Map, obj:object):void {
            let value = map.get(this.getName());
            if(this.mask){
                value = this.mask.encode(value);
            }
            this.input.value = value;
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue():number {
            let value:any = this.input.value;
            value = this.mask.decode(value);
            return value;
        }
        checkValue(value:string):boolean {
            try {
                this.mask.decode(value);
            }catch(e){
                return false;
            }
            return true;
        }
    }
    
    /**
     * duice.InputCheckbox
     */
    export class InputCheckbox extends Input {
        constructor(input:HTMLInputElement){
            super(input);
            this.addClass(this.input, 'duice-input-checkbox');

            // stop click event propagation
            this.input.addEventListener('click', function(event){
                event.stopPropagation();
            },true);
        }
        update(map:duice.Map, obj:object):void {
            let value = map.get(this.getName());
            if(value === true){
                this.input.checked = true;
            }else{
                this.input.checked = false;
            }
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue():boolean {
            return this.input.checked;
        }
        setReadonly(readonly:boolean) {
            if(readonly){
                this.input.style.pointerEvents = 'none';
            }else{
                this.input.style.pointerEvents = '';
            }
        }
    }
    
    /**
     * duice.InputRadio
     */
    export class InputRadio extends Input {
        constructor(input:HTMLInputElement){
            super(input);
            this.addClass(this.input, 'duice-input-radio');
        }
        update(map:duice.Map, obj:object):void {
            let value = map.get(this.getName());
            if(value === this.input.value){
                this.input.checked = true;
            }else{
                this.input.checked = false;
            }
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue():string {
            return this.input.value;
        }
        setReadonly(readonly:boolean) {
            if(readonly){
                this.input.style.pointerEvents = 'none';
            }else{
                this.input.style.pointerEvents = '';
            }
        }
    }
    
    /**
     * duice.InputDate
     */
    export class InputDate extends Input {
        readonly:boolean = false;
        pickerDiv:HTMLDivElement;
        type:string;
        mask:DateMask;
        clickListener:any;
        constructor(input:HTMLInputElement){
            super(input);
            this.addClass(this.input, 'duice-input-date');
            this.type = this.input.getAttribute('type').toLowerCase();
            this.input.removeAttribute('type');
            
            // adds click event listener
            let _this = this;
            this.input.addEventListener('click', function(event){
                if(_this.readonly !== true){
                    _this.openPicker();
                }
            },true);

            // default mask
            if(this.type === 'date'){
                this.mask = new DateMask('yyyy-MM-dd');
            }else{
                this.mask = new DateMask('yyyy-MM-dd HH:mm:ss');
            }
        }
        setMask(mask:DateMask){
            this.mask = mask;
        }
        update(map:duice.Map, obj:object):void {
            let value:string = map.get(this.getName());
            value = defaultIfEmpty(value,'');
            if(this.mask){
                value = this.mask.encode(value);
            }
            this.input.value = value;
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue():string {
            let value = this.input.value;
            value = defaultIfEmpty(value, null);
            if(this.mask){
                value = this.mask.decode(value);
            }
            if(this.type === 'date'){
                value = new DateMask('yyyy-MM-dd').encode(new Date(value).toISOString())
            }
            return value;
        }
        checkValue(value:string):boolean {
            try {
                let s = this.mask.decode(value);
            }catch(e){
                return false;
            }
            return true;
        }
        setReadonly(readonly:boolean):void {
            this.readonly = readonly;
            super.setReadonly(readonly);
        }
        openPicker():void {
            
            // checks pickerDiv is open.
            if(this.pickerDiv){
                return;
            }
            
            let _this = this;
            this.pickerDiv = document.createElement('div');
            this.pickerDiv.classList.add('duice-input-date__pickerDiv');

            // parses parts
            let date:Date;
            if(isEmpty(this.getValue)){
                date = new Date();
            }else{
                date = new Date(this.getValue());
            }
            let yyyy = date.getFullYear();
            let mm = date.getMonth();
            let dd = date.getDate();
            let hh = date.getHours();
            let mi = date.getMinutes();
            let ss = date.getSeconds();
            
            // click event listener
            this.clickListener = function(event:any){
                if(!_this.input.contains(event.target) && !_this.pickerDiv.contains(event.target)){
                    _this.closePicker();
                }
            }
            window.addEventListener('click', this.clickListener);
            
            // header
            let headerDiv = document.createElement('div');
            headerDiv.classList.add('duice-input-date__pickerDiv-headerDiv');
            this.pickerDiv.appendChild(headerDiv);
            
            // titleIcon
            let titleSpan = document.createElement('span');
            titleSpan.classList.add('duice-input-date__pickerDiv-headerDiv-titleSpan');
            headerDiv.appendChild(titleSpan);
            
            // closeButton
            let closeButton = document.createElement('button');
            closeButton.classList.add('duice-input-date__pickerDiv-headerDiv-closeButton');
            headerDiv.appendChild(closeButton);
            closeButton.addEventListener('click', function(event){
                _this.closePicker();
            });
            
            // bodyDiv
            let bodyDiv = document.createElement('div');
            bodyDiv.classList.add('duice-input-date__pickerDiv-bodyDiv');
            this.pickerDiv.appendChild(bodyDiv);
            
            // daySelector
            let dateDiv = document.createElement('div');
            dateDiv.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv');
            bodyDiv.appendChild(dateDiv);
            
            // previous month button
            let prevMonthButton = document.createElement('button');
            prevMonthButton.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv-prevMonthButton');
            dateDiv.appendChild(prevMonthButton);
            prevMonthButton.addEventListener('click', function(event){
                date.setMonth(date.getMonth() - 1);
                updateDate(date);
            });
            
            // todayButton
            let todayButton = document.createElement('button');
            todayButton.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv-todayButton');
            dateDiv.appendChild(todayButton);
            todayButton.addEventListener('click', function(event){
                let newDate = new Date();
                date.setFullYear(newDate.getFullYear());
                date.setMonth(newDate.getMonth());
                date.setDate(newDate.getDate());
                updateDate(date);
            });
            
            // year select
            let yearSelect = document.createElement('select');
            yearSelect.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv-yearSelect');
            dateDiv.appendChild(yearSelect);
            yearSelect.addEventListener('change', function(event){
                date.setFullYear(parseInt(this.value));
                updateDate(date);
            });
            
            // divider
            dateDiv.appendChild(document.createTextNode('-'));
            
            // month select
            let monthSelect = document.createElement('select');
            monthSelect.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv-monthSelect');
            dateDiv.appendChild(monthSelect);
            for(let i = 0, end = 11; i <= end; i ++ ) {
                let option = document.createElement('option');
                option.value = String(i);
                option.text = String(i + 1);
                monthSelect.appendChild(option);
            }
            monthSelect.addEventListener('change', function(event){
                date.setMonth(parseInt(this.value));
                updateDate(date);
            });
            
            // next month button
            let nextMonthButton = document.createElement('button');
            nextMonthButton.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv-nextMonthButton');
            dateDiv.appendChild(nextMonthButton);
            nextMonthButton.addEventListener('click', function(event){
                date.setMonth(date.getMonth() + 1);
                updateDate(date);
            });
            
            // calendar table
            let calendarTable = document.createElement('table');
            calendarTable.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable');
            bodyDiv.appendChild(calendarTable);
            let calendarThead = document.createElement('thead');
            calendarTable.appendChild(calendarThead);
            let weekTr = document.createElement('tr');
            weekTr.classList.add('.duice-input-date__pickerDiv-bodyDiv-calendarTable-weekTr');
            calendarThead.appendChild(weekTr);
            ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'].forEach(function(element){
                let weekTh = document.createElement('th');
                weekTh.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable-weekTh');
                weekTh.appendChild(document.createTextNode(element));
                weekTr.appendChild(weekTh);
            });
            let calendarTbody = document.createElement('tbody');
            calendarTable.appendChild(calendarTbody);
            
            // timeDiv
            let timeDiv = document.createElement('div');
            timeDiv.classList.add('duice-input-date__pickerDiv-bodyDiv-timeDiv');
            bodyDiv.appendChild(timeDiv);
            
            // check input type is date
            if(this.type === 'date'){
                date.setHours(0);
                date.setMinutes(0);
                date.setSeconds(0);
                timeDiv.style.display = 'none';
            }
            
            // now
            let nowButton = document.createElement('button');
            nowButton.classList.add('duice-input-date__pickerDiv-bodyDiv-timeDiv-nowButton');
            timeDiv.appendChild(nowButton);
            nowButton.addEventListener('click', function(event){
                let newDate = new Date();
                date.setHours(newDate.getHours());
                date.setMinutes(newDate.getMinutes());
                date.setSeconds(newDate.getSeconds());
                updateDate(date);
            });

            // hourSelect
            let hourSelect = document.createElement('select');
            hourSelect.classList.add('duice-input-date__pickerDiv-bodyDiv-timeDiv-hourSelect');
            for(let i = 0; i <= 23; i ++){
                let option = document.createElement('option');
                option.value = String(i);
                option.text = padLeft(String(i), 2, '0');
                hourSelect.appendChild(option);
            }
            timeDiv.appendChild(hourSelect);
            hourSelect.addEventListener('change', function(event){
                date.setHours(parseInt(this.value)); 
            });
            
            // divider
            timeDiv.appendChild(document.createTextNode(':'));
            
            // minuteSelect
            let minuteSelect = document.createElement('select');
            minuteSelect.classList.add('duice-input-date__pickerDiv-bodyDiv-timeDiv-minuteSelect');
            for(let i = 0; i <= 59; i ++){
                let option = document.createElement('option');
                option.value = String(i);
                option.text = padLeft(String(i), 2, '0');
                minuteSelect.appendChild(option);
            }
            timeDiv.appendChild(minuteSelect);
            minuteSelect.addEventListener('change', function(event){
                date.setMinutes(parseInt(this.value)); 
            });
            
            // divider
            timeDiv.appendChild(document.createTextNode(':'));
            
            // secondsSelect
            let secondSelect = document.createElement('select');
            secondSelect.classList.add('duice-input-date__pickerDiv-bodyDiv-timeDiv-secondSelect');
            for(let i = 0; i <= 59; i ++){
                let option = document.createElement('option');
                option.value = String(i);
                option.text = padLeft(String(i), 2, '0');
                secondSelect.appendChild(option);
            }
            timeDiv.appendChild(secondSelect);
            secondSelect.addEventListener('change', function(event){
                date.setSeconds(parseInt(this.value)); 
            });
            
            // footer
            let footerDiv = document.createElement('div');
            footerDiv.classList.add('duice-input-date__pickerDiv-footerDiv');
            this.pickerDiv.appendChild(footerDiv);
            
            // confirm
            let confirmButton = document.createElement('button');
            confirmButton.classList.add('duice-input-date__pickerDiv-footerDiv-confirmButton');
            footerDiv.appendChild(confirmButton);
            confirmButton.addEventListener('click', function(event){
                _this.input.value = _this.mask.encode(date.toISOString());
                _this.setChanged();
                _this.notifyObservers(this);
                _this.closePicker();
            });
            
            // show
            this.input.parentNode.insertBefore(this.pickerDiv, this.input.nextSibling);
            this.pickerDiv.style.position = 'absolute';
            this.pickerDiv.style.zIndex = String(getCurrentMaxZIndex() + 1);
            this.pickerDiv.style.left = this.getElementPosition(this.input).left + 'px';
            
            // updates date
            function updateDate(date:Date):void {
                let yyyy = date.getFullYear();
                let mm = date.getMonth();
                let dd = date.getDate();
                let hh = date.getHours();
                let mi = date.getMinutes();
                let ss = date.getSeconds();
                
                // updates yearSelect
                for(let i = yyyy - 5, end = yyyy + 5; i <= end; i ++ ) {
                    let option = document.createElement('option');
                    option.value = String(i);
                    option.text = String(i);
                    yearSelect.appendChild(option);
                }
                yearSelect.value = String(yyyy);
                
                // updates monthSelect
                monthSelect.value = String(mm);
                
                // updates dateTbody
                let startDay = new Date(yyyy,mm,1).getDay();
                let lastDates = [31,28,31,30,31,30,31,31,30,31,30,31];
                if (yyyy%4 && yyyy%100!=0 || yyyy%400===0) {
                    lastDates[1] = 29;
                }
                let lastDate = lastDates[mm];
                let rowNum = Math.ceil((startDay + lastDate - 1)/7);
                let dNum = 0;
                let currentDate = new Date();
                _this.removeChildNodes(calendarTbody);
                for (let i=1; i<=rowNum; i++) {
                    let dateTr = document.createElement('tr');
                    dateTr.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable-dateTr');
                    for (let k=1; k<=7; k++) {
                        let dateTd = document.createElement('td');
                        dateTd.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable-dateTd');
                        if((i === 1 && k < startDay) 
                        || (i === rowNum && dNum >= lastDate)
                        ){
                            dateTd.appendChild(document.createTextNode(''));
                        }else{
                            dNum++;
                            dateTd.appendChild(document.createTextNode(String(dNum)));
                            dateTd.dataset.date = String(dNum);
                            
                            // checks selected
                            if(currentDate.getFullYear() === yyyy
                            && currentDate.getMonth() === mm
                            && currentDate.getDate() === dNum){
                                dateTd.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable-dateTd--today');
                            }
                            if(dd === dNum){
                                dateTd.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable-dateTd--selected');
                            }
                            dateTd.addEventListener('click', function(event){
                                date.setDate(parseInt(this.dataset.date));
                                updateDate(date);
                                event.preventDefault();
                                event.stopPropagation();
                            });
                        }
                        dateTr.appendChild(dateTd);
                    }
                    calendarTbody.appendChild(dateTr);
                }
                
                // updates times
                hourSelect.value = String(hh);
                minuteSelect.value = String(mi);
                secondSelect.value = String(ss);
            }
            updateDate(date);
        }
        closePicker():void {
            this.pickerDiv.parentNode.removeChild(this.pickerDiv);
            this.pickerDiv = null;
            window.removeEventListener('click', this.clickListener);
        }
    }

    /**
     * duice.SelectFactory
     */
    export class SelectFactory extends MapComponentFactory {
        getSelector(): string {
            return `select[is="${getAlias()}-select"]`;
        }
        getComponent(element:HTMLSelectElement):Select {
            let select = new Select(element);
            let option = element.dataset[`${getAlias()}Option`];
            if(option){
                let options = option.split(',');
                let optionList = this.getContextProperty(options[0]);
                let optionValue = options[1];
                let optionText = options[2];
                select.setOption(optionList, optionValue, optionText);
            }
            let bind = element.dataset[`${getAlias()}Bind`];
            let binds = bind.split(',');
            select.bind(this.getContextProperty(binds[0]), binds[1]);
            return select;
        }
    }
    
    /**
     * duice.Select
     */
    export class Select extends MapComponent {
        select:HTMLSelectElement;
        optionList:duice.List;
        optionValue:string;
        optionText:string;
        defaultOptions:Array<HTMLOptionElement> = new Array<HTMLOptionElement>();
        constructor(select:HTMLSelectElement) {
            super(select);
            this.select = select;
            this.addClass(this.select, 'duice-select');
            let _this = this;
            this.select.addEventListener('change', function(event){
                _this.setChanged();
                _this.notifyObservers(this); 
            });
            
            // stores default options
            for(let i = 0, size = this.select.options.length; i < size; i ++){
                this.defaultOptions.push(this.select.options[i])
            }
        }
        setOption(list:duice.List, value:string, text:string):void {
            this.optionList = list;
            this.optionValue = value;
            this.optionText = text;
            this.updateOption();
            this.optionList.addObserver(this);
        }
        update(dataObject:duice.Map|duice.List, obj:object):void {

            // debug
            console.debug(this, dataObject, obj);

            // in case of Map (bind data is changed)
            if(dataObject instanceof duice.Map){
                let map = dataObject;
                let value = map.get(this.getName());
                this.select.value = defaultIfEmpty(value,'');
                if(this.select.selectedIndex < 0){
                    if(this.defaultOptions.length > 0){
                        this.defaultOptions[0].selected = true;
                    }
                }
                this.setDisable(map.isDisable(this.getName()));
                this.setReadonly(map.isReadonly(this.getName()));
            }

            // in case of List (select option is changed)
            if(dataObject instanceof duice.List){
                this.updateOption();
            }
        }
        updateOption():void {
            console.debug(this); 

            // removes all options
            this.removeChildNodes(this.select);
                
            // adds default options
            for(let i = 0, size = this.defaultOptions.length; i < size; i ++){
                this.select.appendChild(this.defaultOptions[i]); 
            }
                
            // update data options
            for(let i = 0, size = this.optionList.getRowCount(); i < size; i ++){
                let optionMap = this.optionList.getRow(i);
                let option = document.createElement('option');
                option.value = optionMap.get(this.optionValue);
                option.appendChild(document.createTextNode(optionMap.get(this.optionText)));
                this.select.appendChild(option);
            }

            // updates value
            this.update(this.map, null);
        }
        getValue():any {
            let value = this.select.value;
            return defaultIfEmpty(value, null);
        }
        setDisable(disable: boolean): void {
            if(disable){
                this.select.setAttribute('disabled', 'true');
            }else{
                this.select.removeAttribute('disabled');
            }
        }
        setReadonly(readonly:boolean):void {
            if(readonly === true){
                this.select.style.pointerEvents = 'none';
                this.select.classList.add('duice-select--readonly');
            }else{
                this.select.style.pointerEvents = '';
                this.select.classList.remove('duice-select--readonly');
            }
        }
    }
    
    /**
     * duice.TextareaFactory
     */
    export class TextareaFactory extends MapComponentFactory {
        getSelector(): string {
            return `textarea[is="${getAlias()}-textarea"]`;
        }
        getComponent(element:HTMLTextAreaElement):Textarea {
            let textarea = new Textarea(element);
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            textarea.bind(this.getContextProperty(bind[0]), bind[1]);
            return textarea;
        }
    }
    
    /**
     * duice.Textarea
     */
    export class Textarea extends MapComponent {
        textarea:HTMLTextAreaElement;
        constructor(textarea:HTMLTextAreaElement) {
            super(textarea);
            this.textarea = textarea;
            this.addClass(this.textarea, 'duice-textarea');
            let _this = this;
            this.textarea.addEventListener('change', function(event){
                _this.setChanged();
                _this.notifyObservers(this); 
            });
        }
        update(map:duice.Map, obj:object):void {
            let value = map.get(this.getName());
            this.textarea.value = defaultIfEmpty(value, '');
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue():any {
            return defaultIfEmpty(this.textarea.value, null);
        }
        setDisable(disable:boolean): void {
            if(disable){
                this.textarea.setAttribute('disabled', 'true');
            }else{
                this.textarea.removeAttribute('disabled');
            }
        }
        setReadonly(readonly:boolean):void {
            if(readonly){
                this.textarea.setAttribute('readonly', 'readonly');
            }else{
                this.textarea.removeAttribute('readonly');
            }
        }
    }
    
    /**
     * duice.ImageFactory
     */
    export class ImgFactory extends MapComponentFactory {
        getSelector(): string {
            return `img[is="${getAlias()}-img"]`;
        }
        getComponent(element:HTMLImageElement):Img {
            let img = new Img(element);
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            img.bind(this.getContextProperty(bind[0]), bind[1]);
            let size = element.dataset[`${getAlias()}Size`];
            if(size){
                let sizes = size.split(',');
                img.setSize(parseInt(sizes[0]),parseInt(sizes[1]));                
            }
            return img;
        }
    }
    
    /**
     * duice.Img
     */
    export class Img extends MapComponent {
        img:HTMLImageElement;
        size:{width:number, height:number};
        originSrc:string;
        value:string;
        disable:boolean;
        readonly:boolean;
        preview:HTMLImageElement;
        blocker:duice.Blocker;
        menuDiv:HTMLDivElement;

        /**
         * Constructor
         * @param img
         */
        constructor(img:HTMLImageElement) {
            super(img);
            this.img = img;
            this.addClass(this.img, 'duice-img');
            this.originSrc = this.img.src;
            let _this = this;

            // listener for contextmenu event
            this.img.addEventListener('click', function(event){
                if(_this.disable || _this.readonly){
                    return false;
                }
                let imgPosition = _this.getElementPosition(this);
                _this.openMenuDiv(imgPosition.top,imgPosition.left);
                event.stopPropagation();
            });
        }

        /**
         * Sets size
         * @param width 
         * @param height 
         */
        setSize(width:number, height:number):void {
            this.size = {width:width, height:height};
            this.img.style.width = width + 'px';
            this.img.style.height = height + 'px';
        }
        
        /**
         * Updates image instance
         * @param map
         * @param obj
         */
        update(map:duice.Map, obj:object):void {
            let value = map.get(this.getName());
            this.value = defaultIfEmpty(value,this.originSrc);
            this.img.src = this.value;
            this.disable = map.isDisable(this.getName());
            this.readonly = map.isReadonly(this.getName());
            if(this.disable){
                this.img.classList.add('duice-img--disable');
            }else{
                this.img.classList.remove('duice-img--disable');
            }
            if(this.readonly){
                this.img.classList.add('duice-img--readonly');
            }else{
                this.img.classList.remove('duice-img--readonly');
            }
        }
        
        /**
         * Return value of image element
         * @return base64 data or image URL
         */
        getValue():any {
            return this.value;
        }

        /**
         * Opens menu division.
         */
        openMenuDiv(top:number, left:number):void {

            // check menu div is already pop.
            if(this.menuDiv){
                return;
            }

            // defines variables
            let _this = this;

            // creates menu div
            this.menuDiv = document.createElement('div');
            this.menuDiv.classList.add('duice-img__menuDiv');

            // creates preview button
            if(!this.disable) {
                let previewButton = document.createElement('button');
                previewButton.classList.add('duice-img__menuDiv-previewButton');
                previewButton.addEventListener('click', function(event:any) {
                    _this.openPreview();
                }, true);
                this.menuDiv.appendChild(previewButton);
            }
            
            // readonly or disable 
            if(!this.disable && !this.readonly) {
                // creates change button
                let changeButton = document.createElement('button');
                changeButton.classList.add('duice-img__menuDiv-changeButton');
                changeButton.addEventListener('click', function(event:any) {
                    _this.changeImage();
                }, true);
                this.menuDiv.appendChild(changeButton);

                // creates view button
                let clearButton = document.createElement('button');
                clearButton.classList.add('duice-img__menuDiv-clearButton');
                clearButton.addEventListener('click', function(event:any) {
                    _this.clearImage();
                }, true);
                this.menuDiv.appendChild(clearButton);
            }
            
            // appends menu div
            this.img.parentNode.insertBefore(this.menuDiv, this.img.nextSibling);

            this.menuDiv.style.position = 'absolute';
            this.menuDiv.style.zIndex = String(getCurrentMaxZIndex() + 1);
            this.menuDiv.style.top = top + 'px';
            this.menuDiv.style.left = left + 'px';

            // listens mouse leaves from menu div.
            window.addEventListener('click', function(event:any){
                   _this.closeMenuDiv();
            }, { once: true });
        }

        /**
         * Closes menu division
         */
        closeMenuDiv():void {
            if(this.menuDiv) {
                this.menuDiv.parentNode.removeChild(this.menuDiv);         
                this.menuDiv = null;
            }
        }

        /**
         * Opens preview
         */
        openPreview():void {
            let _this = this;
            let parentNode = getCurrentWindow().document.body;

            // creates preview
            this.preview = document.createElement('img');
            this.preview.src = this.img.src;
            this.preview.addEventListener('click', function(event){
                _this.closePreview();
            });

            // creates blocker
            this.blocker = new duice.Blocker(parentNode);
            this.blocker.getBlockDiv().addEventListener('click',function(event){
                _this.closePreview();
            });
            this.blocker.block();
            
            // shows preview
            this.preview.style.position = 'absolute';
            this.preview.style.zIndex = String(getCurrentMaxZIndex() + 2);
            parentNode.appendChild(this.preview);
            this.setPositionCentered(this.preview);
        }

        /**
         * Closes preview
         */
        closePreview(){
            if(this.preview){
                this.blocker.unblock();
                this.preview.parentNode.removeChild(this.preview);
                this.preview = null;
            }
        }

        /**
         * Changes image
         */
        changeImage():void {
            // creates file input element
            let _this = this;
            let input = document.createElement('input');
            input.setAttribute("type", "file");
            input.setAttribute("accept", "image/gif, image/jpeg, image/png");
            input.addEventListener('change', function(e){
                let fileReader = new FileReader();
                if (this.files && this.files[0]) {
                    fileReader.addEventListener("load", async function(event:any) {
                        let value = event.target.result;
                        if(_this.size){
                            value = await _this.convertImage(value, _this.size.width, _this.size.height);
                        }else{
                            value = await _this.convertImage(value);
                        }
                        _this.value = value;
                        _this.img.src = value;
                        _this.setChanged();
                        _this.notifyObservers(_this);
                    }); 
                    fileReader.readAsDataURL(this.files[0]);
                }
                e.preventDefault();
                e.stopPropagation();
            });
            input.click();
        }

        /**
         * Converts image
         * @param dataUrl 
         * @param width 
         * @param height 
         */
        convertImage(dataUrl:any, width?:number, height?:number) {
            return new Promise(function(resolve, reject){
                try {
                    let canvas = document.createElement("canvas");
                    let ctx = canvas.getContext("2d");
                    let image = new Image();
                    image.onload = function(){
                        if(width && height){
                            canvas.width = width;
                            canvas.height = height;
                            ctx.drawImage(image, 0, 0, width, height);
                        }else{
                            canvas.width = image.naturalWidth;
                            canvas.height = image.naturalHeight;
                            ctx.drawImage(image, 0, 0);
                        }
                        let dataUrl = canvas.toDataURL("image/png");
                        resolve(dataUrl);
                    };
                    image.src = dataUrl;
                }catch(e){
                    reject(e);
                }
            });
        }

        /**
         * Clears image
         */
        clearImage():void {
            this.value = null;
            this.setChanged();
            this.notifyObservers(this);
        }
    }
   
    /**
     * duice.TableFactory
     */
    export class TableFactory extends ListComponentFactory {
        getSelector(): string {
            return `table[is="${getAlias()}-table"]`;
        }
        getComponent(element:HTMLTableElement):Table {
            let table = new Table(element);
            table.setSelectable(element.dataset[`${getAlias()}Selectable`] === 'true');
            table.setEditable(element.dataset[`${getAlias()}Editable`] === 'true');
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            table.bind(this.getContextProperty(bind[0]), bind[1]);
            return table;
        }
    }

    /**
     * duice.Table
     */
    export class Table extends ListComponent {
        table:HTMLTableElement;
        tbody:HTMLTableSectionElement;
        tbodies:Array<HTMLTableSectionElement> = new Array<HTMLTableSectionElement>();
        selectable:boolean;
        editable:boolean;
    
        /**
         * constructor table
         * @param table
         */
        constructor(table:HTMLTableElement) {
            super(table);
            this.table = table;
            this.addClass(this.table, 'duice-table');
            
            // initializes caption
            let caption = <HTMLTableCaptionElement>this.table.querySelector('caption');
            if(caption){
                caption = this.executeExpression(<HTMLElement>caption, new Object());
                duice.initializeComponent(caption, new Object());
            }
            
            // initializes head
            let thead = <HTMLTableSectionElement>this.table.querySelector('thead');
            if(thead){
                thead.classList.add('duice-table__thead');
                thead.querySelectorAll('tr').forEach(function(tr){
                    tr.classList.add('duice-table__thead-tr');
                });
                thead.querySelectorAll('th').forEach(function(th){
                    th.classList.add('duice-table__thead-tr-th');
                });
                thead = this.executeExpression(<HTMLElement>thead, new Object());
                duice.initializeComponent(thead, new Object());
            }
            
            // clones body
            let tbody = this.table.querySelector('tbody');
            this.tbody = <HTMLTableSectionElement>tbody.cloneNode(true);
            this.tbody.classList.add('duice-table__tbody');
            this.tbody.querySelectorAll('tr').forEach(function(tr){
                tr.classList.add('duice-table__tbody-tr');
            });
            this.tbody.querySelectorAll('td').forEach(function(th){
                th.classList.add('duice-table__tbody-tr-td');
            });
            this.table.removeChild(tbody);
            
            // initializes foot
            let tfoot = <HTMLTableSectionElement>this.table.querySelector('tfoot');
            if(tfoot){
                tfoot.classList.add('duice-table__tfoot');
                tfoot.querySelectorAll('tr').forEach(function(tr){
                    tr.classList.add('duice-table__tfoot-tr');
                });
                tfoot.querySelectorAll('td').forEach(function(td){
                    td.classList.add('duice-table__tfoot-tr-td');
                });
                tfoot = this.executeExpression(<HTMLElement>tfoot, new Object());
                duice.initializeComponent(tfoot, new Object());
            }
        }

        /**
         * Sets selectable flag
         * @param selectable 
         */
        setSelectable(selectable:boolean):void {
            this.selectable = selectable;
        }
        
        /**
         * Sets enable flag
         * @param editable
         */
        setEditable(editable:boolean):void {
            this.editable = editable;
        }
        
        /**
         * Updates table
         * @param list
         * @param obj
         */
        update(list:duice.List, obj:object):void {
            
            // checks changed source instance
            if(obj instanceof duice.Map){
                return;
            }
            
            let _this = this;
            
            // remove previous rows
            for(let i = 0; i < this.tbodies.length; i ++ ) {
                this.table.removeChild(this.tbodies[i]);
            }
            this.tbodies.length = 0;
            
            // creates new rows
            for(let index = 0; index < list.getRowCount(); index ++ ) {
                let map = list.getRow(index);
                let tbody = this.createTbody(index,map);
                tbody.dataset.duiceIndex = String(index);
                
                // select index
                if(this.selectable){
                    tbody.classList.add('duice-table__tbody--selectable');
                    if(index === list.getIndex()){
                        tbody.classList.add('duice-table__tbody--index');
                    }
                    tbody.addEventListener('click', async function(event){
                        let index = Number(this.dataset.duiceIndex);
                        await _this.selectTbody(index);
                    }, true);
                }
                
                // drag and drop event
                if(this.editable) {
                    tbody.setAttribute('draggable', 'true');
                    tbody.addEventListener('dragstart', function(event){
                        event.dataTransfer.setData("text", this.dataset.duiceIndex);
                    });
                    tbody.addEventListener('dragover', function(event){
                        event.preventDefault();
                        event.stopPropagation();
                    });
                    tbody.addEventListener('drop', async function(event){
                        event.preventDefault();
                        event.stopPropagation();
                        let fromIndex = parseInt(event.dataTransfer.getData('text'));
                        let toIndex = parseInt(this.dataset.duiceIndex);
                        await list.moveRow(fromIndex, toIndex);
                    });
                }
                
                // appends body
                this.table.appendChild(tbody);
                this.tbodies.push(tbody);
            }

            // not found row
            if(list.getRowCount() < 1) {
                let emptyTbody = this.createEmptyTbody();
                emptyTbody.style.pointerEvents = 'none';
                this.table.appendChild(emptyTbody);
                this.tbodies.push(emptyTbody);
            }
        }

        /**
         * Selects tbody element
         * @param tbody 
         */
        async selectTbody(index:number) {
            this.getList().suspendNotify();
            await this.getList().selectRow(index);
            for(let i = 0; i < this.tbodies.length; i ++ ) {
                if(i === index){
                    this.tbodies[i].classList.add('duice-table__tbody--index');
                }else{
                    this.tbodies[i].classList.remove('duice-table__tbody--index');
                }
            }
            this.getList().resumeNotify();
        }
        
        /**
         * Creates table body element
         * @param index
         * @param map
         */
        createTbody(index:number, map:duice.Map):HTMLTableSectionElement {
            let _this = this;
            let tbody:HTMLTableSectionElement = <HTMLTableSectionElement>this.tbody.cloneNode(true);
            tbody.classList.add('duice-table__tbody');
            let $context:any = new Object;
            $context['index'] = index;
            $context[this.item] = map;
            tbody = this.executeExpression(<HTMLElement>tbody,$context);
            duice.initializeComponent(tbody,$context);
            return tbody;
        }
        
        /**
         * Creates empty table body element
         */
        createEmptyTbody():HTMLTableSectionElement {
            let emptyTbody:HTMLTableSectionElement = <HTMLTableSectionElement>this.tbody.cloneNode(true);
            this.removeChildNodes(emptyTbody);
            emptyTbody.classList.add('duice-table__tbody--empty')
            let tr = document.createElement('tr');
            tr.classList.add('duice-table__tbody-tr');
            let td = document.createElement('td');
            td.classList.add('duice-table__tbody-tr-td');

            // calculates colspan
            let colspan = 0;
            let childNodes:HTMLCollection = this.tbody.querySelector('tr').children;
            for(let i = 0; i < childNodes.length; i ++ ){
                if(childNodes[i].tagName === 'TH' || childNodes[i].tagName === 'TD'){
                    colspan ++;
                }
            }
            td.setAttribute('colspan',String(colspan));

            // creates empty mesage
            let emptyMessage = document.createElement('div');
            emptyMessage.style.textAlign = 'center';
            emptyMessage.classList.add('duice-table__tbody--empty-message');
            td.appendChild(emptyMessage);
            tr.appendChild(td);
            emptyTbody.appendChild(tr);
            return emptyTbody;
        }
    }
    
    /**
     * duice.UListFactory
     */
    export class UlFactory extends ListComponentFactory {
        getSelector(): string {
            return `ul[is="${getAlias()}-ul"]`;
        }
        getComponent(element:HTMLUListElement):Ul {
            let ul = new Ul(element);
            
            // selectable
            let selectable = element.dataset[`${getAlias()}Selectable`];
            ul.setSelectable(selectable === 'true');

            // editable
            let editable = element.dataset[`${getAlias()}Editable`];
            ul.setEditable(editable === 'true');

            // hierarchy
            let hierarchy = element.dataset[`${getAlias()}Hierarchy`];
            if(hierarchy){
                let hierarchys = hierarchy.split(',');
                ul.setHierarchy(hierarchys[0], hierarchys[1]);
            }

            // foldable
            let foldable = element.dataset[`${getAlias()}Foldable`];
            ul.setFoldable(foldable === 'true');

            // bind
            let bind = element.dataset[`${getAlias()}Bind`];
            let binds = bind.split(',');
            ul.bind(this.getContextProperty(binds[0]), binds[1]);

            // return
            return ul;
        }
    }
    
    /**
     * duice.Ul
     */
    export class Ul extends ListComponent {
        ul:HTMLUListElement;
        li:HTMLLIElement;
        childUl:HTMLUListElement;
        lis:Array<HTMLLIElement> = new Array<HTMLLIElement>();
        selectable:boolean;
        editable:boolean;
        hierarchy:{ idName:string, parentIdName:string }
        foldable:boolean;
        foldName:any = {};
    
        /**
         * Constructor
         * @param ul
         */
        constructor(ul:HTMLUListElement) {
            super(ul);
            this.ul = ul;
            this.addClass(this.ul, 'duice-ul');
            let li = <HTMLLIElement>ul.querySelector('li');

            // checks child UList
            let childUl = <HTMLUListElement>li.querySelector('li > ul');
            if(childUl){
                this.childUl = li.removeChild(childUl);
            }else{
                this.childUl = document.createElement('ul');
            }

            // clone li
            this.li = <HTMLLIElement>li.cloneNode(true);
        }

        /**
         * Sets selectable flag
         * @param selectable 
         */
        setSelectable(selectable:boolean):void {
            this.selectable = selectable;
        }  

        /**
         * Sets editable flag.
         * @param editable
         */
        setEditable(editable:boolean):void {
            this.editable = editable;
        }
        
        /**
         * Sets hierarchy function options.
         * @param idName
         * @param parentIdName
         */
        setHierarchy(idName:string, parentIdName:string):void {
            this.hierarchy = { idName:idName, parentIdName:parentIdName };
        }
        
        /**
         * Sets foldable flag.
         * @param foldable
         */
        setFoldable(foldable:boolean):void {
            this.foldable = foldable;
        }
        
        /**
         * Updates instance
         * @param list
         * @param obj
         */
        update(list:duice.List, obj:object):void {

            // checks changed source instance
            if(obj instanceof duice.Map){
                return;
            }
            
            // initiates
            let _this = this;
            this.ul.innerHTML = '';
            this.lis.length = 0;

            // root style
            this.ul.style.listStyle = 'none';
            this.ul.style.paddingLeft = '0px';
            if(this.hierarchy){
                this.createHierarchyRoot();
            }

            // creates new rows
            for(let index = 0; index < list.getRowCount(); index ++ ) {
                let map = list.getRow(index);
                let path:Array<number> = [];
                
                // checks hierarchy
                if(this.hierarchy){
                    if(isNotEmpty(map.get(this.hierarchy.parentIdName))){
                        continue;
                    }
                }
                
                // creates LI element
                let li = this.createLi(index, map, Number(0));
                if(this.selectable){
                    li.classList.add('duice-ul__li--selectable');
                }
                this.ul.appendChild(li);
            }
            
            // creates orphans
            if(this.hierarchy){
                for(let index = 0, size = list.getRowCount(); index < size; index ++ ) {
                    if(this.isLiCreated(index) === false){
                        let orphanLi = this.createLi(index, list.getRow(index), Number(0));
                        orphanLi.classList.add('duice-ul__li--orphan');
                        this.ul.appendChild(orphanLi);
                    }
                }
            }
        }

        /**
         * Creates hierarchy root
         */
        createHierarchyRoot():void {

            // depth
            let depth:number = 0;
            if(this.editable) depth += 24;
            if(this.foldable) depth += 24;
            if(depth > 0){
                this.ul.style.paddingLeft = depth + 'px';
            }

            // add editable event
            if(this.editable){
                let _this = this;
                // if already constructed, skip.
                if(this.ul.classList.contains('duice-ul--root')){
                    return;
                }
                this.ul.classList.add('duice-ul--root');
                this.ul.addEventListener('dragover', function(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    _this.ul.classList.add('duice-ul--root-dragover');
                });
                this.ul.addEventListener('dragleave', function(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    _this.ul.classList.remove('duice-ul--root-dragover');
                });
                this.ul.addEventListener('drop', async function(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    let fromIndex = parseInt(event.dataTransfer.getData('text'));
                    await _this.moveLi(fromIndex, -1);
                });
            }
        }
        
        /**
         * Creates LI element reference to specified map includes child nodes.
         * @param index
         * @param map
         */
        createLi(index:number, map:duice.Map, depth:number):HTMLLIElement {
            let _this = this;
            let li:HTMLLIElement = <HTMLLIElement>this.li.cloneNode(true);
            li.classList.add('duice-ui-ul__li');
            let $context:any = new Object;
            $context['index'] = index;
            $context['depth'] = Number(depth);
            $context['hasChild'] = (this.hierarchy ? this.hasChild(map) : false);
            $context[this.item] = map;
            li = this.executeExpression(<HTMLElement>li,$context);
            duice.initializeComponent(li,$context);
            this.lis.push(li);
            li.dataset.duiceIndex = String(index);
            
            // sets index
            if(this.selectable){
                if(index === this.getList().getIndex()){
                    li.classList.add('duice-ul__li--index');
                }
                li.addEventListener('click', async function(event){
                    let index = Number(this.dataset.duiceIndex);
                    event.stopPropagation();
                    await _this.selectLi(index, this);
                });
            }

            // editable
            if(this.editable){
                li.setAttribute('draggable', 'true');
                li.addEventListener('dragstart', function(event){
                    event.stopPropagation();
                    event.dataTransfer.setData("text", this.dataset.duiceIndex);
                });
                li.addEventListener('dragover', function(event){
                    event.preventDefault();
                    event.stopPropagation();
                });
                li.addEventListener('drop', async function(event){
                    event.preventDefault();
                    event.stopPropagation();
                    let fromIndex = parseInt(event.dataTransfer.getData('text'));
                    let toIndex = parseInt(this.dataset.duiceIndex);
                    await _this.moveLi(fromIndex, toIndex);
                });
            }

            // creates child node
            if(this.hierarchy) {
                depth ++;
                let childUl = <HTMLUListElement>this.childUl.cloneNode(true);
                childUl.classList.add('duice-ul');
                $context['depth'] = Number(depth);
                childUl = this.executeExpression(childUl,$context);
                let hasChild:boolean = false;
                let hierarchyIdValue = map.get(this.hierarchy.idName);
                for(let i = 0, size = this.list.getRowCount(); i < size; i ++ ){
                    let element = this.list.getRow(i);
                    let hierarchyParentIdValue = element.get(this.hierarchy.parentIdName);
                    if(!isEmpty(hierarchyParentIdValue)
                    && hierarchyParentIdValue === hierarchyIdValue){
                        let childLi = this.createLi(i, element, Number(depth));
                        childUl.appendChild(childLi);
                        hasChild = true;
                    }
                }
                if(hasChild){
                    li.appendChild(childUl);
                }
                
                // sets fold 
                if(this.foldable === true) {
                    if(hasChild) {
                        if(this.isFoldLi(map)){
                            this.foldLi(map, li, true);
                        }else{
                            this.foldLi(map, li, false);
                        }
                        li.addEventListener('click', function(event){
                            event.preventDefault();
                            event.stopPropagation();
                            if(event.target === this){
                                if(_this.isFoldLi(map)){
                                    _this.foldLi(map, this, false);
                                }else{
                                    _this.foldLi(map, this, true);
                                }
                            }
                        });
                    }else{
                        this.foldLi(map, li, false);
                    }
                }
            }

            // return node element
            return li;
        }

        /**
         * selectLi
         * @param index 
         * @param li 
         */
        async selectLi(index:number, li:HTMLLIElement){
            this.getList().suspendNotify();
            await this.getList().selectRow(index);
            for(let i = 0; i < this.lis.length; i ++ ) {
                this.lis[i].classList.remove('duice-ul__li--index');
            }
            li.classList.add('duice-ul__li--index');
            this.getList().resumeNotify();
        }

        /**
         * hasChild
         * @param map 
         */
        hasChild(map:duice.Map):boolean {
            let hierarchyIdValue = map.get(this.hierarchy.idName);
            for(let i = 0, size = this.list.getRowCount(); i < size; i ++ ){
                let element = this.list.getRow(i);
                let hierarchyParentIdValue = element.get(this.hierarchy.parentIdName);
                if(!isEmpty(hierarchyParentIdValue) 
                && hierarchyParentIdValue === hierarchyIdValue){
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Returns specified index is already creates LI element.
         * @param index
         */
        isLiCreated(index:number):boolean {
            for(let i = 0, size = this.lis.length; i < size; i ++ ){
                if(parseInt(this.lis[i].dataset.duiceIndex) === index){
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Return specified map is fold.
         * @param map
         */
        isFoldLi(map:duice.Map){
            if(this.foldName[map.get(this.hierarchy.idName)] === true){
                return true;
            }else{
                return false;
            }
        }
        
        /**
         * folds child nodes
         * @param map
         * @param li
         * @param fold
         */
        foldLi(map:duice.Map, li:HTMLLIElement, fold:boolean){
            if(fold){
                this.foldName[map.get(this.hierarchy.idName)] = true;
                li.classList.remove('duice-ul__li--unfold');
                li.classList.add('duice-ul__li--fold');
            }else{
                this.foldName[map.get(this.hierarchy.idName)] = false;
                li.classList.remove('duice-ul__li--fold');
                li.classList.add('duice-ul__li--unfold');
            }
        }
        
        /**
         * Modes map element from index to index.
         * @param fromIndex
         * @param toIndex
         */
        async moveLi(fromIndex:number, toIndex:number) {
            
            // checks same index
            if(fromIndex === toIndex){
                return;
            }
            
            //defines map
            let sourceRow = this.list.getRow(fromIndex);
            let targetRow = this.list.getRow(toIndex) || null;
            
            // moving action
            if(this.hierarchy){
                
                // checks circular reference
                if(this.isCircularReference(targetRow, sourceRow.get(this.hierarchy.idName))){
                    throw 'Not allow to movem, becuase of Circular Reference.';
                }

                // calls beforeChangeIndex 
                if(this.list.eventListener.onBeforeMoveRow){
                    if(await this.list.eventListener.onBeforeMoveRow.call(this.list, sourceRow, targetRow) === false){
                        throw 'canceled';
                    }
                }
                
                // change parents
                await sourceRow.set(this.hierarchy.parentIdName, targetRow === null ? null : targetRow.get(this.hierarchy.idName));
                
                // calls 
                if(this.list.eventListener.onAfterMoveRow){
                    await this.list.eventListener.onAfterMoveRow.call(this.list, sourceRow, targetRow);
                }
                
                // notifies observers.
                this.setChanged();
                this.notifyObservers(null);
            }else{
                // changes row position
                await this.list.moveRow(fromIndex, toIndex);
            }
        }
        
        /**
         * Gets parent map
         * @param map
         */
        getParentMap(map:duice.Map):duice.Map {
            let parentIdValue = map.get(this.hierarchy.parentIdName);
            for(let i = 0, size = this.list.getRowCount(); i < size; i ++){
                let element = this.list.getRow(i);
                if(element.get(this.hierarchy.idName) === parentIdValue){
                    return element;
                }
            }
            return null;
        }
        
        /**
         * Returns whether circular reference or not
         * @param map
         * @param idValue
         */
        isCircularReference(map:duice.Map, idValue:any):boolean {
            let parentMap = map;
            while(parentMap !== null){
                parentMap = this.getParentMap(parentMap);
                if(parentMap === null){
                    return false;
                }
                if(parentMap.get(this.hierarchy.idName) === idValue){
                    return true;
                }
            }
        }
    }

    // Adds components
    addComponentFactory(new TableFactory());
    addComponentFactory(new UlFactory());
    addComponentFactory(new InputFactory());
    addComponentFactory(new SelectFactory());
    addComponentFactory(new TextareaFactory());
    addComponentFactory(new ImgFactory());
    addComponentFactory(new SpanFactory());
    addComponentFactory(new DivFactory());
    addComponentFactory(new ScriptletFactory());
}

/**
 * DOMContentLoaded event process
 */
document.addEventListener("DOMContentLoaded", function(event) {
    duice.initializeComponent(document, {});
});


