package com.example.demo.dto.out;

import com.example.demo.dto.in.ShoeFilter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;

@Value
@Builder
@JsonDeserialize(builder = AvailableShoe.class)
public class AvailableShoe {


    ShoeFilter.Color color;
    BigInteger size;
    int quantity;

    public AvailableShoe(ShoeFilter.Color color, BigInteger size, int quantity) {
        this.color = color;
        this.size = size;
        this.quantity = quantity;
    }
}
