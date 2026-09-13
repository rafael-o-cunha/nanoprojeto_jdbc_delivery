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
import java.util.Optional;

import com.nanoprojeto.delivery.entities.Order;
import com.nanoprojeto.delivery.entities.OrderStatus;
import com.nanoprojeto.delivery.entities.Product;

public class OrderDao implements IDao<Order> {
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
	public Optional<Order> findById(long id) throws SQLException {
		
		String sql = """
		        SELECT id, latitude, longitude, moment, status
		        FROM tb_order
		        WHERE id = ?
		        """;
		
    	PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, id);
		
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
	        
	        return Optional.of(order);
	    }
		return Optional.empty();
	}

	@Override
	public Order create(Order o) throws SQLException {
		String sql = """
				INSERT INTO tb_order (latitude, longitude, moment, status)
				VALUES (?, ?, ?, ?);
				""";
	
	PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	st.setDouble(1, o.getLatitude());
	st.setDouble(2, o.getLongitude());
	st.setTimestamp(3, Timestamp.from(o.getMoment()));
	st.setInt(4, o.getStatus().ordinal());
	
	st.executeUpdate();
	
	ResultSet rs = st.getGeneratedKeys();
	
	if(rs.next()) {
		o.setId(rs.getLong(1));
	}
	
	return o;
}

	public Order createOrderWithProducts(Order o) throws SQLException {
		try {
			conn.setAutoCommit(false);
			
			o = this.create(o);
			
			String sql = """
					INSERT INTO tb_order_product (order_id, product_id)
					VALUES(?, ?);
					""";
			
			PreparedStatement st = conn.prepareStatement(sql);
			for(Product p : o.getProducts()) {
				st.setLong(1, o.getId());
				st.setLong(2, p.getId());
				st.executeUpdate();
			}
			
			conn.commit();
		}
		catch(SQLException e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
		}
		
		return o;
	}
	
	public boolean update(Order o) throws SQLException {
	    String sql = """
	        UPDATE tb_order SET
	            latitude = ?,
	            longitude = ?,
	            moment = ?,
	            status = ?
	        WHERE id = ?
	        """;

	    PreparedStatement st = conn.prepareStatement(sql);

	    st.setDouble(1, o.getLatitude());
	    st.setDouble(2, o.getLongitude());
	    st.setTimestamp(3, Timestamp.from(o.getMoment()));
	    st.setInt(4, o.getStatus().ordinal());
	    st.setLong(5, o.getId());

	    int rowsAffected = st.executeUpdate();

	    return rowsAffected > 0;
	}
	
	public Optional<Order> findByIdWithProducts(Long id) throws SQLException {
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
			    WHERE o.id = ?
			    """;
    	
    	PreparedStatement st = conn.prepareStatement(sql);
    	st.setLong(1, id);
    	
    	ResultSet rs = st.executeQuery();
		
    	Order order = null;
	    while (rs.next()) {
	    	if(order == null)
	    		order = new Order();

	        Long orderId = rs.getLong("order_id");
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

	    return Optional.ofNullable(order);
	}

	@Override
	public boolean delete(Order o) throws SQLException {
		String sql = """
					DELETE FROM tb_order WHERE
					id = ?
				""";
		
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, o.getId());
		
		int rowsArffected = st.executeUpdate();
		
		return rowsArffected > 0;
	}

	public boolean deleteOrderProductRelationship(Long orderId, Long productId) throws SQLException {
		String sql = """
				DELETE FROM tb_order_product WHERE
				order_id = ?
				AND product_id = ?
				""";
		
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, orderId);
		st.setLong(2, productId);
		
		int rowsAffected = st.executeUpdate();
		
		return rowsAffected > 0;
	}

}
