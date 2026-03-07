package com.store.productcatalogservice.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.store.productcatalogservice.client.IProductService;
import com.store.productcatalogservice.configs.SecurityConfig;
import com.store.productcatalogservice.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProductService productService;

    @Test
    void getProductsReturns401WithoutToken() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProductReturns403ForUserRole() throws Exception {
        mockMvc.perform(post("/products/create")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Phone\",\"description\":\"Smartphone\",\"amount\":1000}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProductReturns201ForAdminRole() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Phone");
        product.setDescription("Smartphone");
        product.setAmount(1000.0);

        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products/create")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Phone\",\"description\":\"Smartphone\",\"amount\":1000}"))
                .andExpect(status().isCreated());
    }
}
