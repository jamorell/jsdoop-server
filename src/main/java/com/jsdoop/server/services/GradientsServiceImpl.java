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

import com.jsdoop.server.persistance.dao.GradientsDAO;
import com.jsdoop.server.persistance.dao.NNModelDAO;
import com.jsdoop.server.persistance.models.Gradients;
import com.jsdoop.server.persistance.models.GradientsMessage;


@Service
@Transactional
public class GradientsServiceImpl implements GradientsService {
	
	private static final Logger log = LoggerFactory.getLogger(GradientsServiceImpl.class);
	
	@Autowired
	private NNModelDAO nnmodelDAO;	
	
	@Autowired
	private GradientsDAO gradientsDAO;
	
//	@Autowired
//	private RabbitMQSender rabbitMQSender;
	
	@Autowired
	private AmqpTemplate rabbitTemplate;
	
	@Autowired
	private AmqpAdmin amqpAdmin;
	
	
//	@Autowired
//	JsonMapperService jacksonMapper;
	
	@Override
	public Long save(Gradients grads, int nWorkers) {
		String gradsID = gradientsDAO.save(grads);
		grads.setIdGrads(gradsID);
		
		//(long idWorker, long idJob, long ageModel, String key) 
		try {
			GradientsMessage message = new GradientsMessage(0,0,0,gradsID, nWorkers);
			String queue1 = "grads_" + grads.getIdJob();
			amqpAdmin.declareQueue(new Queue(queue1, true));
			rabbitTemplate.convertAndSend(queue1, message);
			Long currentAge = nnmodelDAO.getCurrentAge(grads.getIdJob());
			return currentAge;
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
		
		
//		rabbitMQSender.sendGradsLocation(gradsID);
	}

	@Override
	public Gradients get(Gradients grads) {
		log.info("INIT public Gradients get(Gradients grads) {");
		String[] layersNames = grads.getListLayersName();
		if (layersNames == null || layersNames.length == 0) {
			layersNames = this.nnmodelDAO.getLayersNames(grads.getIdJob());
			grads.setListLayersName(layersNames);
		}
		log.info("layersNames = " + Arrays.asList(layersNames)) ;
		log.info("END public Gradients get(Gradients grads) {");		
		return gradientsDAO.get(grads);
	}

	@Override
	public void delete(Gradients gradients) {
		String[] layersNames = gradients.getListLayersName();
		if (layersNames == null || layersNames.length == 0) {
			layersNames = this.nnmodelDAO.getLayersNames(gradients.getIdJob());
			gradients.setListLayersName(layersNames);
		}
		gradientsDAO.delete(gradients);
		
	}

	@Override
	public void deleteAll(long idJob) {
		gradientsDAO.deleteAll(idJob);
		
	}

}
