package com.walyCommerce.walycommerce.repositories;

import com.walyCommerce.walycommerce.entities.Category;
import com.walyCommerce.walycommerce.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {


}
