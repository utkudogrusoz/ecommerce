package com.utkudogrusoz.ecommerce.Model;

import jakarta.persistence.*;
import org.springframework.data.elasticsearch.annotations.Document;

@Entity
@Table(name = "products")
@Document(indexName = "products")
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String description;

    private ProductModel(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.price = builder.price;
        this.description = builder.description;
    }

    public ProductModel() {

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public static class Builder {
        private Long id;
        private String name;
        private double price;
        private String description;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public ProductModel build() {
            return new ProductModel(this);
        }
    }
}
