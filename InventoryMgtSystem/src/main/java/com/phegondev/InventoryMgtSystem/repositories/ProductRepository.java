package com.phegondev.InventoryMgtSystem.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phegondev.InventoryMgtSystem.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByNameContainingOrDescriptionContaining(String name, String description);
}
