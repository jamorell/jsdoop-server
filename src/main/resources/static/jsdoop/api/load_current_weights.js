function find_csa(arr, subarr, from_index) {
    var i = from_index >>> 0,
        sl = subarr.length,
        l = arr.length + 1 - sl;

    loop: for (; i<l; i++) {
        for (var j=0; j<sl; j++)
            if (arr[i+j] !== subarr[j])
                continue loop;
        return i;
    }
    return -1;
}


function stringToBytes(s) {
  const boundaryBytes = [];
  for (var i = 0; i < s.length; i++){  
      boundaryBytes.push(s.charCodeAt(i));
  }
  return boundaryBytes;
}


function getNextElement(layers, weights, bytes, boundaryBytes, from) {
    console.log("getNextElement from =  " + from)
    const semicolonBytes = [59];
    const newLineBytes = [10];
    const name = "name=";
    const contentLength = "Content-Length:";
    const contentLengthBytes = stringToBytes(contentLength);
    const nameBytes = stringToBytes(name);
    
    let boundaryBegin = find_csa(bytes, boundaryBytes, from);
    console.log("boundaryBegin from =  " + boundaryBegin)
    let boundaryEnd = boundaryBegin + boundaryBytes.length;
    console.log("boundaryEnd " + boundaryEnd)
    console.log("bytes.length " + bytes.length)
    if (boundaryEnd + 4 >= bytes.length ) {
        return bytes.length;
    }
    
    let nameBegin = find_csa(bytes, nameBytes, boundaryEnd);
    let nameEnd = nameBegin + nameBytes.length;
    let nameValueEnd = find_csa(bytes, newLineBytes, nameEnd);
   //      console.log("nameEnd = " + nameEnd +"#*")
//     console.log("nameValueEnd = " + nameValueEnd +"#*")
    let nameValue = String.fromCharCode.apply(null, new Uint8Array(bytes.buffer, nameEnd, nameValueEnd - nameEnd));
    console.log("nameValue = " + nameValue +"#**")

    let contentLengthBegin = find_csa(bytes, contentLengthBytes, nameValueEnd); 
    console.log("contentLengthBegin = " + contentLengthBegin +"#**")
    let contentLengthValueEnd = find_csa(bytes, newLineBytes, contentLengthBegin);
    console.log("contentLengthValueEnd = " + contentLengthValueEnd +"#**")
    let nextBoundaryBegin = find_csa(bytes, boundaryBytes, contentLengthValueEnd);
    console.log("nextBoundaryBegin = " + nextBoundaryBegin +"#**")
    if (nextBoundaryBegin === -1 || nextBoundaryBegin < boundaryBegin) {
        nextBoundaryBegin = bytes.length -1;
    }
    console.log("nameValue " + nameValue)
    nameValue = nameValue.trim();
        console.log("nameValue " + nameValue)
    nameValue = nameValue.substring(1, nameValue.length -1);
        console.log("nameValue " + nameValue)

    
    if (nameValue == "layers") {
        const value = String.fromCharCode.apply(null, new Uint8Array(bytes.buffer, contentLengthValueEnd, nextBoundaryBegin - contentLengthValueEnd));
        console.log("value = " + value + "#")
        layers.push(value);
    } else if (nameValue == "weights") {
        const uintarray8Weights = new Uint8Array(bytes.buffer, contentLengthValueEnd, nextBoundaryBegin - contentLengthValueEnd);
        console.log("uintarray8Weights.length " + uintarray8Weights.length)
        console.log("uintarray8Weights.buffer.byteLength " + uintarray8Weights.buffer.byteLength)
        weights.push(toNpyTensor(uintarray8Weights))
        //weights.push(npyparse(uintarray8Weights.slice(uintarray8Weights.byteOffset +7 , uintarray8Weights.byteLength)))
    }
    

    return nextBoundaryBegin;

}


function deserializeMultipart(bytes, boundaryBytes, layers, weights) {
    let counter = 0;
    console.log("bytes.length " + bytes.length)
    while (counter <  bytes.length - 1) {
        counter = getNextElement(layers, weights, bytes, boundaryBytes, counter)
        console.log("counter = " + counter)
    }
    console.log("layers = " + layers)
}





async function httpGet(theUrl)
{
	return new Promise((resolve, reject) => {
	    var xhr = new XMLHttpRequest();
	    xhr.open("GET",theUrl,true);
	    xhr.overrideMimeType("application/octet-stream");
	    //xhr.setRequestHeader("Accept", "application/octet-stream");
	    xhr.responseType = "arraybuffer";
	    xhr.onload = function (v) {
	    	console.log(xhr.response)
        console.log("JSON.stringify(xhr.response) = " + JSON.stringify(xhr.response))
        console.log("JSON.stringify(xhr.getAllResponseHeaders()) = " + JSON.stringify(xhr.getAllResponseHeaders()));
        //if (xhr.getAllResponseHeaders().indexOf("current_age") >= 0) {
        //  console.log("header:", xhr.getResponseHeader("current_age"));
        //}
        var current_age = xhr.getResponseHeader('current_age');



	    	//var current_age = xhr.getResponseHeader('current_age');
	    	    var contentType = xhr.getResponseHeader('Content-Type');
	    console.log("contentType = " + contentType)
		var parts = contentType.split('boundary=');
	    var boundary = parts[1];
	    console.log("boundary =" + boundary);
	    console.log("boundary.indexOf(';charset=UTF-8') " + boundary.indexOf(';charset=UTF-8'));
	    boundary = boundary.substring(0, boundary.indexOf(';charset=UTF-8'));
	    boundary = "--" + boundary;
	    console.log("boundary = " + boundary)
	    
	    let layers = [];
	    let weights = [];

	    deserializeMultipart(new Uint8Array(xhr.response), stringToBytes(boundary), layers, weights)
	    console.log("AAAAAAAAAAAA mylayers = " + layers)
	    //layers = layers.map(layer => layer.substring(0, layer.indexOf(":")).trim())
	    layers = layers.map(layer => layer.trim())
	    //console.log("ZZZZZZZZZZZZ mylayers = " + layers)	    
	    
	    
	     console.log("mylayers = " + layers)   
		resolve([{layers, weights}, current_age])

	    };
	    xhr.onerror = function (e) {
	       reject(e);
	    };
	    xhr.send();
	});
    
}




//async function updateModel(urlCurrentWeights, model, idJob, ageModel, idTask, infoWorker) {
/**
async function load_current_weights_http(model, ageModel, urlCurrentWeights, idJob, username, idTask, infoWorker) {
	const result = await httpGet(urlCurrentWeights + "?id_job=" + idJob + "&age_model=" + ageModel + "&info_worker=" + infoWorker + "&username=" + username + "&id_task=" + idTask);
  const myweights = result[0]
  const newAgeModel = result[1]
  
	console.log(myweights)	
	const modelWeights = model.getWeights();
	console.log("JSON.stringify(myweights) = " + JSON.stringify(myweights))
	let finalWeights = {};
	for (let i = 0; i < myweights["layers"].length; i++) {
	    console.log("adding key myweights['layers'][i] " + myweights["layers"][i])
		finalWeights[myweights["layers"][i]] = myweights["weights"][i];
	}
	
	const tidyFunc = tf.tidy(() => {
		   for (let i = 0; i < modelWeights.length; i++) {
		      console.log("modelWeights[i].name " + modelWeights[i].name)
		      console.log("Object.keys(finalWeights) " + Object.keys(finalWeights))
		      	      console.log("Object.keys(finalWeights).length " + Object.keys(finalWeights).length)
		      console.log("Object.keys(finalWeights) " + Object.keys(finalWeights))
		      	      console.log("modelWeights[i].name " + modelWeights[i].name)

		      console.log("finalWeights[modelWeights[i].name] " + finalWeights["" + modelWeights[i].name])

		      console.log("modelWeights[i].shape " + modelWeights[i].shape)
		      console.log("finalWeights[modelWeights[i].name].shape " + finalWeights[modelWeights[i].name].shape)
		      modelWeights[i].assign(finalWeights[modelWeights[i].name]);
			   console.log("assigned " + i)
          finalWeights[modelWeights[i].name].dispose()
          
		     //modelWeights[i].assign(tf.tensor(loadedWeights[i]));
		     
		     //modelWeights[i].assign(myweights["weights"][i])
		     //console.log("assigned")
		   }
		   return modelWeights;
		});
  print("newAgeModel = " + newAgeModel)
  return newAgeModel;
}
**/
async function load_current_weights_http(model, ageModel, urlCurrentWeights, idJob, username, idTask, infoWorker) {
	const result = await httpGet(urlCurrentWeights + "?id_job=" + idJob + "&age_model=" + ageModel + "&info_worker=" + infoWorker + "&username=" + username + "&id_task=" + idTask);
  const myweights = result[0];
  const newAgeModel = result[1];

  const modelWeights = model.getWeights();

  console.log("total weights received = " + myweights["weights"].length); 
  console.log("total layers received = " + myweights["layers"].length); 
  console.log("total layers in local model = " + modelWeights.length);


  for (let i = 0; i < myweights["weights"].length; i++) {
    assignSpecificWeight(myweights["weights"][i], myweights["layers"][i], modelWeights)
  }
  

  print("newAgeModel = " + newAgeModel)
  return newAgeModel;
}



function assignSpecificWeight(receivedWeight, receivedWeightLayerName, modelWeights) {
  tf.tidy(() => {
    for (let i = 0; i < modelWeights.length; i++) {
      if (modelWeights[i].name == receivedWeightLayerName) {
        modelWeights[i].assign(receivedWeight);
        receivedWeight.dispose();
        console.log("WELL ASSIGNED " + receivedWeightLayerName)
        return;
      }
    }
    console.log("NOT ASSIGNED " + receivedWeightLayerName)
  });
}


