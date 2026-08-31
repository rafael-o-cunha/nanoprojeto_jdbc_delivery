package com.nanoprojeto.delivery.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.nanoprojeto.delivery.entities.Product;

public class ProductDao implements IDao{
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

    	    String imageUri = rs.getString("image_uri");
    	    if (imageUri != null) {
    	        p.setImageUri(imageUri);
    	    }

    	    pList.add(p);
    	}
    	return pList;
	}

}
