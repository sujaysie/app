package com.store.productcatalogservice.models;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.util.Date;
@Data
@MappedSuperclass
public abstract class BaseModel implements Serializable {
    @Id
    private Long id;
    @CreatedDate
    private Date createdAt;
    @UpdateTimestamp
    private Date lastUpdatedAt;
    private State state;
}
