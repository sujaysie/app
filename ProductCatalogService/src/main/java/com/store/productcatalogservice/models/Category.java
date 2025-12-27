package com.store.productcatalogservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity(name = "CATEGORY")
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseModel {
    private String name;
    private String description;
    @OneToMany(mappedBy = "category")
    private List<com.store.productcatalogservice.models.Product> products;
}
