package com.walyCommerce.walycommerce.repositories;

import com.walyCommerce.walycommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
