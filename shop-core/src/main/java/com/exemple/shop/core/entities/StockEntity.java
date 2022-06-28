package com.exemple.shop.core.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

public class StockEntity {

    public StockEntity(Long id, String name, BigInteger size, String color) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.color = color;
    }

    @Getter @Setter private Long id;
    @Getter @Setter private String name;
    @Getter @Setter private BigInteger size;
    @Getter @Setter private String color;
}
