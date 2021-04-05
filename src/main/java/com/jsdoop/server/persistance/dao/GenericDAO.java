package com.jsdoop.server.persistance.dao;

public interface GenericDAO<T> {
	public void save(T element);
	public T get(String key);
}
