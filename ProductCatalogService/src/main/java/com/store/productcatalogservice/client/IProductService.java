package com.example.productcatalogservice.client;

import com.example.productcatalogservice.exceptions.NoProductAvailable;
import com.example.productcatalogservice.models.Product;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
    Product getProductById(long id) throws NoProductAvailable;
    Product createProduct(Product product);
}
