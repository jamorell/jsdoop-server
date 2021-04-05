async function load_model_topology_http(url_model_topology, model_topology_key) {
  try {
    print("url_model_topology " + url_model_topology)
    print("model_topology_key " + model_topology_key)
    const final_url = url_model_topology + '/' + model_topology_key + '/model.json';
    print("final topology url = " + final_url)
    const model = await tf.loadLayersModel(final_url);
    model.summary();
    print("my summary");
    console.log(model.layers[0].name);
    return model;
  } catch (e) {
    console.log(e);
    return null;
  }
}

