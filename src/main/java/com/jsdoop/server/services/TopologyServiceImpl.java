package com.jsdoop.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jsdoop.server.persistance.dao.TopologyDAO;
import com.jsdoop.server.persistance.models.Topology;

@Service
@Transactional
public class TopologyServiceImpl implements TopologyService {

	@Autowired
	private TopologyDAO topologyDAO;
	
	@Override
	public void save(Topology topology) {
		topologyDAO.save(topology);
		
	}

	@Override
	public Topology get(String key) {
		return topologyDAO.get(key);
	}

}
