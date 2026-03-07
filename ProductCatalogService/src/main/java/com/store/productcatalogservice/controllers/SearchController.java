package com.store.productcatalogservice.controllers;

import com.store.productcatalogservice.client.ISearchService;
import com.store.productcatalogservice.dtos.SearchRequestDto;
import com.store.productcatalogservice.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(value = "/search")
public class SearchController {
    @Autowired
    ISearchService searchService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Page<Product> searchProduct(@RequestBody SearchRequestDto searchRequestDto) {
        return searchService.searchProducts(searchRequestDto.getQuery(), searchRequestDto.getPageNumber(), searchRequestDto.getPageSize(), searchRequestDto.getSortParam());
    }
}
