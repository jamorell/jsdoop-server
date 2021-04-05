class TopologyLoaderHTTP {
  constructor(json, is_remote) {
    this.url = US.get_url_topology(json, is_remote)  
    this.key = json["topology"]["topology"]
  }

  load() {
    return load_model_topology_http(this.url, this.key) 
  }
}


