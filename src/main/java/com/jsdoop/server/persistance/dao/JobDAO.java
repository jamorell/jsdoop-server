package com.jsdoop.server.persistance.dao;

public interface JobDAO {
	public Long save(long idJob, String job);
	public String get(long idJob);
	public void delete(long idJob);
}
