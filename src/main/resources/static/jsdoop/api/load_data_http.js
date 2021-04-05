async function load_data_http(url_data_server, id_task, username, info_worker, id_job, key)
{
	return new Promise((resolve, reject) => {
	    var xhr = new XMLHttpRequest();
      const final_url = url_data_server + "?" + "id_task=" + id_task 
+ "&username=" + username + 
"&info_worker=" + info_worker + "&id_job=" + id_job + "&key=" + key
      console.log("final_url = " + final_url)

	    xhr.open("GET",final_url,true);
	    xhr.overrideMimeType("application/octet-stream");
	    xhr.responseType = "arraybuffer";
	    xhr.onload = function (v) {
	    	//console.log("response " + xhr.response)
        var enc = new TextDecoder("utf-8");
        //console.log(enc.decode(xhr.response));
	    	const dataset = new Float32Array(xhr.response);//new Float32Array(xhr.response); //new Uint8Array(xhr.response); //
			  resolve(dataset);	    
	    };
	    xhr.onerror = function (e) {
         console.log(JSON.stringify(e))
	       reject(e);
	    };
	    xhr.send();
	});
}
