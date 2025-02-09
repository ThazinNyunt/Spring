package com.phegondev.InventoryMgtSystem.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phegondev.InventoryMgtSystem.enums.UserRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

	// Generic
	private int status;
	private String message;
	
	// For Login
	private String token;
	private UserRole role;
	private String expirationTime;
	
	// For pagination
	private Integer totalPages;
	private Long totalElements;
	
	// Data Output optional
	private UserDTO user;
	private List<UserDTO> users;
	
	private SupplierDTO supplier;
	private List<SupplierDTO> suppliers;
	
	private CategoryDTO category;
	private List<CategoryDTO> categories;	
	
	private ProductDTO product;
	private List<ProductDTO> products;
	
	private TransactionDTO transaction;
	private List<TransactionDTO> transactions;
	
	private final LocalDateTime timestamp = LocalDateTime.now();
	
}
