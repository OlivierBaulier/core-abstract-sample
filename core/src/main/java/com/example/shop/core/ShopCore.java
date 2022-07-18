package com.example.shop.core;

import com.example.demo.dto.in.ShoeFilter;
import com.example.shop.dto.in.ModelFilter;
import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.Catalog;
import com.example.shop.dto.out.Stock;

public interface ShopCore  {

    /** Used to consult the shoe catalog of the shop
     *
     * @param filter  To limit the research to wanted models
     * @return  the list of the models available models
     */
    Catalog catalog(ModelFilter filter);

    Stock getStock();

    int stockUpdate(StockMovement[] movement) throws Exception;

}
