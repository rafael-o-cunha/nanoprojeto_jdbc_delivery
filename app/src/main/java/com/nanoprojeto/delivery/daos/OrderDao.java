package com.nanoprojeto.delivery.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
		
		String sql = """
			    SELECT 
			        o.id AS order_id,
			        o.latitude,
			        o.longitude,
			        o.moment,
			        o.status,
			        p.id AS product_id,
			        p.name,
			        p.description,
			        p.price,
			        p.image_uri
			    FROM tb_order o
			    INNER JOIN tb_order_product op ON o.id = op.order_id
			    INNER JOIN tb_product p ON p.id = op.product_id
			    """;
    	
    	Statement st = this.conn.createStatement();
    	ResultSet rs = st.executeQuery(sql);
		
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

	@Override
	public Order findById(long id) throws SQLException {
		
		String sql = """
		        SELECT id, latitude, longitude, moment, status
		        FROM tb_order
		        WHERE id = :id
		        """;
		
    	PreparedStatement st = conn.prepareStatement(sql);
		st.setLong("id", id);
		
		ResultSet rs = st.executeQuery();
    	
		if (rs.next()) {
			Order order = new Order();
			order.setId(rs.getLong("id"));
			order.setLatitude(rs.getDouble("latitude"));
			order.setLongitude(rs.getDouble("longitude"));
	        
	        Timestamp ts = rs.getTimestamp("moment");
	        if (ts != null) {
	            order.setMoment(ts.toInstant());
	        }

	        int status = rs.getInt("status");
	        if (!rs.wasNull()) {
	            order.setStatus(OrderStatus.values()[status]);
	        }
	        
	        return order;
	    }
		return null;
	}

}
