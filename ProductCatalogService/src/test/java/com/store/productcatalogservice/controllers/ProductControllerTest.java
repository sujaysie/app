package com.example.productcatalogservice.controllers;

import com.example.productcatalogservice.exceptions.NoProductAvailable;
import com.example.productcatalogservice.models.Product;
import com.example.productcatalogservice.client.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductControllerTest {
    @Autowired
    private ProductController productController;
    @MockBean
    private IProductService productService;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void getProductWithValidIdTest() throws NoProductAvailable {
        Product product = new Product();
        product.setId(5L);
        product.setTitle("iPhone");
        product.setDescription("iPhone");
        when(productService.getProductById(5L)).thenReturn(product);
        var resp = productController.getProductById(5L);
        Assert.notNull(resp, "Product is found");
        assertNotNull(resp);
    }
}