package com.example.demo.dto.in;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigInteger;

@Value
@Builder
@JsonDeserialize(builder = StockMovement.class)
public class StockMovement {

    @Getter
    public BigInteger size;
    @Getter public ShoeFilter.Color color;
    @Getter public int quantity;
}
