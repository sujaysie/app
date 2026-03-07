package com.store.productcatalogservice.controllers;

import com.store.productcatalogservice.dtos.CategoryDto;
import com.store.productcatalogservice.dtos.ProductDto;
import com.store.productcatalogservice.exceptions.NoProductAvailable;
import com.store.productcatalogservice.models.Product;
import com.store.productcatalogservice.client.IProductService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(value = "/products")
public class ProductController {
    @Autowired
    IProductService productService;

    @GetMapping()
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<ProductDto> getProducts() {
        var products = productService.getAllProducts();
        return products.stream().map(this::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") long productId) throws NoProductAvailable {
        Product product = productService.getProductById(productId);
        return new ResponseEntity<>(from(product), HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto product) {
        var prod = productService.createProduct(from(product));
        return new ResponseEntity<>(from(prod), HttpStatus.CREATED);
    }

    private Product from(ProductDto product) {
        Product product1 = new Product();
        product1.setId(product.getId());
        product1.setTitle(product.getTitle());
        product1.setDescription(product.getDescription());
        product1.setAmount(product.getAmount());
        product1.setImageUrl(product.getImageUrl());
        return product1;
    }

    private ProductDto from(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setTitle(product.getTitle());
        productDto.setDescription(product.getDescription());
        if (product.getCategory() != null) {
            productDto.setCategory(CategoryDto.builder().name(product.getCategory().getName()).build());
        }
        productDto.setAmount(product.getAmount());
        productDto.setImageUrl(product.getImageUrl());
        return productDto;
    }
}
