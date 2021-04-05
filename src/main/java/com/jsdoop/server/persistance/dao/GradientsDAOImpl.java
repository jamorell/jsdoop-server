package com.jsdoop.server.persistance.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.jsdoop.server.constants.Constants;
import com.jsdoop.server.persistance.models.Gradients;



@Service
public class GradientsDAOImpl implements GradientsDAO {

	private static final Logger log = LoggerFactory.getLogger(GradientsDAOImpl.class);
	
	@Autowired
	RedisTemplate<String, byte[]> redis;
	

	@Override
	public String save(Gradients grads) {
		log.info("save(Gradients grads) init");
		//  id_queue = id_job + "_gradients"
		// id_grads_or_w = age_model + "_" + (str(random())[-4:]) + str(round(time.time()))[-6:] # Last 6 characters (MS)
		String timeId = "" + System.currentTimeMillis();
		timeId = timeId.substring(timeId.length()- 6);
		String randomId = "" + new Random().nextLong();
		while(randomId.length() < 6) {
			randomId += "0";
		}
		randomId = randomId.substring(randomId.length()- 4);
		String gradsIdString = grads.getAgeModel() + "_" + timeId + randomId;
		
		System.out.println("gradsIdString = " + gradsIdString);
		try {
//			grads.setIdGrads(Long.parseLong(gradsIdString));
			grads.setIdGrads(gradsIdString);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		long idJob = grads.getIdJob();
		
		String[] layersName =  grads.getListLayersName();
		ArrayList<byte[]> gradsByteArr =  (ArrayList<byte[]>) grads.getListGradientsBytes();
		List<Object> results = redis.executePipelined(new RedisCallback<byte[]>() {
		    public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
		    	for (int i = 0; i < layersName.length; i++) {
		    		connection.set((Constants.KEY_GRADS_BASE(idJob) + Constants.SEP + grads.getIdGrads() + Constants.SEP 
		    				+ Constants.LAYER + Constants.SEP + layersName[i]).getBytes(), gradsByteArr.get(i));
		    	}
		    	return null;
		     }
		});		
	
		log.info("save(Gradients grads) end");
		return gradsIdString;
	}	
	


	@Override
	public Gradients get(Gradients grads) {
		try {
			log.info("get(Gradients grads) init");
			String gradsIdString = grads.getIdGrads().toString();
			long idJob = grads.getIdJob();
			String[] layersName =  grads.getListLayersName();
			ArrayList<byte[]> gradsByteArr =  (ArrayList<byte[]>) grads.getListGradientsBytes();
			
			List<String> keys = new ArrayList();
			for (int i = 0; i < layersName.length; i++) {
				String key = Constants.KEY_GRADS_BASE(idJob) + Constants.SEP + gradsIdString  + Constants.SEP + Constants.LAYER + Constants.SEP + layersName[i];
				log.info("getting grad key = " + key);
				keys.add(key);
			}
			List<byte[]> listGrads = redis.opsForValue().multiGet(keys);
			grads.setListGradientsBytes(listGrads);
			log.info("get(Gradients grads) end");	
			return grads;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("get(Gradients grads) end");
			return null;
		}
	}



	@Override
	public void delete(Gradients gradients) {
		try {
			log.info("delete(Gradients gradients) init");
			String[] layers = gradients.getListLayersName();
			
			ArrayList<String> keys = new ArrayList();
			for (int i = 0; i < layers.length; i++) {
				keys.add(Constants.KEY_GRADS_BASE(gradients.getIdJob()) + Constants.SEP + gradients.getIdGrads()  + Constants.SEP + Constants.LAYER + Constants.SEP + layers[i]);
			}			
			redis.delete(keys);
			log.info("delete(Gradients gradients) end");				

		} catch (Exception e) {
			e.printStackTrace();
			log.info("delete(Gradients gradients) end");	
		}
	}

	
	@Override
	public void deleteAll(long idJob) {
		try {
			log.info("deleteAll(long idJob) init");
			String pattern = Constants.KEY_GRADS_BASE(idJob) + Constants.SEP + "*";
			log.info("deletting " + Constants.KEY_GRADS_BASE(idJob) + Constants.SEP + "*");
			Set<String> keys = redis.keys(pattern);
			log.info("total grads to delete = " + keys.size());
			redis.delete(keys);
			log.info("deleteAll(long idJob) end");				

		} catch (Exception e) {
			e.printStackTrace();
			log.info("deleteAll(long idJob) end");	
		}
	}
}
