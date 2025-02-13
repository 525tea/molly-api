package org.example.mollyapi.product.repository;

import org.example.mollyapi.product.entity.Product;
import org.example.mollyapi.search.repository.SearchCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, SearchCustomRepository {
}
