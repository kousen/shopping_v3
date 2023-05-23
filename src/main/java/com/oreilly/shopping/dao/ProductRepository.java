package com.oreilly.shopping.dao;

import com.oreilly.shopping.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
