package com.walyCommerce.walycommerce.services;

import com.walyCommerce.walycommerce.dto.CategoryDTO;
import com.walyCommerce.walycommerce.dto.ProductDTO;
import com.walyCommerce.walycommerce.dto.ProductMinDTO;
import com.walyCommerce.walycommerce.dto.UriDto;
import com.walyCommerce.walycommerce.entities.Category;
import com.walyCommerce.walycommerce.entities.Product;
import com.walyCommerce.walycommerce.repositories.ProductRepository;
import com.walyCommerce.walycommerce.services.exceptions.DatabaseException;
import com.walyCommerce.walycommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private S3service s3service;

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Product product = repository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Recurso não encontrado"));
        return new ProductDTO(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductMinDTO> findAll(String name, Pageable pageable){
        Page<Product> result = repository.searchByName(name, pageable);
        return result.map(x -> new ProductMinDTO(x));
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try{
            Product entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new ProductDTO(entity);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("produto não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("recurso não encontrado");
        }
        try{
            repository.deleteById(id);
        }
        catch(DataIntegrityViolationException e){
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.getCategories().clear();
        for (CategoryDTO catDto : dto.getCategories()){
            Category cat  = new Category();
            cat.setId(catDto.getId());
            entity.getCategories().add(cat);
        }
    }

    public UriDto uploadFile(MultipartFile file){
        URL url = s3service.UploadFile(file);

        return new UriDto(url.toString());
    }

}
