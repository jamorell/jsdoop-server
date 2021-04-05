package com.jsdoop.server.persistance.models;

public class Topology {
	private String key;
	private byte[] topology;
	
	public Topology(String key, byte[] topology) {
		super();
		this.key = key;
		this.topology = topology;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public byte[] getTopology() {
		return topology;
	}

	public void setTopology(byte[] topology) {
		this.topology = topology;
	}
	
	
}
