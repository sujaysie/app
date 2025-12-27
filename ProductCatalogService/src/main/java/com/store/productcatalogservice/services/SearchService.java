package com.store.productcatalogservice.services;

import com.store.productcatalogservice.client.ISearchService;
import com.store.productcatalogservice.dtos.SortParam;
import com.store.productcatalogservice.models.Product;
import com.store.productcatalogservice.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService implements ISearchService {
    @Autowired
    ProductRepo productRepo;
    @Override
    public Page<Product> searchProducts(String searchQuery, int pageNumber, int pageSize, List<SortParam> sortParams) {

        Page<Product> products = productRepo.findProductByTitleEquals(searchQuery, PageRequest.of(pageNumber,pageSize));
        return products;
    }
}
