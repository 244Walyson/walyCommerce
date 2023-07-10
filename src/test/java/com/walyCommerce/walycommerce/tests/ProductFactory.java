package com.walyCommerce.walycommerce.tests;

import com.walyCommerce.walycommerce.entities.Category;
import com.walyCommerce.walycommerce.entities.Product;

public class ProductFactory {

    public static Product createProduct(){
        Category category = new Category(1L, "veiculos");
        Product product = new Product(1L, "carro", "carro bonitão", 20000.00, "carroimg.com");
        product.getCategories().add(category);
        return product;
    }

    public static Product createProductArgs(Long id, String name, String description, Double price, String imgUrl){
        return new Product(id, name, description, price,imgUrl);
    }
}
