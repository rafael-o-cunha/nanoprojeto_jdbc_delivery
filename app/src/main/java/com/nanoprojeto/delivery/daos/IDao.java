package com.nanoprojeto.delivery.daos;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.nanoprojeto.delivery.entities.Product;

public interface IDao<T> {
	List<T> findAll() throws SQLException;
	
	Optional<T> findById(long id) throws SQLException;
	
	T create(T entity) throws SQLException;
	
	boolean update(T entity) throws SQLException;
	
	boolean delete(T entity) throws SQLException;
}
