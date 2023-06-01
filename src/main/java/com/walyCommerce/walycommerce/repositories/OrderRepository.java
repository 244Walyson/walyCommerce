package com.walyCommerce.walycommerce.repositories;

import com.walyCommerce.walycommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
