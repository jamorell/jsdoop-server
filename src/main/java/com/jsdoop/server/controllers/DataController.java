package com.jsdoop.server.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsdoop.server.persistance.models.Dataset;
import com.jsdoop.server.services.DatasetService;

@RestController
public class DataController {
	private static final Logger log = LoggerFactory.getLogger(DataController.class);

	@Autowired
	private DatasetService datasetService;	
	
	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Autowired
	private AmqpAdmin amqpAdmin;
	
	/** 
	 * Save h5 topology and convert it to json and dl4j.
	 * @param key
	 * @param topologyBtyes
	 * @return
	 */
	@PostMapping(path = "/dataset", consumes = "application/octet-stream", produces = "text/plain")
	public ResponseEntity saveDataset(
			@RequestParam(name = "key") String key, 
			@RequestBody byte[] datasetBytes) {
		try {
			log.info("saveDataset init" + " Thread {} " + Thread.currentThread().toString());
			Dataset dataset = new Dataset(key, datasetBytes);
			datasetService.save(dataset);
			HttpHeaders responseHeaders = new HttpHeaders();
			log.info("saveDataset end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity("CREATED", responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("saveDataset end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
		}

	}	
	
	@GetMapping(path = "/dataset", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity getDataset(
			HttpServletRequest request, 
			@RequestParam(name = "id_task") long idTask,  
			@RequestParam(name = "username") String username,
			@RequestParam(name = "info_worker") String infoWorker,
			@RequestParam(name = "id_job") long idJob,
			@RequestParam(name = "key") String key) {
		try {
			log.info("getDataset init" + " Thread {} " + Thread.currentThread().toString());
//			// STATS	
//			String remoteAddr = request.getRemoteAddr();
//			long timeRequest = System.currentTimeMillis();				
//			//
			
			HttpHeaders responseHeaders = new HttpHeaders();
			byte[] result = datasetService.get(key).getDataset();
//			// STATS	
//			long timeResponse = System.currentTimeMillis();		
//			String typeTask = "get_data_" + key;
//			Stats stats = new Stats(username, infoWorker, remoteAddr, timeRequest, timeResponse, null, idJob,
//					typeTask, 0, null);
//			String queue1 = "stats_" + idJob;
//			amqpAdmin.declareQueue(new Queue(queue1, true));
//			rabbitTemplate.convertAndSend(queue1, stats);
//			//
			log.info("getDataset end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity(result, responseHeaders, HttpStatus.OK);	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("getDataset end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
		}

	}	
	
}
