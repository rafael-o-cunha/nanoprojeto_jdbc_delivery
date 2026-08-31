package com.nanoprojeto.delivery.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order {

	private Long id;
	private Double latitude;
	private Double longitude;
	private Instant moment;
	private OrderStatus status;
	private List<Product> products = new ArrayList<>();
	
	public Order() {}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public Instant getMoment() {
		return moment;
	}
	
	public void setMoment(Instant moment) {
		this.moment = moment;
	}
	
	public OrderStatus getStatus() {
		return status;
	}
	
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	
	public List<Product> getProducts() {
		return products;
	}
	
	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Order [")
	      .append("id=").append(id)
	      .append(", latitude=").append(latitude)
	      .append(", longitude=").append(longitude)
	      .append(", moment=").append(moment)
	      .append(", status=").append(status)
	      .append(", products=");

	    if (products != null && !products.isEmpty()) {
	    	sb.append("\n");
	    	for (Product p : products) {
	            sb.append("   -> ").append(p).append("\n");
	        }
	    }

	    sb.append("]");
	    return sb.toString();
	}
	
}
