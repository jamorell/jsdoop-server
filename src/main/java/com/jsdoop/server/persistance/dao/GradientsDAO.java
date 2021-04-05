package com.jsdoop.server.persistance.dao;

import com.jsdoop.server.persistance.models.Gradients;

public interface GradientsDAO {
	public String save(Gradients grads);
	public Gradients get(Gradients grads);	
	public void delete(Gradients grads);
	public void deleteAll(long idJob);	
}
