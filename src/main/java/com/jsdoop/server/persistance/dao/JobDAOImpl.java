package com.jsdoop.server.persistance.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class JobDAOImpl implements JobDAO {
	
	private static final Logger log = LoggerFactory.getLogger(JobDAOImpl.class);
	
	@Autowired
	RedisTemplate<String, byte[]> redis;
	
	
	@Override
	public Long save(long idJob, String job) {
		log.info("INIT public long save(String job) {");

		try {
//			Long idJob = System.currentTimeMillis();
			redis.opsForValue().set("job_" + idJob, job.getBytes());
			log.info("END public long save(String job) {");
			return idJob;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("END public long save(String job) {");
			return null;
		}
	}

	@Override
	public String get(long idJob) {
		log.info("INIT public String get(long idJob) {");
		String toReturn = null;
		try {
			toReturn = new String(redis.opsForValue().get("job_" + idJob));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("END public String get(long idJob) {");
		return toReturn;
	}

	@Override
	public void delete(long idJob) {
		redis.delete("job_" + idJob);
		
	}

}
