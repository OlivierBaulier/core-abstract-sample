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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.stream.IntStream;


@Component
public class DatabaseGateway implements DatabaseAdapter {

    private static final String FILTERED_STOCK_SQL = "SELECT color, size, count(stock_id) AS quantity FROM SHOES_STOCK WHERE  ( :color IS  NULL OR color = :color ) AND (:size IS  NULL OR size =:size ) GROUP BY color, size";
    private static final String CATALOG_SQL = "SELECT DISTINCT 'Shop shoe' as name, color, size FROM SHOES_STOCK  WHERE ( :color IS  NULL OR color = :color ) AND (:size IS  NULL OR size =:size )";
    private static final String STOCK_SQL = "SELECT color, size, count(case outputDate when IS NULL then 1 else null end ) AS quantity FROM SHOES_STOCK  GROUP BY color, size";
    private static final String INSERT_SQL = "INSERT INTO SHOES_STOCK (color, size) VALUES(:color, :size)";
    private static final String FILTERED_COUNT_SQL = "SELECT count(stock_id) AS quantity FROM SHOES_STOCK WHERE outputDate IS  NULL AND ( :color IS  NULL OR color = :color ) AND (:size IS  NULL OR size =:size )";
    private static final String DESTOCK_SQL = "UPDATE SHOES_STOCK set outputDate= NOW() WHERE outputDate IS NULL AND color = :color AND SIZE = :size LIMIT :quantity";

    @Autowired
    @Qualifier("myJdbcTemplate")
    private JdbcTemplate myJdbcTemplate;

    @Autowired
    @Qualifier("myNamedParameterJdbcTemplate")
    private NamedParameterJdbcTemplate myNamedParameterJdbcTemplate;


    public List<AvailableShoe> getAllShoes() {
        return myJdbcTemplate.query(STOCK_SQL, new AvailableShoeMapper() );
    }

    @Override
    public List<Shoe> getCatalog(ShoeFilter filter) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("color", (filter != null && filter.getColor().isPresent() )? filter.getColor().get().name() : null)
                .addValue("size", (filter != null && filter.getSize().isPresent() )? filter.getSize().get() : null);
        return myNamedParameterJdbcTemplate.query(CATALOG_SQL, parameters, new ShoeMapper() );
    }

    @Override
    public List<AvailableShoe> getStock() {
        return myJdbcTemplate.query(STOCK_SQL, new AvailableShoeMapper() );
    }

    @Override
    public List<AvailableShoe> getStockWithFilter(FilterEntity filter) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("color", filter.getColor())
                .addValue("size", filter.getSize());
        return myNamedParameterJdbcTemplate.query(FILTERED_STOCK_SQL, parameters,  new AvailableShoeMapper());
    }

    @Override
    public int countShoes(FilterEntity filter) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("color", filter.getColor())
                .addValue("size", filter.getSize());
        return myNamedParameterJdbcTemplate.queryForObject(FILTERED_COUNT_SQL, parameters, Integer.class).intValue();
    }

    @Override
    public int stock(StockMovement movement) {

        return IntStream.range(0, movement.getQuantity()).map(i -> {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("color", movement.getColor())
                    .addValue("size", movement.getSize());
            return myNamedParameterJdbcTemplate.update(INSERT_SQL, parameters);
        }).sum();

    }

    @Override
    public int destock(StockMovement movement) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("color", movement.getColor())
                .addValue("size", movement.getSize())
                .addValue("quantity", - movement.getQuantity());
        return - myNamedParameterJdbcTemplate.update(DESTOCK_SQL, parameters);
    }

}
