package com.nanoprojeto.delivery.entities;

public class Product {
	
	private Long id;
	private String name;
	private Double price;
	private String description;
	private String imageUri;
	private Integer version;
	
	public Product() {}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getImageUri() {
		return imageUri;
	}
	
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Product [id=" + id +
				", name=" + name +
				", price=" + price +
				", version=" + version +
				", description=" + description +
				", imageUri=" + imageUri + "]";
	}
	
}
