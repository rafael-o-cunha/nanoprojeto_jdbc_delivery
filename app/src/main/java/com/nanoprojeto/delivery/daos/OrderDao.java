package com.nanoprojeto.delivery.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nanoprojeto.delivery.entities.Order;
import com.nanoprojeto.delivery.entities.OrderStatus;
import com.nanoprojeto.delivery.entities.Product;

public class OrderDao implements IDao {
	private Connection conn;

	public OrderDao(Connection conn) {
		super();
		this.conn = conn;
	}
	
	@Override
	public List<Order> findAll() throws SQLException{
		List<Order> oList = new ArrayList<Order>();
		
		Statement st = this.conn.createStatement();
		ResultSet rs = st.executeQuery("select * from tb_order");
    	
		while(rs.next()) {
			Order order = new Order();

		    Long id = rs.getLong("id");
		    if (!rs.wasNull()) {
		        order.setId(id);
		    }

		    Double latitude = rs.getDouble("latitude");
		    if (!rs.wasNull()) {
		        order.setLatitude(latitude);
		    }

		    Double longitude = rs.getDouble("longitude");
		    if (!rs.wasNull()) {
		        order.setLongitude(longitude);
		    }

		    Timestamp ts = rs.getTimestamp("moment");
		    if (ts != null) {
		        order.setMoment(ts.toInstant());
		    }

		    Integer statusStr = rs.getInt("status");
		    if (statusStr != null) {
		        order.setStatus(OrderStatus.values()[statusStr]);
		    }

    	    oList.add(order);
    	}
    	return oList;
	}
	
	public List<Order> findOrdersWithProducts() throws SQLException {
		Map<Long, Order> orderMap = new HashMap<>();
		
		StringBuilder sql = new StringBuilder(); 
    	sql.append(" SELECT ");
    	sql.append("  o.id AS order_id, ");
		sql.append("  o.latitude, ");
		sql.append("  o.longitude, ");
		sql.append("  o.moment, ");
		sql.append("  o.status, ");
		sql.append("  p.id AS product_id, ");
		sql.append("  p.name, ");
		sql.append("  p.description, ");
		sql.append("  p.price, ");
		sql.append("  p.image_uri ");
    	sql.append(" FROM tb_order o ");
    	sql.append(" INNER JOIN tb_order_product op ON o.id = op.order_id ");
    	sql.append(" INNER JOIN tb_product p ON p.id = op.product_id ");
    	
    	Statement st = this.conn.createStatement();
    	ResultSet rs = st.executeQuery(sql.toString());
		
	    while (rs.next()) {
	        Long orderId = rs.getLong("order_id");
	        Order order = orderMap.get(orderId);

	        if (order == null) {
	            order = new Order();

	            if (!rs.wasNull())
	            	order.setId(orderId);

	            Double latitude = rs.getDouble("latitude");
	            if (!rs.wasNull())
	            	order.setLatitude(latitude);

	            Double longitude = rs.getDouble("longitude");
	            if (!rs.wasNull())
	            	order.setLongitude(longitude);

	            Timestamp ts = rs.getTimestamp("moment");
	            if (ts != null)
	            	order.setMoment(ts.toInstant());

	            Integer statusStr = rs.getInt("status");
	            if (statusStr != null)
	            	order.setStatus(OrderStatus.values()[statusStr]);

	            orderMap.put(orderId, order);
	        }

	        // Produto da linha atual
	        Long productId = rs.getLong("product_id");
	        if (!rs.wasNull()) {
	            Product product = new Product();

	            product.setId(productId);

	            String name = rs.getString("name");
	            if (name != null)
	            	product.setName(name);

	            String description = rs.getString("description");
	            if (description != null)
	            	product.setDescription(description);

	            Double price = rs.getDouble("price");
	            if (!rs.wasNull())
	            	product.setPrice(price);

	            String imageUri = rs.getString("image_uri");
	            if (imageUri != null)
	            	product.setImageUri(imageUri);

	            order.getProducts().add(product);
	        }
	    }

	    return new ArrayList<>(orderMap.values());
	}

}
