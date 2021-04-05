
function calculate_gradients(model, xstensor, ystensor) {
  try {
    print("calculate_gradients func");
    const f = () => {
			const tidyresult = tf.tidy(() => {
				console.log("before")
        print("before predict")
				//console.log("model.layers[0] "  + model.layers[0])
				const predYs = model.predict(xstensor);
        print("after predict")
				console.log("after")
				console.log("predYs.shape " + predYs.shape)	
				
//				////
// 				const accuracy = tf.metrics.categoricalAccuracy(ystensor, predYs).mean();
// 				////
// 				return tf.losses.meanSquaredError(ystensor, predYs);
// 				return tf.metrics.categoricalCrossentropy(ystensor, predYs);
        //return tf.losses.softmaxCrossEntropy(ystensor, predYs);
				return tf.losses.meanSquaredError(ystensor, predYs);
			});
      print("after tidyresult")
      return tidyresult;
		}
    return tf.variableGrads(f); 
/**
    const { value, grads } = tf.variableGrads(f); 
  console.log("aaaavalue = " + value)
  console.log("aaaagradients = " + grads)
    return { value, grads }**/
  } catch (e) {
    console.log(e);
    return null;
  }
}

