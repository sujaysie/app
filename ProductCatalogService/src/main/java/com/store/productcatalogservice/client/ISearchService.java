package com.example.productcatalogservice.client;

import com.example.productcatalogservice.dtos.SortParam;
import com.example.productcatalogservice.models.Product;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ISearchService {
    Page<Product> searchProducts(String searchQuery, int pageNumber, int pageSize, List<SortParam> sortParam);

}
