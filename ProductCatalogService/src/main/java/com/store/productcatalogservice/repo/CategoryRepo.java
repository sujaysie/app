package com.store.productcatalogservice.repo;

import com.store.productcatalogservice.models.Category;
import com.store.productcatalogservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
}
