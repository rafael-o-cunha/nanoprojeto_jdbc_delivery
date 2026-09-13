package com.nanoprojeto.delivery.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nanoprojeto.delivery.entities.Product;

public class ProductDao implements IDao<Product>{
	private Connection conn;

	public ProductDao(Connection conn) {
		super();
		this.conn = conn;
	}

	@Override
	public List<Product> findAll() throws SQLException{
		List<Product> pList = new ArrayList<Product>();
		
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("select * from tb_product");
    	
		while(rs.next()) {
    		Product p = new Product();

    	    long id = rs.getLong("id");
    	    if (!rs.wasNull()) {
    	        p.setId(id);
    	    }

    	    String name = rs.getString("name");
    	    if (name != null) {
    	        p.setName(name);
    	    }

    	    String description = rs.getString("description");
    	    if (description != null) {
    	        p.setDescription(description);
    	    }

    	    double price = rs.getDouble("price");
    	    if (!rs.wasNull()) {
    	        p.setPrice(price);
    	    }

			int version = rs.getInt("version");
    	    if (!rs.wasNull()) {
    	        p.setVersion(version);
    	    }

    	    pList.add(p);
    	}
    	return pList;
	}

	public Optional<Product> findById(long id) throws SQLException {
		
		String sql = """
		        SELECT 
					id, 
					name, 
					price, 
					version, 
					description, 
					image_uri 
		        FROM tb_product 
		        WHERE id = ?
		        """;
		
    	PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, id);
		
		ResultSet rs = st.executeQuery();
    	
		if (rs.next()) {
	        Product p = new Product();
	        p.setId(rs.getLong("id"));
	        p.setName(rs.getString("name"));
	        p.setPrice(rs.getDouble("price"));
			p.setVersion(rs.getInt("version"));
	        p.setDescription(rs.getString("description"));
	        p.setImageUri(rs.getString("image_uri"));
	        return Optional.of(p);
	    }
		return Optional.empty();
	}
	
	@Override
	public Product create(Product p) throws SQLException {
		String sql = """
					INSERT INTO tb_product (name, price, description, image_uri)
					VALUES (?, ?, ?, ?, ?);
					""";
		
		PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		st.setString(1, p.getName());
		st.setDouble(2, p.getPrice());
		st.setString(3, p.getDescription());
		st.setString(4, p.getImageUri());
		
		st.executeUpdate();
		
		ResultSet rs = st.getGeneratedKeys();
		if(rs.next()) {
			p.setId(rs.getLong(1));
		}
		
		return p;
	}

	/**
	 * Update sem tratamento de concorrência
	 */
	public boolean update(Product p) throws SQLException {
		String sql = """
				UPDATE tb_product SET
					name= ?,
					price= ?,
					version = version + 1,
					description= ?,
					image_uri= ?
				WHERE id = ?
				""";
		
		PreparedStatement st = conn.prepareStatement(sql);
		st.setString(1, p.getName());
		st.setDouble(2, p.getPrice());
		st.setString(3, p.getDescription());
		st.setString(4, p.getImageUri());
		st.setLong(5, p.getId());
		
		int rowsAffected = st.executeUpdate();
		return rowsAffected > 0;
	}

	public boolean delete(Product p) throws SQLException {
		String sql = """
					DELETE FROM tb_product WHERE
					id = ?
				""";
		
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, p.getId());
		
		int rowsArffected = st.executeUpdate();
		return rowsArffected > 0;
	}

	public List<Product> findAll(int page, int size) throws SQLException{
		List<Product> pList = new ArrayList<Product>();
		
		String sql = """
				SELECT 
					*
				FROM tb_product
				ORDER BY id
				LIMIT ? 
				OFFSET ?
				""";

		PreparedStatement st = conn.prepareStatement(sql);
		int offset = (page - 1) * size;
		st.setInt(1, size);
		st.setInt(2, offset);

		ResultSet rs = st.executeQuery();
    	
		while(rs.next()) {
    		Product p = new Product();

    	    long id = rs.getLong("id");
    	    if (!rs.wasNull()) {
    	        p.setId(id);
    	    }

    	    String name = rs.getString("name");
    	    if (name != null) {
    	        p.setName(name);
    	    }

    	    String description = rs.getString("description");
    	    if (description != null) {
    	        p.setDescription(description);
    	    }

    	    double price = rs.getDouble("price");
    	    if (!rs.wasNull()) {
    	        p.setPrice(price);
    	    }

    	    String imageUri = rs.getString("image_uri");
    	    if (imageUri != null) {
    	        p.setImageUri(imageUri);
    	    }

    	    pList.add(p);
    	}
    	return pList;
	}

	/**
	 * Update com tratamento de concorrência
	 * Exemplo de implementação usando ID e Price apenas.
	 */
	public boolean concurrentUpdateByPrice(Product p, Double oldPrice) throws SQLException {

		try {
			conn.setAutoCommit(false);

			String sql = """
					UPDATE tb_product SET
						name= ?,
						price= ?,
						version= version + 1,
						description= ?,
						image_uri= ?
					WHERE id = ?
					AND price = ?
					""";
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, p.getName());
			st.setDouble(2, p.getPrice());
			st.setString(3, p.getDescription());
			st.setString(4, p.getImageUri());
			
			st.setLong(5, p.getId());

			//representa o dado original que será usado para comparação
			st.setDouble(6, oldPrice);
			
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
            	conn.commit();
            	return true;
			}

			conn.rollback();
			return false;
		
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

	/**
	 * Update com tratamento de concorrência
	 * Exemplo de implementação suando ID e version.
	 */
	public boolean concurrentUpdateByVersion(Product p) throws SQLException {
		try {
			conn.setAutoCommit(false);

			String sql = """
					UPDATE tb_product SET
						name= ?,
						price= ?,
						version = version + 1,
						description= ?,
						image_uri= ?
					WHERE id = ?
					AND version = ?
					""";
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, p.getName());
			st.setDouble(2, p.getPrice());
			st.setString(3, p.getDescription());
			st.setString(4, p.getImageUri());
			
			st.setLong(5, p.getId());

			//pode ser "oldVersion", a depender do comportamento concorrente
			st.setInt(6, p.getVersion());
		
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
            	conn.commit();
            	return true;
			}

			conn.rollback();
			return false;
		
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
	}

}
