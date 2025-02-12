package org.example.mollyapi.product.repository;

import org.example.mollyapi.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
