package com.jsdoop.server.services;

import com.jsdoop.server.persistance.models.Weights;

public interface WeightsService {
	public boolean save(Weights weights, boolean forceSave);
	public Weights get(Weights weights);
	public Weights getOld(Weights weights);
	public boolean deleteWeights(long idJob);
}
