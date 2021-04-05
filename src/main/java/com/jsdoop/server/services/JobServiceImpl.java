package com.jsdoop.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jsdoop.server.persistance.dao.JobDAO;

@Service
public class JobServiceImpl implements JobService {

	private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);
	
	@Autowired
	private JobDAO jobDAO;
	
	@Autowired
	private ObjectMapper jacksonObjectMapper;
	
	@Override
	public Long save(String job) {
		log.info("INIT public long save(String job) {");
		try {
			//Checking if it is correct JSON
			JsonNode jsonNode = jacksonObjectMapper.readTree(job);
			//((ObjectNode)jsonNode).put("value", "NO");
			Long idJob = System.currentTimeMillis();
			((ObjectNode)jsonNode).put("id_job", idJob);
			job = jsonNode.toString();
			idJob = jobDAO.save(idJob, job);
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
		log.info("INIT public String get(String key) {");
		String toReturn =  jobDAO.get(idJob);
		log.info("END public String get(String key) {");
		return toReturn;
	}

	@Override
	public void delete(long idJob) {
		log.info("INIT public void delete(long idJob) {");		
		jobDAO.delete(idJob);
		log.info("END public void delete(long idJob) {");		
	}

}
