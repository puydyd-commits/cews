package com.example.controller;

import com.example.Servlet.QweSerlvet;
import com.example.pojo.Qwe;
import com.example.url.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("qwe")
public class QweController {
    @Autowired
    private QweSerlvet qweSerlvet;
    @RequestMapping("add")
    public Result add(@RequestBody Qwe qwe) {
        return   qweSerlvet.inserta(qwe);
    }
    @RequestMapping("findAll")
    public Result findAll() {
        List<Qwe> qwes = qweSerlvet.selectAll();
        Result result = new Result();
        result.setData(qwes);
        return result;
    }
}
