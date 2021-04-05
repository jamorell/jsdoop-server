package com.jsdoop.server.services;

public interface JobService {
	public Long save(String job);
	public String get(long idJob);
	public void delete(long idJob);
}
