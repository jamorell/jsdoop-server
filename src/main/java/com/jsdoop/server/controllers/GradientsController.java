package com.jsdoop.server.controllers;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jsdoop.server.persistance.models.Gradients;
import com.jsdoop.server.persistance.models.Stats;
import com.jsdoop.server.services.GradientsService;
import com.jsdoop.server.services.WeightsService;
import com.jsdoop.server.utils.StatsAnalyzer;

@RestController
public class GradientsController {
	private static final Logger log = LoggerFactory.getLogger(GradientsController.class);

	@Autowired
	private GradientsService gradientsService;

	@Autowired
	private WeightsService weightsService;
	
	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Autowired
	private AmqpAdmin amqpAdmin;
	
	@Autowired
	private StatsAnalyzer statsAnalyzer;
	
	@GetMapping(value="/gradients", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity getGradients(HttpServletRequest request, 
    		@RequestParam(name = "id_task") long idTask,  	
    		@RequestParam(name = "username") String username,
    		@RequestParam(name = "info_worker") String infoWorker, @RequestParam(name = "id_job") long idJob, @RequestParam(name = "age_model") long ageModel,  @RequestParam(name = "id_grads") String idGrads, @RequestPart(name = "layers", required = false) MultipartFile[] layers) {
		try {
			log.info("getGradients init" + " Thread {} " + Thread.currentThread().toString());			
			// STATS	
			String remoteAddr = request.getRemoteAddr();
			long timeRequest = System.currentTimeMillis();				
			//
			
			Gradients g = new Gradients(idJob, ageModel, idGrads, null, null);
			if (layers != null) {
				g.setListLayersName(WeightsController.getLayersFromMultipart(layers));
			}
			
			g= gradientsService.get(g);
			if (g != null) {
				// https://stackoverflow.com/questions/52828642/how-to-send-response-as-a-multi-part
				String[] listLayers = g.getListLayersName();
				log.info("g.getListLayersName() = " + listLayers);
				List<byte[]> listGrads = g.getListGradientsBytes();
				
				MultiValueMap<String, byte[]> multipartmap
				  = new LinkedMultiValueMap<>();
				
				log.info("getGrads for (int i = 0; i < listLayers.length; i++) {" + listLayers);
				for (int i = 0; i < listLayers.length; i++) {
					multipartmap.add("layers", listLayers[i].getBytes());
				}
				
				log.info("getGrads for (int i = 0; i < listWeights.size(); i++) {" + listLayers);
				log.info("listGrads.size().size()" + listGrads.size());
				for (int i = 0; i < listGrads.size(); i++) {
					multipartmap.add("gradients", listGrads.get(i));
				}
			
				HttpHeaders responseHeaders = new HttpHeaders();
//				responseHeaders.set("current_age", "" + g.getCurrentAge());			
				ResponseEntity<LinkedMultiValueMap<String, byte[]>> response = new ResponseEntity(multipartmap, responseHeaders, HttpStatus.OK);
				// STATS	
				long timeResponse = System.currentTimeMillis();		
				String typeTask = "get_gradients";
				Stats stats = new Stats(username, infoWorker, remoteAddr, timeRequest, timeResponse, ageModel, idJob,
						typeTask, idTask, null);
				String queue1 = "stats"; //"stats_" + idJob;
				stats.setOther("id_grads=" + idGrads);
				
				amqpAdmin.declareQueue(new Queue(queue1, true));
				rabbitTemplate.convertAndSend(queue1, stats);
				//
				
				log.info("getGradients end" + " Thread {} " + Thread.currentThread().toString());
				return response;
//				return new ResponseEntity(multipartmap, HttpStatus.OK);
			} else {
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.set("current_age", "" + ageModel);
				log.info("getGradients end" + " Thread {} " + Thread.currentThread().toString());
				return new ResponseEntity("ERROR: There are no more recent weights", responseHeaders, HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("getGradients end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
		}
    }		

	@PostMapping(path = "/gradients", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity saveGradients(		
    		@RequestParam(name = "id_task") long idTask,  	
    		@RequestParam(name = "execution_time", required = false) Long executionTime,  	
			@RequestParam(name = "info_worker") String infoWorker, // TODO -> header instead of param?
			@RequestParam(name = "username") String username,
			@RequestParam(name = "id_job") long idJob, 
			@RequestParam(name = "age_model") long ageModel, 
			@RequestPart("gradients") MultipartFile[] gradients, 
			@RequestPart("layers") MultipartFile[] layers, 
			HttpServletRequest request) {
			try {
				log.info("saveGradients init" + " Thread {} " + Thread.currentThread().toString());
				log.info("inside saveGradients gradients.length = " + gradients.length);
				log.info("inside saveGradients layers.length = " + layers.length);
				// STATS	
				String remoteAddr = request.getRemoteAddr();
				long timeRequest = System.currentTimeMillis();		
				int nWorkers = statsAnalyzer.newRequest(idJob, username, infoWorker, remoteAddr, timeRequest);
				//

				
				
				String[] layersName = new String[layers.length];
				List<byte[]> listGradients = new ArrayList();
				
				//	testing....
//				INDArray loadedGradients = Nd4j.createNpyFromByteArray(gradients[0].getBytes());
//				System.out.println("loadedGradients[" + 0 + "].shape = " + Arrays.toString(loadedGradients.shape()));
			// testing .....
				
				for (int i = 0; i < layers.length; i++) {
					layersName[i] = new String(layers[i].getBytes());
					System.out.println("inside saveGradients layersName[" + i + "] = " + layersName[i]);
					listGradients.add(gradients[i].getBytes());
					System.out.println("inside saveGradients gradients[" + i + "].length = " + gradients[i].getBytes().length);
				}

				Gradients grads = new Gradients(idJob, ageModel, null, listGradients, layersName);
				Long currentAge = gradientsService.save(grads, nWorkers);

//				// GETTING NEW WEIGHTS
//				Weights w = new Weights(idJob, ageModel, null, layersName);
//				
//				w = weightsService.get(w);				
//				
//				
//				if (w != null) {
//					MultiValueMap<String, byte[]> multipartmap
//					  = new LinkedMultiValueMap<>();
//					
//					log.info("getWeights for (int i = 0; i < listLayers.length; i++) {" + layersName);
//					for (int i = 0; i < layersName.length; i++) {
//						multipartmap.add("layers", layersName[i].getBytes());
//					}
//					
//					List<byte[]> listWeights = w.getListWeightsBytes();
//					
//					log.info("inside saveGradients getWeights for (int i = 0; i < listWeights.size(); i++) {" );
//					log.info("inside saveGradients listWeights.size().size()" + listWeights.size());
//					for (int i = 0; i < listWeights.size(); i++) {
//						multipartmap.add("weights", listWeights.get(i));
//					}
//				
//					HttpHeaders responseHeaders = new HttpHeaders();
//					responseHeaders.set("current_age", "" + w.getCurrentAge());			
//					ResponseEntity<LinkedMultiValueMap<String, byte[]>> response = new ResponseEntity(multipartmap, responseHeaders, HttpStatus.CREATED);
//					log.info("inside saveGradients getWeights end");
//					log.info("saveGradients end");
//					return response;						
//				} else {
					HttpHeaders responseHeaders = new HttpHeaders();
	//			    responseHeaders.set("Location", Constants.KEY_CURRENT_AGE(idJob));
					responseHeaders.set("current_age", "" + currentAge);
					
					// STATS	
					long timeResponse = System.currentTimeMillis();		
					String typeTask = "save_gradients";
					Stats stats = new Stats(username, infoWorker, remoteAddr, timeRequest, timeResponse, ageModel, idJob,
							typeTask, idTask, executionTime);
					String queue1 = "stats"; //"stats_" + idJob;
//					stats.setOther("id_grads=" + grads.getIdGrads());
					stats.setOther("{\"id_grads\": " + grads.getIdGrads() + ",\"n_workers\": " + nWorkers + "  } ");
					
					
//					String queue2 = "stats_" + idJob + "_" + remoteAddr;
//					String queue3 = "stats_" + idJob + "_" + typeTask;
//					String queue4 = "stats_" + idJob + "_" + typeTask + "_" + remoteAddr;
					amqpAdmin.declareQueue(new Queue(queue1, true));
//					amqpAdmin.declareQueue(new Queue(queue2, false));
//					amqpAdmin.declareQueue(new Queue(queue3, false));
//					amqpAdmin.declareQueue(new Queue(queue4, false));
					rabbitTemplate.convertAndSend(queue1, stats);
//					rabbitTemplate.convertAndSend(queue2, stats);
//					rabbitTemplate.convertAndSend(queue3, stats);
//					rabbitTemplate.convertAndSend(queue4, stats);
					//
					log.info("saveGradients end" + " Thread {} " + Thread.currentThread().toString());
					return new ResponseEntity("OK", responseHeaders, HttpStatus.CREATED);					
//				}
			



			} catch (Exception e) { // (IOException e) {
				e.printStackTrace();
				log.info("saveGradients end" + " Thread {} " + Thread.currentThread().toString());
				return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
			}

	}	
	
	

	@PostMapping(path = "/delete_gradients", consumes = "multipart/form-data", produces = "text/plain")
	
	public ResponseEntity deleteGradients(@RequestParam(name = "id_job") long idJob, 			
			@RequestPart("todelete_grads") MultipartFile[] todeleteGrads) {
			try {
				log.info("deleteGradients init" + " Thread {} " + Thread.currentThread().toString());;
				

				
				String[] idsGradsToDelete = null;
				if (todeleteGrads != null) {
					idsGradsToDelete = new String[todeleteGrads.length];
					for (int i = 0; i < todeleteGrads.length; i++) {
						idsGradsToDelete[i] = new String(todeleteGrads[i].getBytes());
					}					
				} 



				HttpHeaders responseHeaders = new HttpHeaders();

			    
				if (idsGradsToDelete != null) {
					log.info("gradientsToDelete.length = " + idsGradsToDelete.length);
					for (int i = 0; i < idsGradsToDelete.length; i++) {
						Gradients gradients = new Gradients(idJob, -1, idsGradsToDelete[i], null, null);
						gradientsService.delete(gradients);
					}
				}
				log.info("deleteGradients end" + " Thread {} " + Thread.currentThread().toString());

				return new ResponseEntity("", responseHeaders, HttpStatus.CREATED);

			} catch (Exception e) { // (IOException e) {
				e.printStackTrace();
				log.info("deleteGradients end" + " Thread {} " + Thread.currentThread().toString());
				return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
			}

	}	
	
	
}
