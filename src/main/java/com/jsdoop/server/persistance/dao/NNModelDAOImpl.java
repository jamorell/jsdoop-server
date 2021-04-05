package com.jsdoop.server.persistance.dao;

import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.jsdoop.server.constants.Constants;


@Service
public class NNModelDAOImpl implements NNModelDAO {
	
	private static final Logger log = LoggerFactory.getLogger(NNModelDAOImpl.class);
	
	@Autowired
	RedisTemplate<String, byte[]> redis;
	
	@Override
	public Long getCurrentAge(long idJob) {
		try {
			log.info("getCurrentAge init");
			String currentAgeString = new String(redis.opsForValue().get(Constants.KEY_CURRENT_AGE(idJob)));
			log.info("getCurrentAge end");
			return Long.parseLong(currentAgeString);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("getCurrentAge end");
			return null;
		}		
	}
	
	@Override
	public String[] getLayersNames(long idJob) {
		try {
			log.info("getLayersNames init");	
			Set<String> keys = redis.keys((Constants.KEY_CURRENT_WEIGHTS(idJob) + Constants.SEP + Constants.LAYER + Constants.SEP + "*"));
	    	String[] layers = new String[keys.size()];
	    	String pre = Constants.KEY_CURRENT_WEIGHTS(idJob) + Constants.SEP + Constants.LAYER + Constants.SEP;
	    	
			
	    	Iterator<String> it = keys.iterator();
			int counter = 0;
			while (it.hasNext()) {
				String key = it.next();
				layers[counter] = key.substring(pre.length(), key.length());
				counter ++;
			}
			log.info("getLayersNames end");
			return layers;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("getLayersNames end");
			return null;
		}		
	}
	
	
	@Override
	public String[] getOldLayersNames(long idJob, long ageModel) {
		try {
			log.info("getOldLayersNames init");	
			Set<String> keys = redis.keys((Constants.KEY_CURRENT_WEIGHTS(idJob) + Constants.SEP + Constants.LAYER + Constants.SEP + "*"));
	    	String[] layers = new String[keys.size()];
//	    	String pre = Constants.KEY_CURRENT_WEIGHTS(idJob) + Constants.SEP + Constants.LAYER + Constants.SEP;
	    	String pre = Constants.KEY_WEIGHTS(idJob, ageModel) + Constants.SEP + Constants.LAYER + Constants.SEP;
			
	    	Iterator<String> it = keys.iterator();
			int counter = 0;
			while (it.hasNext()) {
				String key = it.next();
				layers[counter] = key.substring(pre.length(), key.length());
				counter ++;
			}
			log.info("getOldLayersNames end");
			return layers;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("getOldLayersNames end");
			return null;
		}		
	}

}
