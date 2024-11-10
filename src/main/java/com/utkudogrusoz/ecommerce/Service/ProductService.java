package com.utkudogrusoz.ecommerce.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utkudogrusoz.ecommerce.Dto.request.ProductUpdateRequest;
import com.utkudogrusoz.ecommerce.Model.ProductModel;
import com.utkudogrusoz.ecommerce.Repository.elasticsearch.ProductElasticRepository;
import com.utkudogrusoz.ecommerce.Repository.jpa.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {


    private final ProductRepository productRepository;
    private final ProductElasticRepository productElasticRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductService(ProductRepository productRepository, ProductElasticRepository productElasticRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.productRepository = productRepository;
        this.productElasticRepository = productElasticRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void saveProduct(String name, String description, double price) {
        ProductModel productModel = new ProductModel.Builder()
                .name(name)
                .price(price)
                .description(description)
                .build();

        productRepository.save(productModel);
    }

    public List<ProductModel> getAllProducts() {
        return productRepository.findAll();
    }

    private void sendProductEvent(ProductModel product) {
        try {
            String message = objectMapper.writeValueAsString(product);
            kafkaTemplate.send("product-events", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ProductModel> searchInPostgreSQL(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    public List<ProductModel> searchInElasticsearch(String keyword) {
        return productElasticRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
    }

    public List<ProductModel> saveProducts(List<ProductModel> products) {
        List<ProductModel> savedProducts = productRepository.saveAll(products);

        savedProducts.forEach(this::sendProductEvent);

        return savedProducts;
    }

    public void updateProduct(Long id, ProductUpdateRequest updateRequest) {
        Optional<ProductModel> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            ProductModel product = new ProductModel.Builder()
                    .id(id)
                    .name(updateRequest.name() == null ? productOptional.get().getName() : updateRequest.name())
                    .description(updateRequest.desc() == null ? productOptional.get().getDescription() : updateRequest.desc())
                    .price(updateRequest.price() == null ? productOptional.get().getPrice() : updateRequest.price())
                    .build();
            productRepository.save(product);
        } else {
            throw new RuntimeException("Product not found");
        }
    }


    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new RuntimeException("Product not found");
        }
    }

}
