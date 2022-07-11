package com.example.shop.database;

import com.example.shop.core.entities.FilterEntity;
import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.in.StockMovement;
import com.example.demo.dto.out.AvailableShoe;
import com.example.demo.dto.out.Shoe;
import com.example.shop.core.DatabaseAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.stream.IntStream;

@Component
public class StockGateway implements DatabaseAdapter {

    private static final String FILTERED_AVAILABLE_SQL = "SELECT color, size, count(stock_id) AS quantity FROM SHOES_STOCK WHERE outputDate IS  NULL AND ( :color IS  NULL OR color = :color ) AND (:size IS  NULL OR size =:size ) GROUP BY color, size";
    private static final String CATALOG_SQL = "SELECT DISTINCT 'Shop shoe' as name, color, size FROM SHOES_STOCK  WHERE ( :color IS  NULL OR color = :color ) AND (:size IS  NULL OR size =:size )";
    private static final String AVAILABLE_SQL = "SELECT color, size, count(id) AS quantity FROM SHOES_STOCK WHERE outputDate IS NULL GROUP BY color, size";

    @Autowired
    @Qualifier("myJdbcTemplate")
    private JdbcTemplate myJdbcTemplate;

    @Autowired
    @Qualifier("myNamedParameterJdbcTemplate")
    private NamedParameterJdbcTemplate myNamedParameterJdbcTemplate;


    public List<AvailableShoe> getAllShoes() {

        return null;
    }

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
