class JobLoaderHTTP {
  constructor(is_remote) {
    this.url = US.get_url_job(is_remote) 
  }

  load(id_job) {
    print("ur_job " + this.url)
    return load_job(this.url, id_job)	
  }
}

