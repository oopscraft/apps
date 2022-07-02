"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var duice;
(function (duice) {
    let alias = 'duice';
    let componentFactories = [];
    function setAlias(value) {
        alias = value;
    }
    duice.setAlias = setAlias;
    function getAlias() {
        return alias;
    }
    duice.getAlias = getAlias;
    function addComponentFactory(componentFactory) {
        componentFactories.push(componentFactory);
    }
    duice.addComponentFactory = addComponentFactory;
    function getComponentFactories() {
        return componentFactories;
    }
    duice.getComponentFactories = getComponentFactories;
    function initializeComponent(container, $context) {
        [ListComponentFactory, MapComponentFactory].forEach(function (factoryType) {
            componentFactories.forEach(function (componentFactory) {
                let elements = container.querySelectorAll(componentFactory.getSelector() + `[data-${getAlias()}-bind]:not([data-${getAlias()}-id])`);
                for (let i = 0, size = elements.length; i < size; i++) {
                    let element = elements[i];
                    if (componentFactory instanceof factoryType) {
                        componentFactory.setContext($context);
                        componentFactory.getComponent(element);
                    }
                }
            });
        });
    }
    duice.initializeComponent = initializeComponent;
    function assert(expression, message) {
        if (!expression) {
            throw message;
        }
    }
    duice.assert = assert;
    function isEmpty(value) {
        return value === undefined
            || value === null
            || value === ''
            || trim(value) === '';
    }
    duice.isEmpty = isEmpty;
    function isNotEmpty(value) {
        return !isEmpty(value);
    }
    duice.isNotEmpty = isNotEmpty;
    function defaultIfEmpty(value, defaultValue) {
        if (isEmpty(value) === true) {
            return defaultValue;
        }
        else {
            return value;
        }
    }
    duice.defaultIfEmpty = defaultIfEmpty;
    function trim(value) {
        return (value + "").trim();
    }
    duice.trim = trim;
    function padLeft(value, length, padChar) {
        for (let i = 0, size = (length - value.length); i < size; i++) {
            value = padChar + value;
        }
        return value;
    }
    duice.padLeft = padLeft;
    function padRight(value, length, padChar) {
        for (let i = 0, size = (length - value.length); i < size; i++) {
            value = value + padChar;
        }
        return value;
    }
    duice.padRight = padRight;
    function getCurrentWindow() {
        if (window.frameElement) {
            return window.parent;
        }
        else {
            return window;
        }
    }
    function getCurrentMaxZIndex() {
        let zIndex, z = 0, all = document.getElementsByTagName('*');
        for (let i = 0, n = all.length; i < n; i++) {
            zIndex = document.defaultView.getComputedStyle(all[i], null).getPropertyValue("z-index");
            zIndex = parseInt(zIndex, 10);
            z = (zIndex) ? Math.max(z, zIndex) : z;
        }
        return z;
    }
    class StringMask {
        constructor(pattern) {
            this.pattern = pattern;
        }
        encode(value) {
            if (isEmpty(this.pattern)) {
                return value;
            }
            let encodedValue = '';
            let patternChars = this.pattern.split('');
            let valueChars = value.split('');
            let valueCharsPosition = 0;
            for (let i = 0, size = patternChars.length; i < size; i++) {
                let patternChar = patternChars[i];
                if (patternChar === '#') {
                    encodedValue += defaultIfEmpty(valueChars[valueCharsPosition++], '');
                }
                else {
                    encodedValue += patternChar;
                }
            }
            return encodedValue;
        }
        decode(value) {
            if (isEmpty(this.pattern)) {
                return value;
            }
            let decodedValue = '';
            let patternChars = this.pattern.split('');
            let valueChars = value.split('');
            let valueCharsPosition = 0;
            for (let i = 0, size = patternChars.length; i < size; i++) {
                let patternChar = patternChars[i];
                if (patternChar === '#') {
                    decodedValue += defaultIfEmpty(valueChars[valueCharsPosition++], '');
                }
                else {
                    valueCharsPosition++;
                }
            }
            return decodedValue;
        }
    }
    duice.StringMask = StringMask;
    class NumberMask {
        constructor(scale) {
            this.scale = 0;
            this.scale = scale;
        }
        encode(number) {
            if (isEmpty(number) || isNaN(Number(number))) {
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
        decode(string) {
            if (isEmpty(string)) {
                return null;
            }
            if (string.length === 1 && /[+-]/.test(string)) {
                string += '0';
            }
            string = string.replace(/,/gi, '');
            if (isNaN(Number(string))) {
                throw 'NaN';
            }
            let number = Number(string);
            number = Number(number.toFixed(this.scale));
            return number;
        }
    }
    duice.NumberMask = NumberMask;
    class DateMask {
        constructor(pattern) {
            this.patternRex = /yyyy|yy|MM|dd|HH|hh|mm|ss/gi;
            this.pattern = pattern;
        }
        encode(string) {
            if (isEmpty(string)) {
                return '';
            }
            if (isEmpty(this.pattern)) {
                return new Date(string).toString();
            }
            let date = new Date(string);
            string = this.pattern.replace(this.patternRex, function ($1) {
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
        decode(string) {
            if (isEmpty(string)) {
                return null;
            }
            if (isEmpty(this.pattern)) {
                return new Date(string).toISOString();
            }
            let date = new Date(0, 0, 0, 0, 0, 0);
            let match;
            while ((match = this.patternRex.exec(this.pattern)) != null) {
                let formatString = match[0];
                let formatIndex = match.index;
                let formatLength = formatString.length;
                let matchValue = string.substr(formatIndex, formatLength);
                matchValue = padRight(matchValue, formatLength, '0');
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
    duice.DateMask = DateMask;
    class Blocker {
        constructor(element) {
            this.opacity = 0.2;
            this.element = element;
            this.div = document.createElement('div');
            this.div.classList.add('duice-blocker');
        }
        setOpacity(opacity) {
            this.opacity = opacity;
        }
        block() {
            this.div.style.position = 'fixed';
            this.div.style.zIndex = String(getCurrentMaxZIndex() + 1);
            this.div.style.background = 'rgba(0, 0, 0, ' + this.opacity + ')';
            this.takePosition();
            let _this = this;
            getCurrentWindow().addEventListener('scroll', function () {
                _this.takePosition();
            });
            this.element.appendChild(this.div);
        }
        unblock() {
            this.element.removeChild(this.div);
        }
        takePosition() {
            if (this.element.tagName == 'BODY') {
                this.div.style.width = '100%';
                this.div.style.height = '100%';
                this.div.style.top = '0px';
                this.div.style.left = '0px';
            }
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
        getBlockDiv() {
            return this.div;
        }
    }
    duice.Blocker = Blocker;
    class Dialog {
        constructor(contentDiv) {
            let _this = this;
            this.contentDiv = contentDiv;
            this.dialog = document.createElement('dialog');
            this.dialog.classList.add('duice-dialog');
            this.header = document.createElement('span');
            this.header.classList.add('duice-dialog__header');
            this.dialog.appendChild(this.header);
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
            this.closeButton = document.createElement('span');
            this.closeButton.classList.add('duice-dialog__closeButton');
            this.closeButton.addEventListener('click', function (event) {
                _this.reject();
            });
            this.dialog.appendChild(_this.closeButton);
            currentWindow.addEventListener('resize', function (event) {
                _this.moveToCenterPosition();
            });
        }
        show() {
            if (this.contentDiv.parentNode) {
                this.contentParentNode = this.contentDiv.parentNode;
            }
            this.dialog.appendChild(this.contentDiv);
            getCurrentWindow().document.body.appendChild(this.dialog);
            this.contentDiv.style.display = 'block';
            this.dialog.showModal();
            this.moveToCenterPosition();
        }
        hide() {
            if (this.contentParentNode) {
                this.contentParentNode.appendChild(this.contentDiv);
            }
            this.dialog.close();
            this.contentDiv.style.display = 'none';
        }
        moveToCenterPosition() {
            let currentWindow = getCurrentWindow();
            let computedStyle = currentWindow.getComputedStyle(this.dialog);
            let computedWidth = parseInt(computedStyle.getPropertyValue('width').replace(/px/gi, ''));
            let computedHeight = parseInt(computedStyle.getPropertyValue('height').replace(/px/gi, ''));
            this.dialog.style.left = Math.max(0, currentWindow.innerWidth / 2 - computedWidth / 2) + 'px';
            this.dialog.style.top = Math.max(0, currentWindow.innerHeight / 2 - computedHeight / 2) + 'px';
        }
        open() {
            return __awaiter(this, void 0, void 0, function* () {
                this.show();
                let _this = this;
                this.promise = new Promise(function (resolve, reject) {
                    _this.promiseResolve = resolve;
                    _this.promiseReject = reject;
                });
                return this.promise;
            });
        }
        resolve(...args) {
            this.hide();
            this.promiseResolve(...args);
        }
        reject(...args) {
            this.hide();
            this.promiseReject(...args);
        }
    }
    duice.Dialog = Dialog;
    class Alert extends Dialog {
        constructor(message) {
            let contentDiv = document.createElement('div');
            super(contentDiv);
            this.message = message;
            let _this = this;
            this.iconDiv = document.createElement('div');
            this.iconDiv.classList.add('duice-alert__iconDiv');
            this.messageDiv = document.createElement('div');
            this.messageDiv.classList.add('duice-alert__messageDiv');
            this.messageDiv.innerHTML = this.message;
            this.buttonDiv = document.createElement('div');
            this.buttonDiv.classList.add('duice-alert__buttonDiv');
            this.confirmButton = document.createElement('button');
            this.confirmButton.classList.add('duice-alert__buttonDiv-button');
            this.confirmButton.classList.add('duice-alert__buttonDiv-button--confirm');
            this.confirmButton.addEventListener('click', function (event) {
                _this.resolve();
            });
            this.buttonDiv.appendChild(this.confirmButton);
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
    duice.Alert = Alert;
    class Confirm extends Dialog {
        constructor(message) {
            let contentDiv = document.createElement('div');
            super(contentDiv);
            this.message = message;
            let _this = this;
            this.iconDiv = document.createElement('div');
            this.iconDiv.classList.add('duice-confirm__iconDiv');
            this.messageDiv = document.createElement('div');
            this.messageDiv.classList.add('duice-confirm__messageDiv');
            this.messageDiv.innerHTML = this.message;
            this.buttonDiv = document.createElement('div');
            this.buttonDiv.classList.add('duice-confirm__buttonDiv');
            this.confirmButton = document.createElement('button');
            this.confirmButton.classList.add('duice-confirm__buttonDiv-button');
            this.confirmButton.classList.add('duice-confirm__buttonDiv-button--confirm');
            this.confirmButton.addEventListener('click', function (event) {
                _this.resolve(true);
            });
            this.buttonDiv.appendChild(this.confirmButton);
            this.cancelButton = document.createElement('button');
            this.cancelButton.classList.add('duice-confirm__buttonDiv-button');
            this.cancelButton.classList.add('duice-confirm__buttonDiv-button--cancel');
            this.cancelButton.addEventListener('click', function (event) {
                _this.resolve(false);
            });
            this.buttonDiv.appendChild(this.cancelButton);
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
    duice.Confirm = Confirm;
    class Prompt extends Dialog {
        constructor(message, defaultValue) {
            let contentDiv = document.createElement('div');
            super(contentDiv);
            this.message = message;
            this.defaultValue = defaultValue;
            let _this = this;
            this.iconDiv = document.createElement('div');
            this.iconDiv.classList.add('duice-prompt__iconDiv');
            this.messageDiv = document.createElement('div');
            this.messageDiv.classList.add('duice-prompt__messageDiv');
            this.messageDiv.innerHTML = this.message;
            this.inputDiv = document.createElement('div');
            this.inputDiv.classList.add('duice-prompt__inputDiv');
            this.input = document.createElement('input');
            this.input.classList.add('duice-prompt__inputDiv-input');
            if (this.defaultValue) {
                this.input.value = this.defaultValue;
            }
            this.inputDiv.appendChild(this.input);
            this.buttonDiv = document.createElement('div');
            this.buttonDiv.classList.add('duice-prompt__buttonDiv');
            this.confirmButton = document.createElement('button');
            this.confirmButton.classList.add('duice-prompt__buttonDiv-button');
            this.confirmButton.classList.add('duice-prompt__buttonDiv-button--confirm');
            this.confirmButton.addEventListener('click', function (event) {
                _this.resolve(_this.input.value);
            });
            this.buttonDiv.appendChild(this.confirmButton);
            this.cancelButton = document.createElement('button');
            this.cancelButton.classList.add('duice-prompt__buttonDiv-button');
            this.cancelButton.classList.add('duice-prompt__buttonDiv-button--cancel');
            this.cancelButton.addEventListener('click', function (event) {
                _this.resolve(null);
            });
            this.buttonDiv.appendChild(this.cancelButton);
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
    duice.Prompt = Prompt;
    class TabFolderEventListener {
    }
    class TabFolder {
        constructor() {
            this.tabs = new Array();
            this.eventListener = new TabFolderEventListener();
        }
        addTab(tab) {
            let _this = this;
            const index = Number(this.tabs.length);
            tab.getButton().addEventListener('click', function (event) {
                _this.selectTab(index);
            });
            this.tabs.push(tab);
        }
        selectTab(index) {
            return __awaiter(this, void 0, void 0, function* () {
                if (this.eventListener.onBeforeSelectTab) {
                    if ((yield this.eventListener.onBeforeSelectTab.call(this, this.tabs[index])) === false) {
                        throw 'canceled';
                    }
                }
                for (let i = 0, size = this.tabs.length; i < size; i++) {
                    let tab = this.tabs[i];
                    if (i === index) {
                        tab.setActive(true);
                    }
                    else {
                        tab.setActive(false);
                    }
                }
                if (this.eventListener.onAfterSelectTab) {
                    this.eventListener.onAfterSelectTab.call(this, this.tabs[index]);
                }
            });
        }
        onBeforeSelectTab(listener) {
            this.eventListener.onBeforeSelectTab = listener;
            return this;
        }
        onAfterSelectTab(listener) {
            this.eventListener.onAfterSelectTab = listener;
            return this;
        }
    }
    duice.TabFolder = TabFolder;
    class Tab {
        constructor(button, content) {
            this.button = button;
            this.content = content;
        }
        getButton() {
            return this.button;
        }
        getContent() {
            return this.content;
        }
        setActive(active) {
            if (active === true) {
                this.button.style.opacity = 'unset';
                this.content.style.display = null;
            }
            else {
                this.button.style.opacity = '0.5';
                this.content.style.display = 'none';
            }
        }
    }
    duice.Tab = Tab;
    class Observable {
        constructor() {
            this.observers = new Array();
            this.changed = false;
            this.notifyEnable = true;
        }
        addObserver(observer) {
            for (let i = 0, size = this.observers.length; i < size; i++) {
                if (this.observers[i] === observer) {
                    return;
                }
            }
            this.observers.push(observer);
        }
        removeObserver(observer) {
            for (let i = 0, size = this.observers.length; i < size; i++) {
                if (this.observers[i] === observer) {
                    this.observers.splice(i, 1);
                    return;
                }
            }
        }
        notifyObservers(obj) {
            if (this.notifyEnable && this.hasChanged()) {
                this.clearUnavailableObservers();
                for (let i = 0, size = this.observers.length; i < size; i++) {
                    if (this.observers[i] !== obj) {
                        try {
                            this.observers[i].update(this, obj);
                        }
                        catch (e) {
                            console.error(e, this.observers[i]);
                        }
                    }
                }
                this.clearChanged();
            }
        }
        suspendNotify() {
            this.notifyEnable = false;
        }
        resumeNotify() {
            this.notifyEnable = true;
        }
        setChanged() {
            this.changed = true;
        }
        hasChanged() {
            return this.changed;
        }
        clearChanged() {
            this.changed = false;
        }
        clearUnavailableObservers() {
            for (let i = this.observers.length - 1; i >= 0; i--) {
                try {
                    if (this.observers[i].isAvailable() === false) {
                        this.observers.splice(i, 1);
                    }
                }
                catch (e) {
                    console.error(e, this.observers[i]);
                }
            }
        }
    }
    class DataObject extends Observable {
        constructor() {
            super(...arguments);
            this.available = true;
            this.disable = new Object();
            this.disableAll = false;
            this.readonly = new Object();
            this.readonlyAll = false;
            this.visible = true;
        }
        clone(obj) {
            return JSON.parse(JSON.stringify(obj));
        }
        isAvailable() {
            return true;
        }
        setDisable(name, disable) {
            this.disable[name] = disable;
            this.setChanged();
            this.notifyObservers(this);
        }
        setDisableAll(disable) {
            this.disableAll = disable;
            for (let name in this.disable) {
                this.disable[name] = disable;
            }
            this.setChanged();
            this.notifyObservers(this);
        }
        isDisable(name) {
            if (this.disable.hasOwnProperty(name)) {
                return this.disable[name];
            }
            else {
                return this.disableAll;
            }
        }
        setReadonly(name, readonly) {
            this.readonly[name] = readonly;
            this.setChanged();
            this.notifyObservers(this);
        }
        setReadonlyAll(readonly) {
            this.readonlyAll = readonly;
            for (let name in this.readonly) {
                this.readonly[name] = readonly;
            }
            this.setChanged();
            this.notifyObservers(this);
        }
        isReadonly(name) {
            if (this.readonly.hasOwnProperty(name)) {
                return this.readonly[name];
            }
            else {
                return this.readonlyAll;
            }
        }
        setVisible(visible) {
            this.visible = visible;
            for (let i = 0, size = this.observers.length; i < size; i++) {
                try {
                    if (this.observers[i] instanceof Component) {
                        let uiComponent = this.observers[i];
                        uiComponent.setVisible(visible);
                    }
                }
                catch (e) {
                    console.error(e, this.observers[i]);
                }
            }
        }
        isVisible() {
            return this.visible;
        }
    }
    duice.DataObject = DataObject;
    class MapEventListener {
    }
    class Map extends DataObject {
        constructor(json) {
            super();
            this.data = new Object();
            this.originData = JSON.stringify(this.data);
            this.eventListener = new MapEventListener();
            this.fromJson(json || {});
        }
        update(mapComponent, obj) {
            console.debug('Map.update', mapComponent, obj);
            let name = mapComponent.getName();
            let value = mapComponent.getValue();
            this.set(name, value);
        }
        fromJson(json) {
            this.data = new Object();
            for (let name in json) {
                this.data[name] = json[name];
            }
            this.save();
            this.setChanged();
            this.notifyObservers(this);
        }
        toJson() {
            let json = new Object();
            for (let name in this.data) {
                json[name] = this.data[name];
            }
            return json;
        }
        clear() {
            this.data = new Object();
            this.setChanged();
            this.notifyObservers(this);
        }
        save() {
            this.originData = JSON.stringify(this.toJson());
        }
        reset() {
            this.fromJson(JSON.parse(this.originData));
        }
        isDirty() {
            if (JSON.stringify(this.toJson()) === this.originData) {
                return false;
            }
            else {
                return true;
            }
        }
        set(name, value) {
            return __awaiter(this, void 0, void 0, function* () {
                if (this.eventListener.onBeforeChange) {
                    try {
                        if ((yield this.eventListener.onBeforeChange.call(this, name, value)) === false) {
                            throw 'Map.set is canceled';
                        }
                    }
                    catch (e) {
                        this.setChanged();
                        this.notifyObservers(this);
                        throw e;
                    }
                }
                this.data[name] = value;
                this.setChanged();
                this.notifyObservers(this);
                if (this.eventListener.onAfterChange) {
                    this.eventListener.onAfterChange.call(this, name, value);
                }
                return true;
            });
        }
        get(name) {
            return this.data[name];
        }
        getNames() {
            let names = new Array();
            for (let name in this.data) {
                names.push(name);
            }
            return names;
        }
        setFocus(name) {
            for (let i = 0, size = this.observers.length; i < size; i++) {
                let observer = this.observers[i];
                if (observer instanceof MapComponent) {
                    let mapUiComponent = this.observers[i];
                    if (observer.getName() === name) {
                        mapUiComponent.setFocus();
                        break;
                    }
                }
            }
        }
        onBeforeChange(listener) {
            this.eventListener.onBeforeChange = listener;
        }
        onAfterChange(listener) {
            this.eventListener.onAfterChange = listener;
        }
    }
    duice.Map = Map;
    class ListEventListener {
    }
    class List extends DataObject {
        constructor(jsonArray) {
            super();
            this.data = new Array();
            this.originData = JSON.stringify(this.data);
            this.index = -1;
            this.eventListener = new ListEventListener();
            this.fromJson(jsonArray || []);
        }
        update(listComponent, obj) {
            console.debug('List.update', listComponent, obj);
            this.setChanged();
            this.notifyObservers(obj);
        }
        fromJson(jsonArray) {
            this.clear();
            for (let i = 0; i < jsonArray.length; i++) {
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
        toJson() {
            let jsonArray = new Array();
            for (let i = 0; i < this.data.length; i++) {
                jsonArray.push(this.data[i].toJson());
            }
            return jsonArray;
        }
        clear() {
            for (let i = 0, size = this.data.length; i < size; i++) {
                this.data[i].removeObserver(this);
            }
            this.data = new Array();
            this.setIndex(-1);
        }
        save() {
            this.originData = JSON.stringify(this.toJson());
        }
        reset() {
            this.fromJson(JSON.parse(this.originData));
        }
        isDirty() {
            if (JSON.stringify(this.toJson()) === this.originData) {
                return false;
            }
            else {
                return true;
            }
        }
        setIndex(index) {
            this.index = index;
            this.setChanged();
            this.notifyObservers(this);
        }
        getIndex() {
            return this.index;
        }
        getRowCount() {
            return this.data.length;
        }
        getRow(index) {
            return this.data[index];
        }
        selectRow(index) {
            return __awaiter(this, void 0, void 0, function* () {
                let selectedRow = this.getRow(index);
                if (this.eventListener.onBeforeSelectRow) {
                    if ((yield this.eventListener.onBeforeSelectRow.call(this, selectedRow)) === false) {
                        throw 'canceled';
                    }
                }
                this.setIndex(index);
                if (this.eventListener.onAfterSelectRow) {
                    this.eventListener.onAfterSelectRow.call(this, selectedRow);
                }
                return true;
            });
        }
        moveRow(fromIndex, toIndex) {
            return __awaiter(this, void 0, void 0, function* () {
                let sourceMap = this.getRow(fromIndex);
                let targetMap = this.getRow(toIndex);
                if (this.eventListener.onBeforeMoveRow) {
                    if ((yield this.eventListener.onBeforeMoveRow.call(this, sourceMap, targetMap)) === false) {
                        throw 'canceled';
                    }
                }
                this.index = fromIndex;
                this.data.splice(toIndex, 0, this.data.splice(fromIndex, 1)[0]);
                this.setIndex(toIndex);
                if (this.eventListener.onAfterMoveRow) {
                    yield this.eventListener.onAfterMoveRow.call(this, sourceMap, targetMap);
                }
            });
        }
        addRow(map) {
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
        insertRow(index, map) {
            if (0 <= index && index < this.data.length) {
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
        removeRow(index) {
            if (0 <= index && index < this.data.length) {
                this.data.splice(index, 1);
                this.setIndex(Math.min(this.index, this.data.length - 1));
            }
        }
        indexOf(handler) {
            for (let i = 0, size = this.data.length; i < size; i++) {
                if (handler.call(this, this.data[i]) === true) {
                    return i;
                }
            }
            return -1;
        }
        contains(handler) {
            if (this.indexOf(handler) > -1) {
                return true;
            }
            else {
                return false;
            }
        }
        forEach(handler) {
            for (let i = 0, size = this.data.length; i < size; i++) {
                if (handler.call(this, this.data[i], i) === false) {
                    break;
                }
            }
        }
        setDisable(name, disable) {
            this.data.forEach(function (map) {
                map.setDisable(name, disable);
            });
            super.setDisable(name, disable);
        }
        setDisableAll(disable) {
            this.data.forEach(function (map) {
                map.setDisableAll(disable);
            });
            super.setDisableAll(disable);
        }
        setReadonly(name, readonly) {
            this.data.forEach(function (map) {
                map.setReadonly(name, readonly);
            });
            super.setReadonly(name, readonly);
        }
        setReadonlyAll(readonly) {
            this.data.forEach(function (map) {
                map.setReadonlyAll(readonly);
            });
            super.setReadonlyAll(readonly);
        }
        onBeforeSelectRow(listener) {
            this.eventListener.onBeforeSelectRow = listener;
        }
        onAfterSelectRow(listener) {
            this.eventListener.onAfterSelectRow = listener;
        }
        onBeforeMoveRow(listener) {
            this.eventListener.onBeforeMoveRow = listener;
        }
        onAfterMoveRow(listener) {
            this.eventListener.onAfterMoveRow = listener;
        }
        onBeforeChangeRow(listener) {
            this.eventListener.onBeforeChangeRow = listener;
            this.data.forEach(function (map) {
                map.onBeforeChange(listener);
            });
        }
        onAfterChangeRow(listener) {
            this.eventListener.onAfterChangeRow = listener;
            this.data.forEach(function (map) {
                map.onAfterChange(listener);
            });
        }
    }
    duice.List = List;
    class ComponentFactory {
        setContext(context) {
            this.context = context;
        }
        getContext() {
            return this.context;
        }
        getContextProperty(name) {
            if (this.context[name]) {
                return this.context[name];
            }
            if (window.hasOwnProperty(name)) {
                return window[name];
            }
            try {
                return eval.call(this.context, name);
            }
            catch (e) {
                return null;
            }
        }
    }
    class Component extends Observable {
        constructor(element) {
            super();
            this.element = element;
            this.element.dataset[`${getAlias()}Id`] = this.generateUuid();
        }
        generateUuid() {
            let dt = new Date().getTime();
            let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                let r = (dt + Math.random() * 16) % 16 | 0;
                dt = Math.floor(dt / 16);
                return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
            });
            return uuid;
        }
        addClass(element, className) {
            element.classList.add(className);
        }
        isAvailable() {
            if (!Node.prototype.contains) {
                Node.prototype.contains = function (el) {
                    while (el = el.parentNode) {
                        if (el === this)
                            return true;
                    }
                    return false;
                };
            }
            if (document.contains(this.element)) {
                return true;
            }
            else {
                return false;
            }
        }
        executeExpression(element, $context) {
            let string = element.outerHTML;
            let regExp = new RegExp(`\\[@${getAlias()}\\[([\\s\\S]*?)\\]\\]`, 'mgi');
            string = string.replace(regExp, function (match, command) {
                try {
                    command = command.replace('&amp;', '&');
                    command = command.replace('&lt;', '<');
                    command = command.replace('&gt;', '>');
                    let result = eval(command);
                    return result;
                }
                catch (e) {
                    console.error(e, command);
                    throw e;
                }
            });
            try {
                let template = document.createElement('template');
                template.innerHTML = string;
                return template.content.firstChild;
            }
            catch (e) {
                this.removeChildNodes(element);
                element.innerHTML = string;
                return element;
            }
        }
        removeChildNodes(element) {
            let node, nodes = element.childNodes, i = 0;
            while (node = nodes[i++]) {
                if (node.nodeType === 1) {
                    element.removeChild(node);
                }
            }
            while (element.firstChild) {
                element.removeChild(element.firstChild);
            }
            if (element instanceof HTMLSelectElement) {
                element.options.length = 0;
            }
        }
        setVisible(visible) {
            this.element.style.display = (visible ? '' : 'none');
        }
        setFocus() {
            if (this.element.focus) {
                this.element.focus();
            }
        }
        setPositionCentered(element) {
            let win = getCurrentWindow();
            let computedStyle = win.getComputedStyle(element);
            let computedWidth = parseInt(computedStyle.getPropertyValue('width').replace(/px/gi, ''));
            let computedHeight = parseInt(computedStyle.getPropertyValue('height').replace(/px/gi, ''));
            let computedLeft = Math.max(0, win.innerWidth / 2 - computedWidth / 2) + win.scrollX;
            let computedTop = Math.max(0, win.innerHeight / 2 - computedHeight / 2) + win.scrollY;
            computedTop = computedTop - 100;
            computedTop = Math.max(10, computedTop);
            element.style.left = computedLeft + 'px';
            element.style.top = computedTop + 'px';
        }
        getElementPosition(element) {
            let pos = ('absolute relative').indexOf(getComputedStyle(element).position) == -1;
            let rect1 = { top: element.offsetTop * pos, left: element.offsetLeft * pos };
            let rect2 = element.offsetParent ? this.getElementPosition(element.offsetParent) : { top: 0, left: 0 };
            return {
                top: rect1.top + rect2.top,
                left: rect1.left + rect2.left,
                width: element.offsetWidth,
                height: element.offsetHeight
            };
        }
    }
    class MapComponentFactory extends ComponentFactory {
    }
    duice.MapComponentFactory = MapComponentFactory;
    class MapComponent extends Component {
        bind(map, name, ...args) {
            assert(map instanceof Map, 'duice bind error: ' + this.element.outerHTML);
            this.map = map;
            this.name = name;
            this.map.addObserver(this);
            this.addObserver(this.map);
            this.update(this.map, this.map);
        }
        getMap() {
            return this.map;
        }
        getName() {
            return this.name;
        }
    }
    duice.MapComponent = MapComponent;
    class ListComponentFactory extends ComponentFactory {
    }
    duice.ListComponentFactory = ListComponentFactory;
    class ListComponent extends Component {
        bind(list, item) {
            assert(list instanceof List, 'duice bind error: ' + this.element.outerHTML);
            this.list = list;
            this.item = item;
            this.list.addObserver(this);
            this.addObserver(this.list);
            this.update(this.list, this.list);
        }
        getList() {
            return this.list;
        }
        getItem() {
            return this.item;
        }
    }
    duice.ListComponent = ListComponent;
    class ScriptletFactory extends MapComponentFactory {
        getSelector() {
            return `*[is="${getAlias()}-scriptlet"]`;
        }
        getComponent(element) {
            let scriptlet = new Scriptlet(element);
            let context;
            if (this.getContext() !== window) {
                context = this.getContext();
            }
            else {
                context = {};
            }
            if (element.dataset[`${getAlias()}Bind`]) {
                let bind = element.dataset[`${getAlias()}Bind`].split(',');
                let _this = this;
                bind.forEach(function (name) {
                    context[name] = _this.getContextProperty(name);
                });
            }
            scriptlet.bind(context);
            return scriptlet;
        }
    }
    duice.ScriptletFactory = ScriptletFactory;
    class Scriptlet extends MapComponent {
        constructor(element) {
            super(element);
            this.script = element.dataset[`${getAlias()}Script`];
        }
        bind(context) {
            this.context = context;
            for (let name in this.context) {
                let obj = this.context[name];
                if (typeof obj === 'object' && obj instanceof duice.DataObject) {
                    obj.addObserver(this);
                    this.addObserver(obj);
                    this.update(obj, obj);
                }
            }
        }
        update(dataObject, obj) {
            if (this.script) {
                try {
                    let func = Function('$context', '"use strict";' + this.script + '');
                    let result = func.call(this.element, this.context);
                    return result;
                }
                catch (e) {
                    console.error(this.script);
                    throw e;
                }
            }
        }
        getValue() {
            return null;
        }
    }
    duice.Scriptlet = Scriptlet;
    class SpanFactory extends MapComponentFactory {
        getSelector() {
            return `span[is="${getAlias()}-span"]`;
        }
        getComponent(element) {
            let span = new Span(element);
            if (element.dataset[`${getAlias()}Mask`]) {
                let maskArray = element.dataset[`${getAlias()}Mask`].split(',');
                let maskType = maskArray[0];
                let mask;
                switch (maskType) {
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
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            span.bind(this.getContextProperty(bind[0]), bind[1]);
            return span;
        }
    }
    duice.SpanFactory = SpanFactory;
    class Span extends MapComponent {
        constructor(span) {
            super(span);
            this.span = span;
            this.addClass(this.span, 'duice-span');
        }
        setMask(mask) {
            this.mask = mask;
        }
        update(map, obj) {
            this.removeChildNodes(this.span);
            let value = map.get(this.name);
            value = defaultIfEmpty(value, '');
            if (this.mask) {
                value = this.mask.encode(value);
            }
            this.span.appendChild(document.createTextNode(value));
        }
        getValue() {
            let value = this.span.innerHTML;
            value = defaultIfEmpty(value, null);
            if (this.mask) {
                value = this.mask.decode(value);
            }
            return value;
        }
    }
    duice.Span = Span;
    class DivFactory extends MapComponentFactory {
        getSelector() {
            return `div[is="${getAlias()}-div"]`;
        }
        getComponent(element) {
            let div = new Div(element);
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            div.bind(this.getContextProperty(bind[0]), bind[1]);
            return div;
        }
    }
    duice.DivFactory = DivFactory;
    class Div extends MapComponent {
        constructor(div) {
            super(div);
            this.div = div;
            this.addClass(this.div, 'duice-div');
        }
        update(map, obj) {
            this.removeChildNodes(this.div);
            let value = map.get(this.name);
            value = defaultIfEmpty(value, '');
            this.div.innerHTML = value;
        }
        getValue() {
            let value = this.div.innerHTML;
            return value;
        }
    }
    duice.Div = Div;
    class InputFactory extends MapComponentFactory {
        getSelector() {
            return `input[is="${getAlias()}-input"]`;
        }
        getComponent(element) {
            let input = null;
            let type = element.getAttribute('type');
            let mask = element.dataset[`${getAlias()}Mask`];
            switch (type) {
                case 'text':
                    input = new InputText(element);
                    if (mask) {
                        input.setMask(new StringMask(mask));
                    }
                    break;
                case 'number':
                    input = new InputNumber(element);
                    if (mask) {
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
                    if (mask) {
                        input.setMask(new DateMask(mask));
                    }
                    break;
                default:
                    input = new InputGeneric(element);
            }
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            input.bind(this.getContextProperty(bind[0]), bind[1]);
            return input;
        }
    }
    duice.InputFactory = InputFactory;
    class Input extends MapComponent {
        constructor(input) {
            super(input);
            this.input = input;
            let _this = this;
            this.input.addEventListener('keypress', function (event) {
                let inputChars = String.fromCharCode(event.keyCode);
                let newValue = this.value.substr(0, this.selectionStart) + inputChars + this.value.substr(this.selectionEnd);
                if (_this.checkValue(newValue) === false) {
                    event.preventDefault();
                }
            }, true);
            this.input.addEventListener('paste', function (event) {
                let inputChars = event.clipboardData.getData('text/plain');
                let newValue = this.value.substr(0, this.selectionStart) + inputChars + this.value.substr(this.selectionEnd);
                if (_this.checkValue(newValue) === false) {
                    event.preventDefault();
                }
            }, true);
            this.input.addEventListener('change', function (event) {
                _this.setChanged();
                _this.notifyObservers(this);
            }, true);
            _this.input.setAttribute('autocomplete', 'off');
        }
        checkValue(value) {
            return true;
        }
        setDisable(disable) {
            if (disable) {
                this.input.setAttribute('disabled', 'true');
            }
            else {
                this.input.removeAttribute('disabled');
            }
        }
        setReadonly(readonly) {
            if (readonly === true) {
                this.input.setAttribute('readonly', 'readonly');
            }
            else {
                this.input.removeAttribute('readonly');
            }
        }
    }
    duice.Input = Input;
    class InputGeneric extends Input {
        constructor(input) {
            super(input);
            this.addClass(this.input, 'duice-input-generic');
        }
        update(map, obj) {
            let value = map.get(this.getName());
            this.input.value = defaultIfEmpty(value, '');
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue() {
            let value = this.input.value;
            if (isEmpty(value)) {
                return null;
            }
            else {
                if (isNaN(value)) {
                    return String(value);
                }
                else {
                    return Number(value);
                }
            }
        }
    }
    duice.InputGeneric = InputGeneric;
    class InputText extends Input {
        constructor(input) {
            super(input);
            this.addClass(this.input, 'duice-input-text');
        }
        setMask(mask) {
            this.mask = mask;
        }
        update(map, obj) {
            let value = map.get(this.getName());
            value = defaultIfEmpty(value, '');
            if (this.mask) {
                value = this.mask.encode(value);
            }
            this.input.value = value;
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue() {
            let value = this.input.value;
            value = defaultIfEmpty(value, null);
            if (this.mask) {
                value = this.mask.decode(value);
            }
            return value;
        }
        checkValue(value) {
            let pattern = this.input.getAttribute('pattern');
            if (pattern) {
                let regExp = new RegExp(pattern);
                if (!regExp.test(value)) {
                    return false;
                }
            }
            if (this.mask) {
                try {
                    this.mask.decode(value);
                }
                catch (e) {
                    return false;
                }
            }
            return true;
        }
    }
    duice.InputText = InputText;
    class InputNumber extends Input {
        constructor(input) {
            super(input);
            this.addClass(this.input, 'duice-input-number');
            this.input.removeAttribute('type');
            this.mask = new NumberMask(0);
        }
        setMask(mask) {
            this.mask = mask;
        }
        update(map, obj) {
            let value = map.get(this.getName());
            if (this.mask) {
                value = this.mask.encode(value);
            }
            this.input.value = value;
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue() {
            let value = this.input.value;
            value = this.mask.decode(value);
            return value;
        }
        checkValue(value) {
            try {
                this.mask.decode(value);
            }
            catch (e) {
                return false;
            }
            return true;
        }
    }
    duice.InputNumber = InputNumber;
    class InputCheckbox extends Input {
        constructor(input) {
            super(input);
            this.addClass(this.input, 'duice-input-checkbox');
            this.input.addEventListener('click', function (event) {
                event.stopPropagation();
            }, true);
        }
        update(map, obj) {
            let value = map.get(this.getName());
            if (value === true) {
                this.input.checked = true;
            }
            else {
                this.input.checked = false;
            }
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue() {
            return this.input.checked;
        }
        setReadonly(readonly) {
            if (readonly) {
                this.input.style.pointerEvents = 'none';
            }
            else {
                this.input.style.pointerEvents = '';
            }
        }
    }
    duice.InputCheckbox = InputCheckbox;
    class InputRadio extends Input {
        constructor(input) {
            super(input);
            this.addClass(this.input, 'duice-input-radio');
        }
        update(map, obj) {
            let value = map.get(this.getName());
            if (value === this.input.value) {
                this.input.checked = true;
            }
            else {
                this.input.checked = false;
            }
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue() {
            return this.input.value;
        }
        setReadonly(readonly) {
            if (readonly) {
                this.input.style.pointerEvents = 'none';
            }
            else {
                this.input.style.pointerEvents = '';
            }
        }
    }
    duice.InputRadio = InputRadio;
    class InputDate extends Input {
        constructor(input) {
            super(input);
            this.readonly = false;
            this.addClass(this.input, 'duice-input-date');
            this.type = this.input.getAttribute('type').toLowerCase();
            this.input.removeAttribute('type');
            let _this = this;
            this.input.addEventListener('click', function (event) {
                if (_this.readonly !== true) {
                    _this.openPicker();
                }
            }, true);
            if (this.type === 'date') {
                this.mask = new DateMask('yyyy-MM-dd');
            }
            else {
                this.mask = new DateMask('yyyy-MM-dd HH:mm:ss');
            }
        }
        setMask(mask) {
            this.mask = mask;
        }
        update(map, obj) {
            let value = map.get(this.getName());
            value = defaultIfEmpty(value, '');
            if (this.mask) {
                value = this.mask.encode(value);
            }
            this.input.value = value;
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue() {
            let value = this.input.value;
            value = defaultIfEmpty(value, null);
            if (this.mask) {
                value = this.mask.decode(value);
            }
            if (this.type === 'date') {
                value = new DateMask('yyyy-MM-dd').encode(new Date(value).toISOString());
            }
            return value;
        }
        checkValue(value) {
            try {
                let s = this.mask.decode(value);
            }
            catch (e) {
                return false;
            }
            return true;
        }
        setReadonly(readonly) {
            this.readonly = readonly;
            super.setReadonly(readonly);
        }
        openPicker() {
            if (this.pickerDiv) {
                return;
            }
            let _this = this;
            this.pickerDiv = document.createElement('div');
            this.pickerDiv.classList.add('duice-input-date__pickerDiv');
            let date;
            if (isEmpty(this.getValue)) {
                date = new Date();
            }
            else {
                date = new Date(this.getValue());
            }
            let yyyy = date.getFullYear();
            let mm = date.getMonth();
            let dd = date.getDate();
            let hh = date.getHours();
            let mi = date.getMinutes();
            let ss = date.getSeconds();
            this.clickListener = function (event) {
                if (!_this.input.contains(event.target) && !_this.pickerDiv.contains(event.target)) {
                    _this.closePicker();
                }
            };
            window.addEventListener('click', this.clickListener);
            let headerDiv = document.createElement('div');
            headerDiv.classList.add('duice-input-date__pickerDiv-headerDiv');
            this.pickerDiv.appendChild(headerDiv);
            let titleSpan = document.createElement('span');
            titleSpan.classList.add('duice-input-date__pickerDiv-headerDiv-titleSpan');
            headerDiv.appendChild(titleSpan);
            let closeButton = document.createElement('button');
            closeButton.classList.add('duice-input-date__pickerDiv-headerDiv-closeButton');
            headerDiv.appendChild(closeButton);
            closeButton.addEventListener('click', function (event) {
                _this.closePicker();
            });
            let bodyDiv = document.createElement('div');
            bodyDiv.classList.add('duice-input-date__pickerDiv-bodyDiv');
            this.pickerDiv.appendChild(bodyDiv);
            let dateDiv = document.createElement('div');
            dateDiv.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv');
            bodyDiv.appendChild(dateDiv);
            let prevMonthButton = document.createElement('button');
            prevMonthButton.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv-prevMonthButton');
            dateDiv.appendChild(prevMonthButton);
            prevMonthButton.addEventListener('click', function (event) {
                date.setMonth(date.getMonth() - 1);
                updateDate(date);
            });
            let todayButton = document.createElement('button');
            todayButton.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv-todayButton');
            dateDiv.appendChild(todayButton);
            todayButton.addEventListener('click', function (event) {
                let newDate = new Date();
                date.setFullYear(newDate.getFullYear());
                date.setMonth(newDate.getMonth());
                date.setDate(newDate.getDate());
                updateDate(date);
            });
            let yearSelect = document.createElement('select');
            yearSelect.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv-yearSelect');
            dateDiv.appendChild(yearSelect);
            yearSelect.addEventListener('change', function (event) {
                date.setFullYear(parseInt(this.value));
                updateDate(date);
            });
            dateDiv.appendChild(document.createTextNode('-'));
            let monthSelect = document.createElement('select');
            monthSelect.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv-monthSelect');
            dateDiv.appendChild(monthSelect);
            for (let i = 0, end = 11; i <= end; i++) {
                let option = document.createElement('option');
                option.value = String(i);
                option.text = String(i + 1);
                monthSelect.appendChild(option);
            }
            monthSelect.addEventListener('change', function (event) {
                date.setMonth(parseInt(this.value));
                updateDate(date);
            });
            let nextMonthButton = document.createElement('button');
            nextMonthButton.classList.add('duice-input-date__pickerDiv-bodyDiv-dateDiv-nextMonthButton');
            dateDiv.appendChild(nextMonthButton);
            nextMonthButton.addEventListener('click', function (event) {
                date.setMonth(date.getMonth() + 1);
                updateDate(date);
            });
            let calendarTable = document.createElement('table');
            calendarTable.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable');
            bodyDiv.appendChild(calendarTable);
            let calendarThead = document.createElement('thead');
            calendarTable.appendChild(calendarThead);
            let weekTr = document.createElement('tr');
            weekTr.classList.add('.duice-input-date__pickerDiv-bodyDiv-calendarTable-weekTr');
            calendarThead.appendChild(weekTr);
            ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].forEach(function (element) {
                let weekTh = document.createElement('th');
                weekTh.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable-weekTh');
                weekTh.appendChild(document.createTextNode(element));
                weekTr.appendChild(weekTh);
            });
            let calendarTbody = document.createElement('tbody');
            calendarTable.appendChild(calendarTbody);
            let timeDiv = document.createElement('div');
            timeDiv.classList.add('duice-input-date__pickerDiv-bodyDiv-timeDiv');
            bodyDiv.appendChild(timeDiv);
            if (this.type === 'date') {
                date.setHours(0);
                date.setMinutes(0);
                date.setSeconds(0);
                timeDiv.style.display = 'none';
            }
            let nowButton = document.createElement('button');
            nowButton.classList.add('duice-input-date__pickerDiv-bodyDiv-timeDiv-nowButton');
            timeDiv.appendChild(nowButton);
            nowButton.addEventListener('click', function (event) {
                let newDate = new Date();
                date.setHours(newDate.getHours());
                date.setMinutes(newDate.getMinutes());
                date.setSeconds(newDate.getSeconds());
                updateDate(date);
            });
            let hourSelect = document.createElement('select');
            hourSelect.classList.add('duice-input-date__pickerDiv-bodyDiv-timeDiv-hourSelect');
            for (let i = 0; i <= 23; i++) {
                let option = document.createElement('option');
                option.value = String(i);
                option.text = padLeft(String(i), 2, '0');
                hourSelect.appendChild(option);
            }
            timeDiv.appendChild(hourSelect);
            hourSelect.addEventListener('change', function (event) {
                date.setHours(parseInt(this.value));
            });
            timeDiv.appendChild(document.createTextNode(':'));
            let minuteSelect = document.createElement('select');
            minuteSelect.classList.add('duice-input-date__pickerDiv-bodyDiv-timeDiv-minuteSelect');
            for (let i = 0; i <= 59; i++) {
                let option = document.createElement('option');
                option.value = String(i);
                option.text = padLeft(String(i), 2, '0');
                minuteSelect.appendChild(option);
            }
            timeDiv.appendChild(minuteSelect);
            minuteSelect.addEventListener('change', function (event) {
                date.setMinutes(parseInt(this.value));
            });
            timeDiv.appendChild(document.createTextNode(':'));
            let secondSelect = document.createElement('select');
            secondSelect.classList.add('duice-input-date__pickerDiv-bodyDiv-timeDiv-secondSelect');
            for (let i = 0; i <= 59; i++) {
                let option = document.createElement('option');
                option.value = String(i);
                option.text = padLeft(String(i), 2, '0');
                secondSelect.appendChild(option);
            }
            timeDiv.appendChild(secondSelect);
            secondSelect.addEventListener('change', function (event) {
                date.setSeconds(parseInt(this.value));
            });
            let footerDiv = document.createElement('div');
            footerDiv.classList.add('duice-input-date__pickerDiv-footerDiv');
            this.pickerDiv.appendChild(footerDiv);
            let confirmButton = document.createElement('button');
            confirmButton.classList.add('duice-input-date__pickerDiv-footerDiv-confirmButton');
            footerDiv.appendChild(confirmButton);
            confirmButton.addEventListener('click', function (event) {
                _this.input.value = _this.mask.encode(date.toISOString());
                _this.setChanged();
                _this.notifyObservers(this);
                _this.closePicker();
            });
            this.input.parentNode.insertBefore(this.pickerDiv, this.input.nextSibling);
            this.pickerDiv.style.position = 'absolute';
            this.pickerDiv.style.zIndex = String(getCurrentMaxZIndex() + 1);
            this.pickerDiv.style.left = this.getElementPosition(this.input).left + 'px';
            function updateDate(date) {
                let yyyy = date.getFullYear();
                let mm = date.getMonth();
                let dd = date.getDate();
                let hh = date.getHours();
                let mi = date.getMinutes();
                let ss = date.getSeconds();
                for (let i = yyyy - 5, end = yyyy + 5; i <= end; i++) {
                    let option = document.createElement('option');
                    option.value = String(i);
                    option.text = String(i);
                    yearSelect.appendChild(option);
                }
                yearSelect.value = String(yyyy);
                monthSelect.value = String(mm);
                let startDay = new Date(yyyy, mm, 1).getDay();
                let lastDates = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
                if (yyyy % 4 && yyyy % 100 != 0 || yyyy % 400 === 0) {
                    lastDates[1] = 29;
                }
                let lastDate = lastDates[mm];
                let rowNum = Math.ceil((startDay + lastDate - 1) / 7);
                let dNum = 0;
                let currentDate = new Date();
                _this.removeChildNodes(calendarTbody);
                for (let i = 1; i <= rowNum; i++) {
                    let dateTr = document.createElement('tr');
                    dateTr.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable-dateTr');
                    for (let k = 1; k <= 7; k++) {
                        let dateTd = document.createElement('td');
                        dateTd.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable-dateTd');
                        if ((i === 1 && k < startDay)
                            || (i === rowNum && dNum >= lastDate)) {
                            dateTd.appendChild(document.createTextNode(''));
                        }
                        else {
                            dNum++;
                            dateTd.appendChild(document.createTextNode(String(dNum)));
                            dateTd.dataset.date = String(dNum);
                            if (currentDate.getFullYear() === yyyy
                                && currentDate.getMonth() === mm
                                && currentDate.getDate() === dNum) {
                                dateTd.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable-dateTd--today');
                            }
                            if (dd === dNum) {
                                dateTd.classList.add('duice-input-date__pickerDiv-bodyDiv-calendarTable-dateTd--selected');
                            }
                            dateTd.addEventListener('click', function (event) {
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
                hourSelect.value = String(hh);
                minuteSelect.value = String(mi);
                secondSelect.value = String(ss);
            }
            updateDate(date);
        }
        closePicker() {
            this.pickerDiv.parentNode.removeChild(this.pickerDiv);
            this.pickerDiv = null;
            window.removeEventListener('click', this.clickListener);
        }
    }
    duice.InputDate = InputDate;
    class SelectFactory extends MapComponentFactory {
        getSelector() {
            return `select[is="${getAlias()}-select"]`;
        }
        getComponent(element) {
            let select = new Select(element);
            let option = element.dataset[`${getAlias()}Option`];
            if (option) {
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
    duice.SelectFactory = SelectFactory;
    class Select extends MapComponent {
        constructor(select) {
            super(select);
            this.defaultOptions = new Array();
            this.select = select;
            this.addClass(this.select, 'duice-select');
            let _this = this;
            this.select.addEventListener('change', function (event) {
                _this.setChanged();
                _this.notifyObservers(this);
            });
            for (let i = 0, size = this.select.options.length; i < size; i++) {
                this.defaultOptions.push(this.select.options[i]);
            }
        }
        setOption(list, value, text) {
            this.optionList = list;
            this.optionValue = value;
            this.optionText = text;
            this.updateOption();
            this.optionList.addObserver(this);
        }
        update(dataObject, obj) {
            console.debug(this, dataObject, obj);
            if (dataObject instanceof duice.Map) {
                let map = dataObject;
                let value = map.get(this.getName());
                this.select.value = defaultIfEmpty(value, '');
                if (this.select.selectedIndex < 0) {
                    if (this.defaultOptions.length > 0) {
                        this.defaultOptions[0].selected = true;
                    }
                }
                this.setDisable(map.isDisable(this.getName()));
                this.setReadonly(map.isReadonly(this.getName()));
            }
            if (dataObject instanceof duice.List) {
                this.updateOption();
            }
        }
        updateOption() {
            console.debug(this);
            this.removeChildNodes(this.select);
            for (let i = 0, size = this.defaultOptions.length; i < size; i++) {
                this.select.appendChild(this.defaultOptions[i]);
            }
            for (let i = 0, size = this.optionList.getRowCount(); i < size; i++) {
                let optionMap = this.optionList.getRow(i);
                let option = document.createElement('option');
                option.value = optionMap.get(this.optionValue);
                option.appendChild(document.createTextNode(optionMap.get(this.optionText)));
                this.select.appendChild(option);
            }
            this.update(this.map, null);
        }
        getValue() {
            let value = this.select.value;
            return defaultIfEmpty(value, null);
        }
        setDisable(disable) {
            if (disable) {
                this.select.setAttribute('disabled', 'true');
            }
            else {
                this.select.removeAttribute('disabled');
            }
        }
        setReadonly(readonly) {
            if (readonly === true) {
                this.select.style.pointerEvents = 'none';
                this.select.classList.add('duice-select--readonly');
            }
            else {
                this.select.style.pointerEvents = '';
                this.select.classList.remove('duice-select--readonly');
            }
        }
    }
    duice.Select = Select;
    class TextareaFactory extends MapComponentFactory {
        getSelector() {
            return `textarea[is="${getAlias()}-textarea"]`;
        }
        getComponent(element) {
            let textarea = new Textarea(element);
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            textarea.bind(this.getContextProperty(bind[0]), bind[1]);
            return textarea;
        }
    }
    duice.TextareaFactory = TextareaFactory;
    class Textarea extends MapComponent {
        constructor(textarea) {
            super(textarea);
            this.textarea = textarea;
            this.addClass(this.textarea, 'duice-textarea');
            let _this = this;
            this.textarea.addEventListener('change', function (event) {
                _this.setChanged();
                _this.notifyObservers(this);
            });
        }
        update(map, obj) {
            let value = map.get(this.getName());
            this.textarea.value = defaultIfEmpty(value, '');
            this.setDisable(map.isDisable(this.getName()));
            this.setReadonly(map.isReadonly(this.getName()));
        }
        getValue() {
            return defaultIfEmpty(this.textarea.value, null);
        }
        setDisable(disable) {
            if (disable) {
                this.textarea.setAttribute('disabled', 'true');
            }
            else {
                this.textarea.removeAttribute('disabled');
            }
        }
        setReadonly(readonly) {
            if (readonly) {
                this.textarea.setAttribute('readonly', 'readonly');
            }
            else {
                this.textarea.removeAttribute('readonly');
            }
        }
    }
    duice.Textarea = Textarea;
    class ImgFactory extends MapComponentFactory {
        getSelector() {
            return `img[is="${getAlias()}-img"]`;
        }
        getComponent(element) {
            let img = new Img(element);
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            img.bind(this.getContextProperty(bind[0]), bind[1]);
            let size = element.dataset[`${getAlias()}Size`];
            if (size) {
                let sizes = size.split(',');
                img.setSize(parseInt(sizes[0]), parseInt(sizes[1]));
            }
            return img;
        }
    }
    duice.ImgFactory = ImgFactory;
    class Img extends MapComponent {
        constructor(img) {
            super(img);
            this.img = img;
            this.addClass(this.img, 'duice-img');
            this.originSrc = this.img.src;
            let _this = this;
            this.img.addEventListener('click', function (event) {
                if (_this.disable || _this.readonly) {
                    return false;
                }
                let imgPosition = _this.getElementPosition(this);
                _this.openMenuDiv(imgPosition.top, imgPosition.left);
                event.stopPropagation();
            });
        }
        setSize(width, height) {
            this.size = { width: width, height: height };
            this.img.style.width = width + 'px';
            this.img.style.height = height + 'px';
        }
        update(map, obj) {
            let value = map.get(this.getName());
            this.value = defaultIfEmpty(value, this.originSrc);
            this.img.src = this.value;
            this.disable = map.isDisable(this.getName());
            this.readonly = map.isReadonly(this.getName());
            if (this.disable) {
                this.img.classList.add('duice-img--disable');
            }
            else {
                this.img.classList.remove('duice-img--disable');
            }
            if (this.readonly) {
                this.img.classList.add('duice-img--readonly');
            }
            else {
                this.img.classList.remove('duice-img--readonly');
            }
        }
        getValue() {
            return this.value;
        }
        openMenuDiv(top, left) {
            if (this.menuDiv) {
                return;
            }
            let _this = this;
            this.menuDiv = document.createElement('div');
            this.menuDiv.classList.add('duice-img__menuDiv');
            if (!this.disable) {
                let previewButton = document.createElement('button');
                previewButton.classList.add('duice-img__menuDiv-previewButton');
                previewButton.addEventListener('click', function (event) {
                    _this.openPreview();
                }, true);
                this.menuDiv.appendChild(previewButton);
            }
            if (!this.disable && !this.readonly) {
                let changeButton = document.createElement('button');
                changeButton.classList.add('duice-img__menuDiv-changeButton');
                changeButton.addEventListener('click', function (event) {
                    _this.changeImage();
                }, true);
                this.menuDiv.appendChild(changeButton);
                let clearButton = document.createElement('button');
                clearButton.classList.add('duice-img__menuDiv-clearButton');
                clearButton.addEventListener('click', function (event) {
                    _this.clearImage();
                }, true);
                this.menuDiv.appendChild(clearButton);
            }
            this.img.parentNode.insertBefore(this.menuDiv, this.img.nextSibling);
            this.menuDiv.style.position = 'absolute';
            this.menuDiv.style.zIndex = String(getCurrentMaxZIndex() + 1);
            this.menuDiv.style.top = top + 'px';
            this.menuDiv.style.left = left + 'px';
            window.addEventListener('click', function (event) {
                _this.closeMenuDiv();
            }, { once: true });
        }
        closeMenuDiv() {
            if (this.menuDiv) {
                this.menuDiv.parentNode.removeChild(this.menuDiv);
                this.menuDiv = null;
            }
        }
        openPreview() {
            let _this = this;
            let parentNode = getCurrentWindow().document.body;
            this.preview = document.createElement('img');
            this.preview.src = this.img.src;
            this.preview.addEventListener('click', function (event) {
                _this.closePreview();
            });
            this.blocker = new duice.Blocker(parentNode);
            this.blocker.getBlockDiv().addEventListener('click', function (event) {
                _this.closePreview();
            });
            this.blocker.block();
            this.preview.style.position = 'absolute';
            this.preview.style.zIndex = String(getCurrentMaxZIndex() + 2);
            parentNode.appendChild(this.preview);
            this.setPositionCentered(this.preview);
        }
        closePreview() {
            if (this.preview) {
                this.blocker.unblock();
                this.preview.parentNode.removeChild(this.preview);
                this.preview = null;
            }
        }
        changeImage() {
            let _this = this;
            let input = document.createElement('input');
            input.setAttribute("type", "file");
            input.setAttribute("accept", "image/gif, image/jpeg, image/png");
            input.addEventListener('change', function (e) {
                let fileReader = new FileReader();
                if (this.files && this.files[0]) {
                    fileReader.addEventListener("load", function (event) {
                        return __awaiter(this, void 0, void 0, function* () {
                            let value = event.target.result;
                            if (_this.size) {
                                value = yield _this.convertImage(value, _this.size.width, _this.size.height);
                            }
                            else {
                                value = yield _this.convertImage(value);
                            }
                            _this.value = value;
                            _this.img.src = value;
                            _this.setChanged();
                            _this.notifyObservers(_this);
                        });
                    });
                    fileReader.readAsDataURL(this.files[0]);
                }
                e.preventDefault();
                e.stopPropagation();
            });
            input.click();
        }
        convertImage(dataUrl, width, height) {
            return new Promise(function (resolve, reject) {
                try {
                    let canvas = document.createElement("canvas");
                    let ctx = canvas.getContext("2d");
                    let image = new Image();
                    image.onload = function () {
                        if (width && height) {
                            canvas.width = width;
                            canvas.height = height;
                            ctx.drawImage(image, 0, 0, width, height);
                        }
                        else {
                            canvas.width = image.naturalWidth;
                            canvas.height = image.naturalHeight;
                            ctx.drawImage(image, 0, 0);
                        }
                        let dataUrl = canvas.toDataURL("image/png");
                        resolve(dataUrl);
                    };
                    image.src = dataUrl;
                }
                catch (e) {
                    reject(e);
                }
            });
        }
        clearImage() {
            this.value = null;
            this.setChanged();
            this.notifyObservers(this);
        }
    }
    duice.Img = Img;
    class TableFactory extends ListComponentFactory {
        getSelector() {
            return `table[is="${getAlias()}-table"]`;
        }
        getComponent(element) {
            let table = new Table(element);
            table.setSelectable(element.dataset[`${getAlias()}Selectable`] === 'true');
            table.setEditable(element.dataset[`${getAlias()}Editable`] === 'true');
            let bind = element.dataset[`${getAlias()}Bind`].split(',');
            table.bind(this.getContextProperty(bind[0]), bind[1]);
            return table;
        }
    }
    duice.TableFactory = TableFactory;
    class Table extends ListComponent {
        constructor(table) {
            super(table);
            this.tbodies = new Array();
            this.table = table;
            this.addClass(this.table, 'duice-table');
            let caption = this.table.querySelector('caption');
            if (caption) {
                caption = this.executeExpression(caption, new Object());
                duice.initializeComponent(caption, new Object());
            }
            let thead = this.table.querySelector('thead');
            if (thead) {
                thead.classList.add('duice-table__thead');
                thead.querySelectorAll('tr').forEach(function (tr) {
                    tr.classList.add('duice-table__thead-tr');
                });
                thead.querySelectorAll('th').forEach(function (th) {
                    th.classList.add('duice-table__thead-tr-th');
                });
                thead = this.executeExpression(thead, new Object());
                duice.initializeComponent(thead, new Object());
            }
            let tbody = this.table.querySelector('tbody');
            this.tbody = tbody.cloneNode(true);
            this.tbody.classList.add('duice-table__tbody');
            this.tbody.querySelectorAll('tr').forEach(function (tr) {
                tr.classList.add('duice-table__tbody-tr');
            });
            this.tbody.querySelectorAll('td').forEach(function (th) {
                th.classList.add('duice-table__tbody-tr-td');
            });
            this.table.removeChild(tbody);
            let tfoot = this.table.querySelector('tfoot');
            if (tfoot) {
                tfoot.classList.add('duice-table__tfoot');
                tfoot.querySelectorAll('tr').forEach(function (tr) {
                    tr.classList.add('duice-table__tfoot-tr');
                });
                tfoot.querySelectorAll('td').forEach(function (td) {
                    td.classList.add('duice-table__tfoot-tr-td');
                });
                tfoot = this.executeExpression(tfoot, new Object());
                duice.initializeComponent(tfoot, new Object());
            }
        }
        setSelectable(selectable) {
            this.selectable = selectable;
        }
        setEditable(editable) {
            this.editable = editable;
        }
        update(list, obj) {
            if (obj instanceof duice.Map) {
                return;
            }
            let _this = this;
            for (let i = 0; i < this.tbodies.length; i++) {
                this.table.removeChild(this.tbodies[i]);
            }
            this.tbodies.length = 0;
            for (let index = 0; index < list.getRowCount(); index++) {
                let map = list.getRow(index);
                let tbody = this.createTbody(index, map);
                tbody.dataset.duiceIndex = String(index);
                if (this.selectable) {
                    tbody.classList.add('duice-table__tbody--selectable');
                    if (index === list.getIndex()) {
                        tbody.classList.add('duice-table__tbody--index');
                    }
                    tbody.addEventListener('click', function (event) {
                        return __awaiter(this, void 0, void 0, function* () {
                            let index = Number(this.dataset.duiceIndex);
                            yield _this.selectTbody(index);
                        });
                    }, true);
                }
                if (this.editable) {
                    tbody.setAttribute('draggable', 'true');
                    tbody.addEventListener('dragstart', function (event) {
                        event.dataTransfer.setData("text", this.dataset.duiceIndex);
                    });
                    tbody.addEventListener('dragover', function (event) {
                        event.preventDefault();
                        event.stopPropagation();
                    });
                    tbody.addEventListener('drop', function (event) {
                        return __awaiter(this, void 0, void 0, function* () {
                            event.preventDefault();
                            event.stopPropagation();
                            let fromIndex = parseInt(event.dataTransfer.getData('text'));
                            let toIndex = parseInt(this.dataset.duiceIndex);
                            yield list.moveRow(fromIndex, toIndex);
                        });
                    });
                }
                this.table.appendChild(tbody);
                this.tbodies.push(tbody);
            }
            if (list.getRowCount() < 1) {
                let emptyTbody = this.createEmptyTbody();
                emptyTbody.style.pointerEvents = 'none';
                this.table.appendChild(emptyTbody);
                this.tbodies.push(emptyTbody);
            }
        }
        selectTbody(index) {
            return __awaiter(this, void 0, void 0, function* () {
                this.getList().suspendNotify();
                yield this.getList().selectRow(index);
                for (let i = 0; i < this.tbodies.length; i++) {
                    if (i === index) {
                        this.tbodies[i].classList.add('duice-table__tbody--index');
                    }
                    else {
                        this.tbodies[i].classList.remove('duice-table__tbody--index');
                    }
                }
                this.getList().resumeNotify();
            });
        }
        createTbody(index, map) {
            let _this = this;
            let tbody = this.tbody.cloneNode(true);
            tbody.classList.add('duice-table__tbody');
            let $context = new Object;
            $context['index'] = index;
            $context[this.item] = map;
            tbody = this.executeExpression(tbody, $context);
            duice.initializeComponent(tbody, $context);
            return tbody;
        }
        createEmptyTbody() {
            let emptyTbody = this.tbody.cloneNode(true);
            this.removeChildNodes(emptyTbody);
            emptyTbody.classList.add('duice-table__tbody--empty');
            let tr = document.createElement('tr');
            tr.classList.add('duice-table__tbody-tr');
            let td = document.createElement('td');
            td.classList.add('duice-table__tbody-tr-td');
            let colspan = 0;
            let childNodes = this.tbody.querySelector('tr').children;
            for (let i = 0; i < childNodes.length; i++) {
                if (childNodes[i].tagName === 'TH' || childNodes[i].tagName === 'TD') {
                    colspan++;
                }
            }
            td.setAttribute('colspan', String(colspan));
            let emptyMessage = document.createElement('div');
            emptyMessage.style.textAlign = 'center';
            emptyMessage.classList.add('duice-table__tbody--empty-message');
            td.appendChild(emptyMessage);
            tr.appendChild(td);
            emptyTbody.appendChild(tr);
            return emptyTbody;
        }
    }
    duice.Table = Table;
    class UlFactory extends ListComponentFactory {
        getSelector() {
            return `ul[is="${getAlias()}-ul"]`;
        }
        getComponent(element) {
            let ul = new Ul(element);
            let selectable = element.dataset[`${getAlias()}Selectable`];
            ul.setSelectable(selectable === 'true');
            let editable = element.dataset[`${getAlias()}Editable`];
            ul.setEditable(editable === 'true');
            let hierarchy = element.dataset[`${getAlias()}Hierarchy`];
            if (hierarchy) {
                let hierarchys = hierarchy.split(',');
                ul.setHierarchy(hierarchys[0], hierarchys[1]);
            }
            let foldable = element.dataset[`${getAlias()}Foldable`];
            ul.setFoldable(foldable === 'true');
            let bind = element.dataset[`${getAlias()}Bind`];
            let binds = bind.split(',');
            ul.bind(this.getContextProperty(binds[0]), binds[1]);
            return ul;
        }
    }
    duice.UlFactory = UlFactory;
    class Ul extends ListComponent {
        constructor(ul) {
            super(ul);
            this.lis = new Array();
            this.foldName = {};
            this.ul = ul;
            this.addClass(this.ul, 'duice-ul');
            let li = ul.querySelector('li');
            let childUl = li.querySelector('li > ul');
            if (childUl) {
                this.childUl = li.removeChild(childUl);
            }
            else {
                this.childUl = document.createElement('ul');
            }
            this.li = li.cloneNode(true);
        }
        setSelectable(selectable) {
            this.selectable = selectable;
        }
        setEditable(editable) {
            this.editable = editable;
        }
        setHierarchy(idName, parentIdName) {
            this.hierarchy = { idName: idName, parentIdName: parentIdName };
        }
        setFoldable(foldable) {
            this.foldable = foldable;
        }
        update(list, obj) {
            if (obj instanceof duice.Map) {
                return;
            }
            let _this = this;
            this.ul.innerHTML = '';
            this.lis.length = 0;
            this.ul.style.listStyle = 'none';
            this.ul.style.paddingLeft = '0px';
            if (this.hierarchy) {
                this.createHierarchyRoot();
            }
            for (let index = 0; index < list.getRowCount(); index++) {
                let map = list.getRow(index);
                let path = [];
                if (this.hierarchy) {
                    if (isNotEmpty(map.get(this.hierarchy.parentIdName))) {
                        continue;
                    }
                }
                let li = this.createLi(index, map, Number(0));
                if (this.selectable) {
                    li.classList.add('duice-ul__li--selectable');
                }
                this.ul.appendChild(li);
            }
            if (this.hierarchy) {
                for (let index = 0, size = list.getRowCount(); index < size; index++) {
                    if (this.isLiCreated(index) === false) {
                        let orphanLi = this.createLi(index, list.getRow(index), Number(0));
                        orphanLi.classList.add('duice-ul__li--orphan');
                        this.ul.appendChild(orphanLi);
                    }
                }
            }
        }
        createHierarchyRoot() {
            let depth = 0;
            if (this.editable)
                depth += 24;
            if (this.foldable)
                depth += 24;
            if (depth > 0) {
                this.ul.style.paddingLeft = depth + 'px';
            }
            if (this.editable) {
                let _this = this;
                if (this.ul.classList.contains('duice-ul--root')) {
                    return;
                }
                this.ul.classList.add('duice-ul--root');
                this.ul.addEventListener('dragover', function (event) {
                    event.preventDefault();
                    event.stopPropagation();
                    _this.ul.classList.add('duice-ul--root-dragover');
                });
                this.ul.addEventListener('dragleave', function (event) {
                    event.preventDefault();
                    event.stopPropagation();
                    _this.ul.classList.remove('duice-ul--root-dragover');
                });
                this.ul.addEventListener('drop', function (event) {
                    return __awaiter(this, void 0, void 0, function* () {
                        event.preventDefault();
                        event.stopPropagation();
                        let fromIndex = parseInt(event.dataTransfer.getData('text'));
                        yield _this.moveLi(fromIndex, -1);
                    });
                });
            }
        }
        createLi(index, map, depth) {
            let _this = this;
            let li = this.li.cloneNode(true);
            li.classList.add('duice-ui-ul__li');
            let $context = new Object;
            $context['index'] = index;
            $context['depth'] = Number(depth);
            $context['hasChild'] = (this.hierarchy ? this.hasChild(map) : false);
            $context[this.item] = map;
            li = this.executeExpression(li, $context);
            duice.initializeComponent(li, $context);
            this.lis.push(li);
            li.dataset.duiceIndex = String(index);
            if (this.selectable) {
                if (index === this.getList().getIndex()) {
                    li.classList.add('duice-ul__li--index');
                }
                li.addEventListener('click', function (event) {
                    return __awaiter(this, void 0, void 0, function* () {
                        let index = Number(this.dataset.duiceIndex);
                        event.stopPropagation();
                        yield _this.selectLi(index, this);
                    });
                });
            }
            if (this.editable) {
                li.setAttribute('draggable', 'true');
                li.addEventListener('dragstart', function (event) {
                    event.stopPropagation();
                    event.dataTransfer.setData("text", this.dataset.duiceIndex);
                });
                li.addEventListener('dragover', function (event) {
                    event.preventDefault();
                    event.stopPropagation();
                });
                li.addEventListener('drop', function (event) {
                    return __awaiter(this, void 0, void 0, function* () {
                        event.preventDefault();
                        event.stopPropagation();
                        let fromIndex = parseInt(event.dataTransfer.getData('text'));
                        let toIndex = parseInt(this.dataset.duiceIndex);
                        yield _this.moveLi(fromIndex, toIndex);
                    });
                });
            }
            if (this.hierarchy) {
                depth++;
                let childUl = this.childUl.cloneNode(true);
                childUl.classList.add('duice-ul');
                $context['depth'] = Number(depth);
                childUl = this.executeExpression(childUl, $context);
                let hasChild = false;
                let hierarchyIdValue = map.get(this.hierarchy.idName);
                for (let i = 0, size = this.list.getRowCount(); i < size; i++) {
                    let element = this.list.getRow(i);
                    let hierarchyParentIdValue = element.get(this.hierarchy.parentIdName);
                    if (!isEmpty(hierarchyParentIdValue)
                        && hierarchyParentIdValue === hierarchyIdValue) {
                        let childLi = this.createLi(i, element, Number(depth));
                        childUl.appendChild(childLi);
                        hasChild = true;
                    }
                }
                if (hasChild) {
                    li.appendChild(childUl);
                }
                if (this.foldable === true) {
                    if (hasChild) {
                        if (this.isFoldLi(map)) {
                            this.foldLi(map, li, true);
                        }
                        else {
                            this.foldLi(map, li, false);
                        }
                        li.addEventListener('click', function (event) {
                            event.preventDefault();
                            event.stopPropagation();
                            if (event.target === this) {
                                if (_this.isFoldLi(map)) {
                                    _this.foldLi(map, this, false);
                                }
                                else {
                                    _this.foldLi(map, this, true);
                                }
                            }
                        });
                    }
                    else {
                        this.foldLi(map, li, false);
                    }
                }
            }
            return li;
        }
        selectLi(index, li) {
            return __awaiter(this, void 0, void 0, function* () {
                this.getList().suspendNotify();
                yield this.getList().selectRow(index);
                for (let i = 0; i < this.lis.length; i++) {
                    this.lis[i].classList.remove('duice-ul__li--index');
                }
                li.classList.add('duice-ul__li--index');
                this.getList().resumeNotify();
            });
        }
        hasChild(map) {
            let hierarchyIdValue = map.get(this.hierarchy.idName);
            for (let i = 0, size = this.list.getRowCount(); i < size; i++) {
                let element = this.list.getRow(i);
                let hierarchyParentIdValue = element.get(this.hierarchy.parentIdName);
                if (!isEmpty(hierarchyParentIdValue)
                    && hierarchyParentIdValue === hierarchyIdValue) {
                    return true;
                }
            }
            return false;
        }
        isLiCreated(index) {
            for (let i = 0, size = this.lis.length; i < size; i++) {
                if (parseInt(this.lis[i].dataset.duiceIndex) === index) {
                    return true;
                }
            }
            return false;
        }
        isFoldLi(map) {
            if (this.foldName[map.get(this.hierarchy.idName)] === true) {
                return true;
            }
            else {
                return false;
            }
        }
        foldLi(map, li, fold) {
            if (fold) {
                this.foldName[map.get(this.hierarchy.idName)] = true;
                li.classList.remove('duice-ul__li--unfold');
                li.classList.add('duice-ul__li--fold');
            }
            else {
                this.foldName[map.get(this.hierarchy.idName)] = false;
                li.classList.remove('duice-ul__li--fold');
                li.classList.add('duice-ul__li--unfold');
            }
        }
        moveLi(fromIndex, toIndex) {
            return __awaiter(this, void 0, void 0, function* () {
                if (fromIndex === toIndex) {
                    return;
                }
                let sourceRow = this.list.getRow(fromIndex);
                let targetRow = this.list.getRow(toIndex) || null;
                if (this.hierarchy) {
                    if (this.isCircularReference(targetRow, sourceRow.get(this.hierarchy.idName))) {
                        throw 'Not allow to movem, becuase of Circular Reference.';
                    }
                    if (this.list.eventListener.onBeforeMoveRow) {
                        if ((yield this.list.eventListener.onBeforeMoveRow.call(this.list, sourceRow, targetRow)) === false) {
                            throw 'canceled';
                        }
                    }
                    yield sourceRow.set(this.hierarchy.parentIdName, targetRow === null ? null : targetRow.get(this.hierarchy.idName));
                    if (this.list.eventListener.onAfterMoveRow) {
                        yield this.list.eventListener.onAfterMoveRow.call(this.list, sourceRow, targetRow);
                    }
                    this.setChanged();
                    this.notifyObservers(null);
                }
                else {
                    yield this.list.moveRow(fromIndex, toIndex);
                }
            });
        }
        getParentMap(map) {
            let parentIdValue = map.get(this.hierarchy.parentIdName);
            for (let i = 0, size = this.list.getRowCount(); i < size; i++) {
                let element = this.list.getRow(i);
                if (element.get(this.hierarchy.idName) === parentIdValue) {
                    return element;
                }
            }
            return null;
        }
        isCircularReference(map, idValue) {
            let parentMap = map;
            while (parentMap !== null) {
                parentMap = this.getParentMap(parentMap);
                if (parentMap === null) {
                    return false;
                }
                if (parentMap.get(this.hierarchy.idName) === idValue) {
                    return true;
                }
            }
        }
    }
    duice.Ul = Ul;
    addComponentFactory(new TableFactory());
    addComponentFactory(new UlFactory());
    addComponentFactory(new InputFactory());
    addComponentFactory(new SelectFactory());
    addComponentFactory(new TextareaFactory());
    addComponentFactory(new ImgFactory());
    addComponentFactory(new SpanFactory());
    addComponentFactory(new DivFactory());
    addComponentFactory(new ScriptletFactory());
})(duice || (duice = {}));
document.addEventListener("DOMContentLoaded", function (event) {
    duice.initializeComponent(document, {});
});
