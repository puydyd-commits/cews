package com.example.controller;

import com.example.dao.CategoryMapper;
import com.example.pojo.Category;
import com.example.url.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/list")
    public ResponseEntity<Result<List<Category>>> getCategoryList() {
        List<Category> categories = categoryMapper.selectList(null);
        return ResponseEntity.ok(Result.success(categories));
    }
}