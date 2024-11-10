package com.utkudogrusoz.ecommerce.Repository.jpa;

import com.utkudogrusoz.ecommerce.Model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    @Query(value = "SELECT * FROM products p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    List<ProductModel> searchProducts(@Param("keyword") String keyword);}
