package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.config.GlobalExceptionHandler.ResourceNotFoundException;
import com.example.dao.CategoryMapper;
import com.example.dao.ProductMapper;
import com.example.pojo.Category;
import com.example.pojo.Product;
import com.example.pojo.ProductAddRequest;
import com.example.service.ProductService;
import com.example.url.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @PostMapping("/add")
    public ResponseEntity<Result<Void>> addProduct(@Valid @RequestBody ProductAddRequest request) {
        validateProductAddRequest(request);
        productService.addProduct(request);
        return ResponseEntity.ok(Result.success("商品新增成功", null));
    }

    @GetMapping("/list")
    public ResponseEntity<Result<Map<String, Object>>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long categoryId) {

        pageNum = Math.max(1, pageNum);
        pageSize = Math.max(1, Math.min(100, pageSize));

        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(productName)) {
            queryWrapper.like(Product::getProductName, productName.trim());
        }
        if (categoryId != null && categoryId > 0) {
            Category category = categoryMapper.selectById(categoryId);
            if (category == null) {
                return ResponseEntity.ok(Result.error(400, "分类ID不存在"));
            }
            queryWrapper.eq(Product::getCategoryId, categoryId);
        }

        queryWrapper.orderByDesc(Product::getCreateTime);
        IPage<Product> productPage = productMapper.selectPage(page, queryWrapper);

        List<Product> productList = productPage.getRecords();
        
        Map<Long, String> categoryMap = new HashMap<>();
        if (!productList.isEmpty()) {
            List<Long> categoryIds = productList.stream()
                    .map(Product::getCategoryId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());
            if (!categoryIds.isEmpty()) {
                List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
                categories.forEach(cat -> categoryMap.put(cat.getId(), cat.getCategoryName()));
            }
        }

        productList.forEach(product -> {
            product.setCategoryName(categoryMap.get(product.getCategoryId()));
        });

        Map<String, Object> result = new HashMap<>();
        result.put("list", productList);
        result.put("total", productPage.getTotal());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);

        return ResponseEntity.ok(Result.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<Product>> getProductById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.ok(Result.error(400, "商品ID无效"));
        }
        
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new ResourceNotFoundException("商品不存在");
        }
        
        Category category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) {
            product.setCategoryName(category.getCategoryName());
        }
        
        return ResponseEntity.ok(Result.success(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<Void>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductAddRequest request) {

        if (id == null || id <= 0) {
            return ResponseEntity.ok(Result.error(400, "商品ID无效"));
        }

        Product existingProduct = productMapper.selectById(id);
        if (existingProduct == null) {
            throw new ResourceNotFoundException("商品不存在");
        }

        validateProductAddRequest(request);

        Long categoryId = request.getCategoryId();
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return ResponseEntity.ok(Result.error(400, "分类ID不存在"));
        }

        String trimmedProductName = request.getProductName().trim();
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getCategoryId, categoryId)
                .eq(Product::getProductName, trimmedProductName)
                .ne(Product::getId, id);
        if (productMapper.selectCount(queryWrapper) > 0) {
            return ResponseEntity.ok(Result.error(400, "同一分类下已存在同名商品"));
        }

        Product product = new Product();
        product.setId(id);
        product.setProductName(trimmedProductName);
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategoryId(categoryId);
        product.setImageUrl(request.getImageUrl());
        product.setOrigin(request.getOrigin());
        product.setUpdater(com.example.util.LoginUserUtil.getCurrentUser());
        product.setUpdateTime(java.time.LocalDateTime.now());

        productMapper.updateById(product);

        return ResponseEntity.ok(Result.success("商品更新成功", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteProduct(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.ok(Result.error(400, "商品ID无效"));
        }

        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new ResourceNotFoundException("商品不存在");
        }

        productMapper.deleteById(id);
        return ResponseEntity.ok(Result.success("商品删除成功", null));
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Result<Void>> batchDeleteProducts(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.ok(Result.error(400, "请选择要删除的商品"));
        }

        for (Long id : ids) {
            if (id == null || id <= 0) {
                return ResponseEntity.ok(Result.error(400, "商品ID无效"));
            }
        }

        int deleted = productMapper.deleteBatchIds(ids);
        return ResponseEntity.ok(Result.success("成功删除 " + deleted + " 个商品", null));
    }

    private void validateProductAddRequest(ProductAddRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        
        if (!StringUtils.hasText(request.getProductName())) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        
        String trimmedName = request.getProductName().trim();
        if (trimmedName.length() > 100) {
            throw new IllegalArgumentException("商品名称长度不能超过100个字符");
        }
        
        if (request.getPrice() == null) {
            throw new IllegalArgumentException("商品价格不能为空");
        }
        
        if (request.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("商品价格不能小于0");
        }
        
        if (request.getStock() == null) {
            throw new IllegalArgumentException("商品库存不能为空");
        }
        
        if (request.getStock() < 0) {
            throw new IllegalArgumentException("商品库存不能为负数");
        }
        
        if (request.getCategoryId() == null || request.getCategoryId() <= 0) {
            throw new IllegalArgumentException("分类ID无效");
        }
    }
}