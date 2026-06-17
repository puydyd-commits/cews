package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dao.CategoryMapper;
import com.example.dao.ProductMapper;
import com.example.pojo.Category;
import com.example.pojo.Product;
import com.example.pojo.ProductAddRequest;
import com.example.service.ProductService;
import com.example.util.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(ProductAddRequest request) {
        String trimmedProductName = request.getProductName().trim();
        if (!StringUtils.hasText(trimmedProductName)) {
            throw new RuntimeException("商品名称不能为空");
        }

        Long categoryId = request.getCategoryId();
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new RuntimeException("分类ID不存在");
        }

        int count = productMapper.countByCategoryIdAndProductName(categoryId, trimmedProductName);
        if (count > 0) {
            throw new RuntimeException("同一分类下已存在同名商品");
        }

        Product product = new Product();
        product.setProductName(trimmedProductName);
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategoryId(categoryId);
        product.setImageUrl(request.getImageUrl());
        product.setOrigin(request.getOrigin());
        
        String currentUser = LoginUserUtil.getCurrentUser();
        product.setCreator(currentUser);
        product.setUpdater(currentUser);
        
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());

        int result = productMapper.insert(product);
        if (result != 1) {
            throw new RuntimeException("商品插入失败");
        }
    }
}