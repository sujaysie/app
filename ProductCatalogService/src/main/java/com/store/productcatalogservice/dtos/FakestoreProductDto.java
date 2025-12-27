package com.example.productcatalogservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FakestoreProductDto {
    private Long id;
    private String title;
    private String description;
    private String image;
    private double price;
    private String category;
}
