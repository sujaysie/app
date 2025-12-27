package com.store.productcatalogservice.dtos;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
public class SearchRequestDto {
    private String query;
    private int pageNumber;
    private int pageSize;
    @Nullable
    private List<com.store.productcatalogservice.dtos.SortParam> sortParam;
}
