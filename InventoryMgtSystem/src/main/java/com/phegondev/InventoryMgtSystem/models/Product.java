package com.phegondev.InventoryMgtSystem.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@Data
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Name is required")
	private String name;

	@Column(unique = true)
	@NotBlank(message = "SKU is required")
	private String sku;

	@Positive(message = "Product Price must be a positive value.")
	private BigDecimal price;

	@Min(value = 0, message = "Stock quantity cannot be negative.")
	private Integer stockQuantiy;
	
	private String description;
	private LocalDateTime expiryDate;
	private String imageUrl;
	
	private final LocalDateTime createAt = LocalDateTime.now();

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", sku=" + sku + ", price=" + price + ", stockQuantiy="
				+ stockQuantiy + ", description=" + description + ", expiryDate=" + expiryDate + ", imageUrl="
				+ imageUrl + ", createAt=" + createAt + "]";
	}
	
	
}
