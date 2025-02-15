package com.phegondev.InventoryMgtSystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.dtos.UserDTO;
import com.phegondev.InventoryMgtSystem.models.User;
import com.phegondev.InventoryMgtSystem.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	
	@GetMapping("/all")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Response> getAllUsers() {
		
		return ResponseEntity.ok(userService.getAllUsers());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Response> getUserById(@PathVariable Long id) {
		
		return ResponseEntity.ok(userService.getUserById(id));
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<Response> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
		
		return ResponseEntity.ok(userService.updateUser(id, userDTO));
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
		
		return ResponseEntity.ok(userService.deleteUser(id));
	}
	
	@GetMapping("/transactions/{userId}")
	public ResponseEntity<Response> getUserAndTransaction(@PathVariable Long userId) {
		
		return ResponseEntity.ok(userService.getUserTransaction(userId));
	}
	
	@GetMapping("/current")
	public ResponseEntity<User> getCurrentUser() {

		return ResponseEntity.ok(userService.getCurrentLoggedInUser());
	}
}
