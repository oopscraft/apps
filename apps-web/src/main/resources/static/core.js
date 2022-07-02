// set alias for duice library
var core = duice;
core.setAlias('core');

/**
 * JQUERY AJAX setting
 */
$(document).ajaxStart(function(event) {
	console.debug('$(document).ajaxStart',event);
	NProgress.start();
});
// If not configure, "Provisional headers are shown" error found in CHROME.
$(document).ajaxSend(function(event, jqXHR, settings) {
	console.debug('$(document).ajaxSend',event,jqXHR,settings);
	let csrfToken = _getCookie('X-Csrf-Token');
	jqXHR.setRequestHeader('X-Csrf-Token', csrfToken);
	jqXHR.setRequestHeader('Cache-Control','no-cache, no-store, must-revalidate');
	jqXHR.setRequestHeader('Pragma','no-cache');
	jqXHR.setRequestHeader('Expires','0');
});
// Checks error except cancellation(readyState = 0)
$(document).ajaxError(function(event, jqXHR, settings, thrownError){
	console.debug('$(document).ajaxError', jqXHR);
	if(settings.suppressErrors){
		return;
	}
	if(jqXHR.readyState > 0){
		console.error(event, jqXHR, settings, thrownError);
		_alert('<span style="font-weight:bold; color:red;">' + jqXHR.responseText + '</span>');
	}
});
// Checks
$(document).ajaxSuccess(function(event, jqXHR, settings){
	console.debug('$(document).ajaxSuccess',event,jqXHR,settings);
});
// Checks
$(document).ajaxComplete(function(event, jqXHR, settings){
	console.debug('$(document).ajaxComplete',event,jqXHR,settings);
});
// checks stop event
$(document).ajaxStop(function(event) {
	console.debug('$(document).ajaxStop',event);
	NProgress.done();
});

/**
 * _fetch
 */
const _fetch = async function(url, options, _bypass) {
	console.log(url, options);
	if(!options){
		options = new Object();
	}
	if(!options.headers){
		options.headers = new Object();
	} 
	let csrfToken = _getCookie('X-Csrf-Token');
	options.headers['X-Csrf-Token'] = csrfToken;
	options.headers['Cache-Control'] = 'no-cache, no-store, must-revalidate';
	options.headers['Pragma'] = 'no-cache';
	options.headers['Expires'] = '0';	
	NProgress.start();
	return fetch(url, options)
	.then(async function(response){
		console.log(response);
		NProgress.done();
		
		// skip _bypass
		if(_bypass){
			return response;
		}
		
		// checks response
		if(response.ok) {
			return response;
		}else{
			let responseJson = await response.json();
			console.log(responseJson);
			let message = responseJson.message;
			await _alert(message);
			throw Error(message);
		}
	})
	.catch((error)=>{
		NProgress.done();
		throw Error(error);
	});
}

/**
 * Opens confirm dialog
 */
const _confirm = function(message) {
	return new core.Confirm(message).open();
}

/**
 * Opens alert dialog
 */
const _alert = function(message) {
	return new core.Alert(message).open();
}

/**
 * Opens prompt dialog
 */
const _prompt = function(message){
	return new core.Prompt(message).open();
}

/**
 * Parsed total count from Content-Range header
 * @Param {Response} response
 */
const _getTotalCount = function(response){
	var totalCount = -1;
	var contentRange = response.headers.get("Content-Range");
	try {
    	var totalCount = contentRange.split(' ')[1].split('/')[1];
		totalCount = parseInt(totalCount);
		if(isNaN(totalCount)){
			return -1;
		}
		return totalCount;
	}catch(e){
		console.error(e);
		return -1;
	}
}

/**
 * Opens link
 */
const _openLink = function(linkUrl, linkTarget){
	if(linkTarget === '_blank'){
		window.open(linkUrl,'_blank');
	}else{
		window.location.href = linkUrl;
	}
}

/**
 * Changes locale
 */ 
const _changeLocale = function(locale){
	let url = new URL(document.location.href);
	url.searchParams.delete('_locale');
	url.searchParams.append('_locale', locale);
	document.location.href = url;
}

/**
 * Gets cookie value
 * @param name 
 */
function _getCookie(name) {
    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return value? value[2] : null;
}

/**
 * Sets cookie value
 * @param name
 * @param value 
 * @param day 
 */
function _setCookie(name, value, day) {
    var date = new Date();
    date.setTime(date.getTime() + day * 60 * 60 * 24 * 1000);
    document.cookie = name + '=' + value + ';expires=' + date.toUTCString() + ';path=/';
}

/**
 * Deletes cookie
 * @param name 
 */
function _deleteCookie(name) {
    var date = new Date();
    document.cookie = name + "= " + "; expires=" + date.toUTCString() + "; path=/";
}

/**
 * Checks is empty
 */
function _isEmpty(value){
	if(!value || (value+'').trim().length < 1){
		return true;
	}else{
		return false;
	}
}

// _convertReadableFileSize
function _convertReadableFileSize(bytes, decimalPoint) {
	if (bytes == 0) return '0 Bytes';
	let k = 1000,
		dm = decimalPoint || 2,
		sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
		i = Math.floor(Math.log(bytes) / Math.log(k));
	return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

/**
 * Checks value is number
 * @param value
 */
function _isNumeric(value) {
    return !Array.isArray( value ) && (value - parseFloat(value) + 1) >= 0;
}

/**
 * Checks generic ID (alphabet + number + -,_)
 * @param value 
 */
function _isIdFormat(value) {
    if(value){
        var pattern = /^[a-zA-Z0-9\-\_]{1,}$/;
        return pattern.test(value);
    }
    return false;
}

/**
 * Checks generic password (At least 1 alphabet, 1 number, 1 special char)
 * @param value 
 */
function _isPasswordFormat(value) {
    if(value){
        var pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,}$/;
        return pattern.test(value);
    }
    return false;
}

/**
 * Checks valid email address pattern
 * @param value 
 */
function _isEmailFormat(value) {
    if(value){
        var pattern = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
        return pattern.test(value);
    }
    return false;
}

/**
 * Checks if value is URL address format
 * @param value 
 */
function _isUrlFormat(value) {
    if(value){
        var pattern = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
        return pattern.test(value);
    }
    return false;
}
