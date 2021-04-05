

async function mystart(device) {
  await device.initialize()
}

class ProblemJSONDistNN {
  pre_init = []
  post_init = []
  pre_run = []  
  post_run = []

  constructor(json, is_remote, seed) {
    const self = this;
    self.json = json


    // LOAD STATS 
    print("self.json = " + str(self.json))
    self.stats_loader = new StatsLoaderHTTP(self.json, is_remote)

    // LOAD MODEL TOPOLOGY
    self.topology_loader = new TopologyLoaderHTTP(self.json, is_remote)


    // LOAD MODEL CURRENT WEIGHTS
    self.current_weights_loader = new CurrentWeightsLoaderHTTP(self.json, is_remote)

    // INIT GRADIENTS LOADER
    self.gradients_loader = new GradientsLoaderHTTP(self.json, is_remote)

    // INIT LOSS FUNCTION
    self.loss_calculator = new LossLoaderJSON(self.json)

    // INIT LOADING DATA
    self.dataset = new DatasetLoaderJSON(self.json, seed, is_remote)

    // TERMINATION CRITERIA
    try {
      self.global_max_age_model = self.json["termination_criteria"]["global_max_age_model"]
      self.post_run.append(tc.is_end_max_global_age)
    } catch (e) {
      print("a ver --> " + self.json["termination_criteria"]["global_max_age_model"])
      print("tc " + tc)
      print("a ver2 --> " + tc.is_end_max_global_age)
      print("No termination criteriaaa")
    //  self.global_max_age_model = 99999999
    }

    // Device Simulation
    self.post_init.append(mystart)
    print("self.post_init[0] "+ self.post_init[0])
    // INIT LOCAL COMPUTATION
    if (self.json["worker"]["local_computation"]) {
      self.local_computation = true
      self.local_steps = self.json["worker"]["local_steps"]

      //self.optimizer = tf.train.rmsprop (self.json["optimizer"]["config"]["learning_rate"], self.json["optimizer"]["config"]["decay"], self.json["optimizer"]["config"]["momentum"], self.json["optimizer"]["config"]["epilson"], self.json["optimizer"]["config"]["centered"]); //TODO -> Where is the rho? //self.optimizer = tf.keras.optimizers.deserialize(self.json["optimizer"])
      //self.optimizer = tf.train.rmsprop(self.json["optimizer"]["config"]["learning_rate"]);
      self.optimizer = OptimizerLoaderJSON.get_optimizer(self.json);
      print("self.optimizer = " + JSON.stringify(self.optimizer))
      print("self.local_steps = " + str(self.local_steps))

    } else {
    // INIT WITHOUT LOCAL COMPUTATION
      self.local_computation = false
    }

  } 
}
 

//Task Solver
class TaskSolverDistNN {

  ss_start_time = 0 

  constructor(id_job, username, problem, info_worker) {
    print("Creating TaskSolverDistNN")
    const self = this;
    self.id_job = id_job
    self.username = username
    self.p = problem //problem solver 
    self.is_running = true
    self.info_worker = info_worker

  } 
  async myinit() {
    const self = this;
    for (let posti of self.p.post_init) {
      print("posti = " + posti)
      await posti(self);
    }
  }

  async initialize() {
    const self = this;
    // LOAD MODEL TOPOLOGY
    self.model = await self.p.topology_loader.load()
    let lossMetric = self.p.json["tester"]["losses"]["class_name"];
    lossMetric = lossMetric[0].toLowerCase() + lossMetric.slice(1);
    print("lossMetric === " + lossMetric);
    if (self.p.json["worker"]["local_computation"]) {
      self.model.compile({
        loss: lossMetric,
        optimizer: self.p.optimizer,
        learningRate: self.p.json["optimizer"]["config"]["learning_rate"]
      });
    }

    self.model.summary()
    
    // LOAD MODEL CURRENT WEIGHTS
    self.age_model = -1
    self.age_model = await self.p.current_weights_loader.load(this, 0)
    print("after load initial weights self.age_model = " + self.age_model)

    //// TODO -> check
    self.p.stats_loader.loadedTopology()

	  ////

    // LOCAL COMPUTATION 
    if (self.p.local_computation) {
      self.listOfGrads = {} //[]
      self.finalGrads = {} //[]  
    }
  }
  

  dispose_grads(grads, tensorNames) {
    tensorNames.forEach( (tensorName) => {
    if (Array.isArray(grads[tensorName] )) {//(Array.isArray(grads[tensorName]) { 
      print("isarray")
      for (let i = 0; i < grads[tensorName].length; i++) {
        grads[tensorName][i].dispose();  //CLEAN
      }
      grads[tensorName].splice(0, grads[tensorName].length); //CLEAN
    } else {
      grads[tensorName].dispose();  //CLEAN 
    }
/**
      if (Array.isArray(grads[tensorName]) { 
        for (let i = 0; i < grads[tensorName].length; i++) {
          grads[tensorName][i].dispose();  //CLEAN
        }
        grads[tensorName][i].splice(0, grads[tensorName][i].length); //CLEAN
      } else {
        grads[tensorName].dispose();  //CLEAN 
      }
**/
    });
    for (var member in grads)  { delete grads[member]; }  //CLEAN
    tensorNames.splice(0, tensorNames.length);  //CLEAN
  }


  async local_computation(id_task, start_time) {
    console.log("Local Computation")
    print("with_local_computation ite")
    const self = this;
    print("starting local computation")
    const {xs, ys, mb_selected} = await self.p.dataset.get_random_batch_from_local_dataset(id_task, this)

    //console.log("<< xs = " + xs);
    //console.log("<< ys = " + ys);
    //console.log("<< mb_selected = " + mb_selected);
    print("before calculate grads");
    const { value, grads } = self.p.loss_calculator.grad(self.model, xs, ys) //let loss_value, grads = self.p.loss_calculator.grad(self.model, xs, ys)
    //console.debug("2vvvalue = " + value);
    //console.debug("2gggrads = " + grads);
    xs.dispose()
    ys.dispose()
    const loss_value = value.dataSync()[0];
    console.log(new Date() + "---> LOSS = " +  str(loss_value))

    //''' INIT LOCAL COMPUTATION '''
    self.model.optimizer.applyGradients(grads);
    print("grads applied");
   

    const tensorNames = Object.keys(grads);
    tensorNames.forEach( (tensorName) => {
      if (self.listOfGrads[tensorName] === undefined) {
        self.listOfGrads[tensorName] = [];
      }
      self.listOfGrads[tensorName].append(grads[tensorName])
    });


    //''' END LOCAL COMPUTATION '''
    if (len(self.listOfGrads[Object.keys(self.listOfGrads)[0]]) >= self.p.local_steps) {
      //''' INIT LOCAL COMPUTATION '''
      //for (let i = 0; i < len(self.listOfGrads); i++) {
      //  self.finalGrads.append(tf.addN(self.listOfGrads[i])) //self.finalGrads.append(tf.math.add_n(self.listOfGrads[i]))
      //}
      
      tensorNames.forEach( (tensorName) => {
        let temp = tf.addN(self.listOfGrads[tensorName]);
        for (let i = 0; i < self.listOfGrads[tensorName].length; i++) {
          self.listOfGrads[tensorName][i].dispose();
        }
        self.finalGrads[tensorName] = temp; //tf.addN(self.listOfGrads[tensorName]);
        print("self.finalGrads[tensorName] = " + self.finalGrads[tensorName])
      });
      print("JSON.stringify(finalGrads) = " + JSON.stringify(self.finalGrads));
      const new_current_age = await self.p.gradients_loader.save(self.finalGrads, self, id_task, start_time);
      print("after save gradients")
      self.dispose_grads(self.listOfGrads, tensorNames);
      self.dispose_grads(self.finalGrads, tensorNames);
      print("finished correctly");
      //''' END LOCAL COMPUTATION '''

      print("Z-Z new_current_age " + str(new_current_age))
      print("Z-Z self.age_model " + str(self.age_model))
      await self.update_model(new_current_age, id_task) 
    }

  }

       

  async without_local_computation(id_task, start_time) {
    print("without_local_computation ite")
    const self = this;
    const {xs, ys, mb_selected} = await self.p.dataset.get_random_batch_from_local_dataset(id_task, this)
    print("before calculate grads");
    const { value, grads } = self.p.loss_calculator.grad(self.model, xs, ys)
    const loss_value = value.dataSync()[0];
    print("---> LOSS = " +  str(loss_value))
    const new_current_age = self.p.gradients_loader.save(grads, self, id_task, start_time)
    await self.update_model(new_current_age, id_task)
  }

  async update_model(new_current_age, id_task) {
    const self = this;
    if (int(new_current_age) > int(self.age_model)) {        
      self.age_model = await self.p.current_weights_loader.load(self, id_task)
      print("updating model self.age_model = " + str(self.age_model))

      // TODO -> check
      self.p.stats_loader.stats_loaded_weights();
      //
    }
  }

  async run() {
    const self = this;
    //for (let i = 0; i < 5000; i++) {//if (true) { //while (true) { //TODO
    let i = 0;
    while (true) { 
  
      console.log(new Date() + " Iteration: " + i)

      if (self.is_running) {
        self.ss_start_time = time.time()
        let ms_start_time = int(round(time.time() * 1000))
        print("time.time() = " + str(time.time()))
        print("ms_start_time = " + str(ms_start_time))
        const id_task = ms_start_time
        //while not check_termination_criterion():

        if (self.p.local_computation) {
          await self.local_computation(id_task, ms_start_time)
        }
        else {
          await self.without_local_computation(id_task, ms_start_time)
        }
      }

      //// TODO -> check // FOR INTERFACE GAME
      self.p.stats_loader.stats_calculate_gradient(self.age_model);

	    ////

      //Check Termination
      for (let postr of self.p.post_run) {
        postr(self)
      }
      i++;
    }

  }

  finalize() {
    const self = this;
    print("Finishing ...")
    exit()
  }
}

(async () => {

  //console.log = function() {};
  console.debug("US.get_url_job(true)   = " +  US.get_url_job(true)  )

  console.debug("window.location.search = " + window.location.search);
  var urlSearch = new URLSearchParams(window.location.search);
  var isLocalhost = urlSearch.get("localhost");
  var isDebug = urlSearch.get("debug");
  console.debug("isLocalhost = " + isLocalhost);

  //USERNAME
  var username = urlSearch.get("username");
  var reg = new RegExp(/[a-zA-Z0-9_]{4,10}$/);
  if (username == null || !reg.test(username)) {
	  alert("username is not valid! Try to login again.")
	  $(location).attr('href', "test_main.html");
  } 
  //username = username + "_" + new Date().getTime();
  //const username = "myusername" //testing

  //ID_JOB

  var id_job = urlSearch.get("id_job");
  //if (!id_job) {
  //  id_job = "default_username";
  //}
  reg = new RegExp(/([0-9]){1,15}$/);
  if (id_job == null ||!reg.test(id_job)) {
	  alert("id_job is not valid!  Try to login again.");
	  $(location).attr('href', "test_main.html");
  }
  //const id_job = 1611759070604 //testing


  //INFO_WORKER
  const info_worker = JSON.stringify(navigator.userAgent); //"browser"
  console.debug("JSON.stringify(navigator.userAgent) = " + info_worker);

  //SEED
  //const seed = (info_worker + username).hashCode()
  //print("seedd = " + seed)
  const seed = new Date().getTime() + "" + Math.random();
  Math.seedrandom(seed);

  //const is_remote = true

  var is_remote = urlSearch.get("is_remote");
  if (!is_remote) {
    is_remote = true;
  } else {
    is_remote = (is_remote === 'true');
  }
  print("<is_remote = " + is_remote)


  // LOAD JOB
  job_loader = new JobLoaderHTTP(is_remote)
  json = await job_loader.load(id_job)
  print("json = " + str(json))
  print("first")
  problem = new ProblemJSONDistNN(json, is_remote, seed)
  print("second")
  worker = new TaskSolverDistNN(id_job, username, problem, info_worker)
  await worker.myinit(); //await worker.initialize(); // TODO --> //await worker.myinit()
  print("third")

  // TODO --> check
  //$("#solving_job").text(id_job);
  //$("#thanks").text(username);
  problem.stats_loader.stats_greets(id_job, username)
  //

  await worker.run()
  print("end")


  /**
  print("str(argv) = " + str(sys.argv))

  id_job = null
  try {
    id_job = int(sys.argv[1]) //1606147964029
  } catch {
    exit("ERROR: Please insert a valid numeric job id.")
  }



  username = null
  try:
    username = "worker_py_" + str(sys.argv[2])
  except:
    exit("ERROR: Please insert a valid username.")

  seed = null
  try:
    seed = int(sys.argv[2])
  except:
    exit("ERROR: Please insert a valid seed.")


  is_remote = false
  try:
    is_remote = (sys.argv[3].lower() == 'true')
    print("REMOTE HOST")
  except:
    print("LOCAL HOST")

  print("len(argv) = " + str(len(sys.argv)))
  print("id_job = " + str(id_job))
  print ("username = " + username)
  print("is_remote = " + str(is_remote))




  // LOAD JOB
  job_loader = JobLoaderHTTP(is_remote)
  json = job_loader.load(id_job)
  print("json = " + str(json))

  problem = ProblemJSONDistNN(json, is_remote, seed)
  worker = TaskSolverDistNN(id_job, username, problem)
  worker.run()
  **/


})();
