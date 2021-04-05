package com.jsdoop.server.persistance.models;

public class Stats {
	private String infoWorker;
	private String remoteAddr;
	private long timeRequest;
	private long timeResponse;

	
	private Long ageModel;
	private Long idJob;
	
	private String typeTask;
	
	private String other;
	
	private String username;
	
	private long idTask;
	
	private Long executionTime;

	
	public Stats(String username, String infoWorker, String remoteAddr, long timeRequest, long timeResponse, Long ageModel, Long idJob,
			String typeTask, long idTask, Long executionTime) {
		super();
		this.username = username;
		this.infoWorker = infoWorker;
		this.remoteAddr = remoteAddr;
		this.timeRequest = timeRequest;
		this.timeResponse = timeResponse;
		this.ageModel = ageModel;
		this.idJob = idJob;
		this.typeTask = typeTask;
		
		this.idTask = idTask;
		this.executionTime = executionTime;
	}
	
	

	
	public long getIdTask() {
		return idTask;
	}




	public void setIdTask(long idTask) {
		this.idTask = idTask;
	}




	public Long getExecutionTime() {
		return executionTime;
	}




	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}




	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
	
	public String getInfoWorker() {
		return infoWorker;
	}

	public void setInfoWorker(String infoWorker) {
		this.infoWorker = infoWorker;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public long getTimeRequest() {
		return timeRequest;
	}

	public void setTimeRequest(long timeRequest) {
		this.timeRequest = timeRequest;
	}

	public long getTimeResponse() {
		return timeResponse;
	}

	public void setTimeResponse(long timeResponse) {
		this.timeResponse = timeResponse;
	}

	public Long getAgeModel() {
		return ageModel;
	}

	public void setAgeModel(long ageModel) {
		this.ageModel = ageModel;
	}

	public Long getIdJob() {
		return idJob;
	}

	public void setIdJob(long idJob) {
		this.idJob = idJob;
	}

	public String getTypeTask() {
		return typeTask;
	}

	public void setTypeTask(String typeTask) {
		this.typeTask = typeTask;
	}
	
	
	
}
