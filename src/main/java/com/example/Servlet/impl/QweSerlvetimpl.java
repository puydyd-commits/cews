package com.example.Servlet.impl;

import com.example.Servlet.QweSerlvet;
import com.example.dao.QweDao;
import com.example.pojo.Qwe;
import com.example.url.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QweSerlvetimpl implements QweSerlvet {
@Autowired
private QweDao qweDao;
    @Override
    public Result inserta(Qwe qwe) {
       int  i= qweDao.inserta(qwe);
        return (i>0)?Result.OK():Result.ERROR();
    }

    @Override
    public List<Qwe> selectAll() {
        return qweDao.selectAll();
    }
}
