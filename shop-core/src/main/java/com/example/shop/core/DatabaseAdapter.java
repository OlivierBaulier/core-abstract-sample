package com.example.shop.core;

import com.example.shop.core.entities.FilterEntity;
import com.example.demo.dto.in.StockMovement;
import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.out.AvailableShoe;
import com.example.demo.dto.out.Shoe;

import java.util.List;

public interface DatabaseAdapter {
    List<Shoe> getCatalog(ShoeFilter filter);

    List<AvailableShoe> getStock();

    List<AvailableShoe> getStockWithFilter(FilterEntity filter);

    int countShoes(FilterEntity filter);

    int stock(StockMovement movement);

    int destock(StockMovement movement);
}
