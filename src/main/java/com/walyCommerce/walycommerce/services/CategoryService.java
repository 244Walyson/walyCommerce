package com.walyCommerce.walycommerce.services;

import com.walyCommerce.walycommerce.dto.CategoryDTO;
import com.walyCommerce.walycommerce.dto.ProductDTO;
import com.walyCommerce.walycommerce.dto.ProductMinDTO;
import com.walyCommerce.walycommerce.entities.Category;
import com.walyCommerce.walycommerce.entities.Product;
import com.walyCommerce.walycommerce.repositories.CategoryRepository;
import com.walyCommerce.walycommerce.repositories.ProductRepository;
import com.walyCommerce.walycommerce.services.exceptions.DatabaseException;
import com.walyCommerce.walycommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;


    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        List<Category> result = repository.findAll();
        return result.stream().map(x -> new CategoryDTO(x)).toList();
    }


}
