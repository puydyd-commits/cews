package com.example.dao;

import com.example.pojo.Qwe;
import com.example.url.Query;

import java.util.List;

public interface QweDao {
int inserta(Qwe qwe);
List<Qwe> selectAll();
}
