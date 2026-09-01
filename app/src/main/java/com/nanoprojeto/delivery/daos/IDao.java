package com.nanoprojeto.delivery.daos;

import java.sql.SQLException;
import java.util.List;

import com.nanoprojeto.delivery.entities.Product;

public interface IDao<T> {
	List<T> findAll() throws SQLException;
	
	public T findById(long id) throws SQLException;
}
