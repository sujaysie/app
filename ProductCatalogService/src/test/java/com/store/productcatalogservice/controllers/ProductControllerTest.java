package com.store.productcatalogservice.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.store.productcatalogservice.ProductCatalogServiceApplication;
import com.store.productcatalogservice.client.IProductService;
import com.store.productcatalogservice.exceptions.NoProductAvailable;
import com.store.productcatalogservice.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.util.Assert;

@SpringBootTest(classes = ProductCatalogServiceApplication.class)
class ProductControllerTest {
    @Autowired
    private ProductController productController;

    @MockBean
    private IProductService productService;

    @Test
    @WithMockUser(roles = "USER")
    void getProductWithValidIdTest() throws NoProductAvailable {
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
