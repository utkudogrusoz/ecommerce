package com.utkudogrusoz.ecommerce.Repository.elasticsearch;

import com.utkudogrusoz.ecommerce.Model.ProductModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductElasticRepository extends ElasticsearchRepository<ProductModel, Long> {
    List<ProductModel> findByNameContainingOrDescriptionContaining(String name, String description);
}
