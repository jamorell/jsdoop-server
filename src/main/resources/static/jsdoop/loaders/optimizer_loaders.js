class OptimizerLoaderJSON {
  constructor(json) {

    const self = this;
    //self.loss_object = tf.keras.losses.deserialize(json["tester"]["losses"])
    console.log("zzz self.loss_object = json[tester] = " + json["tester"]);
    console.log("zzz self.loss_object = json[tester][losses] = " + json["tester"]["losses"]);
    console.log("zzz self.loss_object = json[tester][losses][class_name] = " + json["tester"]["losses"]["class_name"]);
    self.loss_object = json["tester"]["losses"]["class_name"]; //tf.losses.meanSquaredError (labels, predictions, weights?, reduction?)

  }
  

  static get_optimizer(json) {

    const optimizer_name = self.json["optimizer"]["class_name"];//["config"]["name"];
    const learning_rate = self.json["optimizer"]["config"]["learning_rate"];

    switch (optimizer_name.toLowerCase()) {
      case C.EJSDNNOptimizers.adadelta.toLowerCase():
        return tf.train.adadelta(learning_rate);
        break;
      case C.EJSDNNOptimizers.adagrad.toLowerCase():
        return tf.train.adagrad(learning_rate);
        break;
      case C.EJSDNNOptimizers.adam.toLowerCase():
        return tf.train.adam(learning_rate);
        break;
      case C.EJSDNNOptimizers.adamax.toLowerCase():
        return tf.train.adamax(learning_rate);
        break;
      case C.EJSDNNOptimizers.momentum.toLowerCase():
        return tf.train.momentum(learning_rate, self.json["optimizer"]["config"]["momentum"]);
        break;
      case C.EJSDNNOptimizers.rmsprop.toLowerCase():
        return tf.train.rmsprop (self.json["optimizer"]["config"]["learning_rate"], self.json["optimizer"]["config"]["rho"], self.json["optimizer"]["config"]["momentum"], 1e-10, self.json["optimizer"]["config"]["centered"]) 
        //return tf.train.rmsprop(learning_rate);
        break;
      case C.EJSDNNOptimizers.sgd.toLowerCase():
        return tf.train.sgd(learning_rate);
        break;
    }
  }
}

