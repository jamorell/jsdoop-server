package com.jsdoop.server.persistance.models;

import java.io.Serializable;

public class GradientsMessage implements Serializable {
//{ "id_worker" : id_worker, "id_job" : id_job, "age_model" : age_model, "key" : id_grads_or_w, "stats" : {} }
	
	private long idWorker;
	private long idJob;
	private long ageModel;
	private String key;
//	private Stats stats;
	private int nWorkers;
	
	public GradientsMessage(long idWorker, long idJob, long ageModel, String key, int nWorkers) {
		this.idWorker = idWorker;
		this.idJob = idJob;
		this.ageModel = ageModel;
		this.key = key;
		this.nWorkers = nWorkers;
	}
	
	public long getIdWorker() {
		return idWorker;
	}
	public void setIdWorker(long idWorker) {
		this.idWorker = idWorker;
	}
	public long getIdJob() {
		return idJob;
	}
	public void setIdJob(long idJob) {
		this.idJob = idJob;
	}
	public long getAgeModel() {
		return ageModel;
	}
	public void setAgeModel(long ageModel) {
		this.ageModel = ageModel;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public int getnWorkers() {
		return nWorkers;
	}

	public void setnWorkers(int nWorkers) {
		this.nWorkers = nWorkers;
	}
	
}
