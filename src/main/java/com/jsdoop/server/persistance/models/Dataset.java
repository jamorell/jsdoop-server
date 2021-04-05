package com.jsdoop.server.persistance.models;

public class Dataset {

	private String key;
	private byte[] dataset;
	
	public Dataset(String key, byte[] dataset) {
		super();
		this.key = key;
		this.dataset = dataset;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public byte[] getDataset() {
		return dataset;
	}

	public void setDataset(byte[] dataset) {
		this.dataset = dataset;
	}
	
	
}
