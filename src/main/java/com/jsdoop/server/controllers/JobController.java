package com.jsdoop.server.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsdoop.server.services.GradientsService;
import com.jsdoop.server.services.JobService;
import com.jsdoop.server.services.WeightsService;
import com.jsdoop.server.utils.StatsAnalyzer;

@RestController
public class JobController {
	private static final Logger log = LoggerFactory.getLogger(JobController.class);

	
	@Autowired
	private GradientsService gradientsService;

	@Autowired
	private WeightsService weightsService;
	
//	@Autowired
//	private AmqpTemplate rabbitTemplate;

	@Autowired
	private AmqpAdmin amqpAdmin;
	
	

	
	@Autowired
	private JobService jobService;
	
	@GetMapping(value="/delete_job")
    public ResponseEntity deleteJob(@RequestParam(name = "id_job") long idJob) {
		try {
			log.info("deleteJob init");
			weightsService.deleteWeights(idJob);
			gradientsService.deleteAll(idJob);
			jobService.delete(idJob);
			amqpAdmin.deleteQueue("grads_" + idJob);
///			amqpAdmin.deleteQueue("stats_" + idJob);
//////			amqpAdmin.deleteQueue("stats_get_data");
//////			amqpAdmin.deleteQueue("stats_get_topology");
//			amqpAdmin.deleteQueue("test_result_" + idJob);
			amqpAdmin.deleteQueue("weights_" + idJob);
			
			//Clean ConcurrentHashMap
			StatsAnalyzer.removeJob(idJob);
		
			HttpHeaders responseHeaders = new HttpHeaders();
			ResponseEntity<LinkedMultiValueMap<String, byte[]>> response = new ResponseEntity("OK", responseHeaders, HttpStatus.OK);
			log.info("deleteJob end");
			return response;


		} catch (Exception e) {
			e.printStackTrace();
			log.info("deleteJob end");
			return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
		}
    }	
	
	
	
	@PostMapping(path = "/save_job", consumes = "application/json", produces = "text/plain")
	public ResponseEntity saveJob(
			@RequestBody String jobString) {
		try {
			log.info("saveJob init" + " Thread {} " + Thread.currentThread().toString());
			Long idJob = jobService.save(jobString);
			
			if (idJob == null) {
				log.info("saveJob end" + " Thread {} " + Thread.currentThread().toString());
				return new ResponseEntity("ERROR: Error saving in database", HttpStatus.CONFLICT);				
			} else {
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.set("id_job", "" + idJob);
				log.info("saveJob end" + " Thread {} " + Thread.currentThread().toString());
				return new ResponseEntity("CREATED", responseHeaders, HttpStatus.CREATED);				
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("saveJob end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
		}

	}	
	@GetMapping(path = "/get_job", produces = "text/plain")
	public ResponseEntity getJob(
			@RequestParam(name = "id_job") long idJob) {
		try {
			log.info("INIT public ResponseEntity getJob" + " Thread {} " + Thread.currentThread().toString());
			String jobString = jobService.get(idJob);
			HttpHeaders responseHeaders = new HttpHeaders();
			return new ResponseEntity(jobString, responseHeaders, HttpStatus.OK);	

		} catch (Exception e) {
			e.printStackTrace();
			log.info("saveJob end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
		}

	}	
	
	
}
