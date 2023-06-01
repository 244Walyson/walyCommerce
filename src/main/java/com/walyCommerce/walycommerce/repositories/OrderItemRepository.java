package com.walyCommerce.walycommerce.repositories;

import com.walyCommerce.walycommerce.entities.Order;
import com.walyCommerce.walycommerce.entities.OrderItem;
import com.walyCommerce.walycommerce.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {


}
