package com.walyCommerce.walycommerce.tests;

import com.walyCommerce.walycommerce.entities.Category;

public class CategoryFactory {

    public static Category createCategory(){
        return new Category(1L, "Games");
    }

    public static Category createCategoryArgs(Long id, String name){
        return new Category(id, name);
    }
}
