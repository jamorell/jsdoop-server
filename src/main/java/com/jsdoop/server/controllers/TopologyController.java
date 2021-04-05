package com.jsdoop.server.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
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

import com.jsdoop.server.persistance.models.Stats;
import com.jsdoop.server.persistance.models.Topology;
import com.jsdoop.server.services.TopologyService;
import com.jsdoop.server.services.model_conversion.ModelConverter;

@RestController
public class TopologyController {
	
	private static final Logger log = LoggerFactory.getLogger(TopologyController.class);

	@Autowired
	private TopologyService topologyService;	
	
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
	@PostMapping(path = "/topology", consumes = "application/octet-stream", produces = "text/plain")
	public ResponseEntity saveTopology(
			@RequestParam(name = "key") String key, 
			@RequestBody byte[] topologyBytes) {
		try {
			log.info("saveTopology init" + " Thread {} " + Thread.currentThread().toString());
			if (!ModelConverter.h5ToJson(topologyBytes, key)) {
				return new ResponseEntity("ERROR: Converting to JSON", HttpStatus.BAD_REQUEST);
			} else {
				byte[] dl4jBytes = ModelConverter.h5ToDL4j(topologyBytes);
				if (dl4jBytes == null || dl4jBytes.length == 0) {
					return new ResponseEntity("ERROR: Converting to DL4J", HttpStatus.BAD_REQUEST);
				} else {
					try {
						log.debug("topologyBytes.length = " + topologyBytes.length);
						Topology topologyDL4J = new Topology(key + "_dl4j", topologyBytes);
						log.info("SAVING DL4J using key " + key + "_dl4j");
						topologyService.save(topologyDL4J);
						Topology topologyH5 = new Topology(key + "_h5", topologyBytes);
						log.info("SAVING DL4J using key " + key + "_h5");
						topologyService.save(topologyH5);
						
						HttpHeaders responseHeaders = new HttpHeaders();
					    responseHeaders.set("Location", key);
					    log.info("saveTopology end" + " Thread {} " + Thread.currentThread().toString());
						return new ResponseEntity("CREATED", responseHeaders, HttpStatus.CREATED);								
					} catch (Exception e) {
						e.printStackTrace();
						log.info("saveTopology end" + " Thread {} " + Thread.currentThread().toString());
						return new ResponseEntity("ERROR: Saving DL4J or H5", HttpStatus.BAD_REQUEST);						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("saveTopology end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
		}

	}	
	
	@GetMapping(path = "/topology", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity getTopology(
			HttpServletRequest request, @RequestParam(name = "info_worker") String infoWorker,
			 @RequestParam(name = "username") String username,
			 @RequestParam(name = "id_job") long idJob,
			@RequestParam(name = "key") String key, 
			@RequestParam(name = "type") String type) {
		try {
			log.info("getTopology init" + " Thread {} " + Thread.currentThread().toString());
			// STATS	
			String remoteAddr = request.getRemoteAddr();
			long timeRequest = System.currentTimeMillis();				
			//
			
			HttpHeaders responseHeaders = new HttpHeaders();
			byte[] result = topologyService.get(key + "_" + type).getTopology();
			// STATS	
			long timeResponse = System.currentTimeMillis();		
			String typeTask = "get_topology_" + key;
			Stats stats = new Stats(username, infoWorker, remoteAddr, timeRequest, timeResponse, null, idJob,
					typeTask, 0, null);
//			String queue1 = "stats_get_toplogy";
			String queue1 = "stats"; //"stats_" + idJob;
			amqpAdmin.declareQueue(new Queue(queue1, true));
			rabbitTemplate.convertAndSend(queue1, stats);
			//
			log.info("getTopology end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity(result , responseHeaders, HttpStatus.OK);	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("getTopology end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
		}

	}	
		
}
