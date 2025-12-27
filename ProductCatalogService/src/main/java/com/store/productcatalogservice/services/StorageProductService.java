package com.store.productcatalogservice.services;

import com.store.productcatalogservice.client.IProductService;
import com.store.productcatalogservice.exceptions.NoProductAvailable;
import com.store.productcatalogservice.models.Product;
import com.store.productcatalogservice.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class StorageProductService implements IProductService {
    @Autowired
    private ProductRepo productRepo;
    @Override
    public List<Product> getAllProducts() {
        return List.of();
    }

    @Override
    public Product getProductById(long id) throws NoProductAvailable {
        return productRepo.findById(id).orElseThrow(NoProductAvailable::new);
    }

    @Override
    public Product createProduct(Product product) {
        return productRepo.save(product);
    }
}
