package com.walyCommerce.walycommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walyCommerce.walycommerce.dto.OrderDTO;
import com.walyCommerce.walycommerce.entities.Order;
import com.walyCommerce.walycommerce.entities.OrderItem;
import com.walyCommerce.walycommerce.entities.Product;
import com.walyCommerce.walycommerce.entities.User;
import com.walyCommerce.walycommerce.tests.OrderFactory;
import com.walyCommerce.walycommerce.tests.ProductFactory;
import com.walyCommerce.walycommerce.tests.TokenUtil;
import com.walyCommerce.walycommerce.tests.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminUsername, adminPassword, clientUsername, clientPassword;
    private Long existingId, nonBelongingOrder,nonExistingId;
    private String adminToken, clientToken, invalidToken;
    private Order order;
    private OrderDTO orderDTO;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 100L;
        nonBelongingOrder = 2L;
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";
        clientUsername = "maria@gmail.com";
        clientPassword = "123456";

        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
        invalidToken = adminToken + "xpto";

        user = UserFactory.createClientUser();
        order = OrderFactory.createOrder(user);
        product = ProductFactory.createProduct();

        OrderItem orderItem = new OrderItem(order, product,  3, 10.00);
        order.getItems().add(orderItem);
    }

    @Test
    void findByIdShouldReturnOrderWhenExistingIdAndAdminLogged() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/orders/{id}", existingId)
                .header("Authorization", "Bearer "+ adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(1L));
        resultActions.andExpect(jsonPath("$.moment").value("2022-07-25T13:00:00Z"));
        resultActions.andExpect(jsonPath("$.client").exists());
        resultActions.andExpect(jsonPath("$.status").value("PAID"));
        resultActions.andExpect(jsonPath("$.paymentDTO").exists());
        resultActions.andExpect(jsonPath("$.items").exists());
        resultActions.andExpect(jsonPath("$.items[1].name").value("Macbook Pro"));
        resultActions.andExpect(jsonPath("$.total").value(1431.0));//.andDo(MockMvcResultHandlers.print()
    }

    @Test
    void findByIdShouldReturnOrderWhenExistingIdAndClientLoggedWithBelongingOrder() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/orders/{id}", existingId)
                .header("Authorization", "Bearer "+ clientToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(1L));
        resultActions.andExpect(jsonPath("$.total").value(1431.0));
    }

    @Test
    void findByIdShouldReturnForbiddenWhenExistingIdAndClientLoggedNonBelongingOrder() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/orders/{id}", nonBelongingOrder)
                .header("Authorization", "Bearer "+ clientToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenNonExistingIdAndAdminLogged() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/orders/{id}", nonExistingId)
                .header("Authorization", "Bearer "+ adminToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenNonExistingIdAndClientLogged() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/orders/{id}", nonExistingId)
                .header("Authorization", "Bearer "+ clientToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/orders/{id}", existingId)
                .header("Authorization", "Bearer "+ invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }
}
