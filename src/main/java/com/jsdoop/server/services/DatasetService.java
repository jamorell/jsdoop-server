package com.jsdoop.server.services;

import com.jsdoop.server.persistance.models.Dataset;

public interface DatasetService {
	public void save(Dataset dataset);
	public Dataset get(String key);
}
