package com.utkudogrusoz.ecommerce.Controller;

import com.utkudogrusoz.ecommerce.Core.Service.RateLimiterService;
import com.utkudogrusoz.ecommerce.Dto.request.ProductUpdateRequest;
import com.utkudogrusoz.ecommerce.Dto.response.ProductResponse;
import com.utkudogrusoz.ecommerce.Model.ProductModel;
import com.utkudogrusoz.ecommerce.Service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
public class ProductController {


    private final ProductService productService;
    private final RateLimiterService rateLimiterService;

    public ProductController(ProductService productService, RateLimiterService rateLimiterService) {
        this.productService = productService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping(path = "/save")
    public ResponseEntity<String> save(@RequestBody ProductUpdateRequest updateRequest) {
        if (updateRequest.name() == null || updateRequest.desc() == null || updateRequest.price() == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Eksik alan: 'name', 'desc' veya 'price' sağlanmalı.");
        }
        try {
            productService.saveProduct(updateRequest.name(), updateRequest.desc(), updateRequest.price());
            return ResponseEntity.status(HttpStatus.CREATED).body("Product kaydedildi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Kaydedilemedi.");
        }
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Long id, @RequestBody ProductUpdateRequest updateRequest) {
        try {
            productService.updateProduct(id, updateRequest);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>("cannot update", HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(path = "/updatePatch/{id}")
    public ResponseEntity<String> updatePatch(@PathVariable("id") Long id, @RequestBody ProductUpdateRequest updateRequest) {
        try {
            productService.updateProduct(id, updateRequest);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<String>("cannot update with patch", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("delete successful");
        } catch (Exception e) {
            return new ResponseEntity<String>("cannot update", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/get")
    public ResponseEntity<List<ProductResponse>> get(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        if (!rateLimiterService.isAllowed(ipAddress)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        try {
            List<ProductModel> productModelList = productService.getAllProducts();
            List<ProductResponse> productResponse = productModelList.stream()
                    .map(productModel -> new ProductResponse(
                            productModel.getId(),
                            productModel.getName(),
                            productModel.getDescription(),
                            productModel.getPrice()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(productResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/batch")
    public List<ProductModel> addProducts(@RequestBody List<ProductModel> products) {
        return productService.saveProducts(products);
    }

    @GetMapping("/search/postgresql")
    public List<ProductModel> searchInPostgreSQL(@RequestBody Map<String, String> requestBody) {

        return productService.searchInPostgreSQL(requestBody.get("search"));
    }

    @GetMapping("/search/elasticsearch")
    public List<ProductModel> searchInElasticsearch(@RequestBody Map<String, String> requestBody) {
        return productService.searchInElasticsearch(requestBody.get("search"));
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
        if (!image.getContentType().equals("image/jpeg")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Sadece JPG formatında resimler yükleyebilirsiniz.");
        }

        try {

            return ResponseEntity.ok("Resim başarıyla yüklendi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Resim yüklenemedi.");
        }
    }

    @RequestMapping(value = "options", method = RequestMethod.OPTIONS)
    public ResponseEntity<String> options() {
        return ResponseEntity.ok().allow(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS, HttpMethod.POST, HttpMethod.PATCH).build();
    }
}
