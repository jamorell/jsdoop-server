class CurrentWeightsLoaderHTTP {
  constructor(json, is_remote) {
    const self = this;
    self.url = US.get_url_current_weights(json, is_remote) 
  }
    

  async load(worker, id_task) {
    const self = this;
    let new_age = await load_current_weights_http(worker.model, worker.age_model, self.url, worker.id_job, worker.username, id_task, worker.info_worker);
    print("CurrentWeightsLoaderHTTP.load new_age = " + new_age);
    new_age = int(new_age);
    print("int() CurrentWeightsLoaderHTTP.load int(new_age) = " + new_age);
    return new_age;
  }
}
