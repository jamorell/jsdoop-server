package com.jsdoop.server.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
import com.jsdoop.server.persistance.models.Weights;
import com.jsdoop.server.services.GradientsService;
import com.jsdoop.server.services.WeightsService;

@RestController
public class WeightsController {
	private static final Logger log = LoggerFactory.getLogger(WeightsController.class);

	@Autowired
	private WeightsService weightsService;
	
	@Autowired
	private GradientsService gradientsService;
	
	
	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Autowired
	private AmqpAdmin amqpAdmin;
	
	public static String[] getLayersFromMultipart(MultipartFile[] layersFile) {
		try {
			String[] layersNames = new String[layersFile.length];
			for (int i = 0; i < layersFile.length; i++) {
				layersNames[i] = new String(layersFile[i].getBytes());
			}		
			return layersNames;			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// @PostMapping(path = "/testing_weights", consumes = "multipart/form-data",
	// produces = "multipart/form-data")
	@PostMapping(path = "/current_weights", consumes = "multipart/form-data", produces = "text/plain")
	//public String testingGradients(@RequestPart("weights") byte[][] weights, @RequestPart("layers") byte[][] layers) {
	public ResponseEntity saveWeights(HttpServletRequest request, @RequestParam(name = "info_worker") String infoWorker, @RequestParam(name = "id_job") long idJob, @RequestParam(name = "age_model") long ageModel, 
//			@RequestParam(name = "id_grads_to_delete", required = false) String[] idsGradsToDelete, @RequestParam(name = "force_save", required = false) String forceSave,
    		@RequestParam(name = "id_task") long idTask,  	
    		@RequestParam(name = "execution_time", required = false) Long executionTime,  	
    		@RequestParam(name = "n_accumulated_gradients", required = false) Long nAccumulatedGradients,  	
			@RequestParam(name = "username") String username,
			@RequestPart("used_grads") MultipartFile[] usedGrads,
			@RequestPart("todelete_grads") MultipartFile[] todeleteGrads, @RequestParam(name = "force_save", required = false) String forceSave,
			@RequestPart("weights") MultipartFile[] weights, @RequestPart("layers") MultipartFile[] layers) {
			try {
				log.info("saveWeights init" + " Thread {} " + Thread.currentThread().toString());
				log.info("weights.length = " + weights.length);
				log.info("layers.length = " + layers.length);
				log.info("force_save = " + forceSave);
				
				// STATS	
				String remoteAddr = request.getRemoteAddr();
				long timeRequest = System.currentTimeMillis();				
				//
				
				String[] layersName = new String[layers.length];
				List<byte[]> listWeights = new ArrayList();
				for (int i = 0; i < layers.length; i++) {
					layersName[i] = new String(layers[i].getBytes());
					listWeights.add(weights[i].getBytes());
				}
				
				String[] idsGradsToDelete = null;
				if (todeleteGrads != null) {
					idsGradsToDelete = new String[todeleteGrads.length];
					for (int i = 0; i < todeleteGrads.length; i++) {
						idsGradsToDelete[i] = new String(todeleteGrads[i].getBytes());
					}					
				} 
				ArrayList<String> usedGradsString = new ArrayList();
				if (usedGrads != null) {
					for (int i = 0; i < usedGrads.length; i++) {
						usedGradsString.add(new String(usedGrads[i].getBytes()));
					}					
				} 


				
//				if (idsGradsToDelete == null) {
//					log.info("empty array");
//					idsGradsToDelete = new String[0];
//				}
				Weights w = new Weights(idJob, ageModel,listWeights, layersName);
				boolean saved;
				if (forceSave == null || forceSave.isBlank()) {
					saved = weightsService.save(w, false);					
				} else {
					saved = weightsService.save(w, true);
				}
//		
				HttpHeaders responseHeaders = new HttpHeaders();
//			    responseHeaders.set("Location", Constants.KEY_CURRENT_AGE(idJob));
				responseHeaders.set("current_age", "" + w.getAgeModel());
			    
				if (saved) {
					if (idsGradsToDelete != null) {
						log.info("gradientsToDelete.length = " + idsGradsToDelete.length);
						for (int i = 0; i < idsGradsToDelete.length; i++) {
//							Gradients gradients = new Gradients(idJob, ageModel, idGrads, listGradientsBytes, listLayersName) {
							Gradients gradients = new Gradients(idJob, ageModel, idsGradsToDelete[i], null, null);
							gradientsService.delete(gradients);
						}
					}
					// STATS	
					long timeResponse = System.currentTimeMillis();		
					String typeTask = "save_weights";
					Stats stats = new Stats(username, infoWorker, remoteAddr, timeRequest, timeResponse, ageModel, idJob,
							typeTask, idTask, executionTime);
					//stats.setOther(String.join(", ", usedGradsString)); // We are not using this
					stats.setOther("{n_accumulated_gradients: " + nAccumulatedGradients+ "}");	
					
					String queue1 = "stats"; //"stats_" + idJob;
					amqpAdmin.declareQueue(new Queue(queue1, true));
					rabbitTemplate.convertAndSend(queue1, stats);
					//
					
					log.info("saveWeights end" + " Thread {} " + Thread.currentThread().toString());

					return new ResponseEntity("", responseHeaders, HttpStatus.CREATED);
				} else {
					log.info("saveWeights end" + " Thread {} " + Thread.currentThread().toString());
					return new ResponseEntity("ERROR: Already Exists.", responseHeaders, HttpStatus.CONFLICT);
				}


			} catch (Exception e) { // (IOException e) {
				e.printStackTrace();
				return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
			}

	}	
	
	
	// TODO -> Se podrían enviar las layers de las que se quieren los pesos (no todas)
	@GetMapping(value="/current_weights", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity getWeights(
    		HttpServletRequest request,
    		@RequestParam(name = "id_task") long idTask,  			
    		@RequestParam(name = "username") String username,
    		@RequestParam(name = "info_worker") String infoWorker, @RequestParam(name = "id_job") long idJob, @RequestParam(name = "age_model") long ageModel,  @RequestPart(name = "layers", required = false) MultipartFile[] layers) {
		try {
			log.info("getWeights init" + " Thread {} " + Thread.currentThread().toString());
			// STATS	
			String remoteAddr = request.getRemoteAddr();
			long timeRequest = System.currentTimeMillis();				
			//
			Weights w = new Weights(idJob, ageModel, null, null);
			if (layers != null) {
				w.setListLayersName(this.getLayersFromMultipart(layers));
			}
			
			w = weightsService.get(w);
			if (w != null) {
				// https://stackoverflow.com/questions/52828642/how-to-send-response-as-a-multi-part
				String[] listLayers = w.getListLayersName();
				log.info("w.getListLayersName() = " + listLayers);
				List<byte[]> listWeights = w.getListWeightsBytes();
				
				MultiValueMap<String, byte[]> multipartmap
				  = new LinkedMultiValueMap<>();
				
				log.info("getWeights for (int i = 0;) i < listLayers.length; i++) {" + listLayers);
				for (int i = 0; i < listLayers.length; i++) {
					multipartmap.add("layers", listLayers[i].getBytes());
				}
				
				log.info("getWeights for (int i = 0; i < listWeights.size(); i++) {" + listLayers);
				log.info("listWeights.size().size()" + listWeights.size());
				for (int i = 0; i < listWeights.size(); i++) {
					multipartmap.add("weights", listWeights.get(i));
				}
			
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.set("current_age", "" + w.getCurrentAge());			
//				responseHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
//				HttpEntity<LinkedMultiValueMap<String, byte[]>> response = new HttpEntity(multipartmap, responseHeaders);
//		        response = restTemplate.postForObject(url, response, byte[].class);

				ResponseEntity<LinkedMultiValueMap<String, byte[]>> response = new ResponseEntity(multipartmap, responseHeaders, HttpStatus.OK);
				
				
				// STATS	
				long timeResponse = System.currentTimeMillis();		
				String typeTask = "get_weights";
				Stats stats = new Stats(username, infoWorker, remoteAddr, timeRequest, timeResponse, ageModel, idJob,
						typeTask, idTask, null);
				String queue1 = "stats"; //"stats_" + idJob;
				amqpAdmin.declareQueue(new Queue(queue1, true));
				rabbitTemplate.convertAndSend(queue1, stats);
				//
				
				log.info("getWeights end" + " Thread {} " + Thread.currentThread().toString());
				return response;
//				return new ResponseEntity(multipartmap, HttpStatus.OK);
			} else {
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.set("current_age", "" + ageModel);

				log.info("getWeights end" + " Thread {} " + Thread.currentThread().toString());

				return new ResponseEntity("ERROR: There are no more recent weights", responseHeaders, HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("getWeights end" + " Thread {} " + Thread.currentThread().toString());
			return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
		}
    }	
	
    @GetMapping("/delete_weights")
    public ResponseEntity deleteWeights(@RequestParam(name = "id_job") String idJob) {
    	try {
        	log.info("deleteWeights init" + " Thread {} " + Thread.currentThread().toString());
        	boolean deleted = weightsService.deleteWeights(Long.parseLong(idJob));
        	if (deleted) {
        		log.info("deleteWeights end" + " Thread {} " + Thread.currentThread().toString());
        		return new ResponseEntity(HttpStatus.OK);
        	} else {
        		log.info("deleteWeights end" + " Thread {} " + Thread.currentThread().toString());
        		return new ResponseEntity("ERROR: It could not be removed", HttpStatus.CONFLICT);
        	}   		
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
    	}
    }
    
    

	@PostMapping(path = "/weights", consumes = "multipart/form-data", produces = "text/plain")
//	public String testingApplyingWeights(@RequestPart("file")  Map<String, MultipartFile> files) {
	public String testingApplyingWeights(@RequestPart("files") MultipartFile[] files) {
//	public String testingApplyingWeights(MultipartFormDataInput input) {
//	public String testingApplyingWeights(@RequestParam("file") MultipartFile[] file, @RequestParam("files") MultipartFile[] files, ModelMap modelMap) {

//		try {
//			System.out.println("files.length " + files.keySet().size());
//			int i = 0;
//		     Iterator<String> it = files.keySet().iterator();
//		     while(it.hasNext()){
//		        byte[] bytes = it.next().getBytes();
//				System.out.println("received weights[" + i + "].length " + bytes.length);
//				System.out.println("received weights[" + i + "] " + bytes);
//				
//				INDArray loadedweights = Nd4j.createNpyFromByteArray(bytes);
//				
//				System.out.println("loadedweights[" + i + "].shape = " + loadedweights.shape());
//				i++;
//		        System.out.println("shape = " + loadedweights.shape().length);
//		        
//		     }
//		     return "OK";	

		try {

			System.out.println("files.length " + files.length);
			for (int i = 0; i < files.length; i++) {
				byte[] bytes = files[i].getBytes();
				// System.out.println("received weights[" + i + "].length " + bytes.length);
				// System.out.println("received weights[" + i + "] " + bytes);

				INDArray loadedweights = Nd4j.createNpyFromByteArray(bytes);

				System.out.println("loadedweights[" + i + "].shape = " + Arrays.toString(loadedweights.shape()));

			}
			return "OK";

//			System.out.println("received weights.length " + weights.length);
//			System.out.println("received weights " + weights);
//			
//			INDArray loadedweights = Nd4j.createNpyFromByteArray(weights);

//			System.out.println("loadedweights.shape = " + loadedweights.shape());
//
//	        return "" + loadedweights.shape();

		} catch (Exception e) { // (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "ERROR: No se pudo hacer el summary()";
	}    
	
	
	// TODO -> Se podrían enviar las layers de las que se quieren los pesos (no todas)
	@GetMapping(value="/old_weights", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE}, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity getOldWeights(@RequestParam(name = "id_job") long idJob, @RequestParam(name = "age_model") long ageModel) {
		log.info("getOldWeights init");
		try {
			// STATS	
			long timeRequest = System.currentTimeMillis();				
			//
			Weights w = new Weights(idJob, ageModel, null, null);
	
			
			w = weightsService.getOld(w);
			if (w != null) {
				// https://stackoverflow.com/questions/52828642/how-to-send-response-as-a-multi-part
				String[] listLayers = w.getListLayersName();
				log.info("w.getListLayersName() = " + listLayers);
				List<byte[]> listWeights = w.getListWeightsBytes();
				
				MultiValueMap<String, byte[]> multipartmap
				  = new LinkedMultiValueMap<>();
				
				log.info("getWeights for (int i = 0;) i < listLayers.length; i++) {" + listLayers);
				for (int i = 0; i < listLayers.length; i++) {
					multipartmap.add("layers", listLayers[i].getBytes());
				}
				
				log.info("getWeights for (int i = 0; i < listWeights.size(); i++) {" + listLayers);
				log.info("listWeights.size().size()" + listWeights.size());
				for (int i = 0; i < listWeights.size(); i++) {
					multipartmap.add("weights", listWeights.get(i));
				}
			
				HttpHeaders responseHeaders = new HttpHeaders();
				
				
				// STATS	
				long timeResponse = System.currentTimeMillis();				
				responseHeaders.add("timeRequest", "" + timeRequest);
				responseHeaders.add("timeResponse", "" + timeResponse);
				//
				
				
				ResponseEntity<LinkedMultiValueMap<String, byte[]>> response = new ResponseEntity(multipartmap, responseHeaders, HttpStatus.OK);
				log.info("multipartmap " + multipartmap );
				log.info("getOldWeights end");
				return response;
//				return new ResponseEntity(multipartmap, HttpStatus.OK);
			} else {
				HttpHeaders responseHeaders = new HttpHeaders();
				log.info("getOldWeights end");
				return new ResponseEntity("ERROR: Old weights not found", responseHeaders, HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("getWeights end");
			return new ResponseEntity("ERROR: Bad Request", HttpStatus.BAD_REQUEST);
		}
    }	
    
}
