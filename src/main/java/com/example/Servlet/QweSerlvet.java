package com.example.Servlet;

import com.example.pojo.Qwe;
import com.example.url.Result;


import java.util.List;

public interface QweSerlvet {
   Result inserta(Qwe qwe);
   List<Qwe> selectAll();


}
