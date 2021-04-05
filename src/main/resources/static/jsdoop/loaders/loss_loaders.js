class LossLoaderJSON {
  constructor(json) {

    const self = this;
    //self.loss_object = tf.keras.losses.deserialize(json["tester"]["losses"])
    console.log("zzz self.loss_object = json[tester] = " + json["tester"]);
    console.log("zzz self.loss_object = json[tester][losses] = " + json["tester"]["losses"]);
    console.log("zzz self.loss_object = json[tester][losses][class_name] = " + json["tester"]["losses"]["class_name"]);
    self.loss_object = json["tester"]["losses"]["class_name"]; //tf.losses.meanSquaredError (labels, predictions, weights?, reduction?)

  }

  grad(model, inputs, y) {
    /**
    const self = this;
    with tf.GradientTape() as tape:
      loss_value = self.loss(model, inputs, y, true)
    return loss_value, tape.gradient(loss_value, model.trainable_variables)
    **/
    const self = this;
    const f = () =>
      tf.tidy(() => {
        const predYs = model.predict(inputs);
        //console.log("predYs = " + predYs)
        //console.log("self.loss_object = " + self.loss_object)
        //if (self.loss_object === "MeanSquaredError") {
        //  return tf.metrics.meanSquaredError(y, predYs).mean();
        //}
        
        switch (self.loss_object.toLowerCase()) {
          case C.EJSDNNLosses.absoluteDifference.toLowerCase():
            return tf.losses.absoluteDifference(y, predYs).mean().asScalar();
          case C.EJSDNNLosses.computeWeightedLoss.toLowerCase():
            return tf.losses.computeWeightedLoss(y, predYs).mean().asScalar();
          case C.EJSDNNLosses.cosineDistance.toLowerCase():
            return tf.losses.cosineDistance(y, predYs).mean().asScalar();
          case C.EJSDNNLosses.hingeLoss.toLowerCase():
            return tf.losses.hingeLoss(y, predYs).mean().asScalar();
          case C.EJSDNNLosses.huberLoss.toLowerCase():
            return tf.losses.huberLoss(y, predYs).mean().asScalar();
          case C.EJSDNNLosses.logLoss.toLowerCase():
            return tf.losses.logLoss(y, predYs).mean().asScalar();
          case C.EJSDNNLosses.meanSquaredError.toLowerCase():
            return tf.losses.meanSquaredError(y, predYs).mean().asScalar();
          case C.EJSDNNLosses.sigmoidCrossEntropy.toLowerCase():
            return tf.losses.sigmoidCrossEntropy(y, predYs).mean().asScalar();
          case C.EJSDNNLosses.softmaxCrossEntropy.toLowerCase():
            return tf.losses.softmaxCrossEntropy(y, predYs).mean().asScalar();
          case C.EJSDNNMetrics.binaryAccuracy.toLowerCase():
            return tf.metrics.binaryAccuracy(y, predYs).mean().mean().asScalar();
          case C.EJSDNNMetrics.binaryCrossentropy.toLowerCase():
            return tf.metrics.binaryCrossentropy(y, predYs).mean().asScalar();
          case C.EJSDNNMetrics.categoricalAccuracy.toLowerCase():
            return tf.metrics.categoricalAccuracy(y, predYs).mean().asScalar();
          case C.EJSDNNMetrics.categoricalCrossentropy.toLowerCase():
            return tf.metrics.categoricalCrossentropy(y, predYs).mean().asScalar();
          case C.EJSDNNMetrics.cosineProximity.toLowerCase():
            return tf.metrics.cosineProximity(y, predYs).mean().asScalar();
          case C.EJSDNNMetrics.meanAbsoluteError.toLowerCase():
            return tf.metrics.meanAbsoluteError(y, predYs).mean().asScalar();
          case C.EJSDNNMetrics.meanAbsolutePercentageError.toLowerCase():
            return tf.metrics.meanAbsolutePercentageError(y, predYs).mean().asScalar();
          case C.EJSDNNMetrics.meanSquaredError.toLowerCase():
            return tf.metrics.meanSquaredError(y, predYs).mean().asScalar();
          case C.EJSDNNMetrics.precision.toLowerCase():
            return tf.metrics.precision(y, predYs).mean().asScalar();
          case C.EJSDNNMetrics.recall.toLowerCase():
            return tf.metrics.recall(y, predYs).mean().asScalar();
          case C.EJSDNNMetrics.sparseCategoricalAccuracy.toLowerCase():
            return tf.metrics.sparseCategoricalAccuracy(y, predYs).mean();
          default:
            return null;
        }
        
      });

    const { value, grads } = tf.variableGrads(f);
    //console.debug("vvvalue = " + value);
    //console.debug("gggrads = " + grads);
    return { value, grads };
  }
  
}

