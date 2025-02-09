package com.phegondev.InventoryMgtSystem.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.phegondev.InventoryMgtSystem.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
	
	private Long id;

	private String name;

	private String email;

	@JsonInclude
	private String password;

	private String phoneNumber;

	private UserRole role;

	private List<TransactionDTO> transaction;
	
	private LocalDateTime createdAt;

}
