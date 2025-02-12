package org.example.mollyapi.product.repository;

import org.example.mollyapi.product.entity.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
}
