package com.walyCommerce.walycommerce.services;

import com.walyCommerce.walycommerce.dto.OrderDTO;
import com.walyCommerce.walycommerce.dto.OrderItemDTO;
import com.walyCommerce.walycommerce.dto.ProductDTO;
import com.walyCommerce.walycommerce.entities.Order;
import com.walyCommerce.walycommerce.entities.OrderItem;
import com.walyCommerce.walycommerce.entities.OrderStatus;
import com.walyCommerce.walycommerce.entities.Product;
import com.walyCommerce.walycommerce.repositories.OrderItemRepository;
import com.walyCommerce.walycommerce.repositories.OrderRepository;
import com.walyCommerce.walycommerce.repositories.ProductRepository;
import com.walyCommerce.walycommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id){
        Order order = repository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Recurso não encontrado"));
        authService.validateSelfOrAdmin(order.getClient().getId());
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {
        Order order =  new Order();
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setClient(userService.authenticade());
        for(OrderItemDTO itemDto : dto.getItems()){
            Product product = productRepository.getReferenceById(itemDto.getProductId());
            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
            order.getItems().add(item);
        }
        order = repository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);
    }
}
