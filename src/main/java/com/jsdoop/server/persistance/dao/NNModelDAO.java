package com.jsdoop.server.persistance.dao;


public interface NNModelDAO {
	public Long getCurrentAge(long idJob);
	public String[] getLayersNames(long idJob);
	String[] getOldLayersNames(long idJob, long ageModel);
}
