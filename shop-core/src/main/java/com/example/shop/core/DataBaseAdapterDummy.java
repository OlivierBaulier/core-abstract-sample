package com.example.shop.core;

import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.in.StockMovement;
import com.example.demo.dto.out.AvailableShoe;
import com.example.demo.dto.out.Shoe;
import com.example.shop.core.entities.FilterEntity;
import org.springframework.stereotype.Component;

import java.beans.BeanProperty;
import java.util.List;

@Component
public class DataBaseAdapterDummy implements DatabaseAdapter{
    @Override
    public List<Shoe> getCatalog(ShoeFilter filter) {
        return null;
    }

    @Override
    public List<AvailableShoe> getStock() {
        return null;
    }

    @Override
    public List<AvailableShoe> getStockWithFilter(FilterEntity filter) {
        return null;
    }

    @Override
    public int countShoes(FilterEntity filter) {
        return 0;
    }

    @Override
    public int stock(StockMovement movement) {
        return 0;
    }

    @Override
    public int destock(StockMovement movement) {
        return 0;
    }
}
