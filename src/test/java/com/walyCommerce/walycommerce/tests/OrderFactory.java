package com.walyCommerce.walycommerce.tests;

import com.walyCommerce.walycommerce.entities.*;

import java.time.Instant;

public class OrderFactory {

    public static Order createOrder(User user){
        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user, new Payment());
        Product product = ProductFactory.createProduct();
        OrderItem orderItem = new OrderItem(order, product, 2, 10.00);
        order.getItems().add(orderItem);
        return order;
    }
}
