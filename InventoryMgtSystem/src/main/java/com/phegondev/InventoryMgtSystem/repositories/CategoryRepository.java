package com.phegondev.InventoryMgtSystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phegondev.InventoryMgtSystem.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}