package com.jsdoop.server.persistance.models;

import java.util.List;

public class Weights {
	private long idJob;
	private long ageModel;
	private List<byte[]> listWeightsBytes;	
	private String[] listLayersName;

	private Long currentAge;
	
	public Weights(long idJob, long ageModel, List<byte[]> listWeightsBytes, String[] listLayersName) {
		super();
		this.idJob = idJob;
		this.ageModel = ageModel;
		this.listWeightsBytes = listWeightsBytes;
		this.listLayersName = listLayersName;
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
	public List<byte[]> getListWeightsBytes() {
		return listWeightsBytes;
	}
	public void setListWeightsBytes(List<byte[]> listWeightsBytes) {
		this.listWeightsBytes = listWeightsBytes;
	}
	public String[] getListLayersName() {
		return listLayersName;
	}
	public void setListLayersName(String[] listLayersName) {
		this.listLayersName = listLayersName;
	}
//	public String[] getIdsGradsToDelete() {
//		return idsGradsToDelete;
//	}
//	public void setIdsGradsToDelete(String[] idsGradsToDelete) {
//		this.idsGradsToDelete = idsGradsToDelete;
//	}

	public Long getCurrentAge() {
		return currentAge;
	}

	public void setCurrentAge(Long currentAge) {
		this.currentAge = currentAge;
	}
	
}
