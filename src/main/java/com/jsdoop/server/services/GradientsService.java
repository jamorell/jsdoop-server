package com.jsdoop.server.services;

import com.jsdoop.server.persistance.models.Gradients;

public interface GradientsService {
	public Long save(Gradients grads, int nWorkers);
	public Gradients get(Gradients grads);
	public void delete(Gradients grads);
	public void deleteAll(long idJob);
}
