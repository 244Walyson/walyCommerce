package com.walyCommerce.walycommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.walyCommerce.walycommerce.dto.ProductDTO;
import com.walyCommerce.walycommerce.entities.Product;
import com.walyCommerce.walycommerce.tests.ProductFactory;
import com.walyCommerce.walycommerce.tests.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;
    private String productName;
    private Product product;
    private ProductDTO productDTO;
    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String adminToken, clientToken, invalidToken;
    private Long existingId, nonExistingId, dependencyId;

    @BeforeEach
    void setUp() throws Exception{
        nonExistingId = 100L;
        existingId = 1L;
        dependencyId = 3L;
        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";
        product = ProductFactory.createProduct();
        productDTO = new ProductDTO(product);

        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
        invalidToken = adminToken + "xpto"; //simulate wrong password

        productName = "Macbook";
    }

    @Test
    void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception{

        ResultActions result = mockMvc.perform(get("/products?name={productName}", productName).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").value(3L));
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[0].price").value(1250.0));
        result.andExpect(jsonPath("$.content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
    }
    @Test
    void findAllShouldReturnPageWhenNameIsEmpty() throws Exception{

        ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").value(1L));
        result.andExpect(jsonPath("$.content[0].name").value("The Lord of the Rings"));
        result.andExpect(jsonPath("$.content[0].price").value(90.5));
        result.andExpect(jsonPath("$.content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
    }

    @Test
    void insertShouldReturnProductDTOCreatedWhenAdminLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.id").value(26));
        resultActions.andExpect(jsonPath("$.name").value(productDTO.getName()));
        resultActions.andExpect(jsonPath("$.price").value(productDTO.getPrice()));
        resultActions.andExpect(jsonPath("$.description").value(productDTO.getDescription()));
        resultActions.andExpect(jsonPath("$.imgUrl").value(productDTO.getImgUrl()));
        resultActions.andExpect(jsonPath("$.categories[0].id").value(1L));

    }

    @Test
    void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception{
        product.setName("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isUnprocessableEntity());


    }

    @Test
    void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidInvalidDescription() throws Exception{
        product.setDescription("ab");
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isUnprocessableEntity());


    }
    @Test
    void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndNegativePrice() throws Exception{
        product.setPrice(-40.00);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isUnprocessableEntity());

    }

    @Test
    void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsZero() throws Exception{
        product.setPrice(0.0);
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isUnprocessableEntity());


    }

    @Test
    void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndProducHasNoCategory() throws Exception{
        product.getCategories().clear();
        productDTO = new ProductDTO(product);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isUnprocessableEntity());


    }
    @Test
    void insertShouldReturnForbiddenWhenClientLogged() throws Exception{

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + clientToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isForbidden());


    }

    @Test
    void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception{

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + invalidToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isUnauthorized());

    }

    @Test
    void deleteShouldReturnNotFoundWhenNonIdExistsAdminLogged() throws Exception {

        ResultActions resultActions = mockMvc.perform(delete("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @Transactional(propagation = Propagation.SUPPORTS)
    void deleteShouldReturnBadRequestWhenDependencyIdAdminLogged() throws Exception {

        ResultActions resultActions = mockMvc.perform(delete("/products/{id}", dependencyId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isBadRequest());
    }
    @Test
    void deleteShouldReturnForbiddenWhenClientLogged() throws Exception {

        ResultActions resultActions = mockMvc.perform(delete("/products/{id}", existingId)
                .header("Authorization", "Bearer " + clientToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isForbidden());
    }
    @Test
    void deleteShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

        ResultActions resultActions = mockMvc.perform(delete("/products/{id}", existingId)
                .header("Authorization", "Bearer " + invalidToken)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isUnauthorized());
    }
}
