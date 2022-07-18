package com.example.shop.database;

import com.example.shop.dto.in.ModelFilter;
import com.example.shop.dto.in.StockMovement;
import com.example.shop.dto.out.AvailableShoe;
import com.example.shop.core.DatabaseAdapter;
import com.example.shop.dto.out.ShoeModel;
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


    private static final String INSERT_SQL = "INSERT INTO SHOES_STOCK(model_id) (SELECT model_id FROM shoes_model  WHERE name = :name AND color = :color AND SIZE = :size)";
    private static final String STOCK_SQL = "SELECT model_id, name, color, size, count(S.stock_id) AS quantity FROM SHOES_MODEL M LEFT JOIN shoes_stock S ON S.model_id = M.model_id AND S.outputDate IS NULL "+
            " GROUP BY M.name, M.color, M.size";
    private static final String FILTERED_STOCK_SQL ="SELECT model_id, name, color, size, count(S.stock_id) AS quantity FROM SHOES_MODEL M LEFT JOIN shoes_stock S ON S.model_id = M.model_id AND S.outputDate IS NULL " +
            "WHERE S.outputDate IS NULL  AND ( :color IS  NULL OR M.color = :color ) AND (:size IS  NULL OR M.size =:size ) AND (:name IS  NULL OR M.name =:name ) " +
            "GROUP BY M.name, M.color, M.SIZE";

    private static final String DESTOCK_SQL = "UPDATE SHOES_STOCK set outputDate= NOW() WHERE outputDate IS NULL AND model_id IN ( SELECT model_id FROM SHOES_MODEL M " +
            "WHERE M.color = :color AND SIZE = :size) LIMIT :quantity";

    private static final String CATALOG_SQL = "SELECT model_id, name, color, size FROM SHOES_MODEL  WHERE ( :color IS  NULL OR color = :color ) AND (:size IS  NULL OR size =:size )";

    private static final String FILTERED_COUNT_SQL ="SELECT count(stock_id) AS quantity FROM SHOES_MODEL M LEFT JOIN shoes_stock S ON S.model_id = M.model_id AND S.outputDate IS NULL " +
            "WHERE  ( :color IS  NULL OR M.color = :color ) AND (:size IS  NULL OR M.size =:size ) AND (:name IS  NULL OR M.name =:name ) ";

    private static final String IS_MODEL_EXIST_SQL = "SELECT count(model_id) FROM shoes_model  WHERE name = :name AND color = :color AND SIZE = :size;  ";

    private static final String ADD_MODEL = "INSERT INTO SHOES_MODEL (name, color, size) VALUES(:name,:color, :size);";



    @Autowired
    @Qualifier("myJdbcTemplate")
    private JdbcTemplate myJdbcTemplate;

    @Autowired
    @Qualifier("myNamedParameterJdbcTemplate")
    private NamedParameterJdbcTemplate myNamedParameterJdbcTemplate;


    @Override
    public List<ShoeModel> getCatalog(ModelFilter filter) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("color", (filter != null )? filter.getColor() : null)
                .addValue("size", (filter != null  )? filter.getSize() : null)
                .addValue("name", (filter != null  )? filter.getName() : null);
        return myNamedParameterJdbcTemplate.query(CATALOG_SQL, parameters, new ShoeMapper() );
    }

    @Override
    public List<AvailableShoe> getStock() {
        return myJdbcTemplate.query(STOCK_SQL, new AvailableShoeMapper() );
    }

    @Override
    public List<AvailableShoe> getStockWithFilter(ModelFilter filter) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("color", filter.getColor())
                .addValue("size", filter.getSize())
                .addValue("name", filter.getName());
        return myNamedParameterJdbcTemplate.query(FILTERED_STOCK_SQL, parameters,  new AvailableShoeMapper());
    }

    @Override
    public int countShoes(ModelFilter filter) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("color", filter.getColor())
                .addValue("size", filter.getSize())
                .addValue("name", filter.getName());
        Integer result = myNamedParameterJdbcTemplate.queryForObject(FILTERED_COUNT_SQL, parameters, Integer.class);
        return (result!= null)? result: 0;
    }

    @Override
    public int stock(StockMovement movement) {
        this.addModelIfNotExist(movement);
        return IntStream.range(0, movement.getQuantity()).map(i -> {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("color", movement.getColor())
                    .addValue("size", movement.getSize())
                    .addValue("name", movement.getName());
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

    private void addModelIfNotExist(StockMovement movement){
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("color", movement.getColor())
                .addValue("size", movement.getSize())
                .addValue("name", movement.getName());
        Integer isExist = myNamedParameterJdbcTemplate.queryForObject(IS_MODEL_EXIST_SQL, parameters, Integer.class);
        if(isExist != null && isExist ==1) return;
        parameters = new MapSqlParameterSource()
                .addValue("color", movement.getColor())
                .addValue("size", movement.getSize())
                .addValue("name", movement.getName());
         myNamedParameterJdbcTemplate.update(ADD_MODEL, parameters);
    }

}
