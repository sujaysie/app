package com.store.productcatalogservice.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "PRODUCT")
public class Product extends BaseModel implements Serializable {
    private String title;
    private String description;
    private String imageUrl;
    private double amount;
    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;
    private boolean isPrimeSpecific;
}
