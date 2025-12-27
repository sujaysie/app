package com.example.productcatalogservice.services;

import com.example.productcatalogservice.client.IProductService;
import com.example.productcatalogservice.dtos.FakestoreProductDto;
import com.example.productcatalogservice.exceptions.NoProductAvailable;
import com.example.productcatalogservice.models.Category;
import com.example.productcatalogservice.models.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@Primary
public class ProductService implements IProductService {
    @Autowired
    RestTemplate restTemplate;
    @Value("${third.party.api}")
    String FAKE_STORE_URL;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    public List<Product> getAllProducts() {
        var products = restTemplate.getForEntity(String.format("%s/products", FAKE_STORE_URL), FakestoreProductDto[].class).getBody();
        return Arrays.stream(products).map(p -> {
            try {
                return from(p);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

//    public Product replaceProduct(Product product) {
//        var productResp = restTemplate.postForEntity(String.format("%s/"), product, Product.class).getBody();
//    }

    public Product getProductById(long id) throws NoProductAvailable {
       // Product product = (Product) redisTemplate.opsForHash().get("PRODUCT",id);
        //if(product!=null) return product;
        var productResp = restTemplate.getForEntity(String.format("%s/products/%d", FAKE_STORE_URL, id), FakestoreProductDto.class);
        if (productResp.getBody() != null && productResp.getStatusCode().is2xxSuccessful()) {
            var p = from(productResp.getBody());
        //    redisTemplate.opsForHash().put("PRODUCT",id,p);
            return p;
        }
        throw new NoProductAvailable();
    }

    public Product createProduct(Product product) {
        return product;
    }

    private Product from(FakestoreProductDto fakestoreProductDto) {
        Product product = new Product();
        product.setId(fakestoreProductDto.getId());
        product.setTitle(fakestoreProductDto.getTitle());
        product.setDescription(fakestoreProductDto.getDescription());
        product.setAmount(fakestoreProductDto.getPrice());
        product.setImageUrl(fakestoreProductDto.getImage());
        product.setCategory(Category.builder().name(fakestoreProductDto.getCategory()).build());
        return product;
    }

    public <T> ResponseEntity<T> requestForEntity(String url, HttpMethod httpMethod, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
        return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
    }
}
