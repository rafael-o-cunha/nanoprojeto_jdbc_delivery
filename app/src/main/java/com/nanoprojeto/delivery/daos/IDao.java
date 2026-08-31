package com.nanoprojeto.delivery.daos;

import java.sql.SQLException;
import java.util.List;

public interface IDao<T> {
	List<T> findAll() throws SQLException;
}
