package com.store.productcatalogservice.client;

import com.store.productcatalogservice.exceptions.NoProductAvailable;
import com.store.productcatalogservice.models.Product;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
    Product getProductById(long id) throws NoProductAvailable;
    Product createProduct(Product product);
}
