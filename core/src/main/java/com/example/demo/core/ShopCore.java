package com.example.demo.core;

import com.example.demo.dto.in.StockMovement;
import com.example.demo.dto.out.Stock;

public interface ShopCore  {

    Stock getStock();

    int stockUpdate(StockMovement[] movement) throws Exception;

}
