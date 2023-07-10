package com.walyCommerce.walycommerce.services;

import com.walyCommerce.walycommerce.dto.OrderDTO;
import com.walyCommerce.walycommerce.entities.Order;
import com.walyCommerce.walycommerce.entities.OrderItem;
import com.walyCommerce.walycommerce.entities.Product;
import com.walyCommerce.walycommerce.entities.User;
import com.walyCommerce.walycommerce.repositories.OrderItemRepository;
import com.walyCommerce.walycommerce.repositories.OrderRepository;
import com.walyCommerce.walycommerce.repositories.ProductRepository;
import com.walyCommerce.walycommerce.services.exceptions.ForbiddenException;
import com.walyCommerce.walycommerce.services.exceptions.ResourceNotFoundException;
import com.walyCommerce.walycommerce.tests.OrderFactory;
import com.walyCommerce.walycommerce.tests.ProductFactory;
import com.walyCommerce.walycommerce.tests.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository repository;

    @Mock
    private AuthService authService;

    private Long existingOrderId, nonExistingOrderId, existingProductId, nonExistingProductId;
    private Order order;
    private OrderDTO orderDTO;
    private User admin, client;
    private Product product;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        existingOrderId = 1L;
        nonExistingOrderId = 10L;
        existingProductId =1L;
        nonExistingProductId = 10L;

        admin = UserFactory.createAdminUser();
        client = UserFactory.customClientUser(1L, "bob");
        order = OrderFactory.createOrder(client);
        orderDTO = new OrderDTO(order);
        product = ProductFactory.createProduct();

        Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));
        Mockito.when(repository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(repository.save(any())).thenReturn(order);
        Mockito.when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));
    }

    @Test
    void findByIdShouldReturnOrderDTOWhenIdExistAndAdminLogged() {
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());
        OrderDTO result = service.findById(existingOrderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    @Test
    void findByIdShouldReturnOrderDTOWhenIdExistAndSelfClientLogged() {
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());
        OrderDTO result = service.findById(existingOrderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingOrderId);
    }

    @Test
    void findByIdShouldThrowForbbidenExceptionWhenOtherClient() {
        Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ForbiddenException.class,()->{
            OrderDTO result = service.findById(existingOrderId);
        });
    }

    @Test
    void findByIdShouldThrwoesResourceNotFoundExceptionWhenIdDOesNotExists() {
        Mockito.doThrow(ResourceNotFoundException.class).when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ResourceNotFoundException.class,()->{
           service.findById(nonExistingOrderId);
        });
    }

    @Test
    void insertShouldReturnOrderDTOWhenValidProductAndAdminLogged() {
        Mockito.when(userService.authenticade()).thenReturn(admin);

        OrderDTO result = service.insert(orderDTO);

        Assertions.assertNotNull(result);
    }
    @Test
    void insertShouldTHrowsResourceNotFoundExceptionWhenUserNotLogged() {
        Mockito.doThrow(ResourceNotFoundException.class).when(userService).authenticade();

        order.setClient(new User());
        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            OrderDTO result = service.insert(orderDTO);
        });

    }

    @Test
    void insertShouildThrowsENtityNotFoundExceptionWhenOrderProducDoesNotExists() {
        Mockito.when(userService.authenticade()).thenReturn(client);

        product.setId(nonExistingProductId);
        OrderItem orderItem = new OrderItem(order, product, 2, 10.00);
        order.getItems().add(orderItem);

        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(EntityNotFoundException.class, ()->{
           OrderDTO result = service.insert(orderDTO);
        });
    }
}
