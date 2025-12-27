package com.example.productcatalogservice.dtos;

import com.example.productcatalogservice.models.Category;
import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private double amount;
    private CategoryDto category;
}
