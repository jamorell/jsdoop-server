package com.jsdoop.server.constants;

public class Constants {
	public static long MAX_TIME_TO_DISCONNECT_WORKER =  30000; //5000; //25000;//60000; //5000;
	
//	public static String ID_JOB = "id_job";
	public static String CURRENT_AGE = "current_age";
	public static String CURRENT_WEIGHTS = "current_weights";
	public static String WEIGHTS = "weights";
	public static String GRADS_BASE = "grads";
	public static String SEP = "_"; //SEPARATOR
	public static String LAYER = "L"; //SEPARATOR
//	public static String KEY_CURRENT_AGE = ID_JOB + SEP + CURRENT_AGE;
//	public static String KEY_CURRENT_WEIGHTS = ID_JOB + SEP + CURRENT_WEIGHTS;
	
	
	public static String KEY_CURRENT_AGE(long idJob) {
		return "" + idJob + SEP + CURRENT_AGE;
	}
	public static String KEY_CURRENT_WEIGHTS(long idJob) {
		return "" + idJob + SEP + CURRENT_WEIGHTS;
	}
	
	public static String KEY_WEIGHTS(long idJob, long ageModel) {
		return "" + idJob + SEP + WEIGHTS + SEP + ageModel;
	}
	public static String KEY_WEIGHTS_DEL(long idJob) {
		return "" + idJob + SEP + WEIGHTS + SEP;
	}
	public static String KEY_GRADS_BASE(long idJob) {
		return "" + idJob + SEP + GRADS_BASE;
	}
	
	
	
	public static int ID_ORDER = 0;
}
