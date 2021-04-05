async function load_stats(url_stats, id_job)
{
	return new Promise((resolve, reject) => {
	    var xhr = new XMLHttpRequest();
      const final_url = url_stats + "?" + "id_job=" + id_job 
      console.log("final_url = " + final_url)

	    xhr.open("GET", final_url, true);
	    xhr.overrideMimeType("application/json");
	    xhr.onload = function (v) {
	    	const values = JSON.parse(xhr.responseText);
			  resolve(values);	    
	    };
	    xhr.onerror = function (e) {
         console.log(JSON.stringify(e))
	       reject(e);
	    };
	    xhr.send();
	});

/**
	return new Promise((resolve, reject) => {
	    var xhr = new XMLHttpRequest();

      xhr.open('GET', url_stats , true);

			//xhr.onreadystatechange = function (aEvt) {
			xhr.onreadystatechange = function (aEvt) {
			  if (xhr.readyState == 4) {  
			    if(xhr.status == 200) {
			        //console.debug(xhr.responseText);
			        //let values = [{"username":"worker_py_5","gradients":171},{"username":"worker_py_3","gradients":130},{"username":"worker_py_1","gradients":89},{"username":"worker_py_14","gradients":86},{"username":"worker_py_7","gradients":85},{"username":"worker_py_10","gradients":81},{"username":"worker_py_12","gradients":81},{"username":"worker_py_6","gradients":76},{"username":"worker_py_11","gradients":76},{"username":"worker_py_13","gradients":74},{"username":"worker_py_9","gradients":72},{"username":"worker_py_8","gradients":72},{"username":"worker_py_15","gradients":70},{"username":"worker_py_2","gradients":68},{"username":"worker_py_0","gradients":61},{"username":"worker_py_4","gradients":59}];
			        let values = JSON.parse(xhr.responseText);

              resolve(values);
			  	} else {
			        console.debug("Error loading page\n" + JSON.stringify(xhr) + JSON.stringify(aEvt));
			        resolve(null);
			    }     
			  }
			};

	    xhr.onerror = function (e) {
         console.log(JSON.stringify(e))
	       reject(e);
	    };
			xhr.send(); 
	});
**/
}
