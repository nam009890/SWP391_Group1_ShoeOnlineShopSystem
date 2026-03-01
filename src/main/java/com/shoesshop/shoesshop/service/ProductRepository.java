package com.shoesshop.shoesshop.repository;

import com.shoesshop.shoesshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}