package com.phegondev.InventoryMgtSystem.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {
	
	private Long id;
	
	private Long categoryId;
	private Long productId;
	private Long supplierId;
	
	private String name;

	private String sku;
	
	private BigDecimal price;
	
	private Integer stockQuantiy;
	
	private String description;
	private LocalDateTime expiryDate;
	private String imageUrl;
	
	private LocalDateTime createAt;


}
