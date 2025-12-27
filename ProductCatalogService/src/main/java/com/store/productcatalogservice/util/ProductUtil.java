package com.store.productcatalogservice.util;

import com.store.productcatalogservice.dtos.CategoryDto;
import com.store.productcatalogservice.dtos.ProductDto;
import com.store.productcatalogservice.models.Product;

public class ProductUtil {
    public static ProductDto from(Product product) {
        ProductDto productDto = new ProductDto();
       // productDto.setId(product.getId());
        productDto.setTitle(product.getTitle());
        productDto.setDescription(product.getDescription());
        if(product.getCategory() != null) productDto.setCategory(CategoryDto.builder().name(product.getCategory().getName()).build());
        productDto.setAmount(product.getAmount());
        productDto.setImageUrl(product.getImageUrl());
        return productDto;
    }
}
