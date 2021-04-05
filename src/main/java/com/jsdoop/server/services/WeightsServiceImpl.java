package com.jsdoop.server.services;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jsdoop.server.persistance.dao.NNModelDAO;
import com.jsdoop.server.persistance.dao.WeightsDAO;
import com.jsdoop.server.persistance.models.Weights;
import com.jsdoop.server.persistance.models.WeightsMessage;

@Service
@Transactional
public class WeightsServiceImpl implements WeightsService {
	
	private static final Logger log = LoggerFactory.getLogger(WeightsServiceImpl.class);
	
	@Autowired
	private NNModelDAO nnmodelDAO;	
	
	@Autowired
	private WeightsDAO weightsDAO;
	
	
	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Autowired
	private AmqpAdmin amqpAdmin;
	
	@Override
	public boolean save(Weights weights, boolean forceSave) {
		log.info("save(Weights weights) init");
		Long currentAge = nnmodelDAO.getCurrentAge(weights.getIdJob());
		if (forceSave || currentAge == null || currentAge < weights.getAgeModel()) {
			if (currentAge == null) {
				currentAge = weights.getAgeModel();
			}
			weightsDAO.save(weights);
			log.info("SAVED !!");
			
			String queue1 = "weights_" + weights.getIdJob();
			amqpAdmin.declareQueue(new Queue(queue1, true));
			WeightsMessage message = new WeightsMessage(weights.getIdJob(), weights.getAgeModel());
			rabbitTemplate.convertAndSend(queue1, message);
			
			log.info("save(Weights weights) end");
			return true;
		} else {
			log.info("NOT SAVED !! Current age of the model is newer.");
			log.info("save(Weights weights) end");
			return false;
		}	
	}


	@Override
	public Weights get(Weights weights) {
		log.info("get(Weights weights) init");
		Long currentAge = nnmodelDAO.getCurrentAge(weights.getIdJob()); // currentAge must exists
		log.info("currentAge = " + currentAge);
		if (currentAge != null && weights.getAgeModel() < currentAge) {
			log.info("GETTING WEIGHTS !!");
			String[] layersNames = weights.getListLayersName();
			if (layersNames == null || layersNames.length == 0) {
				layersNames = this.nnmodelDAO.getLayersNames(weights.getIdJob());
				weights.setListLayersName(layersNames);
			}
			log.info("layersName = " + Arrays.asList(layersNames));
			log.info("layersName.length = " + layersNames.length);			
			weights.setCurrentAge(currentAge);
			return weightsDAO.get(weights);
		} else {
			log.info("NOT GETTING WEIGHTS: There are no more recent weights.");
			return null;
		}
	}
	
	@Override
	public Weights getOld(Weights weights) {
		log.info("getOld(Weights weights) init");
		String[] layersNames = weights.getListLayersName();
		if (layersNames == null || layersNames.length == 0) {
			layersNames = this.nnmodelDAO.getLayersNames(weights.getIdJob());
			weights.setListLayersName(layersNames);
		}
		Weights w =  weightsDAO.getOld(weights);
		//DELETE
		weightsDAO.deleteOldWeights(weights);
		//
		return w;
	}


	@Override
	public boolean deleteWeights(long idJob) {
		return this.weightsDAO.deleteWeights(idJob);
	}	


	
}
