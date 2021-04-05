package com.jsdoop.server.persistance.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.jsdoop.server.persistance.models.Dataset;


@Service
public class DatasetDAOImpl implements DatasetDAO {
	
	private static final Logger log = LoggerFactory.getLogger(TopologyDAOImpl.class);
	
	@Autowired
	RedisTemplate<String, byte[]> redis;
	
	@Override
	public void save(Dataset element) {
		log.info("save(Dataset element) init");
		redis.opsForValue().set(element.getKey(), element.getDataset());
//		jedis.set(element.getKey().getBytes(), element.getDataset());
//		jedis.close();
		log.info("save(Dataset element) end");
	}

	@Override
	public Dataset get(String key) {
		log.info("get(Dataset element) init");
		byte[] datasetBytes = redis.opsForValue().get(key);
		Dataset dataset = new Dataset(key, datasetBytes);
		dataset.setDataset(datasetBytes);
//		byte[] datasetBytes = jedis.get(key.getBytes());
//		Dataset dataset = new Dataset(key, datasetBytes);
//		dataset.setDataset(datasetBytes);
//		jedis.close();
		log.info("get(Dataset element) end");
		return dataset;
	}

}
