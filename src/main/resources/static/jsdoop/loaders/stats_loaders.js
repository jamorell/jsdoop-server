
function mobileCheck() {
  let check = false;
  (function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4))) check = true;})(navigator.userAgent||navigator.vendor||window.opera);
  return check;
};


function initializeGame() {
	$("#div_game").show();
	  init();
	  animate();
}

async function delay(ms) {
  // return await for better async stack trace support in case of errors.
  return await new Promise(resolve => setTimeout(resolve, ms));
}

class StatsLoaderHTTP { 
  constructor(json, is_remote) {
    const self = this;

    self.id_job = json["id_job"];

    self.totalGradientsCalculated = 0;
    self.timesWeightsLoaded = 1;
    self.modelUpdateRate = 0;
    self.lastUpdateModel = 0;

    self.gradientsCalculateRate = 0;
    self.lastCalculatedGradient = new Date().getTime();


    self.url = US.get_url_stats(json, is_remote) 
    self.updateStatsInterval = 10000;

    self._init_plotly_data(); 
    self._init_loop_loader(); 
  }  

  stats_greets(id_job, username) {
    $("#solving_job").text(id_job);
    $("#thanks").text(username);
  }

  loadedTopology() {
    const self = this;
    self.lastUpdateModel = new Date().getTime();
	  $("#loading_img").hide();
	  if (!mobileCheck()) {
		  setTimeout(() => { initializeGame()}, 3000);		
	  }
  }


  updateInterface(current_age_model) {
     const self = this;   
	  $("#calculated_gradients").text(self.totalGradientsCalculated);
	  $("#current_age").text(current_age_model);
	  $("#times_weights_loaded").text(self.timesWeightsLoaded);	
	  $("#model_update_rate").text(self.modelUpdateRate + " ms");	
	  $("#gradients_calculation_rate").text(self.gradientsCalculateRate + " ms");		
	
	  try {
      self.data2[0].delta.reference = self.data2[0].value;
      self.data2[0].value = 1000 / self.gradientsCalculateRate;
      Plotly.redraw('user_stats_grads_speed', self.data2, self.layout2);
      self.data3[0].delta.reference = self.data3[0].value;
      self.data3[0].value = 1000 / self.modelUpdateRate;
      Plotly.redraw('user_stats_weights_speed', self.data3, self.layout3);
	  } catch(err) {	
		  console.debug("Error updating interface");
	  }
  }

  stats_loaded_weights() {
    const self = this;
	  let newUpdateModel = new Date().getTime();
	  self.modelUpdateRate = newUpdateModel - self.lastUpdateModel

    console.log("##### modelupdaterate = " + self.modelUpdateRate)
	  self.lastUpdateModel = newUpdateModel;
	  self.timesWeightsLoaded++;
  }

  stats_calculate_gradient(current_age_model) {
    const self = this;
      //// TODO -> check // FOR INTERFACE GAME
      var newCalculatedGradient = new Date().getTime();
	    self.gradientsCalculateRate = newCalculatedGradient - self.lastCalculatedGradient;
	    self.lastCalculatedGradient = newCalculatedGradient;
	    self.totalGradientsCalculated++;
	    self.updateInterface(current_age_model); 
	    ////
  }

  _init_loop_loader() {
    const self = this;
    let run = async ()=>{
      await delay(self.updateStatsInterval);
      await self.load_ranking();
    	setTimeout( () => { 
		    self._init_loop_loader();
	    }, self.updateStatsInterval); 
    }
    run();
  	//setTimeout( () => { 
		//  self.load_ranking();
	  //}, self.updateStatsInterval); 
  }

  async load_ranking() {

    const self = this;
    // TODO --> check
    const values = await load_stats(self.url, self.id_job); 

    if (values) {
      console.debug("values = " + values)
      self.data[0].x = []
      self.data[0].y = []
      for (let i = 0; i < values.length; i++) {
      self.data[0].x.push(values[i].username)
      self.data[0].y.push(values[i].gradients)
      }
      console.log(JSON.stringify(self.data))
      Plotly.redraw('user_stats');
      return true;
    } else {
      return false;
    }
  }

  _init_plotly_data() {
    const self = this;
	  //try {
	        self.data = [
	      	  {
	      		  
	      	  	//name: 'Collaboration Rankingg.\n (Updated every ' + (updateStatsInterval/1000) +' seconds))',
	      	    x: [],
	      	    y: [],
	      	    type: 'bar',
	      		  marker: {
	        		    color: "#50a3a2",//'rgb(49,130,189)',
	        		    opacity: 0.7,
	        		  }
	      	  }
	      	];
	       
	            self.layout = {
	          		  barmode: 'group',
	          		  title: {
	          		    text:'Collaboration Ranking (every ' + (self.updateStatsInterval / 1000) + 's)',
	          		    font: {
	          		      family: 'Courier New, monospace',
	          		      size: 20
	          		    },
	          		    xref: 'paper',
	          		    //x: 0.05,
	          		  },
	          		  xaxis: {
	            		    title: {
	            		      text: 'User',
	            		      font: {
	            		        family: 'Courier New, monospace',
	            		        size: 18,
	            		        color: '#7f7f7f'
	            		      }
	            		    },
	            		  },
	            		  yaxis: {
	            		    title: {
	            		      text: 'Calculated Gradients',
	            		      font: {
	            		        family: 'Courier New, monospace',
	            		        size: 18,
	            		        color: '#7f7f7f'
	            		      }
	            		    }
	            		  }
	            };

	            Plotly.newPlot('user_stats', self.data, self.layout);
	                  

	          	
	          	
	              self.data2 = [
	            	  {
	            	    type: "indicator",
	            	    mode: "gauge+number+delta",
	            	    value: 0,
	            	    title: { text: "Local Gradients calculation rate (per second)", font: { size: 14 } },
	            	    delta: { reference: 0, increasing: { color: "RebeccaPurple" } },
	            	    gauge: {
	            	      axis: { range: [null, 3], tickwidth: 1, tickcolor: "darkblue" },
	            	      bar: { color: "darkblue" },
	            	      bgcolor: "white",
	            	      borderwidth: 2,
	            	      bordercolor: "gray",
	            	      steps: [
	            	        { range: [0, 1], color: "cyan" },
	            	        { range: [1, 3], color: "royalblue" }
	            	      ],
	            	      threshold: {
	            	        line: { color: "red", width: 4 },
	            	        thickness: 0.01,
	            	        value: 2.5
	            	      }
	            	    }
	            	  }
	            	];

	            	//var layout2 = { width: 600, height: 500, margin: { t: 0, b: 0 }, font: {  family: 'Courier New, monospace' } };
	            	self.layout2 = { height: 500, margin: { t: 0, b: 0 }, font: {  family: 'Courier New, monospace' } };
	            	Plotly.newPlot('user_stats_grads_speed', self.data2, self.layout2);
	            	
	            	
	              self.data3 = [
	              	  {
	              	    type: "indicator",
	              	    mode: "gauge+number+delta",
	              	    value: 0,
	              	    title: { text: "Model update rate (per second)", font: { size: 14 } },
	              	    delta: { reference: 0, increasing: { color: "RebeccaPurple" } },
	              	    gauge: {
	              	      axis: { range: [null, 0.2], tickwidth: 1, tickcolor: "darkblue" },
	              	      bar: { color: "darkblue" },
	              	      bgcolor: "white",
	              	      borderwidth: 2,
	              	      bordercolor: "gray",
	              	      steps: [
	              	        { range: [0, 0.05], color: "cyan" },
	              	        { range: [0.05, 0.2], color: "royalblue" }
	              	      ],
	              	      threshold: {
	              	        line: { color: "red", width: 4 },
	              	        thickness: 0.001,
	              	        value: 0.9
	              	      }
	              	    }
	              	  }
	              	];

	              	//var layout3 = { width: 600, height: 500, margin: { t: 0, b: 0 }, font: {  family: 'Courier New, monospace' } };
	              	self.layout3 = { height: 500, margin: { t: 0, b: 0 }, font: {  family: 'Courier New, monospace' } };
	              	Plotly.newPlot('user_stats_weights_speed', self.data3, self.layout3);
	  //} catch(err) {
		//  console.debug("ERROR: Error creating plotly diagrams.")
	 // }
  }

}
