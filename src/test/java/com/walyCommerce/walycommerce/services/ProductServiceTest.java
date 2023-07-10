package com.walyCommerce.walycommerce.services;

import com.walyCommerce.walycommerce.dto.ProductDTO;
import com.walyCommerce.walycommerce.dto.ProductMinDTO;
import com.walyCommerce.walycommerce.entities.Product;
import com.walyCommerce.walycommerce.repositories.ProductRepository;
import com.walyCommerce.walycommerce.services.exceptions.DatabaseException;
import com.walyCommerce.walycommerce.services.exceptions.ResourceNotFoundException;
import com.walyCommerce.walycommerce.tests.ProductFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private Long existingId, nonExistingId, dependencyId;

    private Product product;
    private ProductDTO productDTO;
    private PageImpl<Product> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        dependencyId = 2L;
        nonExistingId = 100L;
        product = ProductFactory.createProduct();
        productDTO = new ProductDTO(product);
        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        Mockito.when(repository.searchByName(any(), (Pageable) any() )).thenReturn(page);
        Mockito.when(repository.save(any())).thenReturn(product);
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(dependencyId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependencyId);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenExistingId(){
        ProductDTO result= service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), productDTO.getId());
        Assertions.assertEquals(result.getName(), result.getName());
    }

    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenNonExistingId() throws Exception{

        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            ProductDTO result = service.findById(nonExistingId);

        });
    }

    @Test
    public void findAllShouldReturnAllPageProductMinDTO(){
        PageRequest pageable = PageRequest.of(0,12);
        String name = "carr0";
        Page<ProductMinDTO> result = service.findAll(name, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getTotalElements(), 1);
        Assertions.assertEquals(result.iterator().next().getName(), product.getName());

    }

    @Test
    public void insertShouldReturnProductDto(){
        ProductDTO newPeoductDTO = new ProductDTO(product);
        ProductDTO result = service.insert(newPeoductDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), newPeoductDTO.getId());
        Assertions.assertEquals(result.getName(), newPeoductDTO.getName());

    }

    @Test
    public void updateShouldReturnProductDTOWhenExistingId(){
        ProductDTO newProductDTO = new ProductDTO(product);
        ProductDTO result = service.update(existingId, newProductDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), product.getId());
        Assertions.assertEquals(result.getName(), product.getName());
    }

    @Test
    public void updateShouldThrowNotFoundExceptionWhenNonExistingId() throws Exception{
        ProductDTO newProductDTO = new ProductDTO(product);
        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            ProductDTO result = service.update(nonExistingId, newProductDTO);
        });
    }

    @Test
    public void deleteShouldDoNothinWhenExistingId(){
        Assertions.assertDoesNotThrow(()->{
            service.delete(existingId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenNonExistingId(){
        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            service.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDataViolationIntegrityWhenDependencyExistingId(){
        Assertions.assertThrows(DatabaseException.class,()->{
            service.delete(dependencyId);
        });
    }
}
