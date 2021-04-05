class DatasetLoaderJSON {
  constructor(json, seed, is_remote) {

    const self = this;

    self.url = US.get_url_data(json, is_remote) ;
    self.key = json["data"]["dataset"] + "_" + json["data"]["mb_size"]; //"mnist_8_20_x.npy"
    self.x_shape = json["data"]["shape"];
    self.x_shape[0] = json["data"]["mb_size"];
    self.y_shape = [json["data"]["mb_size"], json["data"]["num_classes"]];
    if (seed) {
      Math.seedrandom(seed)
      print("seed = " + str(seed))
    }
    /**
    self.loss_object = tf.keras.losses.deserialize(json["tester"]["losses"])
    self.X_train, self.y_train, self.X_test, self.y_test = loader(json)
    self.X_batches, self.y_batches = create_batches(self.X_train, self.y_train, json)

    // LOCAL DATASET
    self.total_mbatches = get_total_m_batches(json)
    self.local_dataset_len = int(self.total_mbatches / json["data"]["local_portion_dataset"])
    self.local_dataset = random.sample(range(self.total_mbatches), self.local_dataset_len)
    print("self.local_dataset = "+ str(self.local_dataset))
    **/

    // LOCAL DATASET
    self.total_mbatches = get_total_m_batches(json)
    self.local_dataset_len = int(self.total_mbatches / json["data"]["local_portion_dataset"])
    self.local_dataset = random.sample(range(self.total_mbatches), self.local_dataset_len)
    print("ZZZ self.total_mbatches = " + self.total_mbatches)
    print("ZZZ self.local_dataset_len = " + self.local_dataset_len)
    print("ZZZ self.local_dataset = " + self.local_dataset)
  }

  async get_random_batch_from_local_dataset(id_task, worker) {
    /**
    mb_selected = random.randint(0, len(self.local_dataset) - 1) //str(i)
    xs = self.X_batches[self.local_dataset[mb_selected]] //load_data_http(url_data_server, "mnist_8_" + str(mb_selected) + "_x.npy")
    ys = self.y_batches[self.local_dataset[mb_selected]] //load_data_http(url_data_server, "mnist_8_" + str(mb_selected) + "_y.npy")
    return xs, ys, mb_selected
    **/
    const self = this;

    print("llen(self.local_dataset) = " + len(self.local_dataset))
    print("llen(self.local_dataset) - 1 = " + (len(self.local_dataset) - 1)) 

    const mb_selected = random.randint(0, len(self.local_dataset) - 1); //str(i)
    print("ZZZ lmb_selected = " + mb_selected);
    print("ZZZ lmb_selected self.local_dataset[mb_selected]= " + self.local_dataset[mb_selected]); 

    const xkey = self.key + "_" + self.local_dataset[mb_selected] + "_x.npy";
    let xs = await load_data_http(self.url, id_task, worker.username, worker.info_worker, worker.id_job, xkey) //self.X_batches[self.local_dataset[mb_selected]]
    xs = tf.tensor(xs, self.x_shape, 'float32');

    const ykey = self.key + "_" + self.local_dataset[mb_selected] + "_y.npy"; 
    let ys = await load_data_http(self.url, id_task, worker.username, worker.info_worker, worker.id_job, ykey) //self.y_batches[self.local_dataset[mb_selected]] 
    ys = tf.tensor(ys, self.y_shape, 'float32'); //TODO -> int
    return {xs, ys, mb_selected}
  }

  async get_batch(id_task, worker, id_batch) {

    const self = this;
    const xkey = self.key + "_" + id_batch + "_x.npy";
    let xs = await load_data_http(self.url, id_task, worker.username, worker.info_worker, worker.id_job, xkey) 
    xs = tf.tensor(xs, self.x_shape, 'float32');

    const ykey = self.key + "_" + id_batch + "_y.npy"; 
    let ys = await load_data_http(self.url, id_task, worker.username, worker.info_worker, worker.id_job, ykey) 
    ys = tf.tensor(ys, self.y_shape, 'float32'); //TODO -> int
    return {xs, ys, id_batch}
  }

}
