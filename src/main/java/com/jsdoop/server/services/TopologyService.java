package com.jsdoop.server.services;


import com.jsdoop.server.persistance.models.Topology;

public interface TopologyService {
	public void save(Topology topology);
	public Topology get(String key);
}
