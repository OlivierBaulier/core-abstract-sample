package com.example.shop.core;

import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.Stock;

public interface ShopCore  {

    Stock getStock();

    int stockUpdate(StockMovement[] movement) throws Exception;

}
