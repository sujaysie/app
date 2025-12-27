package com.example.productcatalogservice.controllers;

import com.example.productcatalogservice.dtos.SearchRequestDto;
import com.example.productcatalogservice.models.Product;
import com.example.productcatalogservice.client.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public Page<Product> searchProduct(@RequestBody SearchRequestDto searchRequestDto) {

        return searchService.searchProducts(searchRequestDto.getQuery(),searchRequestDto.getPageNumber(),searchRequestDto.getPageSize(),searchRequestDto.getSortParam());
    }
}
