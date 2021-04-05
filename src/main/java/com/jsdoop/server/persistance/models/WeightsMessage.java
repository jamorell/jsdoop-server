package com.jsdoop.server.persistance.models;

public class WeightsMessage {
	private long idJob;
	private long ageModel;
	

	public WeightsMessage(long idJob, long ageModel) {
		super();
		this.ageModel = ageModel;
		this.idJob = idJob;
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



	
}
