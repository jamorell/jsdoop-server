
//(gradients, names, url_gradients, id_job, age_model, username, id_task, start_time ):
//function save_gradients_http(idJob, ageModel, grads, idTask, startTime) {
function save_gradients_http(grads, url_gradients, id_job, age_model, username, id_task, info_worker, startTime ) {
	return new Promise(async (resolve, reject) => {
    print("saving gradients")
		//gradients
		//layers
		const layers = Object.keys(grads);
		
		var formData = new FormData();
		console.log("layers.length = " + layers.length)
		for (let i = 0; i < layers.length; i++) {
			formData.append("layers", new Blob([layers[i]], { type: "plain/text"}));
			const npyserialized = await npyserialize(grads[layers[i]]);
			console.log("npyserialized = " + npyserialized)
			console.log("npyserialized.byteLength = " + npyserialized.byteLength)
			formData.append("gradients", new Blob([npyserialized], {type:"application/octet-stream"}))
			
		}

		console.log("formdata = " + JSON.stringify(formData))
		const formDataKeys = formData.keys()
		for (var key of formData.keys()) {
		   console.log("formData key = " + key); 
		}
		
		
	    var xhr = new XMLHttpRequest();
	    
	    let executionTime = new Date().getTime() - startTime;
      xhr.open("POST", url_gradients + '?id_job=' + id_job + "&age_model=" + age_model + "&info_worker=" + info_worker + "&username=" + username + "&id_task=" + id_task  + "&execution_time=" + executionTime, true);
	    //xhr.open("POST", 'http://' + thehost + ':' + theport + '/gradients?id_job=' + idJob + "&age_model=" + ageModel + "&info_worker=" + infoWorker + "&username=" + username + "&id_task=" + idTask  + "&id_task=" + idTask + "&execution_time=" + executionTime, true);

	    
	    xhr.onload = function (v) {
	    	print("" + testingId +  "******saving grads response " + xhr.response)
	    	print("xhr.response.status " + xhr.response.status)
// 	    	if (xhr.response.status == 500) {
// 	    		process.exit()
	    	
// 	    	}
	    	print("v = " + v)
			resolve(xhr.getResponseHeader('current_age'));	    

	    };
	    xhr.onerror = function (e) {
	       reject(e);
	    };
	    const testingId = Math.random();
	    console.log("" + testingId + "******sending formData = " + formData)
	    xhr.send(formData);
	});	
}
