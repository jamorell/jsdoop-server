async function load_job(url, id_job) {
	return new Promise((resolve, reject) => {
	    var xhr = new XMLHttpRequest();


	    xhr.open("GET", url + "?id_job=" + id_job, true);
	    xhr.overrideMimeType("application/octet-stream");
	    xhr.responseType = "arraybuffer";
	    xhr.onload = function (v) {
	    	console.debug("response " + xhr.response)
        var enc = new TextDecoder("utf-8");
        console.debug(enc.decode(xhr.response));
	    	
			  resolve(JSON.parse(enc.decode(xhr.response)));	    
	    };
	    xhr.onerror = function (e) {
         console.debug(JSON.stringify(e))
	       reject(e);
	    };
	    xhr.send();
	});
}
