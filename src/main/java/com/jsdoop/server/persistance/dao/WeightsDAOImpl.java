package com.jsdoop.server.persistance.dao;

import java.util.ArrayList;
import java.util.List;
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
import com.jsdoop.server.persistance.models.Weights;




@Service
public class WeightsDAOImpl implements WeightsDAO {
	
	private static final Logger log = LoggerFactory.getLogger(WeightsDAOImpl.class);
	
	@Autowired
	RedisTemplate<String, byte[]> redis;
	
	@Override
	public void save(Weights weights) {
		try {
			log.info("save(Weights weights) init");
			redis.setEnableTransactionSupport(true);
			long idJob = weights.getIdJob();
			String[] layersName =  weights.getListLayersName();
			ArrayList<byte[]> weightsByteArr =  (ArrayList<byte[]>) weights.getListWeightsBytes();
			List<Object> results = redis.executePipelined(new RedisCallback<byte[]>() {
			    public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
			    	connection.watch(Constants.KEY_CURRENT_AGE(idJob).getBytes());
			    	connection.multi();
			    	for (int i = 0; i < layersName.length; i++) {
			    		connection.set((Constants.KEY_CURRENT_WEIGHTS(idJob) + Constants.SEP + Constants.LAYER + Constants.SEP + layersName[i]).getBytes(), weightsByteArr.get(i));
			    		connection.set((Constants.KEY_WEIGHTS(idJob, weights.getAgeModel()) + Constants.SEP + Constants.LAYER + Constants.SEP + layersName[i]).getBytes(), weightsByteArr.get(i));
			    	}
			    	connection.set(Constants.KEY_CURRENT_AGE(idJob).getBytes(), ("" + weights.getAgeModel()).getBytes() );
			    	

			    	connection.exec();
			    	return null;
			     }
			});			
			log.info("save(Weights weights) end");	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("save(Weights weights) end");	
		}		
	}

	@Override
	public Weights get(Weights weights) {
		try {
			log.info("ggget(Weights weights) init");
			
			long idJob = weights.getIdJob();
			String[] layersName =  weights.getListLayersName();
			log.info("total layers = " + layersName.length);
//			ArrayList<byte[]> weightsByteArr =  (ArrayList<byte[]>) weights.getListWeightsBytes();
			List<String> keys = new ArrayList();
			for (int i = 0; i < layersName.length; i++) {
				String key = Constants.KEY_CURRENT_WEIGHTS(idJob) + Constants.SEP + Constants.LAYER + Constants.SEP + layersName[i];
//				String key = Constants.KEY_WEIGHTS(idJob, weights.getAgeModel()) + Constants.SEP + Constants.LAYER + Constants.SEP + layersName[i];
				keys.add(key);
			}
			
			redis.setEnableTransactionSupport(true);

			redis.watch(Constants.KEY_CURRENT_AGE(idJob));
			redis.multi();			
//			List<byte[]> listWeights = redis.opsForValue().multiGet(keys);
			redis.opsForValue().multiGet(keys);
			List<Object> results = redis.exec();
			
			log.info("************results.size() " + results.size());
			List<byte[]> listWeights = null;
			if (results != null && results.size() >=1) {
				listWeights = (List<byte[]>) results.get(0);
			}
//			System.exit(-1);
			weights.setListWeightsBytes(listWeights);
			log.info("get(Weights weights) end");			
			return weights;			

	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("get(Weights weights) end");
			return null;
		}

	}

	
	@Override
	public Weights getOld(Weights weights) {
		try {
			log.info("getOld(Weights weights) init");
			
			long idJob = weights.getIdJob();
			String[] layersName =  weights.getListLayersName();
			log.info("total layers = " + layersName.length);
//			ArrayList<byte[]> weightsByteArr =  (ArrayList<byte[]>) weights.getListWeightsBytes();
			List<String> keys = new ArrayList();
			for (int i = 0; i < layersName.length; i++) {
//				String key = Constants.KEY_CURRENT_WEIGHTS(idJob) + Constants.SEP + Constants.LAYER + Constants.SEP + layersName[i];
				String key = Constants.KEY_WEIGHTS(idJob, weights.getAgeModel()) + Constants.SEP + Constants.LAYER + Constants.SEP + layersName[i];
				keys.add(key);
			}
			
			redis.setEnableTransactionSupport(true);

			redis.watch(Constants.KEY_CURRENT_AGE(idJob));
			redis.multi();			
//			List<byte[]> listWeights = redis.opsForValue().multiGet(keys);
			redis.opsForValue().multiGet(keys);
			List<Object> results = redis.exec();
			
			log.info("************results.size() " + results.size());
			List<byte[]> listWeights = null;
			if (results != null && results.size() >=1) {
				listWeights = (List<byte[]>) results.get(0);
			}
//			System.exit(-1);
			weights.setListWeightsBytes(listWeights);
			

			log.info("getOld(Weights weights) end");			
			return weights;			

	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("getOld(Weights weights) end");
			return null;
		}

	}
	
	@Override
	public boolean deleteOldWeights(Weights w) {
		try {
			log.info("deleteOldWeights(long idJob, long ageModel) init");
			
			String[] layers = w.getListLayersName();
			
			ArrayList<String> keys = new ArrayList();
			for (int i = 0; i < layers.length; i++) {
				keys.add(Constants.KEY_WEIGHTS(w.getIdJob(), w.getAgeModel()) + Constants.SEP + Constants.LAYER + Constants.SEP + layers[i]);
			}			
			redis.delete(keys);
			
	
			log.info("deleteOldWeights(long idJob, long ageModel) end");				
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("deleteOldWeights(long idJob, long ageModel) end");	
			return false;
		}
	}
	
	
	@Override
	public boolean deleteWeights(long idJob) {
		try {
			log.info("deleteWeights(long idJob)  init");
			log.info("deletting " + Constants.KEY_WEIGHTS_DEL(idJob)+ "*");
			Set<String> keys = redis.keys(Constants.KEY_WEIGHTS_DEL(idJob) + "*");
			redis.delete(keys);
			log.info("deletting " + Constants.KEY_CURRENT_WEIGHTS(idJob) + "*");
			keys = redis.keys(Constants.KEY_CURRENT_WEIGHTS(idJob) + "*");
			redis.delete(keys);		
			log.info("deletting " + Constants.KEY_CURRENT_AGE(idJob));
			redis.delete(Constants.KEY_CURRENT_AGE(idJob));	

			
//			List<Object> results = redis.executePipelined(new RedisCallback<byte[]>() {
//			    public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
//			    	Set<byte[]> toDelete = connection.keys((Constants.KEY_CURRENT_WEIGHTS(idJob) + "*").getBytes());			    	
//			    	connection.del(toDelete.toArray(new byte[toDelete.size()][]));
//			    	toDelete = connection.keys((Constants.KEY_WEIGHTS_DEL(idJob) + "_" + "*").getBytes());
//			    	connection.del(toDelete.toArray(new byte[toDelete.size()][]));
//			    	connection.del(Constants.KEY_CURRENT_AGE(idJob).getBytes());
//			    	return null;
//			     }
//			});			
//			log.info("results.size() " + results.size());
			log.info("deleteWeights(long idJob)  end");				
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("deleteWeights(long idJob)  end");	
			return false;
		}
	}


}
