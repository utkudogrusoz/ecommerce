package com.utkudogrusoz.ecommerce.Core.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utkudogrusoz.ecommerce.Model.ProductModel;
import com.utkudogrusoz.ecommerce.Repository.elasticsearch.ProductElasticRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductConsumer {

    private final ProductElasticRepository productElasticRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductConsumer(ProductElasticRepository productElasticRepository) {
        this.productElasticRepository = productElasticRepository;
    }

    @KafkaListener(topics = "product-events", groupId = "product-group")
    public void consume(String message) {
        try {
            ProductModel product = objectMapper.readValue(message, ProductModel.class);
            productElasticRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}