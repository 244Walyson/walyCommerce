package com.walyCommerce.walycommerce.services;

import com.walyCommerce.walycommerce.dto.OrderDTO;
import com.walyCommerce.walycommerce.dto.ProductDTO;
import com.walyCommerce.walycommerce.entities.Order;
import com.walyCommerce.walycommerce.entities.Product;
import com.walyCommerce.walycommerce.repositories.OrderRepository;
import com.walyCommerce.walycommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id){
        Order order = repository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Recurso não encontrado"));
        return new OrderDTO(order);
    }
}
