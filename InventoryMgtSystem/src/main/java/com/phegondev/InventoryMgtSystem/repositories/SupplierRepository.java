package com.phegondev.InventoryMgtSystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phegondev.InventoryMgtSystem.models.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

	
}
