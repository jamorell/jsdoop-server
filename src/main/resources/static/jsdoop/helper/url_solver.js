class JSDURLSolver {
  static get_url_job(is_remote) {
    const path = "/get_job";
    print("getting job is_remote = " + is_remote)
    if (is_remote === true) {
      print("job_remote")
      return C.JOB_HOST_REMOTE + ":" + str(C.JOB_PORT_REMOTE) + path
    } else {
      print("job_local")
      return C.JOB_HOST_LOCAL + ":" + str(C.JOB_PORT_LOCAL) + path
    }
  }


  static get_url_topology(job_json, is_remote) {
    const the_type = "topology"
    const path = "/files/topology"
    return JSDURLSolver.__generic_url(job_json, the_type, path, is_remote);
  }

  static get_url_data(job_json, is_remote) {
    const the_type = "data"
    const path = "/dataset"
    return JSDURLSolver.__generic_url(job_json, the_type, path, is_remote);
  }

  static get_url_current_weights(job_json, is_remote) {
    const the_type = "weights"
    const path = "/current_weights"
    return JSDURLSolver.__generic_url(job_json, the_type, path, is_remote)
  }

  static get_url_gradients(job_json, is_remote) {
    const the_type = "gradients"
    const path = "/gradients"
    return JSDURLSolver.__generic_url(job_json, the_type, path, is_remote)
  }

  static __generic_url(job_json, the_type, path, is_remote) {
    if (is_remote === true) {
      const host = "host_remote"
      const port = "port_remote"
      return job_json[the_type][host] + ":" + str(job_json[the_type][port]) + path
    } else {
      const host = "host_local"
      const port = "port_local"
      return job_json[the_type][host] + ":" + str(job_json[the_type][port]) + path
    }
  }

  static get_url_stats(job_json, is_remote) {
    //const the_type = "weights" // TODO -> check
    const path = "/user_stats"
    if (is_remote === true) {
      return C.SQL_REMOTE_HOST + ":" + C.SQL_REMOTE_PORT + path
    } else {
      return C.SQL_LOCAL_HOST + ":" + C.SQL_LOCAL_PORT + path
    }
  }

}

