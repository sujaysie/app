package com.store.productcatalogservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SortParam {
    private String attribute;
    private com.store.productcatalogservice.dtos.SortType sortType;
}
