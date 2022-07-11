package com.example.shop.database;

import com.example.demo.dto.out.AvailableShoe;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AvailableShoeMapper implements RowMapper<AvailableShoe> {

    @Override
    public AvailableShoe mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AvailableShoe.builder()
                .color(rs.getString("color"))
                .quantity(rs.getInt("quantity"))
                .size(rs.getBigDecimal("size").toBigInteger()).build();
    }
}
