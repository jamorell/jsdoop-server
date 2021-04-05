package com.jsdoop.server.persistance.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.jsdoop.server.persistance.models.Topology;

@Service
public class TopologyDAOImpl implements TopologyDAO {

	private static final Logger log = LoggerFactory.getLogger(TopologyDAOImpl.class);
	
	@Autowired
	RedisTemplate<String, byte[]> redis;
	
	@Override
	public void save(Topology topology) {
		log.info("save(Topology topology) init");
		redis.opsForValue().set(topology.getKey(), topology.getTopology());
//		jedis.set(topology.getKey().getBytes(), topology.getTopology());
//		jedis.close();
		log.info("save(Topology topology) end");
	}

	@Override
	public Topology get(String key) {
		log.info("get(Topology topology) init");
		log.info("topology key = " + key);
		byte[] topologyBytes = redis.opsForValue().get(key);

		log.info("topology bytes = " + topologyBytes.length);
//		byte[] topologyBytes = jedis.get(key.getBytes());
		Topology topology = new Topology(key, topologyBytes);
		topology.setTopology(topologyBytes);
//		jedis.close();
		log.info("get(Topology topology) end");
		return topology;
	}

}
