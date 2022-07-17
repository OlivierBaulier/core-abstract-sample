package com.example.shop.core.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Builder
public class StockEntity {



    public StockEntity( String color, BigInteger size) {
        this.size = size;
        this.color = color;
    }

    @Id
    @Getter @Setter
    private Long stock_id;
    @Getter @Setter
    private BigInteger size;
    @Getter @Setter
    private String color;
    @Getter
    private Date inputDate;
    @Getter
    private Date outputDate;

    public StockEntity(Long stock_id, BigInteger size, String color, Date inputDate, Date outputDate) {
        this.stock_id = stock_id;
        this.size = size;
        this.color = color;
        this.inputDate = inputDate;
        this.outputDate = outputDate;
    }
}
