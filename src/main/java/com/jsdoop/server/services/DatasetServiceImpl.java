package com.jsdoop.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jsdoop.server.persistance.dao.DatasetDAO;
import com.jsdoop.server.persistance.models.Dataset;

@Service
@Transactional
public class DatasetServiceImpl implements DatasetService {

	@Autowired
	private DatasetDAO datasetDAO;
	
	@Override
	public void save(Dataset dataset) {
		datasetDAO.save(dataset);
		
	}

	@Override
	public Dataset get(String key) {
		return datasetDAO.get(key);
	}

}
