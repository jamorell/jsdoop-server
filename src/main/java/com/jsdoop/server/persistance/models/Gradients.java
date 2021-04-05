package com.jsdoop.server.persistance.models;

import java.util.List;

public class Gradients {
	private long idJob;
	private long ageModel;
	private List<byte[]> listGradientsBytes;
	private String[] listLayersName;
	
//	private Long idGrads;
	private String idGrads;
	private Long currentAge;
	
//	public Gradients(long idJob, long ageModel, Long idGrads, List<byte[]> listGradientsBytes, String[] listLayersName) {
	public Gradients(long idJob, long ageModel, String idGrads, List<byte[]> listGradientsBytes, String[] listLayersName) {
		super();
		this.idJob = idJob;
		this.ageModel = ageModel;
		this.idGrads = idGrads;
		this.listGradientsBytes = listGradientsBytes;
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

	public List<byte[]> getListGradientsBytes() {
		return listGradientsBytes;
	}

	public void setListGradientsBytes(List<byte[]> listGradientsBytes) {
		this.listGradientsBytes = listGradientsBytes;
	}

	public String[] getListLayersName() {
		return listLayersName;
	}

	public void setListLayersName(String[] listLayersName) {
		this.listLayersName = listLayersName;
	}

	public String getIdGrads() {
		return idGrads;
	}

	public void setIdGrads(String idGrads) {
		this.idGrads = idGrads;
	}



	public Long getCurrentAge() {
		return currentAge;
	}

	public void setCurrentAge(Long currentAge) {
		this.currentAge = currentAge;
	}
	
}
