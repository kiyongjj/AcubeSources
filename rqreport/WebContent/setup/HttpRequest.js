xhr = {};
xhr.Request = function() {
	this.httpRequest = null;
}
xhr.Request.prototype = {
	getXMLHttpRequest : function () {
		if (window.ActiveXObject) {
			try {
				return new ActiveXObject("Msxml2.XMLHTTP");
			} catch(e) {
				try {
					return new ActiveXObject("Microsoft.XMLHTTP");
				} catch(e1) { return null; }
			}
		} else if (window.XMLHttpRequest) {
			return new XMLHttpRequest();
		} else {
			return null;
		}
	},
	sendRequest : function(url, params, callback, method) {
		this.httpRequest = this.getXMLHttpRequest();
		
		var httpMethod = method ? method : 'GET';
		if (httpMethod != 'GET' && httpMethod != 'POST') {
			httpMethod = 'GET';
		}
		var httpParams = (params == null || params == '') ? null : params;
		var httpUrl = url;
		if (httpMethod == 'GET' && httpParams != null) {
			httpUrl = httpUrl + "?" + httpParams;
		}
		this.httpRequest.open(httpMethod, httpUrl, true);  // false : synchronous , true : asynchronous
		this.httpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		this.httpRequest.onreadystatechange = callback;
		this.httpRequest.send(httpMethod == 'POST' ? httpParams : null);
	}
}