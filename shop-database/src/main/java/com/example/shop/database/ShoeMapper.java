package com.example.shop.database;

import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.out.Shoe;
import com.example.shop.dto.out.ShoeModel;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShoeMapper implements RowMapper<ShoeModel> {

    @SneakyThrows
    @Override
    public ShoeModel mapRow(ResultSet rs, int rowNum)  {
        ShoeFilter.Color color ;

        return  ShoeModel.builder()
                .model_id(rs.getInt("model_id"))
                .name(rs.getString("name"))
                .color(rs.getString("color"))
                .size(rs.getInt("size")).build();
    }
}
