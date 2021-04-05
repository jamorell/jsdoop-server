


class GradientsLoaderHTTP { 
  constructor(json, is_remote) {
    const self = this;
    self.url = US.get_url_gradients(json, is_remote) 
    print("gradients url = " + self.url) 
  }  

  async save( grads_to_save, worker, id_task, start_time) {
    const self = this;
    print("gradients url2 = " + self.url) 
    const tensorNames = Object.keys(grads_to_save);
    const fixedGrads = self.fixingNames(grads_to_save, tensorNames);
    print("before fixedGrads = " + JSON.stringify(fixedGrads))
    print("gradients url3 = " + self.url) 
    const new_current_age = await save_gradients_http(fixedGrads, self.url, worker.id_job, worker.age_model, worker.username, id_task, worker.info_worker, start_time )
    //const new_current_age = 99;
    print("new_current_age after save gradients = " + new_current_age)
    return new_current_age
  }

  fixingNames(grads, tensorNames) {
    print("fixingNames = " + tensorNames);
    const jsonGrads = {};
    const patt = /_[0-9]*$/i;
    tensorNames.forEach(tensorName => {
      let newTensorName = tensorName;
      // Actualizamos el nombre del tensor para evitar problemas del nombre autogenerado por TF
      const matched = tensorName.match(patt);
      if (matched && matched.length > 0) {
        newTensorName = tensorName.substring(0, tensorName.indexOf(matched[0]));
      }
      // logger.warn(tensorName + " -> " + newTensorName);
      print("before arraysync tensorName = " + tensorName);
      print("before arraysync grads[tensorName] = " + grads[tensorName]); 
      jsonGrads[newTensorName] = grads[tensorName]; // grads[tensorName].flatten().arraySync();
    });
    // //////////////////////////////
    return jsonGrads;
  }
}
