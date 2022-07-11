package com.example.shop.database;

import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.out.Shoe;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShoeMapper implements RowMapper<Shoe> {

    @SneakyThrows
    @Override
    public Shoe mapRow(ResultSet rs, int rowNum) throws SQLException {
        ShoeFilter.Color color ;
        switch(rs.getString("color")){
            case "BLACK":
                color = ShoeFilter.Color.BLACK;
                break;
            case "BLUE":
                color = ShoeFilter.Color.BLUE;
                break;
            default:
                throw new Exception(String.format("Unexpected  color: \"%s\"", rs.getString("color")));
        }

        return  Shoe.builder()
                .name("Shop shoe")
                .color(color)
                .size(rs.getBigDecimal("size").toBigInteger()).build();
    }
}
