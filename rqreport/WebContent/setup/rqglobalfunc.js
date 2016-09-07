rqglobalfunc = {}
rqglobalfunc.rqglobal = function(){
	enc64List = new Array();
    dec64List = new Array();
    
    //version = parseFloat(navigator.appVersion);
	//AppName = (navigator.appName);
	msg = '';
	browserName = "UnKnown";
	browserVersion = jQuery.browser.version;
	if ($.browser.safari) { //safari, opera, msie, mozilla
    	browserName = "Safari"; 
  	}else if($.browser.opera){
  		browserName = "Opera"; 
  	}else if($.browser.msie){
  		browserName = "Internet Explorer"; 
	}else if($.browser.mozilla){
		browserName = "Mozilla";
	}
}
rqglobalfunc.rqglobal.prototype = {
  	///////////////////// base 64 ////////////////////////////////////////
  	initBase64 : function(){
	    var i;
	    for (i = 0; i < 26; i++) {
	        enc64List[enc64List.length] = String.fromCharCode(65 + i);
	    }
	    for (i = 0; i < 26; i++) {
	        enc64List[enc64List.length] = String.fromCharCode(97 + i);
	    }
	    for (i = 0; i < 10; i++) {
	        enc64List[enc64List.length] = String.fromCharCode(48 + i);
	    }
	    enc64List[enc64List.length] = "+";
	    enc64List[enc64List.length] = "/";
	    for (i = 0; i < 128; i++) {
	        dec64List[dec64List.length] = -1;
	    }
	    for (i = 0; i < 64; i++) {
	        dec64List[enc64List[i].charCodeAt(0)] = i;
	    }
  	},  	
  	base64Encode : function(str) {
  		var c, d, e, end = 0;
	    var u, v, w, x;
	    var ptr = -1;
	    var input = str.split("");
	    var output = "";
	    while(end == 0) {
	        c = (typeof input[++ptr] != "undefined") ? input[ptr].charCodeAt(0) :
	            ((end = 1) ? 0 : 0);
	        d = (typeof input[++ptr] != "undefined") ? input[ptr].charCodeAt(0) :
	            ((end += 1) ? 0 : 0);
	        e = (typeof input[++ptr] != "undefined") ? input[ptr].charCodeAt(0) :
	            ((end += 1) ? 0 : 0);
	        u = enc64List[c >> 2];
	        v = enc64List[(0x00000003 & c) << 4 | d >> 4];
	        w = enc64List[(0x0000000F & d) << 2 | e >> 6];
	        x = enc64List[e & 0x0000003F];
	        if (end >= 1) {x = "=";}
	        if (end == 2) {w = "=";}
	        if (end < 3) {output += u + v + w + x;}
	    }
	    var formattedOutput = "";
	    var lineLength = 76;
	    while (output.length > lineLength) {
	     formattedOutput += output.substring(0, lineLength) + "\n";
	     output = output.substring(lineLength);
	    }
	    formattedOutput += output;
	    return formattedOutput;
  	},  	
  	base64Decode : function(str){
	  	var c=0, d=0, e=0, f=0, i=0, n=0;
	    var input = str.split("");
	    var output = "";
	    var ptr = 0;
	    do {
	        f = input[ptr++].charCodeAt(0);
	        i = dec64List[f];
	        if ( f >= 0 && f < 128 && i != -1 ) {
	            if ( n % 4 == 0 ) {
	                c = i << 2;
	            } else if ( n % 4 == 1 ) {
	                c = c | ( i >> 4 );
	                d = ( i & 0x0000000F ) << 4;
	            } else if ( n % 4 == 2 ) {
	                d = d | ( i >> 2 );
	                e = ( i & 0x00000003 ) << 6;
	            } else {
	                e = e | i;
	            }
	            n++;
	            if ( n % 4 == 0 ) {
	                output += String.fromCharCode(c) +
	                          String.fromCharCode(d) +
	                          String.fromCharCode(e);
	            }
	        }
	    }
	    while (typeof input[ptr] != "undefined");
	    output += (n % 4 == 3) ? String.fromCharCode(c) + String.fromCharCode(d) :
	              ((n % 4 == 2) ? String.fromCharCode(c) : "");
	    return output;
  	},
  	// http://simon.incutio.com /////
  	addLoadEvent : function(func){
	  	var oldonload = window.onload;
		if (typeof window.onload != 'function') {
			window.onload = func;
		} else {
		    window.onload = function() {
			    oldonload();
			    func();
			}
		}
  	}
}
