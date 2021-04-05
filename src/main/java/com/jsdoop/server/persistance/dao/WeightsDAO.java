package com.jsdoop.server.persistance.dao;

import com.jsdoop.server.persistance.models.Weights;

public interface WeightsDAO {
	public void save(Weights weights);
//	public Weights get(String key);
	public Weights get(Weights weights);
	public Weights getOld(Weights weights);
	public boolean deleteWeights(long idJob);
	public boolean deleteOldWeights(Weights w);
}
