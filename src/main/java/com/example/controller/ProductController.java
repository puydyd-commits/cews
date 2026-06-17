package com.example.controller;

import com.example.pojo.ProductAddRequest;
import com.example.service.ProductService;
import com.example.url.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<Result<Void>> addProduct(@Valid @RequestBody ProductAddRequest request) {
        productService.addProduct(request);
        return ResponseEntity.ok(Result.success("商品新增成功", null));
    }
}